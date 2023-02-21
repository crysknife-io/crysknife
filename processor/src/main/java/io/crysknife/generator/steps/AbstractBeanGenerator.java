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

import com.github.javaparser.ast.expr.Expression;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;

public abstract class AbstractBeanGenerator extends IOCGenerator<BeanDefinition> {

  private final BeanLookupCallGenerator beanLookupCallGenerator;
  private final Step[] steps;

  public AbstractBeanGenerator(Step... steps) {
    this(new BeanLookupCallGenerator() {
      @Override
      public Expression generate(ClassBuilder clazz, IOCContext context,
          InjectableVariableDefinition fieldPoint) {
        return BeanLookupCallGenerator.super.generate(clazz, context, fieldPoint);
      }
    }, steps);

  }

  public AbstractBeanGenerator(BeanLookupCallGenerator beanLookupCallGenerator, Step... steps) {
    this.beanLookupCallGenerator = beanLookupCallGenerator;
    this.steps = steps;
  }

  public void generate(IOCContext iocContext, ClassBuilder clazz, BeanDefinition beanDefinition) {
    for (Step step : steps) {
      step.execute(iocContext, clazz, beanDefinition);
    }
  }



}
