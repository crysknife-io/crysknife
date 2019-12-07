package org.treblereel.gwt.crysknife.generator.point;

import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.TypeElement;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/3/19
 */
public class ConstructorPoint extends Point {

    private final List<FieldPoint> arguments = new LinkedList<>();

    public ConstructorPoint(String name, TypeElement type) {
        super(type, name);
    }

    public void addArgument(FieldPoint arg) {
        arguments.add(arg);
    }

    public List<FieldPoint> getArguments() {
        return arguments;
    }
}
