/*
 * Copyright © 2021 Treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.crysknife.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import io.crysknife.annotation.Application;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.BeanDefinitionFactory;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.definition.MethodDefinition;
import io.crysknife.definition.MethodDefinitionFactory;
import io.crysknife.definition.ProducesBeanDefinition;
import io.crysknife.definition.VariableDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.api.Generator;
import io.crysknife.generator.api.IOCGenerator;
import io.crysknife.generator.api.WiringElementType;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.context.oracle.BeanOracle;
import io.crysknife.logger.TreeLogger;
import io.crysknife.processor.ProducesProcessor;
import io.crysknife.util.TypeUtils;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/3/21
 */
public class BeanProcessorTask implements Task {

  private final IOCContext iocContext;
  private final TreeLogger logger;
  private final TypeMirror objectTypeMirror;
  private final Types types;
  private final Elements elements;
  private final RoundEnvironment roundEnvironment;
  private final BeanDefinitionFactory beanDefinitionFactory;
  private final MethodDefinitionFactory methodDefinitionFactory;
  private final Set<String> scoped;
  private final BeanOracle oracle;

  public BeanProcessorTask(IOCContext iocContext, TreeLogger logger) {
    this.iocContext = iocContext;
    this.logger = logger;
    this.oracle = new BeanOracle(iocContext, logger);
    this.beanDefinitionFactory = new BeanDefinitionFactory(iocContext, logger);
    this.methodDefinitionFactory = new MethodDefinitionFactory(iocContext, logger);

    this.types = iocContext.getGenerationContext().getTypes();
    this.elements = iocContext.getGenerationContext().getElements();
    this.roundEnvironment = iocContext.getGenerationContext().getRoundEnvironment();
    this.objectTypeMirror = elements.getTypeElement(Object.class.getCanonicalName()).asType();

    this.scoped = iocContext.getGenerators().entries().stream()
        .filter(gen -> gen.getKey().wiringElementType.equals(WiringElementType.BEAN))
        .sorted(Comparator
            .comparingInt(o -> o.getValue().getClass().getAnnotation(Generator.class).priority()))
        .map(v -> v.getKey().annotation).collect(Collectors.toSet());
  }

  public void execute() {
    Set<TypeElement> elements = findClasspathAndRoundBeansAnnotatedWithScopeAnnotations();
    execute(elements);
  }

  public void execute(Set<TypeElement> elements) {
    elements.forEach(this::processBean);
    process();
  }

  private void process() {
    processInjectionPointsInUnscopedBeans();
    findProduces();

    processTypes();
    processTypeDecorators();
    processFieldDecorators();
    processMethodDecorators();

    logger.log(TreeLogger.INFO, "beans registered " + iocContext.getBeans().size());

    long count = iocContext.getBeans().values().stream().map(BeanDefinition::getFields)
        .mapToLong(Collection::size).sum();
    count = count + iocContext.getBeans().values().stream()
        .map(BeanDefinition::getConstructorParams).mapToLong(Collection::size).sum();

    logger.log(TreeLogger.INFO, "fields registered " + count);
  }

  // TODO annotated field could be non-injectable
  private void processFieldDecorators() {
    iocContext.getGenerators().asMap().entrySet().stream()
        .filter(iocGeneratorMetaCollectionEntry -> iocGeneratorMetaCollectionEntry
            .getKey().wiringElementType.equals(WiringElementType.FIELD_DECORATOR))
        .forEach(iocGeneratorMetaCollectionEntry -> {
          iocGeneratorMetaCollectionEntry.getValue().forEach(gen -> {
            TypeElement annotation =
                elements.getTypeElement(iocGeneratorMetaCollectionEntry.getKey().annotation);

            Set<VariableElement> elements = roundEnvironment.getElementsAnnotatedWith(annotation)
                .stream().filter(elm -> elm.getKind().isField())
                .map(field -> MoreElements.asVariable(field)).collect(Collectors.toSet());

            elements.addAll(iocContext
                .getFieldsByAnnotation(iocGeneratorMetaCollectionEntry.getKey().annotation));

            elements.forEach(field -> {
              TypeMirror erased = iocContext.getGenerationContext().getTypes()
                  .erasure(field.getEnclosingElement().asType());
              BeanDefinition bean = iocContext.getBeans().get(erased);
              bean.getFields().stream().filter(f -> f.getVariableElement().equals(field))
                  .forEach(f -> f.getDecorators()
                      .addAll(iocGeneratorMetaCollectionEntry.getValue().stream()
                          .map(em -> (IOCGenerator<VariableDefinition>) em)
                          .collect(Collectors.toSet())));
            });
          });
        });
  }

