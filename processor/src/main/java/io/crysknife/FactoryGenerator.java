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
import java.util.Set;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 2/20/19
 */
public class FactoryGenerator {

  private final IOCContext iocContext;

  FactoryGenerator(IOCContext iocContext) {
    this.iocContext = iocContext;
  }

  void generate() {
    Set<Map.Entry<TypeElement, BeanDefinition>> beans =
        new HashSet<>(iocContext.getBeans().entrySet());

    for (Map.Entry<TypeElement, BeanDefinition> entry : beans) {
      new ClassBuilder(entry.getValue()).build();
    }
  }
}
