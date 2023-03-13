/*
 * Copyright © 2022 Treblereel
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

package io.crysknife.generator;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.google.auto.common.MoreTypes;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.api.ClassMetaInfo;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.util.GenerationUtils;
import io.crysknife.util.Utils;
import io.crysknife.validation.PreDestroyValidator;

import jakarta.annotation.PreDestroy;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import java.util.List;
import java.util.stream.Collectors;

public class PreDestroyGenerator {

  private PreDestroyValidator validator;
  private GenerationUtils utils;

  private TreeLogger treeLogger;

  private IOCContext iocContext;

  public PreDestroyGenerator(TreeLogger treeLogger, IOCContext iocContext) {
    this.validator = new PreDestroyValidator(iocContext);
    this.utils = new GenerationUtils(iocContext);
    this.treeLogger = treeLogger;
    this.iocContext = iocContext;
  }

  public void generate(BeanDefinition beanDefinition, ClassBuilder classBuilder) {
    classBuilder.getOnDestroyMethod().addAndGetParameter(
        new Parameter().setName("instance").setType(beanDefinition.getQualifiedName()));
    classBuilder.getOnDestroyMethod().addAnnotation(Override.class);

    List<ExecutableElement> preDestroy = Utils
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
      generatePreDestroyInstanceCall(classBuilder.getOnDestroyMethod(), beanDefinition.getType(),
          preDestroy.get(0));
    }
  }

  private void generatePreDestroyInstanceCall(MethodDeclaration onDestroy, TypeMirror parent,
      ExecutableElement preDestroy) {
    try {
      validator.validate(preDestroy);
      onDestroy.getBody().get().addAndGetStatement(utils.generateMethodCall(parent, preDestroy));
    } catch (UnableToCompleteException e) {
      throw new GenerationException(e);
    }
  }

}
