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

import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.nextstep.definition.BeanDefinition;
import io.crysknife.nextstep.validation.InjectionPointValidator;

import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/4/21
 */
public abstract class InjectionPointProcessor {

  protected final IOCContext context;
  protected final TreeLogger logger;

  public InjectionPointProcessor(IOCContext context, TreeLogger logger) {
    this.context = context;
    this.logger = logger;
  }

  public abstract void process(BeanDefinition bean) throws UnableToCompleteException;

  protected void process(BeanDefinition bean, Set<VariableElement> points)
      throws UnableToCompleteException {

    List<UnableToCompleteException> errors = new ArrayList<>();

    for (VariableElement field : points) {
      try {
        new InjectionPointValidator(context, field.getEnclosingElement()).validate(field);
        process(bean, field);
      } catch (UnableToCompleteException e) {
        errors.add(e);
      }
    }

    if (!errors.isEmpty()) {
      TreeLogger logger =
          this.logger.branch(TreeLogger.ERROR, "Unable to process bean: " + bean.getType());
      for (UnableToCompleteException error : errors) {
        // logger.log(TreeLogger.ERROR, error.getMessage());
      }
      throw new UnableToCompleteException();
    }
  }


  protected abstract void process(BeanDefinition bean, VariableElement field)
      throws UnableToCompleteException;


}
