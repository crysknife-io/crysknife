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

package io.crysknife.nextstep.definition;

import io.crysknife.generator.IOCGenerator;

import javax.lang.model.element.VariableElement;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/4/21
 */
public class InjectionPointDefinition implements Definition {

  private final VariableElement variableElement;
  private final BeanDefinition beanDefinition;
  private final Set<IOCGenerator> generators = new HashSet<>();

  public InjectionPointDefinition(BeanDefinition beanDefinition, VariableElement variableElement) {
    this.variableElement = variableElement;
    this.beanDefinition = beanDefinition;
  }

  public VariableElement getVariableElement() {
    return variableElement;
  }

  public BeanDefinition getBeanDefinition() {
    return beanDefinition;
  }

  public Set<IOCGenerator> getGenerators() {
    return generators;
  }
}
