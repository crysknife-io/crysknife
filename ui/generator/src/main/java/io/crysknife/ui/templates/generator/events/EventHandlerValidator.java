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

package io.crysknife.ui.templates.generator.events;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.ui.templates.client.annotation.EventHandler;
import io.crysknife.ui.templates.client.annotation.ForEvent;
import io.crysknife.ui.templates.client.annotation.SinkNative;
import io.crysknife.validation.Check;
import io.crysknife.validation.Validator;


public class EventHandlerValidator extends Validator<ExecutableElement> {

    public EventHandlerValidator(IOCContext context) {
        super(context);

        addCheck(new Check<ExecutableElement>() {
            @Override
            public void check(ExecutableElement method) throws UnableToCompleteException {
                if (method.getModifiers().contains(Modifier.STATIC)) {
                    log(method, "@%s method must not be static", EventHandler.class.getSimpleName());
                }
            }
        });
        addCheck(new Check<ExecutableElement>() {
            @Override
            public void check(ExecutableElement method) throws UnableToCompleteException {
                if (!method.getReturnType().getKind().equals(TypeKind.VOID)) {
                    log(method, "@%s method must have no return type", EventHandler.class.getSimpleName());
                }
            }
        });
        addCheck(new Check<ExecutableElement>() {
            @Override
            public void check(ExecutableElement method) throws UnableToCompleteException {
                if (method.getParameters().isEmpty() || method.getParameters().size() > 1) {
                    log(method, "@%s method must have one parameter", EventHandler.class.getSimpleName());
                }
            }
        });

        addCheck(new Check<ExecutableElement>() {
            @Override
            public void check(ExecutableElement method) throws UnableToCompleteException {
                if (method.getParameters().isEmpty() || method.getParameters().size() > 1) {
                    log(method, "@%s method must have one parameter", EventHandler.class.getSimpleName());
                }
            }
        });

        addCheck(new Check<ExecutableElement>() {
            @Override
            public void check(ExecutableElement method) throws UnableToCompleteException {
                if (MoreTypes.asTypeElement(method.getParameters().get(0).asType()).getModifiers()
                        .contains(Modifier.ABSTRACT)) {
                    log(method, "@%s method must have one non abstract parameter",
                            EventHandler.class.getSimpleName());
                }
            }
        });

        addCheck(new Check<ExecutableElement>() {
            @Override
            public void check(ExecutableElement method) throws UnableToCompleteException {
                VariableElement parameter = method.getParameters().get(0);

                if (MoreElements.isAnnotationPresent(method, SinkNative.class)
                        && MoreElements.isAnnotationPresent(parameter, ForEvent.class)) {
                    log(method, "@%s method must annotated with @%s or has parameter annotated with @%s",
                            SinkNative.class.getCanonicalName(), ForEvent.class.getSimpleName());
                }

                DeclaredType declaredType = MoreTypes.asDeclared(parameter.asType());
                if (!(MoreElements.isAnnotationPresent(method, SinkNative.class)
                        && MoreElements.isAnnotationPresent(parameter, ForEvent.class))) {
                    // TODO add better validation
                }
            }
        });
    }
}
