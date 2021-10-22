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

import com.google.auto.common.MoreElements;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.context.IOCContext;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 10/13/21
 */
public class PostConstructValidator {

  private final IOCContext context;

  private Set<Check> checks = new HashSet<Check>() {
    {
      add(new Check<ExecutableElement>() {
        @Override
        public void check(ExecutableElement variableElement) throws UnableToCompleteException {
          if (variableElement.getModifiers().contains(Modifier.ABSTRACT)) {
            log(variableElement, "@PostConstruct method must not be abstract");
          }
        }
      });

      add(new Check<ExecutableElement>() {
        @Override
        public void check(ExecutableElement variableElement) throws UnableToCompleteException {
          if (variableElement.getModifiers().contains(Modifier.STATIC)) {
            log(variableElement, "@PostConstruct method must be non-static");
          }
        }
      });

      add(new Check<ExecutableElement>() {
        @Override
        public void check(ExecutableElement variableElement) throws UnableToCompleteException {
          if (!variableElement.getParameters().isEmpty()) {
            log(variableElement, "@PostConstruct method must have no args");
          }
        }
      });
    }
  };


  public PostConstructValidator(IOCContext context) {
    this.context = context;
  }

  public void validate(ExecutableElement method) throws UnableToCompleteException {
    for (Check check : checks) {
      check.check(MoreElements.asExecutable(method));
    }
  }
}
