package org.treblereel.gwt.crysknife.generator;

import javax.inject.Inject;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.google.auto.common.MoreTypes;
import org.treblereel.gwt.crysknife.annotation.Generator;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.definition.Definition;
import org.treblereel.gwt.crysknife.generator.point.FieldPoint;
import org.treblereel.gwt.crysknife.navigation.client.local.Navigation;
import org.treblereel.gwt.crysknife.navigation.client.local.TransitionTo;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/3/20
 */
@Generator
public class TransitionToGenerator extends ScopedBeanGenerator {

  public static final String TRANSITION_TO_FACTORY = "TransitionTo_Factory";

  public TransitionToGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  public void register() {
    iocContext.register(Inject.class, TransitionTo.class, WiringElementType.BEAN, this);
    iocContext.getBlacklist().add(TransitionTo.class.getCanonicalName());
  }

  @Override
  public void generateBeanFactory(ClassBuilder clazz, Definition definition) {

  }

  @Override
  public Expression generateBeanCall(ClassBuilder clazz, FieldPoint fieldPoint,
      BeanDefinition beanDefinition) {
    clazz.getClassCompilationUnit()
        .addImport("org.treblereel.gwt.crysknife.navigation.client.local.TransitionTo");

    return new ObjectCreationExpr().setType(TransitionTo.class)
        .addArgument(MoreTypes.asDeclared(fieldPoint.getField().asType()).getTypeArguments().get(0)
            .toString() + ".class")
        .addArgument(
            new MethodCallExpr(
                new MethodCallExpr(new MethodCallExpr(new NameExpr("BeanManagerImpl"), "get"),
                    "lookupBean").addArgument(Navigation.class.getCanonicalName() + ".class"),
                "get"));
  }
}
