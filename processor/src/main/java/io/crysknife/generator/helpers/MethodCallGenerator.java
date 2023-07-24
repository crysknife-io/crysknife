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
import io.crysknife.generator.context.ExecutionEnv;
import io.crysknife.generator.context.IOCContext;
import org.treblereel.j2cl.processors.utils.J2CLUtils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MethodCallGenerator {

  private final Configuration cfg = new Configuration(Configuration.VERSION_2_3_29);

  private final IOCContext iocContext;

  private final J2CLUtils j2CLUtils;


  Template temp;

  public MethodCallGenerator(IOCContext iocContext) {
    this.iocContext = iocContext;
    this.j2CLUtils = new J2CLUtils(iocContext.getGenerationContext().getProcessingEnvironment());

    cfg.setClassForTemplateLoading(this.getClass(), "/templates/");
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    cfg.setLogTemplateExceptions(false);
    cfg.setWrapUncheckedExceptions(true);
    cfg.setFallbackOnNullLoopVariable(false);
  }

  public String generate(TypeMirror parent, ExecutableElement method) {
    return generate(parent, method, Collections.EMPTY_LIST);
  }

  public String generate(TypeMirror parent, ExecutableElement method, List<String> args) {
    boolean isJre = iocContext.getGenerationContext().getExecutionEnv().equals(ExecutionEnv.JRE);
    boolean isPrivate = method.getModifiers().contains(javax.lang.model.element.Modifier.PRIVATE);
    Map<String, Object> root = new HashMap<>();
    root.put("jre", isJre);
    root.put("private", method.getModifiers().contains(javax.lang.model.element.Modifier.PRIVATE));
    if (isJre) {
      root.put("name", method.getSimpleName().toString());
      if (!args.isEmpty()) {
        root.put("args", args.stream().collect(Collectors.joining(", ")));
      }
    } else {
      if (isPrivate) {
        root.put("name", j2CLUtils.createDeclarationMethodDescriptor(method).getMangledName());
      } else {
        root.put("name", method.getSimpleName().toString());
      }
      if (!args.isEmpty()) {
        if (isPrivate) {
          root.put("args", String.join(",", args));
        } else {
          root.put("args", args.stream().map(c -> String.format("Js.uncheckedCast(%s)", c))
              .collect(Collectors.joining(",")));
        }
      }
    }
    root.put("parent", parent.toString());

    OutputStream os = new OutputStream() {
      private StringBuilder sb = new StringBuilder();

      @Override
      public void write(int b) {
        sb.append((char) b);
      }

      public String toString() {
        return sb.toString();
      }
    };

    try {
      if (temp == null) {
        temp = cfg.getTemplate("methodcall.ftlh");
      }

      Writer out = new OutputStreamWriter(os, "UTF-8");
      temp.process(root, out);
      return os.toString();
    } catch (IOException e) {
      throw new GenerationException(e);
    } catch (TemplateException e) {
      throw new GenerationException(e);
    }
  }
}
