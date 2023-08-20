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

package io.crysknife.task;

import io.crysknife.exception.GenerationException;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.helpers.FreemarkerTemplateGenerator;
import io.crysknife.logger.TreeLogger;

import javax.annotation.processing.FilerException;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;

public class BeanManagerGeneratorStepTask implements Task {

  private final FreemarkerTemplateGenerator freemarkerTemplateGenerator =
      new FreemarkerTemplateGenerator("beanmanagergeneratorsteptask.ftlh");

  private final IOCContext iocContext;

  private final TreeLogger logger;

  public BeanManagerGeneratorStepTask(IOCContext iocContext, TreeLogger logger) {
    this.iocContext = iocContext;
    this.logger = logger;
  }

  @Override
  public void execute() throws UnableToCompleteException {
    String source = freemarkerTemplateGenerator.toSource(new HashMap<>());
    try {
      String fileName = "io.crysknife.client.internal.BeanManagerGeneratorStepTaskMarker";
      write(iocContext, fileName, source);
    } catch (IOException e) {
      throw new GenerationException(e);
    }
  }

  private void write(IOCContext iocContext, String fileName, String source) throws IOException {
    try {
      JavaFileObject sourceFile = iocContext.getGenerationContext().getProcessingEnvironment()
          .getFiler().createSourceFile(fileName);
      try (Writer writer = sourceFile.openWriter()) {
        writer.write(source);
      }
    } catch (FilerException e) {
      throw new GenerationException(e);
    }
  }
}
