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

package io.crysknife.generator;

import io.crysknife.exception.GenerationException;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.GenerationContext;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.logger.TreeLogger;
import io.crysknife.util.Utils;

import javax.annotation.processing.FilerException;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/4/19
 */
public abstract class BeanIOCGenerator<T extends BeanDefinition> extends IOCGenerator<T> {

  public BeanIOCGenerator(TreeLogger treeLogger, IOCContext iocContext) {
    super(treeLogger, iocContext);
  }

  public void write(ClassBuilder clazz, T beanDefinition, GenerationContext context) {
    try {
      String fileName = Utils.getQualifiedFactoryName(beanDefinition.getType());
      String source = clazz.toSourceCode();
      build(fileName, source, context);
    } catch (javax.annotation.processing.FilerException e1) {
      context.getProcessingEnvironment().getMessager().printMessage(Diagnostic.Kind.NOTE,
          e1.getMessage());
    } catch (IOException e1) {
      throw new GenerationException(e1);
    }
  }

  protected void build(String fileName, String source, GenerationContext context)
      throws IOException {
    JavaFileObject builderFile =
        context.getProcessingEnvironment().getFiler().createSourceFile(fileName);

    try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
      out.append(source);
    } catch (FilerException e) {
      throw new GenerationException(e);
    }
  }
}
