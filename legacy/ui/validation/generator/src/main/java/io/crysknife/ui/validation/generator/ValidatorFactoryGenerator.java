/*
 * Copyright (C) 2013 Red Hat, Inc. and/or its affiliates.
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

import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;

import java.io.PrintWriter;

import javax.validation.ValidatorFactory;

/**
 * Generates an implementation of {@link ValidatorFactory} which provides a generated implementation
 * of a GWT {@link javax.validation.Validator}.
 *
 * @author Johannes Barop <jb@barop.de>
 * @author Christian Sadilek <csadilek@redhat.com>
 */
public class ValidatorFactoryGenerator {

  private final String packageName = "io.crysknife.ui.validation.client";
  private final String className = ValidatorFactory.class.getSimpleName() + "Impl";

  public String generate(TreeLogger logger, IOCContext context) {

    /*    final PrintWriter printWriter = context.tryCreate(logger, packageName, className);
    
    long start = System.currentTimeMillis();
    if (printWriter != null) {
      logger.log(TreeLogger.Type.INFO, "Generating validator factory...");
    
      ClassStructureBuilder<?> validatorInterface = new GwtValidatorGenerator().generate(context);
      ClassStructureBuilder<?> builder =
          ClassBuilder.define(packageName + "." + className, AbstractGwtValidatorFactory.class)
              .publicScope().body();
    
      BlockBuilder<?> methodBuilder =
          builder.publicMethod(AbstractGwtValidator.class, "createValidator");
      if (validatorInterface == null) {
        methodBuilder.append(Stmt
            .nestedCall(Stmt.newObject(BeanValidator.class, Stmt.loadLiteral(null))).returnValue())
            .finish();
      } else {
        methodBuilder.append(Stmt
            .nestedCall(Stmt.newObject(BeanValidator.class, Cast.to(AbstractGwtValidator.class,
                Stmt.invokeStatic(GWT.class, "create", validatorInterface.getClassDefinition()))))
            .returnValue()).finish();
        builder.getClassDefinition()
            .addInnerClass(new InnerClass(validatorInterface.getClassDefinition()));
      }
    
      String gen = builder.toJavaString();
      printWriter.append(gen);
    
      RebindUtils.writeStringToJavaSourceFileInErraiCacheDir(packageName, className, gen);
    
      log.info("Generated validator factory in " + (System.currentTimeMillis() - start) + "ms");
      context.commit(logger, printWriter);*/
    return packageName + "." + className;
  }
}
