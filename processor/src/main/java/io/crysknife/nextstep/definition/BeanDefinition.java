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

import com.google.auto.common.MoreTypes;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/3/21
 */
public class BeanDefinition implements Definition {

  private final TypeMirror type;

  private Set<InjectionPointDefinition> fields = new LinkedHashSet<>();
  private Set<InjectionPointDefinition> constructorParams = new LinkedHashSet<>();
  private Set<MethodDefinition> methods = new LinkedHashSet<>();

  private Set<BeanDefinition> subclasses = new LinkedHashSet<>();

  BeanDefinition(TypeMirror type) {
    this.type = type;
  }

  public TypeMirror getType() {
    return type;
  }

  public Set<InjectionPointDefinition> getFields() {
    return fields;
  }

  public Set<InjectionPointDefinition> getConstructorParams() {
    return constructorParams;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    BeanDefinition that = (BeanDefinition) o;
    return MoreTypes.asTypeElement(type).getQualifiedName().toString()
        .equals(MoreTypes.asTypeElement(that.type).getQualifiedName().toString());
  }

  @Override
  public int hashCode() {
    return Objects.hash(MoreTypes.asTypeElement(type).getQualifiedName().toString());
  }

  public Set<BeanDefinition> getSubclasses() {
    return subclasses;
  }

  public void setSubclasses(Set<BeanDefinition> subclasses) {
    this.subclasses = subclasses;
  }

  public Set<MethodDefinition> getMethods() {
    return methods;
  }
}
