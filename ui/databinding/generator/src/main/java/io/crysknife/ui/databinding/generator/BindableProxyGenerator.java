/*
 * Copyright © 2025 Treblereel
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
package io.crysknife.ui.databinding.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.generator.api.ClassMetaInfo;
import io.crysknife.generator.api.Generator;
import io.crysknife.generator.api.IOCGenerator;
import io.crysknife.generator.api.WiringElementType;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.helpers.FreemarkerTemplateGenerator;
import io.crysknife.logger.TreeLogger;
import io.crysknife.ui.databinding.annotation.Bindable;
import io.crysknife.ui.databinding.internal.BindableProxyRegistry;

/**
 * Generates {@code *_BindableProxy} subclasses for each {@code @Bindable} model class. The proxy
 * overrides setter methods to intercept property changes and notify the
 * {@link io.crysknife.ui.databinding.internal.BindingAgent}.
 *
 * <p>Also registers each proxy with {@link BindableProxyRegistry} in the bean factory constructor
 * so that {@link io.crysknife.ui.databinding.api.DataBinder#forType(Class)} can find them.
 *
 * @author Dmitrii Tikhomirov
 */
@Generator
public class BindableProxyGenerator extends IOCGenerator<BeanDefinition> {

  private final FreemarkerTemplateGenerator templateGenerator =
      new FreemarkerTemplateGenerator("bindable-proxy.ftlh");

  private final Set<String> processedTypes = new java.util.HashSet<>();

  public BindableProxyGenerator(TreeLogger treeLogger, IOCContext iocContext) {
    super(treeLogger, iocContext);
  }

  @Override
  public void register() {
    iocContext.register(Bindable.class, WiringElementType.CLASS_DECORATOR, this);
  }

  @Override
  public void generate(ClassMetaInfo classMetaInfo, BeanDefinition beanDefinition) {
    TypeElement typeElement = MoreTypes.asTypeElement(beanDefinition.getType());
    String qualifiedName = typeElement.getQualifiedName().toString();

    if (processedTypes.contains(qualifiedName)) {
      return;
    }
    processedTypes.add(qualifiedName);

    validate(typeElement);

    List<PropertyInfo> properties = extractProperties(typeElement);
    if (properties.isEmpty()) {
      return;
    }

    generateProxyClass(typeElement, properties);

    String proxyFqn = qualifiedName + "_BindableProxy";
    classMetaInfo.addImport(BindableProxyRegistry.class);
    classMetaInfo.addImport(proxyFqn);
    classMetaInfo.addToFactoryConstructor(() -> String.format(
        "io.crysknife.ui.databinding.internal.BindableProxyRegistry.register(%s.class, %s::new);",
        qualifiedName, proxyFqn));
  }

  private void validate(TypeElement typeElement) {
    if (typeElement.getModifiers().contains(Modifier.FINAL)) {
      throw new io.crysknife.exception.GenerationException(
          "@Bindable class must not be final: " + typeElement.getQualifiedName());
    }

    for (ExecutableElement method : getAllSetters(typeElement)) {
      if (method.getModifiers().contains(Modifier.FINAL)) {
        throw new io.crysknife.exception.GenerationException(
            "@Bindable class has final setter which cannot be overridden: "
                + typeElement.getQualifiedName() + "." + method.getSimpleName());
      }
      if (method.getModifiers().contains(Modifier.PRIVATE)) {
        throw new io.crysknife.exception.GenerationException(
            "@Bindable class has private setter which cannot be overridden: "
                + typeElement.getQualifiedName() + "." + method.getSimpleName());
      }

      TypeMirror paramType = method.getParameters().get(0).asType();
      if (paramType.getKind() == TypeKind.ARRAY) {
        throw new io.crysknife.exception.GenerationException(
            "@Bindable class has array property which is not supported: "
                + typeElement.getQualifiedName() + "." + method.getSimpleName());
      }
    }
  }

  private void generateProxyClass(TypeElement typeElement, List<PropertyInfo> properties) {
    String packageName = MoreElements.getPackage(typeElement).getQualifiedName().toString();
    String beanName = typeElement.getSimpleName().toString();
    String qualifiedName = typeElement.getQualifiedName().toString();

    Map<String, Object> root = new HashMap<>();
    root.put("package", packageName);
    root.put("bean", beanName);
    root.put("properties", properties);

    String source = templateGenerator.toSource(root);
    writeJavaFile(qualifiedName + "_BindableProxy", source);
  }

