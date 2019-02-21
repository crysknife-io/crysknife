package org.treblereel.gwt.crysknife.internal;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/19/19
 */
public class FieldInjectionPoint extends InjectionPoint {

    private TypeElement declared;

    public FieldInjectionPoint(TypeElement parent, ElementKind type, String name, TypeElement declared) {
        super(parent, type, name);
        this.declared = declared;
    }

    @Override
    public String toString() {
        return "FieldInjectionPoint{" +
                " name : " + getName() +
                ", parent : " + getParent().getQualifiedName() +
                ", type : " + getType() +
                ", declared : " + declared +
                '}';
    }

    public TypeElement getDeclared() {
        return declared;
    }
}
