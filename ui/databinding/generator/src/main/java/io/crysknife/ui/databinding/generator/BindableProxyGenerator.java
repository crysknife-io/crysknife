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

package io.crysknife.ui.databinding.generator;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.ui.databinding.client.api.Bindable;
import io.crysknife.util.GenerationUtils;
import io.crysknife.util.Utils;
import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 11/21/19
 */
public class BindableProxyGenerator {

  private final TypeMirror listTypeMirror;
  private final TypeMirror objectTypeMirror;
  private final Set<UnableToCompleteException> errors = new HashSet<>();
  private MethodDeclaration methodDeclaration;
  private TypeElement type;
  private String newLine = System.lineSeparator();
  private Types types;
  private Elements elements;

  private GenerationUtils generationUtils;


  BindableProxyGenerator(IOCContext context, MethodDeclaration methodDeclaration,
      TypeElement type) {
    this.methodDeclaration = methodDeclaration;
    this.type = type;
    this.types = context.getGenerationContext().getTypes();
    this.elements = context.getGenerationContext().getElements();

    listTypeMirror = elements.getTypeElement(List.class.getCanonicalName()).asType();
    objectTypeMirror = elements.getTypeElement(Object.class.getCanonicalName()).asType();
    generationUtils = new GenerationUtils(context);

    errors.clear();
  }

  void generate() throws UnableToCompleteException {
    Set<UnableToCompleteException> errors = new HashSet<>();
    String clazzName = type.getQualifiedName().toString().replaceAll("\\.", "_") + "Proxy";

    StringBuffer sb = new StringBuffer();

    Set<VariableElement> properties = Utils.getAllFieldsIn(elements, type).stream()
        .filter(e -> !e.getModifiers().contains(Modifier.FINAL))
        .filter(e -> !e.getModifiers().contains(Modifier.STATIC))
        .map(e -> MoreElements.asVariable(e)).collect(Collectors.toSet());

    Set<PropertyHolder> fields = new HashSet<>();

    for (VariableElement field : properties) {
      try {
        boolean isFinal = field.getModifiers().contains(Modifier.FINAL);
        String setter = isFinal ? null : getSetter(field);

        PropertyHolder propertyHolder = new PropertyHolder();
        propertyHolder.field = field;
        propertyHolder.name = field.getSimpleName().toString();
        propertyHolder.isFinal = isFinal;
        propertyHolder.getter = getGetter(field);
        propertyHolder.setter = setter;
        propertyHolder.type = field.asType();
        fields.add(propertyHolder);
      } catch (UnableToCompleteException e) {
        errors.add(e);
      }
    }

    Utils.getAllMethodsIn(elements, type).stream()
        .filter(method -> !method.getSimpleName().toString().equals("getClass"))
        .filter(method -> method.getSimpleName().toString().startsWith("get")).forEach(method -> {
          String name = method.getSimpleName().toString();
          if (!fields.stream().filter(f -> f.getter.equals(name)).findFirst().isPresent()) {
            String variableName = name.replaceFirst("get", "");
            variableName =
                variableName.substring(0, 1).toLowerCase(Locale.ROOT) + variableName.substring(1);
            PropertyHolder propertyHolder = new PropertyHolder();
            propertyHolder.name = variableName;
            propertyHolder.isFinal = true;
            propertyHolder.getter = name;
            propertyHolder.type = method.getReturnType();
            propertyHolder.getMethod = method;
            fields.add(propertyHolder);
          }
        });


    sb.append(String.format("class %s extends %s implements BindableProxy { ", clazzName,
        type.getSimpleName()));
    sb.append(newLine);
    sb.append(String.format("private BindableProxyAgent<%s> agent;", type.getSimpleName()));
    sb.append(newLine);

    sb.append(String.format("private %s target;", type.getSimpleName()));
    sb.append(newLine);

    sb.append(String.format("public %s() {", clazzName));
    sb.append(newLine);
    sb.append(String.format("  this(new %s());", type.getSimpleName()));
    sb.append(newLine);
    sb.append("}");
    sb.append(newLine);
    sb.append(String.format("public %s(%s targetVal) {", clazzName, type.getSimpleName()));
    sb.append(newLine);
    sb.append(String.format("  agent = new BindableProxyAgent<%s>(this, targetVal);",
        type.getSimpleName()));
    sb.append(newLine);
    sb.append("  target = targetVal;");
    sb.append(newLine);
    sb.append("  final Map<String, PropertyType> p = agent.propertyTypes;");
    sb.append(newLine);

    generatePropertyType(sb, fields, type);

    sb.append(String.format("  p.put(\"this\", new PropertyType(%s.class, true, false));",
        type.getSimpleName()));

    sb.append(newLine);
    sb.append("  agent.copyValues();");
    sb.append(newLine);

    sb.append(newLine);
    sb.append("}");
    sb.append(newLine);

    sb.append(newLine);
    sb.append("public BindableProxyAgent getBindableProxyAgent() {");
    sb.append(newLine);
    sb.append(" return agent;");
    sb.append(newLine);
    sb.append("}");

    sb.append(newLine);
    sb.append("public void updateWidgets() {");
    sb.append(newLine);
    sb.append(" agent.updateWidgetsAndFireEvents();");
    sb.append(newLine);
    sb.append("}");

    sb.append(newLine);
    sb.append(String.format("public %s unwrap() {", type.getSimpleName()));
    sb.append(newLine);
    sb.append(" return target;");
    sb.append(newLine);
    sb.append("}");

    deepUnwrap(fields, sb);
    equals(clazzName, sb);

    sb.append(newLine);
    sb.append("public int hashCode() {");
    sb.append(newLine);
    sb.append(" return target.hashCode();");
    sb.append(newLine);
    sb.append("}");

    sb.append(newLine);
    sb.append("public String toString() {");
    sb.append(newLine);
    sb.append(" return target.toString();");
    sb.append(newLine);
    sb.append("}");

    sb.append(newLine);
    sb.append("private void changeAndFire(String property, Object value) {");
    sb.append(newLine);
    sb.append(" final Object oldValue = get(property);");
    sb.append(newLine);
    sb.append(" set(property, value);");
    sb.append(newLine);
    sb.append(" agent.updateWidgetsAndFireEvent(false, property, oldValue, value);");
    sb.append(newLine);
    sb.append("}");

    getterAndSetter(type, fields, sb);
    get(fields, sb);
    set(type, fields, sb);
    getBeanProperties(sb);

    sb.append("}");
    sb.append(newLine);

    addBindableProxy(clazzName, sb);

    methodDeclaration.getBody().get().addAndGetStatement(sb.toString());

    if (!errors.isEmpty()) {
      throw new UnableToCompleteException(errors);
    }
  }

