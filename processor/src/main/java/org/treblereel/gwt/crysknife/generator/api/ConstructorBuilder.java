package org.treblereel.gwt.crysknife.generator.api;

import javax.lang.model.element.TypeElement;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
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
                classBuilder.getClassCompilationUnit().addImport("org.treblereel.gwt.crysknife.client.BeanManagerImpl");
                classBuilder.getClassCompilationUnit().addImport("org.treblereel.gwt.crysknife.client.Instance");

                String varName = Utils.toVariableName(argument.getQualifiedName().toString());

                ClassOrInterfaceType beanManager = new ClassOrInterfaceType();
                beanManager.setName("BeanManagerImpl");

                MethodCallExpr callForBeanManagerImpl = new MethodCallExpr(beanManager.getNameAsExpression(), "get");
                MethodCallExpr callForPriducer = new MethodCallExpr(callForBeanManagerImpl, "lookupBean")
                        .addArgument(new StringLiteralExpr(argument.getQualifiedName().toString()));

                ThisExpr clazz = new ThisExpr();
                FieldAccessExpr field = new FieldAccessExpr(clazz, varName);
                AssignExpr assign = new AssignExpr().setTarget(field).setValue(callForPriducer);

                constructorDeclaration.getBody().addStatement(assign);
            }
        }
    }
}
