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

package io.crysknife.generator;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.lang.model.type.TypeMirror;

import com.google.auto.common.MoreTypes;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.ProducesBeanDefinition;
import io.crysknife.generator.context.oracle.BeanOracle;
import io.crysknife.task.Task;

import static javax.lang.model.element.Modifier.ABSTRACT;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 2/20/19
 */
public class FactoryGenerator implements Task {

  private final IOCContext iocContext;
  private final BeanOracle oracle;

  public FactoryGenerator(IOCContext iocContext) {
    this.iocContext = iocContext;
    this.oracle = new BeanOracle(iocContext);
  }

  public void execute() throws UnableToCompleteException {
    Set<TypeMirror> processed = new HashSet<>();

    for (TypeMirror bean : iocContext.getOrderedBeans()) {
      TypeMirror erased = iocContext.getGenerationContext().getTypes().erasure(bean);
      if (!processed.contains(bean)) {
        processed.add(bean);
        BeanDefinition beanDefinition = iocContext.getBeans().get(erased);
        if (beanDefinition instanceof ProducesBeanDefinition) {
          continue;
        }

        if (isSuitableBeanDefinition(beanDefinition)) {
          new ClassBuilder(beanDefinition).build();
        } else {
          Optional<BeanDefinition> maybe = oracle.guessDefaultImpl(erased);
          maybe.ifPresent(candidate -> {
            new ClassBuilder(candidate).build();
          });
        }
      }
    }
  }

  private boolean isSuitableBeanDefinition(BeanDefinition beanDefinition) {
    return MoreTypes.asTypeElement(beanDefinition.getType()).getKind().isClass()
        && !MoreTypes.asTypeElement(beanDefinition.getType()).getModifiers().contains(ABSTRACT);
  }

}
