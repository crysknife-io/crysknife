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
import io.crysknife.exception.GenerationException;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.ui.databinding.client.api.Bindable;
import io.crysknife.util.Utils;
import org.apache.commons.lang3.StringUtils;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 11/21/19
 */
public class BindableProxyGenerator {

  private MethodDeclaration methodDeclaration;
  private TypeElement type;
  private String newLine = System.lineSeparator();
  private Types types;
  private Elements elements;

  private final TypeMirror listTypeMirror;
  private final TypeMirror objectTypeMirror;
  private final Set<UnableToCompleteException> errors = new HashSet<>();


  BindableProxyGenerator(Elements elements, Types types, MethodDeclaration methodDeclaration,
      TypeElement type) {
    this.methodDeclaration = methodDeclaration;
    this.type = type;
    this.types = types;
    this.elements = elements;

    listTypeMirror = elements.getTypeElement(List.class.getCanonicalName()).asType();
    objectTypeMirror = elements.getTypeElement(Object.class.getCanonicalName()).asType();

    errors.clear();
  }

  void generate() throws UnableToCompleteException {
    String clazzName = type.getQualifiedName().toString().replaceAll("\\.", "_") + "Proxy";

    StringBuffer sb = new StringBuffer();

    Set<VariableElement> fields =
        Utils.getAllFieldsIn(elements, type).stream().filter(elm -> elm.getKind().isField())
            .filter(e -> !e.getModifiers().contains(Modifier.FINAL))
            .filter(e -> !e.getModifiers().contains(Modifier.STATIC))
            .map(e -> MoreElements.asVariable(e)).collect(Collectors.toSet());

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

    generatePropertyType(sb, type);

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

  private void set(TypeElement type, Set<VariableElement> fields, StringBuffer sb) {
    sb.append("public void set(String property, Object value) {");
    sb.append(newLine);
    sb.append("  switch (property) {");
    sb.append(newLine);

    fields.forEach(elm -> {

      try {
        sb.append(String.format("case \"%s\": target.%s((%s) value);",
            elm.getSimpleName().toString(), getSetter(elm), getFieldType(getType(type, elm))));
      } catch (UnableToCompleteException e) {
        errors.add(e);
      }
      sb.append(newLine);
      sb.append("break;");
      sb.append(newLine);
    });

    sb.append(String.format("  case \"this\": target = (%s) value;", type));
    sb.append(newLine);
    sb.append("agent.target = target;");
    sb.append(newLine);
    sb.append("break;");
    sb.append(newLine);
    sb.append(
        String.format("default: throw new NonExistingPropertyException(\"%s\", property);", type));
    sb.append(newLine);
    sb.append("}");
    sb.append(newLine);
    sb.append("}");
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

  private void get(Set<VariableElement> fields, StringBuffer sb) {
    sb.append(newLine);
    sb.append("public Object get(String property) {");
    sb.append(newLine);
    sb.append("  switch (property) {");
    sb.append(newLine);

    fields.forEach(elm -> {
      try {
        sb.append(String.format("case \"%s\": return %s();", elm.getSimpleName().toString(),
            getGetter(elm)));
      } catch (UnableToCompleteException e) {
        errors.add(e);
      }
      sb.append(newLine);
    });

    sb.append("  case \"this\": return target;");
    sb.append(newLine);
    sb.append(
        String.format("default: throw new NonExistingPropertyException(\"%s\", property);", type));
    sb.append(newLine);
    sb.append("}");
    sb.append(newLine);
    sb.append("}");
    sb.append(newLine);
  }

  private void getterAndSetter(TypeElement type, Set<VariableElement> fields, StringBuffer sb) {
    fields.forEach(elm -> {
      try {
        getterAndSetter(type, elm, sb);
      } catch (UnableToCompleteException e) {
        errors.add(e);
      }
    });
  }

  private void getterAndSetter(TypeElement type, VariableElement elm, StringBuffer sb)
      throws UnableToCompleteException {
    sb.append(newLine);
    sb.append(String.format("public %s %s() {", getType(type, elm), getGetter(elm)));
    sb.append(newLine);
    sb.append(String.format("  return target.%s();", getGetter(elm)));
    sb.append(newLine);
    sb.append("}");
    sb.append(newLine);

    sb.append(newLine);
    sb.append(String.format("public void %s(%s value) {", getSetter(elm), getType(type, elm)));
    sb.append(newLine);
    sb.append(String.format("  changeAndFire(\"%s\", value);", elm.getSimpleName().toString()));
    sb.append(newLine);
    sb.append("}");
    sb.append(newLine);
  }


  private TypeMirror getType(TypeElement parent, VariableElement elm) {
    if (elm.asType().getKind().equals(TypeKind.TYPEVAR)) {
      TypeMirror typeMirror = types.asMemberOf(MoreTypes.asDeclared(parent.asType()), elm);
      if (typeMirror.getKind().equals(TypeKind.TYPEVAR)) {
        return objectTypeMirror;
      }
      return typeMirror;
    }
    return elm.asType();
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

  private void deepUnwrap(Set<VariableElement> fields, StringBuffer sb) {
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

  private void deepUnwrap(VariableElement elm, StringBuffer sb) throws UnableToCompleteException {
    if (!isBindableType(elm)) {
      sb.append("  ");
      sb.append(String.format("clone.%s(t.%s());", getSetter(elm), getGetter(elm)));
      sb.append(newLine);
    } else {
      sb.append(String.format("if (t.%s() instanceof BindableProxy) {", getGetter(elm),
          type.getSimpleName()));
      sb.append(newLine);
      sb.append(String.format("  clone.%s((%s) ((BindableProxy) %s()).deepUnwrap());",
          getSetter(elm), elm.asType(), getGetter(elm)));
      sb.append(newLine);
      sb.append(String.format("} else if (BindableProxyFactory.isBindableType(t.%s())) {",
          getGetter(elm)));
      sb.append(newLine);
      sb.append(String.format(
          "  clone.%s((%s) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.%s())).deepUnwrap());",
          getSetter(elm), elm.asType(), getGetter(elm)));
      sb.append(newLine);
      sb.append("} else {");
      sb.append(newLine);
      sb.append(String.format("  clone.%s(t.%s());", getSetter(elm), getGetter(elm)));
      sb.append(newLine);
      sb.append("}");
      sb.append(newLine);
    }



  }

  private boolean isBindableType(VariableElement elm) {
    if (elm.asType().getKind().isPrimitive()) {
      return false;
    }

    if (elm.asType().getKind().equals(TypeKind.ARRAY)) {
      return false;
    }

    return MoreTypes.asElement(elm.asType()).getAnnotation(Bindable.class) != null;
  }

  private String getGetter(VariableElement variable) throws UnableToCompleteException {
    String method = compileGetterMethodName(variable);
    return Utils.getAllMethodsIn(elements, type).stream()
        .filter(e -> e.getKind().equals(ElementKind.METHOD))
        .filter(e -> e.toString().equals(method))
        .filter(e -> e.getModifiers().contains(Modifier.PUBLIC)).findFirst()
        .map(e -> e.getSimpleName().toString())
        .orElseThrow(() -> new UnableToCompleteException(String
            .format("Unable to find getter [%s] in [%s]", method, variable.getEnclosingElement())));
  }

  private String compileGetterMethodName(VariableElement variable) {
    String varName = variable.getSimpleName().toString();
    return (isBoolean(variable) ? "is" : "get") + StringUtils.capitalize(varName) + "()";
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

  private boolean isBoolean(VariableElement variable) {
    return variable.asType().getKind().equals(TypeKind.BOOLEAN)
        || variable.asType().toString().equals(Boolean.class.getCanonicalName());
  }

  private void generatePropertyType(StringBuffer sb, TypeElement type) {
    Utils.getAllFieldsIn(elements, type).stream()
        .forEach(field -> generatePropertyType(type, sb, field));
  }

  private void generatePropertyType(TypeElement type, StringBuffer sb, VariableElement field) {
    boolean isList = types.isSubtype(listTypeMirror, types.erasure(field.asType()));
    sb.append(String.format("  p.put(\"%s\", new PropertyType(%s.class, %s, %b));",
        field.getSimpleName().toString().toLowerCase(Locale.ROOT),
        getFieldType(getType(type, field)), isBindableType(field), isList));
    sb.append(newLine);
  }
}
