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

package io.crysknife.task;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.auto.common.MoreTypes;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.api.ClassMetaInfo;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.generator.context.oracle.BeanOracle;
import io.crysknife.logger.TreeLogger;

import static javax.lang.model.element.Modifier.ABSTRACT;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 2/20/19
 */
public class FactoryGeneratorTask implements Task {

  private final IOCContext iocContext;
  private final BeanOracle oracle;

  private final TreeLogger logger;

  public FactoryGeneratorTask(IOCContext iocContext, TreeLogger logger) {
    this.iocContext = iocContext;
    this.logger = logger;
    this.oracle = new BeanOracle(iocContext, logger);
  }

  public void execute() throws UnableToCompleteException {
    Map<String, BeanDefinition> beans =
        iocContext.getOrderedBeans().stream().map(erased -> iocContext.getBean(erased))
            .filter(beanDefinition -> beanDefinition.hasFactory())
            .filter(beanDefinition -> beanDefinition.getIocGenerator().isPresent())
            .collect(Collectors.toMap(BeanDefinition::getQualifiedName, Function.identity(),
                (existing, replacement) -> existing));

    beans.values().stream().filter(bean -> !bean.isFactoryGenerationFinished())
        .forEach(this::generate);
  }

  public void generate(BeanDefinition beanDefinition) {
    beanDefinition.getIocGenerator().ifPresent(iocGenerator -> {
      iocGenerator.generate(new ClassMetaInfo(), beanDefinition);
      beanDefinition.setFactoryGenerationFinished(true);
    });
  }

  private boolean isSuitableBeanDefinition(BeanDefinition beanDefinition) {
    return MoreTypes.asTypeElement(beanDefinition.getType()).getKind().isClass()
        && !MoreTypes.asTypeElement(beanDefinition.getType()).getModifiers().contains(ABSTRACT);
  }

}
