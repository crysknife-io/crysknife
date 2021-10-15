/*
 * Copyright © 2021 Treblereel
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

package io.crysknife.validation;

import io.crysknife.exception.UnableToCompleteException;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 10/13/21
 */
public interface Check<T> {

  void check(T element) throws UnableToCompleteException;

  default void log(ExecutableElement variableElement, String msg) throws UnableToCompleteException {
    StringBuffer sb = new StringBuffer();
    sb.append("Error at ").append(variableElement.getEnclosingElement()).append(".")
        .append(variableElement.getSimpleName()).append(" : ").append(msg);
    throw new UnableToCompleteException(sb.toString());
  }
}
