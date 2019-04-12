package org.treblereel.gwt.crysknife.generator.api;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
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
    public void build(BeanDefinition argument) {
        ConstructorDeclaration constructorDeclaration = classBuilder.getClassDeclaration().addConstructor(Modifier.Keyword.PRIVATE);
        classBuilder.getClassCompilationUnit().addImport("org.treblereel.gwt.crysknife.client.BeanManagerImpl");
        classBuilder.getClassCompilationUnit().addImport("org.treblereel.gwt.crysknife.client.Instance");

        String varName = Utils.toVariableName(argument.getQualifiedName());

        ClassOrInterfaceType beanManager = new ClassOrInterfaceType();

        beanManager.setName(argument.getFactoryVariableName());

        MethodCallExpr callForBeanManagerImpl = new MethodCallExpr(beanManager.getNameAsExpression(), "get");

        MethodCallExpr callForProducer = new MethodCallExpr(callForBeanManagerImpl, "lookupBean")
                .addArgument(new FieldAccessExpr(new NameExpr(argument.getQualifiedName()), "class"));

        ThisExpr clazz = new ThisExpr();
        FieldAccessExpr field = new FieldAccessExpr(clazz, varName);
        AssignExpr assign = new AssignExpr().setTarget(field).setValue(callForProducer);

        constructorDeclaration.getBody().addStatement(assign);
    }
}
