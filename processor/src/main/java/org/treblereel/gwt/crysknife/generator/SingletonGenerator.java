package org.treblereel.gwt.crysknife.generator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.treblereel.gwt.crysknife.annotation.Generator;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/2/19
 */
@Generator(priority = 100)
public class SingletonGenerator extends ScopedBeanGenerator {

    @Override
    public void register(IOCContext iocContext) {
        iocContext.register(Singleton.class, WiringElementType.DEPENDENT_BEAN, this);
        iocContext.register(ApplicationScoped.class, WiringElementType.DEPENDENT_BEAN, this);
        this.iocContext = iocContext;
    }

    @Override
    public void generateInstanceGetMethodBuilder(ClassBuilder builder, BeanDefinition beanDefinition) {
        super.generateInstanceGetMethodBuilder(builder, beanDefinition);
        BlockStmt body = builder.getGetMethodDeclaration().getBody().get();

        FieldAccessExpr instance = new FieldAccessExpr(new ThisExpr(), "instance");
        IfStmt ifStmt = new IfStmt().setCondition(new BinaryExpr(instance, new NullLiteralExpr(), BinaryExpr.Operator.NOT_EQUALS));
        ifStmt.setThenStmt(new ReturnStmt(instance));
        body.addAndGetStatement(ifStmt);
        body.addAndGetStatement(generateInstanceInitializer(builder, beanDefinition));
        builder.addField(beanDefinition.getClassName(), "instance", Modifier.Keyword.PRIVATE);
    }
}
