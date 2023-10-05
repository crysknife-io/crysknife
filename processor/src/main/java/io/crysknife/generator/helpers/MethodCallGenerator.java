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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeMirror;

import io.crysknife.generator.context.ExecutionEnv;
import io.crysknife.generator.context.IOCContext;
import org.treblereel.j2cl.processors.utils.J2CLUtils;


public class MethodCallGenerator {

  private final FreemarkerTemplateGenerator freemarkerTemplateGenerator =
      new FreemarkerTemplateGenerator("methodcall.ftlh");
  private final IOCContext iocContext;

  private final J2CLUtils j2CLUtils;

  public MethodCallGenerator(IOCContext iocContext) {
    this.iocContext = iocContext;
    this.j2CLUtils = new J2CLUtils(iocContext.getGenerationContext().getProcessingEnvironment());
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
    return freemarkerTemplateGenerator.toSource(root);
  }
}
