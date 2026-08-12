/*
 * Copyright © 2024 Treblereel
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
package io.crysknife.generator.info;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.processing.FilerException;
import javax.lang.model.element.ExecutableElement;
import javax.tools.JavaFileObject;

import com.google.auto.common.MoreTypes;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.InterceptorInfo;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.helpers.FreemarkerTemplateGenerator;

public class AroundInvokeAspectGenerator {

  private final FreemarkerTemplateGenerator freemarkerTemplateGenerator =
      new FreemarkerTemplateGenerator("jre/aroundinvoke.ftlh");

  private final IOCContext iocContext;

  public AroundInvokeAspectGenerator(IOCContext iocContext) {
    this.iocContext = iocContext;
  }

  public void generate(BeanDefinition beanDefinition) {
    Map<ExecutableElement, List<InterceptorInfo>> interceptedMethods =
        beanDefinition.getInterceptedMethods();
    if (interceptedMethods.isEmpty()) {
      return;
    }

    String clazz =
        MoreTypes.asTypeElement(beanDefinition.getType()).getSimpleName().toString();
    String pkg = beanDefinition.getPackageName();
    List<AroundMethod> methods = new ArrayList<>();

    int index = 0;
    for (Map.Entry<ExecutableElement, List<InterceptorInfo>> entry :
        interceptedMethods.entrySet()) {
      ExecutableElement method = entry.getKey();
      List<InterceptorInfo> chain = entry.getValue();

      String methodName = method.getSimpleName().toString();

      String paramTypes = method.getParameters().stream()
          .map(p -> iocContext.getGenerationContext().getTypes()
              .erasure(p.asType()).toString())
          .collect(Collectors.joining(", "));

      String pointcut = String.format("execution(* %s.%s.%s(%s))",
          pkg, clazz, methodName, paramTypes);

      String chainExpr = chain.stream().map(info -> {
        String interceptorFqdn = MoreTypes.asTypeElement(info.getInterceptorType())
            .getQualifiedName().toString();
        String aroundMethodName =
            info.getAroundInvokeMethod().getSimpleName().toString();
        return String.format(
            "(ctx) -> beanManager.lookupBean(%s.class).getInstance().%s(ctx)",
            interceptorFqdn, aroundMethodName);
      }).collect(Collectors.joining(",\n            "));

      String adviceName = "around_" + methodName + "_" + index;
      methods.add(new AroundMethod(methodName, adviceName, pointcut, chainExpr));
      index++;
    }

    Map<String, Object> root = new HashMap<>();
    root.put("package", pkg);
    root.put("bean", clazz);
    root.put("methods", methods);

    String source = freemarkerTemplateGenerator.toSource(root);
    String fileName = pkg + "." + clazz + "AroundInvokeAspect";
    try {
      write(fileName, source);
    } catch (IOException e) {
      throw new GenerationException(e);
    }
  }

  private void write(String fileName, String source) throws IOException {
    try {
      JavaFileObject sourceFile = iocContext.getGenerationContext()
          .getProcessingEnvironment().getFiler().createSourceFile(fileName);
      try (Writer writer = sourceFile.openWriter()) {
        writer.write(source);
      }
    } catch (FilerException ignored) {
    }
  }

  public static class AroundMethod {

    private final String name;
    private final String adviceName;
    private final String pointcut;
    private final String chain;

    AroundMethod(String name, String adviceName, String pointcut, String chain) {
      this.name = name;
      this.adviceName = adviceName;
      this.pointcut = pointcut;
      this.chain = chain;
    }

    public String getName() {
      return name;
    }

    public String getAdviceName() {
      return adviceName;
    }

    public String getPointcut() {
      return pointcut;
    }

    public String getChain() {
      return chain;
    }
  }
}
