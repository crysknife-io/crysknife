package org.treblereel.gwt.crysknife.generator;

import javax.inject.Inject;

import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.treblereel.gwt.crysknife.annotation.Generator;
import org.treblereel.gwt.crysknife.client.BeanManager;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/30/19
 */
@Generator()
public class BeanManagerProducerGenerator extends ScopedBeanGenerator {

  public BeanManagerProducerGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  public void register() {
    iocContext.register(Inject.class, BeanManager.class, WiringElementType.FIELD_TYPE, this);
  }

  @Override
  public void generateInstanceGetMethodReturn(ClassBuilder classBuilder,
      BeanDefinition beanDefinition) {
    MethodCallExpr methodCallExpr =
        new MethodCallExpr(new NameExpr(BeanManager.class.getCanonicalName() + "Impl"), "get");
    classBuilder.getGetMethodDeclaration().getBody().get()
        .addAndGetStatement(new ReturnStmt(methodCallExpr));
  }
}
