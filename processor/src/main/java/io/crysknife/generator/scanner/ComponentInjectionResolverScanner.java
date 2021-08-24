/*
 * Copyright © 2020 Treblereel
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

package io.crysknife.generator.scanner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Default;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import io.crysknife.exception.GenerationException;
import io.crysknife.generator.IOCGenerator;
import io.crysknife.generator.WiringElementType;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.definition.BeanDefinition;
import io.crysknife.generator.point.FieldPoint;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/5/19
 */
public class ComponentInjectionResolverScanner {

  private final IOCContext iocContext;

  private Set<TypeElement> dependentBeans = new HashSet<>();

  public ComponentInjectionResolverScanner(IOCContext iocContext) {
    this.iocContext = iocContext;
  }

  public void scan() {

    Set<String> annotations = iocContext.getGenerators().entries().stream()
        .filter(elm -> elm.getKey().wiringElementType.equals(WiringElementType.BEAN))
        .map(elm -> elm.getKey().annotation).collect(Collectors.toSet());

    annotations.forEach(annotation -> iocContext.getTypeElementsByAnnotation(annotation)
        .forEach(bean -> iocContext.getBeanDefinitionOrCreateAndReturn(bean)));

    iocContext.getBeans().forEach((type, bean) -> {
      for (FieldPoint field : bean.getFieldInjectionPoints()) {
        processFieldInjectionPoint(field, bean);
      }
      if (bean.getConstructorInjectionPoint() != null) {
        bean.getConstructorInjectionPoint().getArguments()
            .forEach(field -> processFieldInjectionPoint(field, bean));
      }
    });

    addUnmanagedBeans();
  }

  // Process as Dependent Beans //TODO
  private void addUnmanagedBeans() {
    TypeElement type = iocContext.getGenerationContext().getElements()
        .getTypeElement(Object.class.getCanonicalName());

    IOCContext.IOCGeneratorMeta meta = new IOCContext.IOCGeneratorMeta(
        Dependent.class.getCanonicalName(), type, WiringElementType.BEAN);

    dependentBeans.forEach(bean -> {
      BeanDefinition beanDefinition = iocContext.getBeanDefinitionOrCreateAndReturn(bean);
      if (iocContext.getGenerators().get(meta).stream().findFirst().isPresent()) {
        IOCGenerator gen = iocContext.getGenerators().get(meta).stream().findFirst().get();
        beanDefinition.setGenerator(gen);
        iocContext.getBeans().put(bean, beanDefinition);
      } else {
        throw new GenerationException("Unable to find generator based on meta " + meta.toString());
      }
    });
  }

  private void processFieldInjectionPoint(FieldPoint field, BeanDefinition definition) {
    BeanDefinition beanDefinition = null;
    if (field.isNamed()) {
      beanDefinition = iocContext.getBeans().get(field.getType());
    } else if (iocContext.getQualifiers().containsKey(field.getType())) {
      beanDefinition =
          getTypeQualifierValue(field.getField(), iocContext.getQualifiers().get(field.getType()));
    } else if (field.getType().getKind().isInterface()) {
      TypeMirror beanType = field.getType().asType();
      Types types = iocContext.getGenerationContext().getTypes();
      Optional<TypeElement> result = iocContext.getBeans().keySet().stream()
          .filter(bean -> types.isSubtype(bean.asType(), beanType))
          .filter(elm -> elm.getKind().equals(ElementKind.CLASS)).findFirst();
      if (result.isPresent()) {
        beanDefinition = iocContext.getBeans().get(result.get());
        if (!iocContext.getQualifiers().containsKey(field.getType())) {
          iocContext.getQualifiers().put(field.getType(), new HashMap<>());
        }
        iocContext.getQualifiers().get(field.getType()).put(Default.class.getCanonicalName(),
            beanDefinition);
      }
    }

    if (beanDefinition != null) {
      definition.getDependsOn().add(beanDefinition);
    }
    if (!iocContext.getBeans().containsKey(field.getType())) {
      dependentBeans.add(field.getType());
    }
  }

  private BeanDefinition getTypeQualifierValue(VariableElement element,
      Map<String, BeanDefinition> qualifiers) {

    for (AnnotationMirror annotation : iocContext.getGenerationContext().getElements()
        .getAllAnnotationMirrors(element)) {
      if (qualifiers.containsKey(annotation.toString())) {
        return qualifiers.get(annotation.toString());
      }
    }
    return qualifiers.get(Default.class.getCanonicalName());
  }
}
