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

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import io.crysknife.annotation.Generator;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.BeanDefinitionFactory;
import io.crysknife.definition.InjectionPointDefinition;
import io.crysknife.definition.MethodDefinitionFactory;
import io.crysknife.definition.ProducesBeanDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.IOCGenerator;
import io.crysknife.generator.WiringElementType;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.context.oracle.BeanOracle;
import io.crysknife.logger.TreeLogger;
import io.crysknife.processor.ProducesProcessor;
import io.crysknife.util.Utils;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/3/21
 */
public class BeanProcessorTask implements Task {

  private final IOCContext iocContext;
  private final TreeLogger logger;
  private final TypeMirror objectTypeMirror;
  private final Types types;
  private final BeanDefinitionFactory beanDefinitionFactory;
  private final MethodDefinitionFactory methodDefinitionFactory;
  private final Set<String> scoped;
  private final BeanOracle oracle;
  private final Set<TypeMirror> buildin = new HashSet<>();

  public BeanProcessorTask(IOCContext iocContext, TreeLogger logger) {
    this.iocContext = iocContext;
    this.logger = logger;
    this.oracle = new BeanOracle(iocContext);
    this.beanDefinitionFactory = new BeanDefinitionFactory(iocContext, logger);
    this.methodDefinitionFactory = new MethodDefinitionFactory(iocContext, logger);
    this.objectTypeMirror = iocContext.getGenerationContext().getElements()
        .getTypeElement(Object.class.getCanonicalName()).asType();

    types = iocContext.getGenerationContext().getTypes();

    scoped = iocContext.getGenerators().entries().stream()
        .sorted(Comparator
            .comparingInt(o -> o.getValue().getClass().getAnnotation(Generator.class).priority()))
        .filter(gen -> gen.getKey().wiringElementType.equals(WiringElementType.BEAN))
        .map(v -> v.getKey().annotation).collect(Collectors.toSet());
  }

  public void execute() {
    logger.log(TreeLogger.INFO, "start processing");
    findInjectionPoints();
    findProduces();

    processTypes();
    processTypeDecorators();
    processMethodDecorators();
    processMethodParamDecorators();

    logger.log(TreeLogger.INFO, "beans registered " + iocContext.getBeans().size());

    long count = iocContext.getBeans().values().stream().map(BeanDefinition::getFields)
        .flatMap(Collection::stream).count();
    count = count + iocContext.getBeans().values().stream()
        .map(BeanDefinition::getConstructorParams).flatMap(Collection::stream).count();


    logger.log(TreeLogger.INFO, "fields registered " + count);
  }

