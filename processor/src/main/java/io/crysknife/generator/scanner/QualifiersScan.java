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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.inject.Default;
import javax.enterprise.inject.Typed;
import javax.inject.Named;
import javax.inject.Qualifier;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypesException;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/12/19
 */
public class QualifiersScan {

  private final IOCContext iocContext;

  private final Set<VariableElement> points = new HashSet<>();

  public QualifiersScan(IOCContext iocContext) {
    this.iocContext = iocContext;
  }

  public void process() {
    processQualifierAnnotation();
    processNamedAnnotation();
    processDefaultAnnotation();
    processTypedAnnotation();
  }

  private void processNamedAnnotation() {
    iocContext.getTypeElementsByAnnotation(Named.class.getCanonicalName())
        .forEach(named -> named.getInterfaces().forEach(parent -> {
          BeanDefinition iface =
              iocContext.getBeanDefinitionOrCreateAndReturn(MoreTypes.asTypeElement(parent));
          if (!iocContext.getQualifiers().containsKey(iface.getType())) {
            iocContext.getQualifiers().put(iface.getType(), new HashMap<>());
          }

          String namedValue = (named.getAnnotation(Named.class).value().length() == 0)
              ? named.getQualifiedName().toString()
              : named.getAnnotation(Named.class).value();

          iocContext.getQualifiers().get(iface.getType()).put(namedValue,
              iocContext.getBeanDefinitionOrCreateAndReturn(named));
        }));
  }

  private void processQualifierAnnotation() {
    iocContext.getTypeElementsByAnnotation(Qualifier.class.getCanonicalName()).stream()
        .filter(elm -> elm.getKind().equals(ElementKind.ANNOTATION_TYPE)).forEach(qualified -> {
          iocContext.getFieldsByAnnotation(qualified.getQualifiedName().toString())
              .forEach(candidate -> processAnnotation(candidate, qualified));
          iocContext.getTypeElementsByAnnotation(qualified.getQualifiedName().toString())
              .forEach(candidate -> processAnnotation(candidate, qualified));
        });
  }

  private void processDefaultAnnotation() {
    Element qualified = iocContext.getGenerationContext().getProcessingEnvironment()
        .getElementUtils().getTypeElement(Default.class.getCanonicalName());

    iocContext.getGenerationContext().getRoundEnvironment().getElementsAnnotatedWith(Default.class)
        .forEach(annotated -> processAnnotation(annotated, qualified));
  }

  private void processAnnotation(Element element, Element qualified) {
    if (element.getKind().isField()) {
      points.add(MoreElements.asVariable(element));
    } else if (element.getKind().isClass()) {
      processQualifier(MoreElements.asType(element), qualified);
    }
  }

  private void processQualifier(TypeElement qualifier, Element annotation) {
    qualifier.getInterfaces().forEach(parent -> {
      BeanDefinition iface =
          iocContext.getBeanDefinitionOrCreateAndReturn(MoreTypes.asTypeElement(parent));
      if (!iocContext.getQualifiers().containsKey(iface.getType())) {
        iocContext.getQualifiers().put(iface.getType(), new HashMap<>());
      }
      iocContext.getQualifiers().get(iface.getType()).put(annotation.toString(),
          iocContext.getBeanDefinitionOrCreateAndReturn(qualifier));
    });
  }

  private void processTypedAnnotation() {
    iocContext.getTypeElementsByAnnotation(Typed.class.getCanonicalName()).forEach(clazz -> {
      Set<TypeElement> types = getTypedValueAsArray(clazz);
      for (TypeElement type : types) {
        BeanDefinition candidate = iocContext.getBeanDefinitionOrCreateAndReturn(type);
        BeanDefinition defaultImpl = iocContext.getBeanDefinitionOrCreateAndReturn(clazz);
        candidate.setDefaultImplementation(defaultImpl);
        if (!iocContext.getQualifiers().containsKey(type)) {
          iocContext.getQualifiers().put(type, new HashMap<>());
        }
        iocContext.getQualifiers().get(type).put(Default.class.getCanonicalName(), defaultImpl);
      }
    });
  }

  private Set<TypeElement> getTypedValueAsArray(TypeElement type) {
    try {
      type.getAnnotation(Typed.class).value();
    } catch (MirroredTypesException e) {
      return e.getTypeMirrors().stream().map(val -> MoreTypes.asTypeElement(val))
          .collect(Collectors.toSet());
    }
    return Collections.emptySet();
  }
}
