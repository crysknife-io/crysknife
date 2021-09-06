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

package io.crysknife.nextstep;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.nextstep.definition.BeanDefinition;
import io.crysknife.nextstep.definition.InjectionPointDefinition;

import javax.inject.Inject;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.VariableElement;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/4/21
 */
public class ConstructorInjectionPointProcessor extends InjectionPointProcessor {


  public ConstructorInjectionPointProcessor(IOCContext context, TreeLogger logger) {
    super(context, logger);
  }

  @Override
  public void process(BeanDefinition bean) throws UnableToCompleteException {
    Set<VariableElement> fields = context.getGenerationContext().getElements()
        .getAllMembers(MoreTypes.asTypeElement(bean.getType())).stream()
        .filter(field -> field.getKind().equals(ElementKind.CONSTRUCTOR))
        .filter(elm -> elm.getAnnotation(Inject.class) != null)
        .map(elm -> MoreElements.asExecutable(elm)).limit(1).map(e -> e.getParameters())
        .flatMap(Collection::stream).collect(Collectors.toSet());

    process(bean, fields);
  }

  @Override
  protected void process(BeanDefinition bean, VariableElement field)
      throws UnableToCompleteException {
    bean.getConstructorParams().add(new InjectionPointDefinition(bean, field));
  }
}