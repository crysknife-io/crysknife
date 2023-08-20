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

package io.crysknife.generator.info;

import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.google.auto.common.MoreTypes;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.helpers.FreemarkerTemplateGenerator;
import io.crysknife.util.GenerationUtils;
import io.crysknife.util.TypeUtils;
import jakarta.inject.Named;

import javax.annotation.processing.FilerException;
import javax.lang.model.element.AnnotationMirror;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterceptorGenerator {

  private final FreemarkerTemplateGenerator freemarkerTemplateGenerator =
      new FreemarkerTemplateGenerator("jre/aspect.ftlh");

  private final GenerationUtils generationUtils;

  private final IOCContext iocContext;

  public InterceptorGenerator(IOCContext iocContext) {
    this.iocContext = iocContext;
    this.generationUtils = new GenerationUtils(iocContext);
  }

  public void generate(BeanDefinition beanDefinition) {
    String clazz = MoreTypes.asTypeElement(beanDefinition.getType()).getSimpleName().toString();
    String pkg = beanDefinition.getPackageName();
    List<InterceptorField> fields = new ArrayList<>();


    Map<String, Object> root = new HashMap<>();
    root.put("package", beanDefinition.getPackageName());
    root.put("bean", clazz);
    root.put("fields", fields);

    for (InjectableVariableDefinition fieldPoint : beanDefinition.getFields()) {
      String methodName =
          fieldPoint.getVariableElement().getEnclosingElement().toString().replaceAll("\\.", "_")
              + "_" + fieldPoint.getVariableElement().getSimpleName();
      String target = getAnnotationValue(beanDefinition, fieldPoint);
      String call = getCall(fieldPoint);
      String field = fieldPoint.getVariableElement().getSimpleName().toString();
      fields.add(new InterceptorField(target, methodName, field, call));
    }

    String source = freemarkerTemplateGenerator.toSource(root);
    String fileName = pkg + "." + clazz + "Info";
    try {
      write(iocContext, fileName, source);
    } catch (IOException e) {
      throw new GenerationException(e);
    }
  }

  private String getAnnotationValue(BeanDefinition bean, InjectableVariableDefinition fieldPoint) {
    StringBuilder sb = new StringBuilder();
    sb.append("get(").append("*").append(" ");
    sb.append(fieldPoint.getVariableElement().getEnclosingElement());
    sb.append(".");
    sb.append(fieldPoint.getVariableElement().getSimpleName());
    sb.append(")");
    if (!isLocal(bean, fieldPoint)) {
      sb.append(" && target(");
      sb.append(generationUtils.erase(bean.getType()));
      sb.append(")");
    }
    return sb.toString();
  }

  private String getCall(InjectableVariableDefinition fieldPoint) {
    String _beanCall;
    if (fieldPoint.getImplementation().isPresent()
        && fieldPoint.getImplementation().get().getIocGenerator().isPresent()) {
      _beanCall = fieldPoint.getImplementation().get().getIocGenerator().get()
          .generateBeanLookupCall(fieldPoint);
    } else if (fieldPoint.getGenerator().isPresent()) {
      _beanCall = fieldPoint.getGenerator().get().generateBeanLookupCall(fieldPoint);
    } else {
      String name = generationUtils.getActualQualifiedBeanName(fieldPoint);
      MethodCallExpr call = new MethodCallExpr(new NameExpr("beanManager"), "lookupBean")
          .addArgument(new FieldAccessExpr(new NameExpr(name), "class"));
      if (fieldPoint.getImplementation().isEmpty()) {
        List<AnnotationMirror> qualifiers = new ArrayList<>(TypeUtils
            .getAllElementQualifierAnnotations(iocContext, fieldPoint.getVariableElement()));
        for (AnnotationMirror qualifier : qualifiers) {
          call.addArgument(generationUtils.createQualifierExpression(qualifier));
        }
        Named named = fieldPoint.getVariableElement().getAnnotation(Named.class);
        if (named != null) {
          call.addArgument(new MethodCallExpr(
              new NameExpr("io.crysknife.client.internal.QualifierUtil"), "createNamed")
                  .addArgument(new StringLiteralExpr(
                      fieldPoint.getVariableElement().getAnnotation(Named.class).value())));
        }
      }
      _beanCall = call.toString();
    }
    return _beanCall;
  }

  private boolean isLocal(BeanDefinition bean, InjectableVariableDefinition fieldPoint) {
    return generationUtils.isTheSame(fieldPoint.getVariableElement().getEnclosingElement().asType(),
        bean.getType());
  }

  private void write(IOCContext iocContext, String fileName, String source) throws IOException {

    try {
      JavaFileObject sourceFile = iocContext.getGenerationContext().getProcessingEnvironment()
          .getFiler().createSourceFile(fileName);
      try (Writer writer = sourceFile.openWriter()) {
        writer.write(source);
      }
    } catch (FilerException e) {
    }
  }


  public class InterceptorField {


    private String target;

    private String method;
    private String field;
    private String clazz;

    InterceptorField(String target, String method, String field, String clazz) {
      this.target = target;
      this.method = method;
      this.field = field;
      this.clazz = clazz;
    }

    public String getTarget() {
      return target;
    }

    public String getMethod() {
      return method;
    }

    public String getField() {
      return field;
    }

    public String getClazz() {
      return clazz;
    }
  }

}
