package org.treblereel.gwt.crysknife.generator.point;

import javax.lang.model.element.TypeElement;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/2/19
 */
public abstract class Point {

    protected final String name;

    protected TypeElement type;

    public Point(TypeElement type, String name) {
        this.type = type;
        this.name = name;
    }

    public TypeElement getType() {
        return type;
    }

    public void setType(TypeElement type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }
}
