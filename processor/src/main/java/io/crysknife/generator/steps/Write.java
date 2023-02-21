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

package io.crysknife.generator.steps;

import io.crysknife.definition.BeanDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.util.Utils;

import javax.annotation.processing.FilerException;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;

public class Write implements Step<BeanDefinition> {

  @Override
  public void execute(IOCContext iocContext, ClassBuilder classBuilder,
      BeanDefinition beanDefinition) {

    try {
      String fileName = Utils.getQualifiedFactoryName(beanDefinition.getType());
      String source = classBuilder.toSourceCode();
      build(iocContext, fileName, source);
    } catch (javax.annotation.processing.FilerException e1) {
      // just ignore it
    } catch (IOException e1) {
      throw new GenerationException(e1);
    }
  }

  protected void build(IOCContext iocContext, String fileName, String source) throws IOException {
    JavaFileObject builderFile = iocContext.getGenerationContext().getProcessingEnvironment()
        .getFiler().createSourceFile(fileName);

    try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
      out.append(source);
    } catch (FilerException e) {
      throw new GenerationException(e);
    }
  }

}
