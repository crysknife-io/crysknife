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

package io.crysknife;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import com.google.auto.common.MoreTypes;
import io.crysknife.annotation.Application;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.nextstep.BeanProcessor;
import io.crysknife.nextstep.definition.BeanDefinition;
import io.crysknife.nextstep.oracle.BeanOracle;

import static javax.lang.model.element.Modifier.ABSTRACT;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 2/20/19
 */
public class FactoryGenerator {

  private final IOCContext iocContext;
  private final BeanProcessor beanProcessor;
  private final BeanOracle oracle;


  FactoryGenerator(IOCContext iocContext, BeanProcessor beanProcessor) {
    this.iocContext = iocContext;
    this.beanProcessor = beanProcessor;
    this.oracle = new BeanOracle(iocContext, beanProcessor.getBeans());
  }

  void generate() {
    Set<TypeMirror> processed = new HashSet<>();

    iocContext.getOrderedBeans().stream()
        // .filter(field -> (MoreTypes.asTypeElement(field).getAnnotation(Application.class) ==
        // null))
        .forEach(bean -> {
          TypeMirror erased = iocContext.getGenerationContext().getTypes().erasure(bean);
          System.out.println("TOP " + bean);
          if (!processed.contains(bean)) {
            processed.add(bean);
            io.crysknife.nextstep.definition.BeanDefinition beanDefinition =
                beanProcessor.getBeans().get(erased);
            if (isSuitableBeanDefinition(beanDefinition)) {
              new ClassBuilder(beanDefinition).build();
            } else {
              Optional<BeanDefinition> maybe = oracle.guessDefaultImpl(erased);
              maybe.ifPresent(candidate -> {
                new ClassBuilder(candidate).build();
              });
            }
          }
        });


    /*
     * Set<Map.Entry<TypeElement, BeanDefinition>> beans = new
     * HashSet<>(iocContext.getBeans().entrySet());
     * 
     * for (Map.Entry<TypeElement, BeanDefinition> entry : beans) {
     * System.out.println("FactoryGenerator " + entry.getKey()); new
     * ClassBuilder(entry.getValue()).build(); }
     */
  }

  private boolean isSuitableBeanDefinition(BeanDefinition beanDefinition) {
    return MoreTypes.asTypeElement(beanDefinition.getType()).getKind().isClass()
        && !MoreTypes.asTypeElement(beanDefinition.getType()).getModifiers().contains(ABSTRACT);
  }

}