  private void addBindableProxy(String clazzName, StringBuffer sb) {
    sb.append(String.format(
        "BindableProxyFactory.addBindableProxy(%s.class, new BindableProxyProvider() {",
        type.getSimpleName()));
    sb.append(newLine);
    sb.append("  public BindableProxy getBindableProxy(Object model) {");
    sb.append(newLine);
    sb.append(String.format("    return new %s((%s) model);", clazzName, type.getSimpleName()));
    sb.append(newLine);
    sb.append("  }");
    sb.append(newLine);

    sb.append("  public BindableProxy getBindableProxy() {");
    sb.append(newLine);
    sb.append(String.format("    return new %s();", clazzName));
    sb.append(newLine);
    sb.append("  }");
    sb.append(newLine);
    sb.append("})");
    sb.append(newLine);
  }

  private void getBeanProperties(StringBuffer sb) {
    sb.append("public Map getBeanProperties() {");
    sb.append(newLine);
    sb.append("  final Map props = new HashMap(agent.propertyTypes);");
    sb.append(newLine);
    sb.append("  props.remove(\"this\");");
    sb.append(newLine);
    sb.append("  return Collections.unmodifiableMap(props);");
    sb.append(newLine);
    sb.append("}");
    sb.append(newLine);
  }

  private void set(TypeElement type, Set<PropertyHolder> fields, StringBuffer sb) {
    sb.append("public void set(String property, Object value) {");
    sb.append(newLine);
    sb.append("  switch (property) {");
    sb.append(newLine);

    fields.forEach(elm -> {
      if (!elm.isFinal) {
        sb.append(String.format("    case \"%s\": target.%s((%s) value);", elm.name, elm.setter,
            getFieldType(getType(type, elm))));
        sb.append(newLine);
        sb.append("    break;");
        sb.append(newLine);
      }
    });

    sb.append(String.format("    case \"this\": target = (%s) value;", type));
    sb.append(newLine);
    sb.append("    agent.target = target;");
    sb.append(newLine);
    sb.append("    break;");
    sb.append(newLine);
    sb.append(String
        .format("    default: throw new NonExistingPropertyException(\"%s\", property);", type));
    sb.append(newLine);
    sb.append("  }");
    sb.append(newLine);
    sb.append("}");
    sb.append(newLine);
  }