  private void processMethodParamDecorators() {
    iocContext.getGenerators().asMap().entrySet().stream()
        .filter(iocGeneratorMetaCollectionEntry -> iocGeneratorMetaCollectionEntry
            .getKey().wiringElementType.equals(WiringElementType.PARAMETER))
        .forEach(iocGeneratorMetaCollectionEntry -> {
          iocGeneratorMetaCollectionEntry.getValue().forEach(gen -> {

            TypeElement annotation = iocContext.getGenerationContext().getElements()
                .getTypeElement(iocGeneratorMetaCollectionEntry.getKey().annotation);
            Set<ExecutableElement> elements = iocContext.getGenerationContext()
                .getRoundEnvironment().getElementsAnnotatedWith(annotation).stream()
                .filter(elm -> elm.getKind().equals(ElementKind.PARAMETER))
                .map(elm -> MoreElements.asVariable(elm))
                .map(elm -> MoreElements.asExecutable(elm.getEnclosingElement()))
                .map(elm -> MoreElements.asExecutable(elm)).collect(Collectors.toSet());

            elements.stream().forEach(e -> {
              TypeMirror erased = iocContext.getGenerationContext().getTypes()
                  .erasure(e.getEnclosingElement().asType());
              BeanDefinition bean = iocContext.getBeans().get(erased);
              ExecutableType methodType = (ExecutableType) e.asType();
              bean.getMethods().stream()
                  .filter(mmethod -> iocContext.getGenerationContext().getTypes()
                      .isSameType(methodType, mmethod.getExecutableElement().asType()))
                  .findFirst().orElse(methodDefinitionFactory.of(bean, e)).getDecorators()
                  .addAll(iocGeneratorMetaCollectionEntry.getValue());
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
              .forEach(type -> iocContext.getBeans().get(type).getDecorators()
                  .addAll(iocGeneratorMetaCollectionEntry.getValue()));
        });
  }

  private void processMethodDecorators() {
    iocContext.getGenerators().asMap().entrySet().stream()
        .filter(iocGeneratorMetaCollectionEntry -> iocGeneratorMetaCollectionEntry
            .getKey().wiringElementType.equals(WiringElementType.METHOD_DECORATOR))
        .forEach(iocGeneratorMetaCollectionEntry -> iocGeneratorMetaCollectionEntry.getValue()
            .forEach(gen -> {
              TypeElement annotation = iocContext.getGenerationContext().getElements()
                  .getTypeElement(iocGeneratorMetaCollectionEntry.getKey().annotation);

              Set<ExecutableElement> elements = iocContext.getGenerationContext()
                  .getRoundEnvironment().getElementsAnnotatedWith(annotation).stream()
                  .filter(elm -> elm.getKind().equals(ElementKind.METHOD))
                  .map(elm -> MoreElements.asExecutable(elm)).collect(Collectors.toSet());

              elements.stream().forEach(e -> {
                TypeMirror erased = iocContext.getGenerationContext().getTypes()
                    .erasure(e.getEnclosingElement().asType());
                BeanDefinition bean = iocContext.getBeans().get(erased);
                ExecutableType methodType = (ExecutableType) e.asType();
                bean.getMethods().stream()
                    .filter(mmethod -> iocContext.getGenerationContext().getTypes()
                        .isSameType(methodType, mmethod.getExecutableElement().asType()))
                    .findFirst().orElse(methodDefinitionFactory.of(bean, e)).getDecorators()
                    .addAll(iocGeneratorMetaCollectionEntry.getValue());
              });
            }));
  }

  private void findProduces() {
    ProducesProcessor producesProcessor = new ProducesProcessor(iocContext, logger);

    Set<Element> produces = (Set<Element>) iocContext.getGenerationContext().getRoundEnvironment()
        .getElementsAnnotatedWith(Produces.class);

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
      Set<InjectionPointDefinition> dependencies = new HashSet<>();
      dependencies.addAll(definition.getFields());
      dependencies.addAll(definition.getConstructorParams());

      for (InjectionPointDefinition point : dependencies) {
        checkIfGeneric(point.getVariableElement());

        TypeMirror beanTypeMirror = iocContext.getGenerationContext().getTypes()
            .erasure(point.getVariableElement().asType());
        TypeElement beanTypeElement = MoreTypes.asTypeElement(beanTypeMirror);

        Optional<IOCGenerator> candidate = getGenerator(Inject.class.getCanonicalName(),
            beanTypeElement, WiringElementType.FIELD_TYPE);

        // Case 1: buildin type
        if (candidate.isPresent()) {
          point.setGenerator(candidate.get());
        } else if (iocContext.getBeans().get(beanTypeMirror) instanceof ProducesBeanDefinition) {
          // TODO
        } else {
          BeanDefinition implementation = oracle.guess(point);
          if (implementation != null) {
            point.setImplementation(implementation);
            definition.getDependencies().add(implementation);
          }
        }
      }
    });
  }

  private void checkIfGeneric(VariableElement variableElement) {}

  private void findInjectionPoints() {
    TreeLogger logger = this.logger.branch(TreeLogger.INFO, "find Injection Points");

    iocContext.getGenerators().entries().stream().map(gen -> gen.getKey().exactType)
        .filter(elm -> !types.isSameType(elm.asType(), objectTypeMirror)).map(TypeElement::asType)
        .forEach(buildin::add);


    Set<TypeElement> annotatedScopedBean = scoped.stream().map(sc -> {
      TypeElement annotation = iocContext.getGenerationContext().getElements().getTypeElement(sc);
      return iocContext.getGenerationContext().getRoundEnvironment()
          .getElementsAnnotatedWith(annotation);
    }).flatMap(Collection::stream).filter(elm -> elm.getKind().isClass()).map(MoreElements::asType)
        .sorted(Comparator.comparing(o -> o.getQualifiedName().toString()))
        .collect(Collectors.toCollection(LinkedHashSet::new));

    scoped.stream()
        .map(sc -> iocContext.getGenerationContext().getScanResult().getClassesWithAnnotation(sc))
        .flatMap(Collection::stream)
        .map(elm -> iocContext.getGenerationContext().getElements().getTypeElement(elm.getName()))
        .filter(elm -> elm != null)
        .forEach(annotatedScopedBean::add);
    processBeans(annotatedScopedBean);
  }

  private void processBeans(Set<TypeElement> annotatedScopedBean) {
    List<UnableToCompleteException> errors = new ArrayList<>();
    for (TypeElement typeElement : annotatedScopedBean) {
      try {
        processBean(typeElement);
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

  private Optional<BeanDefinition> processBean(TypeElement type) throws UnableToCompleteException {
    TypeMirror key = types.erasure(type.asType());
    if (!iocContext.getBeans().containsKey(key)) {
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
    } else {
      return Optional.of(iocContext.getBeans().get(key));
    }
  }

  private void setBeanDefinitionGenerator(BeanDefinition bean) {
    Set<String> annotations = bean.getAnnotationMirrors().stream()
        .map(anno -> anno.getAnnotationType().toString()).collect(Collectors.toSet());

    Set<String> intersection = new HashSet<>(annotations);
    intersection.retainAll(scoped);

    if (!intersection.isEmpty()) {
      String annotation = intersection.iterator().next();
      Optional<IOCGenerator> generator = getGenerator(annotation,
          MoreTypes.asTypeElement(objectTypeMirror), WiringElementType.BEAN);
      generator.ifPresent(bean::setIocGenerator);
    }
  }

  Optional<IOCGenerator> getGenerator(String annotation, TypeElement type,
      WiringElementType wiringElementType) {
    IOCContext.IOCGeneratorMeta meta =
        new IOCContext.IOCGeneratorMeta(annotation, type, wiringElementType);
    Iterable<IOCGenerator> generators = iocContext.getGenerators().get(meta);
    if (generators.iterator().hasNext()) {
      return Optional.of(generators.iterator().next());
    }
    return Optional.empty();
  }

  public Map<TypeMirror, BeanDefinition> getBeans() {
    return iocContext.getBeans();
  }

}
