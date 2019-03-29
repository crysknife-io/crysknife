package org.treblereel.gwt.crysknife.generator;

import javax.inject.Inject;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.treblereel.gwt.crysknife.client.BeanManager;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.Definition;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/30/19
 */
public class BeanManagerProducerGenerator extends IOCGenerator {

    @Override
    public void register(IOCContext iocContext) {
        iocContext.register(Inject.class, BeanManager.class, WiringElementType.FIELD_TYPE, this);
    }

    @Override
    public void generate(ClassBuilder clazz, Definition beanDefinition) {
        MethodCallExpr methodCallExpr = new MethodCallExpr(new NameExpr(BeanManager.class.getCanonicalName() + "Impl"), "get");
        clazz.getGetMethodDeclaration().getBody().get().addAndGetStatement(new ReturnStmt(methodCallExpr));
    }
}
