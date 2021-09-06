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

package io.crysknife.nextstep.validation;

import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.util.Utils;

import javax.inject.Named;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/3/21
 */
public class InjectionPointValidator {

  private FieldValidator fieldValidator;
  private IOCContext context;

  private Set<Check> checks = new HashSet<Check>() {
    {
      add(new Check() {
        @Override
        public void check(VariableElement variableElement) throws UnableToCompleteException {
          if (variableElement.getModifiers().contains(Modifier.ABSTRACT)) {
            log(variableElement, "Field injection point must not be abstract");
          }
        }
      });

      add(new Check() {
        @Override
        public void check(VariableElement variableElement) throws UnableToCompleteException {
          List<AnnotationMirror> qualifiers =
              Utils.getAllElementQualifierAnnotations(context, variableElement);
          if (qualifiers.size() > 1) {
            log(variableElement,
                "Injection point must be annotated with only one @Qualifier, but there is "
                    + qualifiers.size());
          }
          Named named = variableElement.getAnnotation(Named.class);
          if (named != null && !qualifiers.isEmpty()) {
            log(variableElement,
                "Injection point must be annotated with @Named or @Qualifier, but not both at the same time");
          }
        }
      });
    }
  };


  public InjectionPointValidator(IOCContext context, Element parent) {
    this.context = context;
    if (parent.getKind().equals(ElementKind.CLASS)) {
      fieldValidator = new InjectionPointFieldValidator(this);
    } else if (parent.getKind().equals(ElementKind.CONSTRUCTOR)) {
      fieldValidator = new InjectionPointConstructorValidator(this);
    }
  }

  public void validate(VariableElement variableElement) throws UnableToCompleteException {
    fieldValidator.validate(variableElement);
  }

  private interface FieldValidator {

    void validate(VariableElement variableElement) throws UnableToCompleteException;
  }

  private interface Check {

    void check(VariableElement variableElement) throws UnableToCompleteException;

    default void log(VariableElement variableElement, String msg) throws UnableToCompleteException {
      StringBuffer sb = new StringBuffer();
      sb.append("Error at ").append(variableElement.getEnclosingElement()).append(".")
          .append(variableElement.getSimpleName()).append(" : ").append(msg);
      throw new UnableToCompleteException(sb.toString());
    }
  }

  private static class InjectionPointFieldValidator implements FieldValidator {

    private Set<Check> checks = new HashSet<>();

    private InjectionPointFieldValidator(InjectionPointValidator validator) {
      checks.addAll(validator.checks);

      checks.add(new Check() {
        @Override
        public void check(VariableElement variableElement) throws UnableToCompleteException {
          if (variableElement.getModifiers().contains(Modifier.FINAL)) {
            log(variableElement, "Field injection point must not be final");
          }
        }
      });

      checks.add(new Check() {
        @Override
        public void check(VariableElement variableElement) throws UnableToCompleteException {
          if (variableElement.getModifiers().contains(Modifier.FINAL)) {
            log(variableElement, "Field injection point must not be final");
          }
        }
      });

    }

    @Override
    public void validate(VariableElement variableElement) throws UnableToCompleteException {
      for (Check check : checks) {
        check.check(variableElement);
      }
    }
  }

  private static class InjectionPointConstructorValidator implements FieldValidator {

    private Set<Check> checks = new HashSet<>();

    private InjectionPointConstructorValidator(InjectionPointValidator validator) {
      checks.addAll(validator.checks);
    }

    @Override
    public void validate(VariableElement variableElement) throws UnableToCompleteException {
      for (Check check : checks) {
        check.check(variableElement);
      }
    }
  }
}