  private List<PropertyInfo> extractProperties(TypeElement typeElement) {
    List<PropertyInfo> properties = new ArrayList<>();
    Map<String, ExecutableElement> getters = new HashMap<>();
    Map<String, ExecutableElement> setters = new HashMap<>();

    for (ExecutableElement method : getAllMethods(typeElement)) {
      String name = method.getSimpleName().toString();
      if (isGetter(method)) {
        String propName = extractPropertyName(name, isBoolean(method));
        getters.put(propName, method);
      } else if (isSetter(method)) {
        String propName = extractPropertyNameFromSetter(name);
        setters.put(propName, method);
      }
    }

    for (Map.Entry<String, ExecutableElement> entry : setters.entrySet()) {
      String propName = entry.getKey();
      ExecutableElement setter = entry.getValue();
      ExecutableElement getter = getters.get(propName);
      if (getter != null) {
        TypeMirror propType = setter.getParameters().get(0).asType();
        String typeStr = propType.toString();
        String getterName = getter.getSimpleName().toString();
        String capitalizedName =
            propName.substring(0, 1).toUpperCase() + propName.substring(1);

        boolean isBindable = false;
        boolean isList = false;
        TypeMirror erasedType = types.erasure(propType);
        if (erasedType.getKind() == TypeKind.DECLARED) {
          TypeElement propTypeElement = MoreTypes.asTypeElement(erasedType);
          isBindable = propTypeElement.getAnnotation(Bindable.class) != null;
          isList = propTypeElement.getQualifiedName().toString().equals("java.util.List");
        }

        properties
            .add(new PropertyInfo(propName, typeStr, getterName, capitalizedName,
                isBindable, isList));
      }
    }

    return properties;
  }

  private List<ExecutableElement> getAllMethods(TypeElement typeElement) {
    return javax.lang.model.util.ElementFilter.methodsIn(elements.getAllMembers(typeElement))
        .stream()
        .filter(m -> !m.getModifiers().contains(Modifier.STATIC))
        .filter(m -> !m.getModifiers().contains(Modifier.PRIVATE))
        .filter(m -> !m.getEnclosingElement().toString().equals("java.lang.Object"))
        .collect(Collectors.toList());
  }

  private List<ExecutableElement> getAllSetters(TypeElement typeElement) {
    return getAllMethods(typeElement).stream().filter(this::isSetter).collect(Collectors.toList());
  }

  private boolean isGetter(ExecutableElement method) {
    String name = method.getSimpleName().toString();
    return method.getParameters().isEmpty()
        && method.getReturnType().getKind() != TypeKind.VOID
        && ((name.startsWith("get") && name.length() > 3)
            || (name.startsWith("is") && name.length() > 2 && isBoolean(method)));
  }

  private boolean isSetter(ExecutableElement method) {
    String name = method.getSimpleName().toString();
    return name.startsWith("set") && name.length() > 3 && method.getParameters().size() == 1
        && method.getReturnType().getKind() == TypeKind.VOID;
  }

  private boolean isBoolean(ExecutableElement method) {
    TypeMirror returnType = method.getReturnType();
    return returnType.getKind() == TypeKind.BOOLEAN
        || returnType.toString().equals("java.lang.Boolean");
  }

  private String extractPropertyName(String getterName, boolean isBooleanGetter) {
    String raw;
    if (isBooleanGetter && getterName.startsWith("is")) {
      raw = getterName.substring(2);
    } else {
      raw = getterName.substring(3);
    }
    return raw.substring(0, 1).toLowerCase() + raw.substring(1);
  }

  private String extractPropertyNameFromSetter(String setterName) {
    String raw = setterName.substring(3);
    return raw.substring(0, 1).toLowerCase() + raw.substring(1);
  }

  /**
   * Data class passed to the Freemarker template for each property.
   */
  public static class PropertyInfo {
    private final String name;
    private final String type;
    private final String getterName;
    private final String capitalizedName;
    private final boolean bindable;
    private final boolean list;

    public PropertyInfo(String name, String type, String getterName, String capitalizedName,
        boolean bindable, boolean list) {
      this.name = name;
      this.type = type;
      this.getterName = getterName;
      this.capitalizedName = capitalizedName;
      this.bindable = bindable;
      this.list = list;
    }

    public String getName() {
      return name;
    }

    public String getType() {
      return type;
    }

    public String getGetterName() {
      return getterName;
    }

    public String getCapitalizedName() {
      return capitalizedName;
    }

    public boolean isBindable() {
      return bindable;
    }

    public boolean isList() {
      return list;
    }
  }
}
