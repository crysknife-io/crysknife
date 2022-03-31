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

package io.crysknife.ui.validation.generator;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.*;
import io.crysknife.annotation.Generator;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.BeanIOCGenerator;
import io.crysknife.generator.WiringElementType;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;

import javax.inject.Inject;
import javax.validation.Validator;
import java.io.IOException;

@Generator
public class ValidationGenerator extends BeanIOCGenerator<BeanDefinition> {

  public ValidationGenerator(TreeLogger treeLogger, IOCContext iocContext) {
    super(treeLogger, iocContext);
  }

  @Override
  public void register() {

    iocContext.register(Inject.class, Validator.class, WiringElementType.BEAN, this); // PARAMETER
  }

  @Override
  public void generate(ClassBuilder clazz, BeanDefinition beanDefinition) {
    CompilationUnit gwtValidatorGenerator =
        new GwtValidatorGenerator().generate(logger, iocContext);
    if (gwtValidatorGenerator != null) {
      try {
        build(GwtValidatorGenerator.FILE_NAME, gwtValidatorGenerator.toString());
      } catch (javax.annotation.processing.FilerException e) {
        // just ignore it
      } catch (IOException e) {
        throw new GenerationException(e);
      }
    }
  }

  @Override
  public Expression generateBeanLookupCall(ClassBuilder classBuilder,
      InjectableVariableDefinition fieldPoint) {
    return generationUtils.wrapCallInstanceImpl(classBuilder,
        new ObjectCreationExpr().setType("io.crysknife.ui.validation.client.GwtValidatorImpl"));
  }
}
