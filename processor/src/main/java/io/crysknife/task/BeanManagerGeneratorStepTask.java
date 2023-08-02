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

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import io.crysknife.exception.GenerationException;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.util.StringOutputStream;

import javax.annotation.processing.FilerException;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class BeanManagerGeneratorStepTask implements Task {

  protected final Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);

  private Template temp;

  {
    cfg.setClassForTemplateLoading(this.getClass(), "/templates/");
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    cfg.setLogTemplateExceptions(false);
    cfg.setWrapUncheckedExceptions(true);
    cfg.setFallbackOnNullLoopVariable(false);
  }

  private final IOCContext iocContext;

  private final TreeLogger logger;

  public BeanManagerGeneratorStepTask(IOCContext iocContext, TreeLogger logger) {
    this.iocContext = iocContext;
    this.logger = logger;
  }

  @Override
  public void execute() throws UnableToCompleteException {
    StringOutputStream os = new StringOutputStream();
    try (Writer out = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
      if (temp == null) {
        temp = cfg.getTemplate("beanmanagergeneratorsteptask.ftlh");
      }
      temp.process(new HashMap<>(), out);
      String fileName = "io.crysknife.client.internal.BeanManagerGeneratorStepTaskMarker";
      write(iocContext, fileName, os.toString());
    } catch (TemplateException | IOException e) {
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
