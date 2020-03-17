package org.treblereel.gwt.crysknife.generator;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.lang.model.element.TypeElement;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreTypes;
import org.treblereel.gwt.crysknife.annotation.Generator;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.definition.Definition;
import org.treblereel.gwt.crysknife.generator.point.FieldPoint;
import org.treblereel.gwt.crysknife.util.Utils;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 3/31/19
 */
@Generator(priority = 999)
public class EventProducerGenerator extends ScopedBeanGenerator {

    public EventProducerGenerator(IOCContext iocContext) {
        super(iocContext);
    }

    @Override
    public void register() {
        iocContext.register(Inject.class, Event.class, WiringElementType.BEAN, this);
        iocContext.getBlacklist().add(Event.class.getCanonicalName());

        TypeElement type = iocContext.getGenerationContext().getElements().getTypeElement(Event.class.getCanonicalName());
        BeanDefinition beanDefinition = iocContext.getBeanDefinitionOrCreateAndReturn(type);
        beanDefinition.setGenerator(this);
        iocContext.getBeans().put(type, beanDefinition);
    }

    @Override
    public void generateBeanFactory(ClassBuilder clazz, Definition definition) {
        if (definition instanceof BeanDefinition) {
            BeanDefinition beanDefinition = (BeanDefinition) definition;
            initClassBuilder(clazz, beanDefinition);
            generateFactoryCreateMethod(clazz, beanDefinition);
            write(clazz, beanDefinition, iocContext.getGenerationContext());
        }
    }

    @Override
    public void initClassBuilder(ClassBuilder clazz, BeanDefinition beanDefinition) {
        clazz.getClassCompilationUnit().setPackageDeclaration(beanDefinition.getPackageName());
        clazz.setClassName(beanDefinition.getClassFactoryName());
        clazz.getClassCompilationUnit().addImport("org.treblereel.gwt.crysknife.client.internal.AbstractEventFactory");

        ClassOrInterfaceType factory = new ClassOrInterfaceType();
        factory.setName("AbstractEventFactory");
        clazz.getExtendedTypes().add(factory);
    }

    @Override
    public Expression generateBeanCall(ClassBuilder classBuilder, FieldPoint fieldPoint, BeanDefinition beanDefinition) {
        classBuilder.getClassCompilationUnit().addImport("javax.enterprise.event.Event_Factory");
        MoreTypes.asDeclared(fieldPoint.getField().asType()).getTypeArguments();

        return new NameExpr("Event_Factory.get().get(" + MoreTypes.asDeclared(fieldPoint.getField()
                                                                                      .asType())
                .getTypeArguments().get(0) + ".class)");
    }

    @Override
    public void addFactoryFieldInitialization(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        classBuilder.getClassCompilationUnit().addImport("javax.enterprise.event.Event_Factory");
        String varName = Utils.toVariableName(beanDefinition.getQualifiedName());
        MethodCallExpr callForBeanManagerImpl = new MethodCallExpr(new NameExpr("Event_Factory").getNameAsExpression(), "get");
        ThisExpr clazz = new ThisExpr();
        FieldAccessExpr field = new FieldAccessExpr(clazz, varName);
        AssignExpr assign = new AssignExpr().setTarget(field).setValue(callForBeanManagerImpl);
        classBuilder.addStatementToConstructor(assign);
    }

    @Override
    public void generateFactoryCreateMethod(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
        MethodDeclaration getMethodDeclaration = classBuilder
                .addMethod("get", Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);
        getMethodDeclaration.setType("Event_Factory");
        classBuilder.setGetMethodDeclaration(getMethodDeclaration);

        BlockStmt body = classBuilder.getGetMethodDeclaration().getBody().get();
        IfStmt ifStmt = new IfStmt().setCondition(new BinaryExpr(new NameExpr("instance"), new NullLiteralExpr(), BinaryExpr.Operator.EQUALS));
        ObjectCreationExpr newInstance = new ObjectCreationExpr();
        newInstance.setType(new ClassOrInterfaceType()
                                    .setName("Event_Factory"));

        ifStmt.setThenStmt(new BlockStmt().addAndGetStatement(new AssignExpr().setTarget(new NameExpr("instance")).setValue(newInstance)));
        body.addAndGetStatement(ifStmt);
        body.addAndGetStatement(new ReturnStmt(new NameExpr("instance")));
        classBuilder.addField("javax.enterprise.event.Event_Factory", "instance", Modifier.Keyword.PRIVATE, Modifier.Keyword.STATIC);
    }
}

