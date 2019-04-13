package org.treblereel.gwt.crysknife.generator.point;

import java.util.Objects;

import javax.inject.Named;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import org.treblereel.gwt.crysknife.util.Utils;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/3/19
 */
public class FieldPoint extends Point {

    private VariableElement field;

    private FieldPoint(String name, TypeElement injection, VariableElement field) {
        super(injection, name);
        this.field = field;
    }

    public static FieldPoint of(VariableElement injection) {
        TypeElement type = MoreElements.asType(MoreTypes.asElement(injection.asType()));
        FieldPoint point = new FieldPoint(injection.getSimpleName().toString(), type, injection);
        return point;
    }

    public VariableElement getField() {
        return field;
    }

    @Override
    public Expression generate() {
        ThisExpr clazz = new ThisExpr();
        FieldAccessExpr instance = new FieldAccessExpr(clazz, "instance");
        FieldAccessExpr field = new FieldAccessExpr(instance, getName());
        MethodCallExpr call;
        if (MoreTypes.asTypeElement(this.field.asType()).getTypeParameters().size() > 0) {
            MethodCallExpr instanceCall = new MethodCallExpr(new NameExpr(Utils.toVariableName(type)), "get");
            call = new MethodCallExpr(instanceCall, "get");
            MoreTypes.asDeclared(this.field.asType()).getTypeArguments().forEach(tp -> {
                //TODO
                call.addArgument(new FieldAccessExpr(new NameExpr(tp.toString()), "class"));
            });
        } else {
            call = new MethodCallExpr(new NameExpr(Utils.toVariableName(type)), "get");
        }

        return new AssignExpr().setTarget(field).setValue(call);
    }

    public boolean isNamed() {
        return field.getAnnotation(Named.class) != null;
    }

    //TODO
    public boolean isQualified() {
        throw new NotImplementedException();
    }

    public String getNamed() {
        return field.getAnnotation(Named.class).value();
    }

    @Override
    public String toString() {
        return "FieldPoint{" +
                "injection=" + Utils.getQualifiedName(type) +
                " name=" + name +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FieldPoint that = (FieldPoint) o;
        return Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}
