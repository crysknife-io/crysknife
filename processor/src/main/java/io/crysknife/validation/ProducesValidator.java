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

package io.crysknife.validation;

import com.google.auto.common.MoreElements;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.context.IOCContext;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/6/21
 */
public class ProducesValidator {

  private final IOCContext context;

  private Set<Check> checks = new HashSet<Check>() {
    {
      add(new Check() {
        @Override
        public void check(ExecutableElement variableElement) throws UnableToCompleteException {
          if (variableElement.getModifiers().contains(Modifier.ABSTRACT)) {
            log(variableElement, "@Produces method must not be abstract");
          }
        }
      });

      add(new Check() {
        @Override
        public void check(ExecutableElement variableElement) throws UnableToCompleteException {
          if (!variableElement.getModifiers().contains(Modifier.PUBLIC)) {
            log(variableElement, "@Produces method must be public");
          }
        }
      });

      add(new Check() {
        @Override
        public void check(ExecutableElement variableElement) throws UnableToCompleteException {
          if (variableElement.getModifiers().contains(Modifier.STATIC)) {
            log(variableElement, "@Produces method must be non-static");
          }
        }
      });

      add(new Check() {
        @Override
        public void check(ExecutableElement variableElement) throws UnableToCompleteException {
          if (!variableElement.getParameters().isEmpty()) {
            log(variableElement, "@Produces method must have no args");
          }
        }
      });
    }
  };

  public ProducesValidator(IOCContext context) {
    this.context = context;
  }

  public void validate(Element element) throws UnableToCompleteException {
    if (element.getKind().isField()) {
      VariableElement field = MoreElements.asVariable(element);
      StringBuffer sb = new StringBuffer();
      sb.append("Error at ").append(field.getEnclosingElement()).append(".")
          .append(field.getSimpleName()).append(" : ")
          .append(" Only method can be annotated with @Produces");
      throw new UnableToCompleteException(sb.toString());
    }

    for (Check check : checks) {
      check.check(MoreElements.asExecutable(element));
    }
  }

  private interface Check {

    void check(ExecutableElement variableElement) throws UnableToCompleteException;

    default void log(Element variableElement, String msg) throws UnableToCompleteException {
      StringBuffer sb = new StringBuffer();
      sb.append("Error at ").append(variableElement.getEnclosingElement()).append(".")
          .append(variableElement.getSimpleName()).append(" : ").append(msg);
      throw new UnableToCompleteException(sb.toString());
    }
  }
}
