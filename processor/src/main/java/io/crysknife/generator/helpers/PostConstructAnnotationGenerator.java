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

package io.crysknife.generator.helpers;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import javax.lang.model.element.ExecutableElement;

import com.google.auto.common.MoreTypes;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.util.TypeUtils;
import io.crysknife.validation.PostConstructValidator;

public class PostConstructAnnotationGenerator {

  private PostConstructValidator validator;
  private IOCContext iocContext;

  MethodCallGenerator methodCallGenerator;


  public PostConstructAnnotationGenerator(IOCContext iocContext) {
    this.validator = new PostConstructValidator(iocContext);
    this.iocContext = iocContext;

    methodCallGenerator = new MethodCallGenerator(iocContext);
  }

  public void execute(List<String> calls, BeanDefinition beanDefinition) {
    LinkedList<ExecutableElement> postConstructs = TypeUtils
        .getAllMethodsIn(iocContext.getGenerationContext().getElements(),
            MoreTypes.asTypeElement(beanDefinition.getType()))
        .stream().filter(elm -> elm.getAnnotation(PostConstruct.class) != null)
        .collect(Collectors.toCollection(LinkedList::new));

    Iterator<ExecutableElement> elm = postConstructs.descendingIterator();
    while (elm.hasNext()) {
      ExecutableElement executableElement = elm.next();

      try {
        validator.validate(executableElement);
      } catch (UnableToCompleteException e) {
        throw new GenerationException(e);
      }

      String call = methodCallGenerator.generate(beanDefinition.getType(), executableElement);
      calls.add(call);
    }
  }

}
