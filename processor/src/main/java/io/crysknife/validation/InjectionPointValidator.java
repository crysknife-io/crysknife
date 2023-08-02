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

import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.util.TypeUtils;

import jakarta.inject.Named;
import javax.lang.model.element.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/3/21
 */
public class InjectionPointValidator extends Validator<VariableElement> {

  private Validator fieldValidator;

  private Set<Check> checks = new HashSet<Check>() {
    {
      add(new Check<VariableElement>() {
        @Override
        public void check(VariableElement variableElement) throws UnableToCompleteException {
          if (variableElement.getModifiers().contains(Modifier.ABSTRACT)) {
            log(variableElement, "Field injection point must not be abstract");
          }
        }
      });

      add(new Check<VariableElement>() {
        @Override
        public void check(VariableElement variableElement) throws UnableToCompleteException {
          List<AnnotationMirror> qualifiers =
              TypeUtils.getAllElementQualifierAnnotations(context, variableElement);
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
    super(context);
    if (parent.getKind().equals(ElementKind.CLASS)) {
      fieldValidator = new InjectionPointFieldValidator(context, this);
    } else if (parent.getKind().equals(ElementKind.CONSTRUCTOR)) {
      fieldValidator = new InjectionPointConstructorValidator(context, this);
    }
  }

  public void validate(VariableElement variableElement) throws UnableToCompleteException {
    fieldValidator.validate(variableElement);
  }

  private static class InjectionPointFieldValidator extends Validator<VariableElement> {

    private InjectionPointFieldValidator(IOCContext context, InjectionPointValidator validator) {
      super(context);
      validator.checks.forEach(check -> {
        addCheck(check);
      });
      addCheck(new Check<VariableElement>() {
        @Override
        public void check(VariableElement variableElement) throws UnableToCompleteException {
          if (variableElement.getModifiers().contains(Modifier.FINAL)) {
            log(variableElement, "Field injection point must not be final");
          }
        }
      });

      addCheck(new Check<VariableElement>() {
        @Override
        public void check(VariableElement variableElement) throws UnableToCompleteException {
          if (variableElement.getModifiers().contains(Modifier.FINAL)) {
            log(variableElement, "Field injection point must not be final");
          }
        }
      });
    }

  }

  private static class InjectionPointConstructorValidator extends Validator<VariableElement> {

    private InjectionPointConstructorValidator(IOCContext context,
        InjectionPointValidator validator) {
      super(context);
      validator.checks.forEach(check -> {
        addCheck(check);
      });
    }
  }
}