  private void get(Set<PropertyHolder> fields, StringBuffer sb) {
    sb.append(newLine);
    sb.append("public Object get(String property) {");
    sb.append(newLine);
    sb.append("  switch (property) {");
    sb.append(newLine);

    fields.forEach(elm -> {
      sb.append(String.format("    case \"%s\": return %s();", elm.name, elm.getter));
      sb.append(newLine);
    });

    sb.append("    case \"this\": return target;");
    sb.append(newLine);
    sb.append(String
        .format("    default: throw new NonExistingPropertyException(\"%s\", property);", type));
    sb.append(newLine);
    sb.append("  }");
    sb.append(newLine);
    sb.append("}");
    sb.append(newLine);
  }

  private void getterAndSetter(TypeElement type, Set<PropertyHolder> fields, StringBuffer sb) {
    fields.forEach(elm -> {
      try {
        getterAndSetter(type, elm, sb);
      } catch (UnableToCompleteException e) {
        errors.add(e);
      }
    });
  }

  private void getterAndSetter(TypeElement type, PropertyHolder elm, StringBuffer sb)
      throws UnableToCompleteException {
    sb.append(newLine);
    sb.append(String.format("public %s %s() {", getType(type, elm), elm.getter));
    sb.append(newLine);
    sb.append(String.format("  return target.%s();", elm.getter));
    sb.append(newLine);
    sb.append("}");
    sb.append(newLine);

    if (!elm.isFinal) {
      sb.append(newLine);
      sb.append(String.format("public void %s(%s value) {", elm.setter, getType(type, elm)));
      sb.append(newLine);
      sb.append(String.format("  changeAndFire(\"%s\", value);", elm.name));
      sb.append(newLine);
      sb.append("}");
      sb.append(newLine);
    }
  }

  private void equals(String clazzName, StringBuffer sb) {
    sb.append(newLine);
    sb.append("public boolean equals(Object obj) {");
    sb.append(newLine);
    sb.append(String.format("  if (obj instanceof %s) {", clazzName));
    sb.append(newLine);
    sb.append(String.format("    obj = ((%s) obj).unwrap();", clazzName));
    sb.append(newLine);
    sb.append("  }");
    sb.append(newLine);
    sb.append(" return target.equals(obj);");
    sb.append(newLine);
    sb.append("}");
  }

  private void deepUnwrap(Set<PropertyHolder> fields, StringBuffer sb) {
    sb.append(newLine);
    sb.append(String.format("public %s deepUnwrap() {", type.getSimpleName()));
    sb.append(newLine);
    sb.append(
        String.format("  final %s clone = new %s();", type.getSimpleName(), type.getSimpleName()));
    sb.append(newLine);
    sb.append(String.format("  final %s t = unwrap();", type.getSimpleName()));
    sb.append(newLine);

    Set<UnableToCompleteException> errors = new HashSet<>();

    fields.forEach(elm -> {
      try {
        deepUnwrap(elm, sb);
      } catch (UnableToCompleteException e) {
        errors.add(e);
      }
    });

    sb.append(" return clone;");
    sb.append(newLine);
    sb.append("}");
    sb.append(newLine);

  }

