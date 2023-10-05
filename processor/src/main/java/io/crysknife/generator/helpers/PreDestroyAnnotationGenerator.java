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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.annotation.PreDestroy;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

import com.google.auto.common.MoreTypes;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.util.TypeUtils;
import io.crysknife.validation.PreDestroyValidator;


public class PreDestroyAnnotationGenerator {

  private final IOCContext iocContext;
  private final PreDestroyValidator validator;
  private final MethodCallGenerator methodCallGenerator;

  public PreDestroyAnnotationGenerator(IOCContext iocContext) {
    this.iocContext = iocContext;
    this.validator = new PreDestroyValidator(iocContext);
    this.methodCallGenerator = new MethodCallGenerator(iocContext);
  }

  public Optional<String> generate(BeanDefinition beanDefinition) {
    List<ExecutableElement> preDestroy = TypeUtils
        .getAllMethodsIn(iocContext.getGenerationContext().getElements(),
            MoreTypes.asTypeElement(beanDefinition.getType()))
        .stream().filter(elm -> elm.getAnnotation(PreDestroy.class) != null)
        .collect(Collectors.toList());
    if (!preDestroy.isEmpty()) {
      if (preDestroy.size() > 1) {
        throw new GenerationException(
            String.format("Bean %s must have only one method annotated with @PreDestroy",
                beanDefinition.getType()));
      }
      return generatePreDestroyInstanceCall(beanDefinition.getType(), preDestroy.get(0));
    }
    return Optional.empty();
  }

  private Optional<String> generatePreDestroyInstanceCall(TypeMirror parent,
      ExecutableElement preDestroy) {
    try {
      validator.validate(preDestroy);
      String call = methodCallGenerator.generate(parent, preDestroy);
      return Optional.of(call);
    } catch (UnableToCompleteException e) {
      throw new GenerationException(e);
    }
  }
}
