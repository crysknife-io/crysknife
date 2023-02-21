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
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class InstanceGetFieldDecorators implements Step<BeanDefinition> {

  @Override
  public void execute(IOCContext iocContext, ClassBuilder classBuilder,
      BeanDefinition beanDefinition) {

    Set<InjectableVariableDefinition> points = new HashSet<>(beanDefinition.getFields());
    points.addAll(beanDefinition.getConstructorParams());

    points.forEach(point -> point.getDecorators().stream()
        .sorted(
            Comparator.comparingInt(o -> o.getClass().getAnnotation(Generator.class).priority()))
        .forEach(generator -> generator.generate(classBuilder, point)));
  }

}
