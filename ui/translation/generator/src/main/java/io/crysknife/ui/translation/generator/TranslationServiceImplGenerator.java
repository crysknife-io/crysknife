/*
 * Copyright © 2021 Treblereel
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

package io.crysknife.ui.translation.generator;

import io.crysknife.exception.GenerationException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.ui.translation.api.annotations.TranslationKey;

import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 11/5/21
 */
public class TranslationServiceImplGenerator {

  private final IOCContext context;
  private final String newLine = System.lineSeparator();
  private final String IMPL = "io.crysknife.ui.translation.api.spi.TranslationServiceImpl";

  TranslationServiceImplGenerator(IOCContext context) {
    this.context = context;
  }

  void generate() {
    StringBuffer sb = new StringBuffer();
    sb.append("package io.crysknife.ui.translation.api.spi;").append(newLine);
    sb.append(newLine);
    sb.append(newLine);
    sb.append("public class TranslationServiceImpl extends TranslationService {").append(newLine);
    sb.append("  public final static TranslationService INSTANCE = new TranslationServiceImpl();")
        .append(newLine);
    sb.append(newLine);
    sb.append("  private TranslationServiceImpl() {").append(newLine);

    context.getFieldsByAnnotation(TranslationKey.class.getCanonicalName()).forEach(key -> {
      TranslationKey annotation = key.getAnnotation(TranslationKey.class);
      String defaultValue = annotation.defaultValue();
      String keyValue = key.getConstantValue().toString();

      sb.append("    registerTranslation(\"" + keyValue + "\", \"" + defaultValue + "\", null);")
          .append(newLine);
    });

    sb.append("  }").append(newLine);
    sb.append("}").append(newLine);


    generate(sb.toString());
  }

  void generate(String source) {
    JavaFileObject builderFile;
    try {
      builderFile = context.getGenerationContext().getProcessingEnvironment().getFiler()
          .createSourceFile(IMPL);
      try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
        out.append(source);
      }
    } catch (javax.annotation.processing.FilerException e) {
      // ignore
      System.out.println(e.getMessage());
    } catch (IOException e) {
      throw new GenerationException("Unable to generate TemplateWidget ", e);
    }
  }

}
