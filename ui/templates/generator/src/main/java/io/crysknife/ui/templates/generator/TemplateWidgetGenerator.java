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

package io.crysknife.ui.templates.generator;

import com.google.auto.common.MoreElements;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.context.IOCContext;
import org.apache.commons.io.IOUtils;

import javax.annotation.processing.FilerException;
import javax.lang.model.element.PackageElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 6/25/20
 */
public class TemplateWidgetGenerator {

  private IOCContext iocContext;
  private StringBuilder clazz;

  TemplateWidgetGenerator(IOCContext iocContext) {
    this.iocContext = iocContext;
  }

  TemplateWidgetGenerator build(boolean isGWT2) {
    if (isGWT2)
      throw new GenerationException("Unsupported operation");

    PackageElement pkg = iocContext.getGenerationContext().getProcessingEnvironment()
        .getElementUtils().getPackageElement(this.getClass().getPackage().getName());

    URL url = iocContext.getGenerationContext().getResourceOracle()
        .findResource(MoreElements.getPackage(pkg), "TemplateWidget.java.bak");

    if (url == null) {
      throw new GenerationException(
          "Cannot find template for io.crysknife.ui.templates.generator.TemplateWidget.java");
    }

    try {
      this.clazz = new StringBuilder(IOUtils.toString(url, Charset.defaultCharset()));
    } catch (IOException e) {
      throw new GenerationException(
          "Cannot find template for io.crysknife.ui.templates.generator.TemplateWidget.java");
    }
    return this;
  }

  void generate() {
    JavaFileObject builderFile;
    try {
      builderFile = iocContext.getGenerationContext().getProcessingEnvironment().getFiler()
          .createSourceFile("org.gwtproject.user.client.ui.TemplateWidget");
      try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
        out.append(clazz);
      }
    } catch (FilerException f) {
      // just ignore it
    } catch (IOException e) {
      throw new GenerationException("Unable to generate TemplateWidget " + e.getMessage());
    }
  }
}
