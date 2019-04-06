package org.treblereel.gwt.crysknife.generator;

import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.definition.Definition;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/3/19
 */
public abstract class ScopedBeanGenerator extends BeanIOCGenerator {

    protected FieldAccessExpr instance;


    protected Expression generateInstanceInitializer(ClassBuilder classBuilder, BeanDefinition definition) {
        if (definition.getConstructorInjectionPoint() == null) {
            instance = new FieldAccessExpr(new ThisExpr(), "instance");
            ObjectCreationExpr newInstance = new ObjectCreationExpr();
            newInstance.setType(new ClassOrInterfaceType()
                                        .setName(definition.getClassName()));
            return new AssignExpr().setTarget(instance).setValue(newInstance);
        } else {
            return definition.getConstructorInjectionPoint().generate();
        }
    }

}