  private void deepUnwrap(PropertyHolder elm, StringBuffer sb) throws UnableToCompleteException {
    if (elm.isFinal) {
      return;
    }

    if (generationUtils.isAssignableFrom(elm.type, Collection.class)) {

      String colType;
      String colTypeImpl;

      if ((MoreTypes.asTypeElement(elm.type).getKind().isInterface()
          || MoreTypes.asTypeElement(elm.type).getModifiers().contains(Modifier.ABSTRACT))
          && (generationUtils.isAssignableFrom(elm.type, List.class)
              || generationUtils.isAssignableFrom(elm.type, Set.class))) {

        colType =
            generationUtils.isAssignableFrom(elm.type, Set.class) ? Set.class.getCanonicalName()
                : List.class.getCanonicalName();
        colTypeImpl =
            generationUtils.isAssignableFrom(elm.type, Set.class) ? HashSet.class.getCanonicalName()
                : ArrayList.class.getCanonicalName();
      } else {
        if (!MoreTypes.asTypeElement(elm.type).getKind().isInterface()
            && !MoreTypes.asTypeElement(elm.type).getModifiers().contains(Modifier.ABSTRACT)) {
          colType = MoreTypes.asTypeElement(elm.type).getQualifiedName().toString();
          colTypeImpl = MoreTypes.asTypeElement(elm.type).getQualifiedName().toString();
        } else {
          System.out.println("Bean validation on collection " + elm.name + " in class "
              + elm.field.getEnclosingElement()
              + " won't work. Change to either List or Set or use a concrete type instead.");
          return;
        }
      }

      sb.append("  ");
      sb.append(String.format("if (t.%s() != null) {", elm.getter));
      sb.append(newLine);
      sb.append("    ");
      sb.append(String.format("final %s %sClone = new %s();", colType, elm.name, colTypeImpl));
      sb.append(newLine);
      sb.append("    ");
      sb.append(String.format("for (Object %sElem : t.%s()) {", elm.name, elm.getter));
      sb.append(newLine);
      sb.append("      ");
      sb.append(String.format("if (%sElem instanceof BindableProxy) {", elm.name, elm.getter));
      sb.append(newLine);
      sb.append("        ");
      sb.append(
          String.format("%sClone.add(((BindableProxy) %sElem).deepUnwrap());", elm.name, elm.name));
      sb.append(newLine);
      sb.append("      ");
      sb.append("} else {");
      sb.append(newLine);
      sb.append("        ");
      sb.append(String.format("%sClone.add(%sElem);", elm.name, elm.name));
      sb.append(newLine);
      sb.append("      ");
      sb.append("}");
      sb.append(newLine);
      sb.append("    ");
      sb.append("}");
      sb.append(newLine);
      sb.append("    ");
      sb.append(String.format("clone.%s(%sClone);", elm.setter, elm.name));
      sb.append(newLine);
      sb.append("  ");
      sb.append("}");
      sb.append(newLine);


    } else if (!isBindableType(elm.type)) {
      sb.append("  ");
      sb.append(String.format("clone.%s(t.%s());", elm.setter, elm.getter));
      sb.append(newLine);
    } else {
      sb.append(String.format("if (t.%s() instanceof BindableProxy) {", elm.getter,
          type.getSimpleName()));
      sb.append(newLine);
      sb.append(String.format("  clone.%s((%s) ((BindableProxy) %s()).deepUnwrap());", elm.setter,
          elm.type, elm.getter));
      sb.append(newLine);
      sb.append(
          String.format("} else if (BindableProxyFactory.isBindableType(t.%s())) {", elm.getter));
      sb.append(newLine);
      sb.append(String.format(
          "  clone.%s((%s) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.%s())).deepUnwrap());",
          elm.setter, elm.type, elm.getter));
      sb.append(newLine);
      sb.append("} else {");
      sb.append(newLine);
      sb.append(String.format("  clone.%s(t.%s());", elm.setter, elm.getter));
      sb.append(newLine);
      sb.append("}");
      sb.append(newLine);
    }


  }

  private String getGetter(VariableElement variable) throws UnableToCompleteException {
    if (isBoolean(variable)) {
      return getBooleanGetter(variable);
    }

    String method = compileGetterMethodName(variable);
    return Utils.getAllMethodsIn(elements, type).stream().filter(e -> e.toString().equals(method))
        .filter(e -> e.getModifiers().contains(Modifier.PUBLIC)).findFirst()
        .map(e -> e.getSimpleName().toString())
        .orElseThrow(() -> new UnableToCompleteException(String
            .format("Unable to find getter [%s] in [%s]", method, variable.getEnclosingElement())));
  }

