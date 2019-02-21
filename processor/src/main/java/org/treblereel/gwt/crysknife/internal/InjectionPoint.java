package org.treblereel.gwt.crysknife.internal;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 2/19/19
 */
public abstract class InjectionPoint {

    private String name;

    private ElementKind type;

    private TypeElement parent;

    public InjectionPoint(TypeElement parent, ElementKind type, String name) {
        this.name = name;
        this.type = type;
        this.parent = parent;
    }


    public TypeElement getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public ElementKind getType() {
        return type;
    }

    public static enum Type {
        FIELD, CONSTRUSTOR;
    }
}
