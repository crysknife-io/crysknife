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

package io.crysknife.validation;

import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.context.IOCContext;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

public class GeneratorValidator extends Validator<TypeElement> {


  public GeneratorValidator(IOCContext context) {
    super(context);
    addCheck(new Check<>() {
      @Override
      public void check(TypeElement typeElement) throws UnableToCompleteException {
        if (typeElement.getModifiers().contains(Modifier.ABSTRACT)) {
          log(typeElement, "Generator method must not be abstract");
        }
      }
    });

    addCheck(new Check<>() {
      @Override
      public void check(TypeElement typeElement) throws UnableToCompleteException {
        if (typeElement.getModifiers().contains(Modifier.STATIC)) {
          log(typeElement, "Generator must be non-static");
        }
      }
    });
  }
}
