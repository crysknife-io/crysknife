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

package io.crysknife.processor;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.inject.Inject;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.InjectionParameterDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;


/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/4/21
 */
public class ConstructorInjectionPointProcessor extends InjectionPointProcessor {


    public ConstructorInjectionPointProcessor(IOCContext context, TreeLogger logger) {
        super(context, logger);
    }

    @Override
    public void process(BeanDefinition bean) throws UnableToCompleteException {
        List<ExecutableElement> constructors = context.getGenerationContext().getElements()
                .getAllMembers(MoreTypes.asTypeElement(bean.getType())).stream()
                .filter(field -> field.getKind().equals(ElementKind.CONSTRUCTOR))
                .filter(elm -> elm.getAnnotation(Inject.class) != null).map(MoreElements::asExecutable)
                .collect(Collectors.toList());

        if (constructors.isEmpty()) {
            return;
        }

        if (constructors.size() > 1) {
            throw new GenerationException(
                    bean.getType() + "must contain only one constructor annotated with @Inject");
        }
        ExecutableElement constructor = constructors.iterator().next();
        for (int i = 0; i < constructor.getParameters().size(); i++) {
            process(bean, constructor.getParameters().get(i));
        }
    }

    @Override
    protected void process(BeanDefinition bean, VariableElement field)
            throws UnableToCompleteException {
        bean.getConstructorParams().add(new InjectionParameterDefinition(bean, field));
    }
}
