/*
 * Copyright © 2023 Treblereel
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

package io.crysknife.generator.steps;

import io.crysknife.annotation.Generator;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.generator.ProducesGenerator;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;

import java.util.Comparator;

public class InstanceGetMethodDecorators implements Step<BeanDefinition> {

  @Override
  public void execute(IOCContext iocContext, ClassBuilder classBuilder,
      BeanDefinition beanDefinition) {

    beanDefinition.getMethods().stream().forEach(method -> {
      method.getDecorators().stream()
          .sorted(
              Comparator.comparingInt(o -> o.getClass().getAnnotation(Generator.class).priority()))
          .forEach(decorator -> {
            // if (decorator instanceof ProducesGenerator) {
            // TODO Produces
            // } else {
            decorator.generate(classBuilder, method);
            // }
          });
    });
  }
}
