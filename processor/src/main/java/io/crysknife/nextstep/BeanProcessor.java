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

package io.crysknife.nextstep;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import io.crysknife.exception.GenerationException;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.WiringElementType;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.PrintWriterTreeLogger;
import io.crysknife.logger.TreeLogger;
import io.crysknife.nextstep.definition.BeanDefinition;
import io.crysknife.nextstep.definition.BeanDefinitionFactory;

import javax.inject.Inject;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
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
public class BeanProcessor {

  private final IOCContext iocContext;
  private final PrintWriterTreeLogger logger;
  private final TypeMirror objectTypeMirror;
  private final Types types;
  private final Elements elements;
  private final BeanDefinitionFactory beanDefinitionFactory;

  private Map<String, BeanDefinition> holder = new HashMap<>();
  private Map<TypeMirror, BeanDefinition> beans = new HashMap<>();
  private Set<TypeMirror> buildin = new HashSet<>();

  public BeanProcessor(IOCContext iocContext, PrintWriterTreeLogger logger) {
    this.iocContext = iocContext;
    this.logger = logger;
    this.beanDefinitionFactory = new BeanDefinitionFactory(iocContext, logger);
    this.objectTypeMirror = iocContext.getGenerationContext().getElements()
        .getTypeElement(Object.class.getCanonicalName()).asType();

    types = iocContext.getGenerationContext().getTypes();
    elements = iocContext.getGenerationContext().getElements();
  }

  public void process() {
    logger.log(TreeLogger.INFO, "start processing");
    findInjectionPoints();



    beans.forEach((k, v) -> {
      System.out.println("BEAN " + k);

      v.getSubclasses().forEach(sub -> {
        System.out.println("          " + sub.getType());
      });

      v.getFields().forEach(field -> {
        System.out.println("          F " + field.getVariableElement());
      });
      v.getConstructorParams().forEach(field -> {
        System.out.println("          C " + field.getVariableElement());
      });

    });

    holder.forEach((k, v) -> {
      // System.out.println("REZ " + k);
      v.getFields().forEach(f -> {
        // System.out.println(" field " + f + " " + v.getType());

      });
    });

    logger.log(TreeLogger.INFO, "beans registred " + holder.size());

    long count =
        holder.values().stream().map(BeanDefinition::getFields).flatMap(Collection::stream).count();

    logger.log(TreeLogger.INFO, "fields registred " + count);


  }

  private void findInjectionPoints() {
    TreeLogger logger = this.logger.branch(TreeLogger.INFO, "find Injection Points");

    Set<String> scoped = iocContext.getGenerators().entries().stream()
        .filter(gen -> gen.getKey().wiringElementType.equals(WiringElementType.BEAN))
        .map(v -> v.getKey().annotation).collect(Collectors.toSet());

    iocContext.getGenerators().entries().stream().map(gen -> gen.getKey().exactType)
        .filter(elm -> !types.isSameType(elm.asType(), objectTypeMirror)).map(type -> type.asType())
        .forEach(e -> buildin.add(e));


    Set<TypeElement> annotatedScopedBean = scoped.stream().map(sc -> {
      TypeElement annotation = iocContext.getGenerationContext().getElements().getTypeElement(sc);
      return iocContext.getGenerationContext().getRoundEnvironment()
          .getElementsAnnotatedWith(annotation);
    }).flatMap(Collection::stream).filter(elm -> elm.getKind().isClass()).map(MoreElements::asType)
        .sorted(Comparator.comparing(o -> o.getQualifiedName().toString()))
        .collect(Collectors.toCollection(LinkedHashSet::new));


    processBeans(annotatedScopedBean);


    annotatedScopedBean.stream().forEach(clazz -> {
      // System.out.println("CLAZZ " + clazz.getQualifiedName());
    });



    scoped.forEach(sc -> {
      // System.out.println("BEAN " + sc);
    });

    Set<Element> points = (Set<Element>) iocContext.getGenerationContext().getRoundEnvironment()
        .getElementsAnnotatedWith(Inject.class);

    points.forEach(p -> {
      if (p.getKind().equals(ElementKind.FIELD)) {
        VariableElement variableElement = (VariableElement) p;
        /*
         * logger.branch(TreeLogger.INFO, "field " + variableElement.asType() + " named " +
         * variableElement.getSimpleName().toString() + " at " +
         * variableElement.getEnclosingElement());
         */
      } else if (p.getKind().equals(ElementKind.CONSTRUCTOR)) {
        ExecutableElement executableElement = (ExecutableElement) p;

        executableElement.getParameters().stream().forEach(c -> {
          VariableElement variableElement = (VariableElement) c;
          /*
           * logger.branch(TreeLogger.INFO, "const " + variableElement.asType() + " named " +
           * variableElement.getSimpleName().toString() + " at " +
           * variableElement.getEnclosingElement());
           */
        });

      }


      // logger.branch(TreeLogger.INFO, "point ");

    });
  }

  private void processBeans(Set<TypeElement> annotatedScopedBean) {
    List<UnableToCompleteException> errors = new ArrayList<>();
    for (TypeElement typeElement : annotatedScopedBean) {
      try {
        processBean(typeElement);
      } catch (UnableToCompleteException e) {
        errors.add(e);
      }
      if (!errors.isEmpty()) {
        for (UnableToCompleteException error : errors) {
          logger.log(TreeLogger.Type.ERROR, error.getMessage());
        }
        throw new GenerationException();
      }
    }
  }

  private Optional<BeanDefinition> processBean(TypeElement type) throws UnableToCompleteException {
    TypeMirror key = types.erasure(type.asType());
    if (!beans.containsKey(key)) {
      BeanDefinition bean = beanDefinitionFactory.of(key);
      for (TypeMirror supr : types.directSupertypes(key)) {
        if (!types.isSameType(objectTypeMirror, supr)) {
          TypeMirror parent = types.erasure(supr);
          Optional<BeanDefinition> candidate = processBean(MoreTypes.asTypeElement(parent));
          candidate.ifPresent(can -> can.getSubclasses().add(bean));
        }
      }
      beans.put(key, bean);
      return Optional.of(bean);
    } else {
      return Optional.of(beans.get(key));
    }
  }

}
