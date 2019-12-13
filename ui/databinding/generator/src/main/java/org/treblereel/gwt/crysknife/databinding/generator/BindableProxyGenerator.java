package org.treblereel.gwt.crysknife.databinding.generator;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.google.auto.common.MoreTypes;
import org.apache.commons.lang3.StringUtils;
import org.treblereel.gwt.crysknife.databinding.client.api.Bindable;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/21/19
 */
public class BindableProxyGenerator {

    private MethodDeclaration methodDeclaration;
    private TypeElement type;
    private String newLine = System.lineSeparator();
    private Types types;

    BindableProxyGenerator(Types types, MethodDeclaration methodDeclaration, TypeElement type) {
        this.methodDeclaration = methodDeclaration;
        this.type = type;
        this.types = types;
    }

    void generate() {
        String clazzName = type.getQualifiedName().toString().replaceAll("\\.", "_") + "Proxy";

        StringBuffer sb = new StringBuffer();

        sb.append(String.format("class %s extends %s implements BindableProxy { ", clazzName, type.getSimpleName()));
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
        sb.append(String.format("  agent = new BindableProxyAgent<%s>(this, targetVal);", type.getSimpleName()));
        sb.append(newLine);
        sb.append("  target = targetVal;");
        sb.append(newLine);
        sb.append("  final Map<String, PropertyType> p = agent.propertyTypes;");
        sb.append(newLine);

        generatePropertyType(sb, type);

        sb.append(String.format("  p.put(\"this\", new PropertyType(%s.class, true, false));", type.getSimpleName()));
        sb.append("  agent.copyValues();");
        sb.append(newLine);

        sb.append(newLine);
        sb.append("}");
        sb.append(newLine);

        sb.append("public BindableProxyAgent getBindableProxyAgent() {");
        sb.append(newLine);
        sb.append(" return agent;");
        sb.append(newLine);
        sb.append("}");

        sb.append("public void updateWidgets() {");
        sb.append(newLine);
        sb.append(" agent.updateWidgetsAndFireEvents();");
        sb.append(newLine);
        sb.append("}");

        sb.append(String.format("public %s unwrap() {", type.getSimpleName()));
        sb.append(newLine);
        sb.append(" return target;");
        sb.append(newLine);
        sb.append("}");

        deepUnwrap(sb);
        equals(clazzName, sb);

        sb.append("public int hashCode() {");
        sb.append(newLine);
        sb.append(" return target.hashCode();");
        sb.append(newLine);
        sb.append("}");

        sb.append("public String toString() {");
        sb.append(newLine);
        sb.append(" return target.toString();");
        sb.append(newLine);
        sb.append("}");

        sb.append("private void changeAndFire(String property, Object value) {");
        sb.append(newLine);
        sb.append(" final Object oldValue = get(property);");
        sb.append(newLine);
        sb.append(" set(property, value);");
        sb.append(newLine);
        sb.append(" agent.updateWidgetsAndFireEvent(false, property, oldValue, value);");
        sb.append(newLine);
        sb.append("}");

        getterAndSetter(sb);
        get(sb);
        set(sb);
        getBeanProperties(sb);

        sb.append("}");
        sb.append(newLine);

        addBindableProxy(clazzName, sb);

        methodDeclaration.getBody().get().addAndGetStatement(sb.toString());
    }

