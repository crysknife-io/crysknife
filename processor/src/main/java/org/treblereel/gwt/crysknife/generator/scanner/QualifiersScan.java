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

package org.treblereel.gwt.crysknife.generator.scanner;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.Default;
import javax.inject.Named;
import javax.inject.Qualifier;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;

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
  }

  private void processNamedAnnotation() {
    iocContext.getTypeElementsByAnnotation(Named.class.getCanonicalName())
        .forEach(named -> named.getInterfaces().forEach(parent -> {
          BeanDefinition iface =
              iocContext.getBeanDefinitionOrCreateAndReturn(MoreTypes.asTypeElement(parent));
          if (!iocContext.getQualifiers().containsKey(iface.getType())) {
            iocContext.getQualifiers().put(iface.getType(), new HashMap<>());
          }
          iocContext.getQualifiers().get(iface.getType()).put(
              named.getAnnotation(Named.class).value(),
              iocContext.getBeanDefinitionOrCreateAndReturn(named));
        }));
  }

  private void processQualifierAnnotation() {
    iocContext.getTypeElementsByAnnotation(Qualifier.class.getCanonicalName())
        .forEach(qualified -> {
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
}
