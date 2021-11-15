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
import io.crysknife.generator.context.IOCContext;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 10/13/21
 */
public abstract class Check<T> {

  protected IOCContext context;

  Check<T> setContext(IOCContext context) {
    this.context = context;
    return this;
  }

  public abstract void check(T element) throws UnableToCompleteException;

  public void log(ExecutableElement element, String msg, String... args)
      throws UnableToCompleteException {
    StringBuffer sb = new StringBuffer();
    sb.append("Error at ").append(element.getEnclosingElement()).append(".")
        .append(element.getSimpleName()).append(" : ").append(msg);
    context.getGenerationContext().getProcessingEnvironment().getMessager()
        .printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), element);
    throw new UnableToCompleteException(sb.toString());
  }

  public void log(TypeElement element, String msg, String... args)
      throws UnableToCompleteException {
    StringBuffer sb = new StringBuffer();
    sb.append("Error at ").append(element.toString()).append(" : ").append(msg);
    context.getGenerationContext().getProcessingEnvironment().getMessager()
        .printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), element);
    throw new UnableToCompleteException(sb.toString());
  }

  public void log(VariableElement element, String msg, String... args)
      throws UnableToCompleteException {
    StringBuffer sb = new StringBuffer();
    sb.append("Error at ").append(element.getEnclosingElement()).append(".").append(element)
        .append(" : ").append(msg);
    context.getGenerationContext().getProcessingEnvironment().getMessager()
        .printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), element);
    throw new UnableToCompleteException(sb.toString());
  }

}