    private void addBindableProxy(String clazzName, StringBuffer sb) {
        sb.append(String.format("BindableProxyFactory.addBindableProxy(%s.class, new BindableProxyProvider() {", type.getSimpleName()));
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
        sb.append("});");
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

    private void set(StringBuffer sb) {
        sb.append("public void set(String property, Object value) {");
        sb.append(newLine);
        sb.append("  switch (property) {");
        sb.append(newLine);

        type.getEnclosedElements().stream().filter(elm -> elm.getKind().isField())
                .forEach(f -> {
                    VariableElement elm = (VariableElement) f;
                    sb.append(String.format("case \"%s\": target.%s((%s) value);", f.getSimpleName().toString(), getSetter(elm),
                                            getFieldType(f.asType())));
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
        sb.append(String.format("default: throw new NonExistingPropertyException(\"%s\", property);", type));
        sb.append(newLine);
        sb.append("}");
        sb.append(newLine);
        sb.append("}");
        sb.append(newLine);
    }

    private String getFieldType(TypeMirror mirror) {
        if (mirror.getKind().isPrimitive()) {
            return types.boxedClass(MoreTypes.asPrimitiveType(mirror)).toString();
        }
        return mirror.toString();
    }

    private void get(StringBuffer sb) {
        sb.append("public Object get(String property) {");
        sb.append(newLine);
        sb.append("  switch (property) {");
        sb.append(newLine);

        type.getEnclosedElements().stream().filter(elm -> elm.getKind().isField())
                .forEach(f -> {
                    VariableElement elm = (VariableElement) f;
                    sb.append(String.format("case \"%s\": return %s();", f.getSimpleName().toString(), getGetter(elm)));
                    sb.append(newLine);
                });

        sb.append("  case \"this\": return target;");
        sb.append(newLine);
        sb.append(String.format("default: throw new NonExistingPropertyException(\"%s\", property);", type));
        sb.append(newLine);
        sb.append("}");
        sb.append(newLine);
        sb.append("}");
        sb.append(newLine);
    }

    private void getterAndSetter(StringBuffer sb) {
        type.getEnclosedElements().stream().filter(elm -> elm.getKind().isField())
                .forEach(f -> {
                    VariableElement elm = (VariableElement) f;
                    sb.append(String.format("public %s %s() {", f.asType(), getGetter(elm)));
                    sb.append(newLine);
                    sb.append(String.format("  return target.%s();", getGetter(elm)));
                    sb.append(newLine);
                    sb.append("}");
                    sb.append(newLine);

                    sb.append(String.format("public void %s(%s %s) {", getGetter(elm), f.asType(), f.getSimpleName().toString()));
                    sb.append(newLine);
                    sb.append(String.format("  changeAndFire(\"%s\", %s);", f.getSimpleName().toString(), f.getSimpleName().toString()));
                    sb.append(newLine);
                    sb.append("}");
                    sb.append(newLine);
                });
    }

    private void equals(String clazzName, StringBuffer sb) {
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

    private void deepUnwrap(StringBuffer sb) {
        sb.append(String.format("public %s deepUnwrap() {", type.getSimpleName()));
        sb.append(newLine);
        sb.append(String.format("  final %s clone = new %s();", type.getSimpleName(), type.getSimpleName()));
        sb.append(newLine);
        sb.append(String.format("  final %s t = unwrap();", type.getSimpleName()));
        sb.append(newLine);

        type.getEnclosedElements().stream().filter(elm -> elm.getKind().isField())
                .forEach(f -> {

                    VariableElement elm = (VariableElement) f;
                    if (!isBindableType(elm)) {
                        sb.append("  ");
                        sb.append(String.format("clone.%s(t.%s());", getSetter(elm), getGetter(elm)));
                        sb.append(newLine);
                    } else {
                        sb.append(String.format("if (t.%s() instanceof BindableProxy) {", getGetter(elm), type.getSimpleName()));
                        sb.append(newLine);
                        sb.append(String.format("  clone.%s((%s) ((BindableProxy) %s()).deepUnwrap());", getSetter(elm), f.asType(), getGetter(elm)));
                        sb.append(newLine);
                        sb.append(String.format("} else if (BindableProxyFactory.isBindableType(t.%s())) {", getGetter(elm)));
                        sb.append(newLine);
                        sb.append(String.format("  clone.%s((%s) ((BindableProxy) BindableProxyFactory.getBindableProxy(t.%s())).deepUnwrap());", getSetter(elm),
                                                f.asType(), getGetter(elm)));
                        sb.append(newLine);
                        sb.append("} else {");
                        sb.append(newLine);
                        sb.append(String.format("  clone.%s(t.%s());", getSetter(elm), getGetter(elm)));
                        sb.append(newLine);
                        sb.append("}");
                        sb.append(newLine);
                    }
                });

        sb.append(" return clone;");
        sb.append(newLine);
        sb.append("}");
        sb.append(newLine);
    }

    private boolean isBindableType(VariableElement elm) {
        if (elm.asType().getKind().isPrimitive()) {
            return false;
        }
        return MoreTypes.asTypeElement(elm.asType()).getAnnotation(Bindable.class) != null;
    }

    private String getGetter(VariableElement variable) {
        String method = compileGetterMethodName(variable);
        return type.getEnclosedElements().stream()
                .filter(e -> e.getKind().equals(ElementKind.METHOD))
                .filter(e -> e.toString().equals(method))
                .filter(e -> e.getModifiers().contains(Modifier.PUBLIC))
                .findFirst().map(e -> e.getSimpleName().toString())
                .orElseThrow(() -> new Error(String.format("Unable to find getter [%s] in [%s]", method, variable.getEnclosingElement())));
    }

    private String compileGetterMethodName(VariableElement variable) {
        String varName = variable.getSimpleName().toString();
        return (isBoolean(variable) ? "is" : "get") + StringUtils.capitalize(varName) + "()";
    }

    private String compileSetterMethodName(VariableElement variable) {
        String varName = variable.getSimpleName().toString();
        StringBuffer sb = new StringBuffer();
        sb.append("set");
        sb.append(StringUtils.capitalize(varName));
        sb.append("(");
        sb.append(variable.asType());
        sb.append(")");
        return sb.toString();
    }

    private String getSetter(VariableElement variable) {
        String method = compileSetterMethodName(variable);
        return type.getEnclosedElements().stream()
                .filter(e -> e.getKind().equals(ElementKind.METHOD))
                .filter(e -> e.toString().equals(method))
                .filter(e -> e.getModifiers().contains(Modifier.PUBLIC))
                .findFirst().map(e -> e.getSimpleName().toString())
                .orElseThrow(() -> new Error(String.format("Unable to find setter [%s] in [%s]", method, variable.getEnclosingElement())));
    }

    private boolean isBoolean(VariableElement variable) {
        return variable.getKind().equals(TypeKind.BOOLEAN);
    }

    private void generatePropertyType(StringBuffer sb, TypeElement type) {
        type.getEnclosedElements().stream().filter(elm -> elm.getKind().isField())
                .forEach(field -> generatePropertyType(sb, ((VariableElement) field)));
    }

    private void generatePropertyType(StringBuffer sb, VariableElement field) {
        sb.append(String.format("  p.put(\"%s\", new PropertyType(%s.class, %s, false));", field.getSimpleName(), getFieldType(field.asType()), isBindableType(field)));
        sb.append(newLine);
    }
}