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

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import io.crysknife.exception.GenerationException;
import io.crysknife.util.StringOutputStream;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

public class FreemarkerTemplateGenerator {

  private final Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);

  private final Template template;

  {
    cfg.setClassForTemplateLoading(this.getClass(), "/templates/");
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    cfg.setLogTemplateExceptions(false);
    cfg.setWrapUncheckedExceptions(true);
    cfg.setFallbackOnNullLoopVariable(false);
  }

  public FreemarkerTemplateGenerator(final String templateName) {
    try {
      template = cfg.getTemplate(templateName);
    } catch (Exception e) {
      throw new GenerationException("Unable to load template " + templateName, e);
    }
  }

  public String toSource(Object mapping) {
    StringOutputStream os = new StringOutputStream();
    try (Writer out = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
      template.process(mapping, out);
      return os.toString();
    } catch (TemplateException | IOException e) {
      throw new GenerationException(e);
    }
  }

}
