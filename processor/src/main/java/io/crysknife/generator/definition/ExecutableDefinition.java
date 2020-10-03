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

package io.crysknife.generator.definition;

import java.util.Objects;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/3/19
 */
public class ExecutableDefinition extends Definition {

  private final ExecutableElement executableElement;

  private final TypeElement enclosingElement;

  private ExecutableDefinition(ExecutableElement executableElement, TypeElement enclosingElement) {
    this.executableElement = executableElement;
    this.enclosingElement = enclosingElement;
  }

  public static ExecutableDefinition of(ExecutableElement executableElement,
      TypeElement enclosingElement) {
    return new ExecutableDefinition(executableElement, enclosingElement);
  }

  @Override
  public String toString() {
    return "ExecutableDefinition{" + "executableElement=" + executableElement + " generator = [ "
        + (generator.isPresent() ? generator.get().getClass().getCanonicalName() : "") + " ]" + '}';
  }

  public ExecutableElement getExecutableElement() {
    return executableElement;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ExecutableDefinition that = (ExecutableDefinition) o;
    return Objects.equals(executableElement, that.executableElement)
        && Objects.equals(enclosingElement, that.enclosingElement);
  }

  @Override
  public int hashCode() {
    return Objects.hash(executableElement, enclosingElement);
  }
}
