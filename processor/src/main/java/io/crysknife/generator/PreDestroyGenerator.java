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

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import io.crysknife.exception.GenerationException;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.util.GenerationUtils;
import io.crysknife.validation.PreDestroyValidator;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

public class PreDestroyGenerator {

  private PreDestroyValidator validator;
  private GenerationUtils utils;

  private TreeLogger treeLogger;

  public PreDestroyGenerator(TreeLogger treeLogger, IOCContext iocContext) {
    this.validator = new PreDestroyValidator(iocContext);
    this.utils = new GenerationUtils(iocContext);
    this.treeLogger = treeLogger;
  }

  public void generate(TypeMirror parent, ClassBuilder classBuilder, ExecutableElement preDestroy) {
    try {
      validator.validate(preDestroy);
    } catch (UnableToCompleteException e) {
      throw new GenerationException(e);
    }
    treeLogger.log(TreeLogger.Type.INFO,
        String.format("generating @PreDestroy at %s.%s", parent.toString(), preDestroy));

    MethodDeclaration onDestroy = classBuilder.addMethod("onDestroy", Modifier.Keyword.PROTECTED);
    onDestroy.addAnnotation(Override.class);
    onDestroy.getBody().get().addAndGetStatement(utils.generateMethodCall(parent, preDestroy));
  }

}