  private String getBooleanGetter(VariableElement variable) throws UnableToCompleteException {
    String varName = variable.getSimpleName().toString();
    String is = "is" + StringUtils.capitalize(varName) + "()";
    String get = "get" + StringUtils.capitalize(varName) + "()";

    Optional<ExecutableElement> maybeIsGetter =
        Utils.getAllMethodsIn(elements, type).stream().filter(e -> e.toString().equals(is))
            .filter(e -> e.getModifiers().contains(Modifier.PUBLIC)).findFirst();
    if (maybeIsGetter.isPresent()) {
      return maybeIsGetter.get().getSimpleName().toString();
    }
    Optional<ExecutableElement> maybeGetGetter =
        Utils.getAllMethodsIn(elements, type).stream().filter(e -> e.toString().equals(get))
            .filter(e -> e.getModifiers().contains(Modifier.PUBLIC)).findFirst();
    if (maybeGetGetter.isPresent()) {
      return maybeGetGetter.get().getSimpleName().toString();
    }
    throw new UnableToCompleteException(String.format("Unable to find getter [%s]/[%s] in [%s]", is,
        get, variable.getEnclosingElement()));
  }

  private String compileGetterMethodName(VariableElement variable) {
    String varName = variable.getSimpleName().toString();
    return "get" + StringUtils.capitalize(varName) + "()";
  }

  private boolean isBoolean(VariableElement variable) {
    return variable.asType().getKind().equals(TypeKind.BOOLEAN)
        || variable.asType().toString().equals(Boolean.class.getCanonicalName());
  }

  private String getSetter(VariableElement variable) throws UnableToCompleteException {
    String method = compileSetterMethodName(variable);
    return Utils.getAllMethodsIn(elements, type).stream()
        .filter(e -> e.getKind().equals(ElementKind.METHOD))
        .filter(e -> e.toString().equals(method))
        .filter(e -> e.getModifiers().contains(Modifier.PUBLIC)).findFirst()
        .map(e -> e.getSimpleName().toString())
        .orElseThrow(() -> new UnableToCompleteException(String
            .format("Unable to find setter [%s] in [%s]", method, variable.getEnclosingElement())));
  }

  private String compileSetterMethodName(VariableElement variable) {

    MoreElements.asType(variable.getEnclosingElement()).getTypeParameters();

    String varName = variable.getSimpleName().toString();
    StringBuffer sb = new StringBuffer();
    sb.append("set");
    sb.append(StringUtils.capitalize(varName));
    sb.append("(");
    sb.append(variable.asType());
    sb.append(")");
    return sb.toString();
  }

  private void generatePropertyType(StringBuffer sb, Set<PropertyHolder> fields, TypeElement type) {
    fields.stream().forEach(field -> generatePropertyType(type, sb, field));
  }

  private void generatePropertyType(TypeElement type, StringBuffer sb, PropertyHolder field) {
    boolean isList = types.isSubtype(listTypeMirror, types.erasure(field.type));
    sb.append(String.format("  p.put(\"%s\", new PropertyType(%s.class, %s, %b));", field.name,
        getFieldType(getType(type, field)), isBindableType(field.type), isList));
    sb.append(newLine);
  }

  private String getFieldType(TypeMirror mirror) {
    TypeMirror collection = elements.getTypeElement(Collection.class.getCanonicalName()).asType();

    if (types.isSubtype(mirror, collection)) {
      return types.erasure(mirror).toString();
    }
    if (mirror.getKind().isPrimitive()) {
      return types.boxedClass(MoreTypes.asPrimitiveType(mirror)).toString();
    }
    return types.erasure(mirror).toString();
  }

  private TypeMirror getType(TypeElement parent, PropertyHolder elm) {
    if (elm.type.getKind().equals(TypeKind.TYPEVAR)) {
      TypeMirror typeMirror = types.asMemberOf(MoreTypes.asDeclared(parent.asType()), elm.field);
      if (typeMirror.getKind().equals(TypeKind.TYPEVAR)) {
        return objectTypeMirror;
      }
      return typeMirror;
    }
    return elm.type;
  }

  private boolean isBindableType(TypeMirror elm) {
    if (elm == null) {
      return false;
    }

    if (elm.getKind().isPrimitive()) {
      return false;
    }

    if (elm.getKind().equals(TypeKind.ARRAY)) {
      return false;
    }

    return MoreTypes.asElement(elm).getAnnotation(Bindable.class) != null;
  }

  private static class PropertyHolder {
    VariableElement field;
    TypeMirror type;
    String name;
    boolean isFinal;
    String getter;
    String setter;
    ExecutableElement getMethod;

    @Override
    public String toString() {
      return "PropertyHolder{" + "type=" + type + ", name='" + name + '\'' + ", isFinal=" + isFinal
          + ", getter='" + getter + '\'' + ", setter='" + setter + '\'' + '}';
    }
  }
}
