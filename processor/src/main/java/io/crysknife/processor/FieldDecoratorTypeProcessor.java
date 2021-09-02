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

package io.crysknife.processor;

import com.google.auto.common.MoreElements;
import io.crysknife.generator.IOCGenerator;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.definition.BeanDefinition;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/1/21
 */
public class FieldDecoratorTypeProcessor extends TypeProcessor {

  protected FieldDecoratorTypeProcessor(IOCGenerator generator) {
    super(generator);
  }

  @Override
  public void process(IOCContext context, Element element) {
    if (element.getKind().isField()) {
      VariableElement field = MoreElements.asVariable(element);
      TypeElement enclosingElement = MoreElements.asType(field.getEnclosingElement());
      BeanDefinition beanDefinition = context.getBeanDefinitionOrCreateAndReturn(enclosingElement);
      beanDefinition.getFieldInjectionPoints().stream()
          .filter(f -> f.getField().getSimpleName().equals(field.getSimpleName())).findFirst()
          .ifPresent(rez -> rez.postActions.add(generator));
    }
  }
}
