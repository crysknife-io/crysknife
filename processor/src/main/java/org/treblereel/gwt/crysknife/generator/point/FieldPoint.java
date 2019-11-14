package org.treblereel.gwt.crysknife.generator.point;

import java.util.Objects;

import javax.inject.Named;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import org.treblereel.gwt.crysknife.util.Utils;

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

    public boolean isNamed() {
        return field.getAnnotation(Named.class) != null;
    }

    //TODO
    public boolean isQualified() {
        throw new UnsupportedOperationException();
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
