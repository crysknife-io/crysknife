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

package io.crysknife.processor;

import javax.lang.model.element.Element;

import com.google.auto.common.MoreElements;
import io.crysknife.generator.IOCGenerator;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/7/19
 */
public class ClassDecoratorTypeProcessor extends TypeProcessor {

  protected ClassDecoratorTypeProcessor(IOCGenerator generator) {
    super(generator);
  }

  @Override
  public void process(IOCContext context, Element element) {
    if (MoreElements.isType(element)) {
      BeanDefinition beanDefinition =
          context.getBeanDefinitionOrCreateAndReturn(MoreElements.asType(element));
      context.getBeans().get(MoreElements.asType(element)).addDecorator(generator, beanDefinition);
    }
  }
}