  private void processTypeDecorators() {
    iocContext.getGenerators().asMap().entrySet().stream()
        .filter(iocGeneratorMetaCollectionEntry -> iocGeneratorMetaCollectionEntry
            .getKey().wiringElementType.equals(WiringElementType.CLASS_DECORATOR))
        .forEach(iocGeneratorMetaCollectionEntry -> {
          iocContext
              .getTypeElementsByAnnotation(iocGeneratorMetaCollectionEntry.getKey().annotation)
              .stream().map(e -> iocContext.getGenerationContext().getTypes().erasure(e.asType()))
              .forEach(type -> iocContext.getBean(type).getDecorators()
                  .addAll(iocGeneratorMetaCollectionEntry.getValue().stream()
                      .map(em -> (IOCGenerator<BeanDefinition>) em).collect(Collectors.toSet())));
        });
  }

  private void processMethodDecorators() {
    iocContext.getGenerators().asMap().entrySet().stream()
        .filter(iocGeneratorMetaCollectionEntry -> iocGeneratorMetaCollectionEntry
            .getKey().wiringElementType.equals(WiringElementType.METHOD_DECORATOR))
        .forEach(iocGeneratorMetaCollectionEntry -> iocGeneratorMetaCollectionEntry.getValue()
            .forEach(gen -> {
              TypeElement annotation =
                  elements.getTypeElement(iocGeneratorMetaCollectionEntry.getKey().annotation);
              Set<ExecutableElement> elements =
                  iocContext.getMethodsByAnnotation(annotation.toString()).stream()
                      .filter(elm -> elm.getKind().equals(ElementKind.METHOD))
                      .map(elm -> MoreElements.asExecutable(elm)).collect(Collectors.toSet());

              elements.forEach(e -> {
                BeanDefinition bean = iocContext.getBean(e.getEnclosingElement().asType());
                ExecutableType methodType = (ExecutableType) e.asType();
                bean.getMethods().stream()
                    .filter(mmethod -> MoreTypes
                        .asExecutable(mmethod.getExecutableElement().asType()).equals(methodType))
                    .findFirst().orElse(methodDefinitionFactory.of(bean, e)).getDecorators()
                    .addAll(iocGeneratorMetaCollectionEntry.getValue().stream()
                        .map(em -> (IOCGenerator<MethodDefinition>) em)
                        .collect(Collectors.toSet()));
              });
            }));
  }

  private void findProduces() {
    ProducesProcessor producesProcessor = new ProducesProcessor(iocContext, logger);
    Set<Element> foundByAPT =
        (Set<Element>) roundEnvironment.getElementsAnnotatedWith(Produces.class);
    Set<Element> produces = new HashSet<>(foundByAPT);
    produces.addAll(iocContext.getMethodsByAnnotation(Produces.class.getCanonicalName()));
    List<UnableToCompleteException> errors = new ArrayList<>();

    for (Element produce : produces) {
      try {
        producesProcessor.process(produce);
      } catch (UnableToCompleteException e) {
        errors.add(e);
      }
    }

    if (!errors.isEmpty()) {
      for (UnableToCompleteException error : errors) {
        logger.log(TreeLogger.Type.ERROR, error.getMessage());
      }
      throw new GenerationException();
    }
  }

