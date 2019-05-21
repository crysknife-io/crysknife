package org.treblereel.gwt.crysknife.generator.point;

import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.TypeElement;

import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.util.Utils;

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

/*    @Override
    public Expression generate() {
        FieldAccessExpr instance = new FieldAccessExpr(new ThisExpr(), "instance");
        ObjectCreationExpr newInstance = new ObjectCreationExpr();
        newInstance.setType(new ClassOrInterfaceType()
                                    .setName(getName()));

        for (FieldPoint argument : arguments) {
            newInstance.addArgument(new MethodCallExpr(new NameExpr(Utils.toVariableName(argument.getType())), "get"));
        }
        return new AssignExpr().setTarget(instance).setValue(newInstance);
    }*/
}
