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


package io.crysknife.generator;

import com.google.auto.common.MoreTypes;
import io.crysknife.annotation.CircularDependency;
import io.crysknife.generator.api.Generator;
import io.crysknife.client.internal.proxy.ProxyBeanFactory;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.generator.api.ClassMetaInfo;
import io.crysknife.generator.api.IOCGenerator;
import io.crysknife.generator.api.WiringElementType;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.helpers.FreemarkerTemplateGenerator;
import io.crysknife.logger.TreeLogger;
import io.crysknife.util.TypeUtils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/30/21
 */
@Generator
public class ProxyGenerator extends IOCGenerator<BeanDefinition> {

  private final FreemarkerTemplateGenerator freemarkerTemplateGenerator =
      new FreemarkerTemplateGenerator("proxy.ftlh");

  private static List<String> OBJECT_METHODS = new ArrayList<>() {
    {
      add("wait");
      add("finalize");
    }
  };

  public ProxyGenerator(TreeLogger treeLogger, IOCContext iocContext) {
    super(treeLogger, iocContext);
  }

  @Override
  public void register() {
    iocContext.register(CircularDependency.class, WiringElementType.CLASS_DECORATOR, this);
  }

  public void generate(ClassMetaInfo classMetaInfo, BeanDefinition beanDefinition) {
    Map<String, Object> root = new HashMap<>();
    validate(beanDefinition);

    classMetaInfo.addImport(ProxyBeanFactory.class);
    root.put("package", beanDefinition.getPackageName());
    root.put("bean", beanDefinition.getSimpleClassName());

    String nullConstructorParams = null;
    if (!beanDefinition.getConstructorParams().isEmpty()) {
      nullConstructorParams =
          beanDefinition.getConstructorParams().stream().map(p -> p.getVariableElement().asType())
              .map(f -> String.format("(%s) null", f.toString())).collect(Collectors.joining(","));

    }
    root.put("nullConstructorParams", nullConstructorParams);

    String params = null;
    if (!beanDefinition.getConstructorParams().isEmpty()) {
      params = beanDefinition.getConstructorParams().stream()
          .map(p -> "_constructor_" + p.getVariableElement().getSimpleName().toString())
          .map(f -> "this." + f + ".get().getInstance()").collect(Collectors.joining(","));
    }

    root.put("constructorParams", params);

    Set<Method> methods = TypeUtils
        .getAllMethodsIn(elements, MoreTypes.asTypeElement(beanDefinition.getType())).stream()
        .filter(elm -> !elm.getModifiers().contains(javax.lang.model.element.Modifier.STATIC))
        .filter(elm -> !elm.getModifiers().contains(javax.lang.model.element.Modifier.PRIVATE))
        .filter(elm -> !elm.getModifiers().contains(javax.lang.model.element.Modifier.ABSTRACT))
        .filter(elm -> !elm.getModifiers().contains(javax.lang.model.element.Modifier.NATIVE))
        .filter(elm -> !elm.getModifiers().contains(javax.lang.model.element.Modifier.FINAL))
        .filter(elm -> !OBJECT_METHODS.contains(elm.getSimpleName().toString()))
        .map(this::addMethod).collect(Collectors.toSet());
    root.put("methods", methods);

    String source = freemarkerTemplateGenerator.toSource(root);
    classMetaInfo.addToBody(() -> source);
  }

  private void validate(BeanDefinition beanDefinition) {
    System.out.println("Validating " + beanDefinition.getType());
  }

  private Method addMethod(ExecutableElement elm) {
    Method method = new Method();
    method.name = elm.getSimpleName().toString();
    method.returnType = elm.getReturnType().toString();
    method.isVoid = elm.getReturnType().toString().equals("void");
    method.parameters = elm.getParameters().stream()
        .map(p -> (p.asType().getKind().equals(TypeKind.TYPEVAR) ? "Object" : p.asType().toString())
            + " " + p.getSimpleName().toString())
        .collect(Collectors.joining(","));

    method.parametersNames = elm.getParameters().stream().map(p -> p.getSimpleName().toString())
        .collect(Collectors.joining(","));

    return method;
  }

  public static class Method {
    private String name;
    private boolean isVoid;
    private String returnType;
    private String parameters;

    private String parametersNames;

    public String getName() {
      return name;
    }

    public String getReturnType() {
      return returnType;
    }

    public String getParameters() {
      return parameters;
    }

    public String getParametersNames() {
      return parametersNames;
    }

    public boolean isIsVoid() {
      return isVoid;
    }
  }
}
