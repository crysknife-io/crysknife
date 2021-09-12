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

package io.crysknife.definition;

import io.crysknife.generator.IOCGenerator;

import javax.lang.model.element.VariableElement;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/11/21
 */
public class VariableDefinition implements Definition {

  private final VariableElement variableElement;
  private final BeanDefinition parent;
  protected final Set<IOCGenerator<VariableDefinition>> decorators = new HashSet<>();

  public VariableDefinition(BeanDefinition parent, VariableElement variableElement) {
    this.variableElement = variableElement;
    this.parent = parent;
  }

  public VariableElement getVariableElement() {
    return variableElement;
  }

  public BeanDefinition getBeanDefinition() {
    return parent;
  }

  public Set<IOCGenerator<VariableDefinition>> getDecorators() {
    return decorators;
  }
}
