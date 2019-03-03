package org.treblereel.gwt.crysknife.generator.api;

import javax.inject.Provider;
import javax.lang.model.element.TypeElement;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.treblereel.gwt.crysknife.util.Utils;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/3/19
 */
public class ConstructorBuilder extends Builder {

    public ConstructorBuilder(ClassBuilder classBuilder) {
        super(classBuilder);
    }

    @Override
    public void build() {
        ConstructorDeclaration constructorDeclaration = classBuilder.getClassDeclaration().addConstructor(Modifier.Keyword.PRIVATE);
        if (!classBuilder.beanDefinition.getDependsOn().isEmpty()) {
            for (TypeElement argument : classBuilder.beanDefinition.getDependsOn()) {
                String varName = Utils.toVariableName(argument.getQualifiedName().toString());

                ClassOrInterfaceType type = new ClassOrInterfaceType();
                type.setName(Provider.class.getSimpleName());
                type.setTypeArguments(new ClassOrInterfaceType().setName(argument.getQualifiedName().toString()));

                Parameter param = new Parameter();
                param.setName(varName);
                param.setType(type);

                constructorDeclaration.addAndGetParameter(param);
                constructorDeclaration.getBody().getStatements();

                ThisExpr clazz = new ThisExpr();
                FieldAccessExpr field = new FieldAccessExpr(clazz, varName);
                AssignExpr assign = new AssignExpr().setTarget(field).setValue(new NameExpr(varName));
                constructorDeclaration.getBody().addStatement(assign);
            }
        }
    }
}