  private void processTypes() {
    iocContext.getBeans().forEach((type, definition) -> {
      Set<InjectableVariableDefinition> dependencies = new HashSet<>();
      dependencies.addAll(definition.getFields());
      dependencies.addAll(definition.getConstructorParams());

      for (InjectableVariableDefinition point : dependencies) {
        checkIfGeneric(point.getVariableElement());

        TypeMirror beanTypeMirror = types.erasure(point.getVariableElement().asType());
        TypeElement beanTypeElement = MoreTypes.asTypeElement(beanTypeMirror);

        Optional<IOCGenerator> candidate = iocContext.getGenerator(Inject.class.getCanonicalName(),
            beanTypeElement, WiringElementType.FIELD_TYPE);

        BeanDefinition implementation = oracle.guess(type, point);
        // Case 1: buildin type
        if (candidate.isPresent()) {
          point.setGenerator(candidate.get());
          point.setImplementation(implementation);
        } else if (!(iocContext.getBeans().get(beanTypeMirror) instanceof ProducesBeanDefinition)) {
          if (implementation != null) {
            point.setImplementation(implementation);
            definition.getDependencies().add(implementation);
          }
        }
      }
    });
  }

  private void checkIfGeneric(VariableElement variableElement) {}

  private Set<TypeElement> findClasspathAndRoundBeansAnnotatedWithScopeAnnotations() {
    return Stream.of(scoped.stream().map(sc -> {
      TypeElement annotation = elements.getTypeElement(sc);
      return roundEnvironment.getElementsAnnotatedWith(annotation);
    }).flatMap(Collection::stream).filter(elm -> elm.getKind().isClass()).map(MoreElements::asType),
        scoped.stream()
            .map(sc -> iocContext.getGenerationContext().getScanResult()
                .getClassesWithAnnotation(sc))
            .flatMap(Collection::stream).map(elm -> elements.getTypeElement(elm.getName()))
            .filter(elm -> elm.getKind().isClass()))
        .flatMap(Function.identity()).collect(Collectors.toSet());
  }

  private void processInjectionPointsInUnscopedBeans() {
    String[] annotations = scoped.toArray(new String[scoped.size()]);

    Set<TypeMirror> unscoped = roundEnvironment.getElementsAnnotatedWith(Inject.class).stream()
        .map(p -> MoreElements.asType(p.getEnclosingElement()))
        .filter(type -> type.getAnnotation(Application.class) == null) // TODO
        .filter(
            type -> type.getKind().isClass() && !type.getModifiers().contains(Modifier.ABSTRACT))
        .filter(point -> !TypeUtils.containsAnnotation(point, annotations))
        .map(elm -> iocContext.getGenerationContext().getTypes().erasure(elm.asType()))
        .filter(type -> !iocContext.getBeans().containsKey(type)).collect(Collectors.toSet());

    IOCGenerator dependentGenerator = iocContext.getGenerator(Dependent.class.getCanonicalName(),
        MoreTypes.asTypeElement(objectTypeMirror), WiringElementType.BEAN).get();

    for (TypeMirror typeMirror : unscoped) {
      TypeElement typeElement = MoreTypes.asTypeElement(typeMirror);
      processBean(typeElement).ifPresent(bean -> bean.setIocGenerator(dependentGenerator));
    }
  }

  private Optional<BeanDefinition> processBean(TypeElement type) {
    TypeMirror key = types.erasure(type.asType());
    if (!iocContext.getBeans().containsKey(key)) {
      try {
        BeanDefinition bean = beanDefinitionFactory.of(key);
        setBeanDefinitionGenerator(bean);

        for (TypeMirror supr : types.directSupertypes(key)) {
          if (!types.isSameType(objectTypeMirror, supr)) {
            TypeMirror parent = types.erasure(supr);
            Optional<BeanDefinition> candidate = processBean(MoreTypes.asTypeElement(parent));
            candidate.ifPresent(can -> can.getSubclasses().add(bean));
          }
        }
        iocContext.getBeans().put(key, bean);
        return Optional.of(bean);
      } catch (UnableToCompleteException e) {
        throw new GenerationException(e);
      }
    } else {
      return Optional.of(iocContext.getBeans().get(key));
    }
  }

  private void setBeanDefinitionGenerator(BeanDefinition bean) {
    Set<String> intersection = bean.getAnnotationMirrors().stream()
        .map(anno -> anno.getAnnotationType().toString()).collect(Collectors.toSet());
    intersection.retainAll(scoped);

    if (!intersection.isEmpty()) {
      String annotation = intersection.iterator().next();
      Optional<IOCGenerator> generator = iocContext.getGenerator(annotation,
          MoreTypes.asTypeElement(objectTypeMirror), WiringElementType.BEAN);
      generator.ifPresent(bean::setIocGenerator);
    }
  }
}
