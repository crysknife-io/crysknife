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

import com.google.auto.common.MoreElements;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.ui.translation.api.annotations.TranslationKey;
import io.crysknife.util.Utils;
import org.apache.commons.io.IOUtils;

import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 11/5/21
 */
public class TranslationServiceImplGenerator {

  private final IOCContext context;
  private final String newLine = System.lineSeparator();
  private final String IMPL = "io.crysknife.ui.translation.api.spi.TranslationServiceImpl";
  private final Set<VariableElement> fields;

  TranslationServiceImplGenerator(IOCContext context) {
    this.context = context;
    fields = context.getFieldsByAnnotation(TranslationKey.class.getCanonicalName());
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

    fields.forEach(key -> {
      TranslationKey annotation = key.getAnnotation(TranslationKey.class);
      String defaultValue = annotation.defaultValue();
      String keyValue = key.getConstantValue().toString();

      sb.append("    registerTranslation(\"" + keyValue + "\", \"" + defaultValue + "\", null);")
          .append(newLine);
    });

    maybeGenerateContantHolders(fields, sb);

    sb.append("  }").append(newLine);
    sb.append("}").append(newLine);


    generate(sb.toString(), IMPL);
  }

  private void maybeGenerateContantHolders(Set<VariableElement> fields, StringBuffer source) {
    Set<TypeElement> holders = fields.stream().map(field -> field.getEnclosingElement())
        .map(element -> MoreElements.asType(element)).filter(holder -> {
          String lookup = holder.toString().replaceAll("\\.", "/") + ".properties";
          URL url = context.getGenerationContext().getResourceOracle().findResource(lookup);
          return url != null;
        }).collect(Collectors.toSet());

    holders.forEach(holder -> {
      generateResource(holder, source);
    });
  }

  private void generateResource(TypeElement holder, StringBuffer parent) {
    String lookup = holder.toString().replaceAll("\\.", "/") + ".properties";
    URL url = context.getGenerationContext().getResourceOracle().findResource(lookup);
    String clazz = Utils.getPackageName(holder).replaceAll("\\.", "_") + "_"
        + holder.getSimpleName() + "property";

    parent.append("    ").append("registerPropertiesBundle(").append(clazz).append(".getContent()")
        .append(", null);").append(newLine);


    StringBuffer source = new StringBuffer();
    source.append("package io.crysknife.ui.translation.api.spi;").append(newLine);
    source.append(newLine);
    source.append(newLine);
    source.append("public class ").append(clazz).append(" {").append(newLine);
    source.append(newLine);
    source.append("  private ").append(clazz).append("() {").append(newLine);
    source.append("  }").append(newLine);

    source.append("  static String getContent() {").append(newLine);

    source.append("    return ").append("\"");
    try {
      source.append(IOUtils.toString(url, Charset.defaultCharset()));
    } catch (IOException e) {
      new GenerationException(e);
    }
    source.append("\";").append(newLine);
    source.append("  }").append(newLine);
    source.append("}").append(newLine);
    generate(source.toString(), "io.crysknife.ui.translation.api.spi." + clazz);
  }

  void generate(String source, String filename) {
    JavaFileObject builderFile;
    try {
      builderFile = context.getGenerationContext().getProcessingEnvironment().getFiler()
          .createSourceFile(filename);
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
