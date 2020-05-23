package org.treblereel.gwt.crysknife.generator.dataelements;

import java.lang.reflect.Field;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import org.treblereel.gwt.crysknife.client.Instance;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.templates.client.annotation.DataField;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 4/26/20
 */
class DataElementInfoJREGeneratorBuilder extends Generator {

    private BeanDefinition bean;
    private ClassBuilder classBuilder;

    DataElementInfoJREGeneratorBuilder(IOCContext iocContext) {
        super(iocContext);
    }

    @Override
    protected String build(BeanDefinition bean) {
        this.bean = bean;
        classBuilder = new ClassBuilder(bean);
        initClass();
        addFields();
        addOnInvoke();
        return classBuilder.toSourceCode();
    }

    private void initClass() {
        classBuilder.setClassName(bean.getClassName() + "DataElementInfo");
        classBuilder.getClassCompilationUnit().setPackageDeclaration(bean.getPackageName());
        classBuilder.getClassDeclaration().getAnnotations().add(new NormalAnnotationExpr().setName(new Name("Aspect")));
        classBuilder.getClassCompilationUnit().addImport("org.aspectj.lang.ProceedingJoinPoint");
        classBuilder.getClassCompilationUnit().addImport("org.aspectj.lang.annotation.Around");
        classBuilder.getClassCompilationUnit().addImport("org.aspectj.lang.annotation.Aspect");
        classBuilder.getClassCompilationUnit().addImport("org.treblereel.gwt.crysknife.client.BeanManagerImpl");
        classBuilder.getClassCompilationUnit().addImport(Field.class);
        classBuilder.getClassCompilationUnit().addImport(Supplier.class);
        classBuilder.getClassCompilationUnit().addImport(Instance.class);
    }

    private void addFields() {
        ElementFilter.fieldsIn(bean.getType().getEnclosedElements()).stream()
                .filter(field -> MoreElements.isAnnotationPresent(field, DataField.class))
                .filter(field -> !MoreElements.isAnnotationPresent(field, Inject.class))
                .forEach(fieldPoint -> {

                    generateFactoryFieldDeclaration(fieldPoint);
                    MethodDeclaration methodDeclaration = classBuilder.addMethod(fieldPoint.getSimpleName().toString(), Modifier.Keyword.PUBLIC);
                    methodDeclaration.setType(Object.class.getSimpleName());
                    methodDeclaration.addParameter("ProceedingJoinPoint", "joinPoint");
                    methodDeclaration.addThrownException(Throwable.class);

                    NormalAnnotationExpr annotationExpr = new NormalAnnotationExpr();
                    annotationExpr.setName(new Name("Around"));
                    annotationExpr.getPairs().add(new MemberValuePair()
                                                          .setName("value")
                                                          .setValue(getAnnotationValue(bean, fieldPoint)));
                    methodDeclaration.addAnnotation(annotationExpr);

                    methodDeclaration.getBody().ifPresent(body -> {
                        body.addAndGetStatement(new ReturnStmt(new MethodCallExpr("onInvoke")
                                                                       .addArgument("joinPoint")
                                                                       .addArgument(new StringLiteralExpr(fieldPoint.getSimpleName().toString()))
                                                                       .addArgument(new MethodCallExpr(new NameExpr(fieldPoint.getSimpleName().toString()), "get"))));
                    });
                });
    }

    private void addOnInvoke() {
        MethodDeclaration methodDeclaration = classBuilder.addMethod("onInvoke", Modifier.Keyword.PRIVATE);
        methodDeclaration.setType(Object.class.getSimpleName());
        methodDeclaration.addParameter("ProceedingJoinPoint", "joinPoint");
        methodDeclaration.addParameter("String", "fieldName");
        methodDeclaration.addParameter("Instance", "instance");
        methodDeclaration.addThrownException(Throwable.class);

        methodDeclaration.getBody().ifPresent(body -> {
            body.addAndGetStatement(new VariableDeclarationExpr(
                    new ClassOrInterfaceType().setName(Field.class.getSimpleName()), "field"));
            TryStmt ts = new TryStmt();
            body.addAndGetStatement(ts);
            BlockStmt blockStmt = new BlockStmt();
            ts.setTryBlock(blockStmt);

            blockStmt.addAndGetStatement(new AssignExpr().setTarget(new NameExpr("field"))
                                                 .setValue(new MethodCallExpr(new MethodCallExpr(
                                                         new MethodCallExpr(new NameExpr("joinPoint"),
                                                                            "getTarget"),
                                                         "getClass"),
                                                                              "getDeclaredField").addArgument(new NameExpr("fieldName"))));
            ThrowStmt throwStmt = new ThrowStmt(
                    new ObjectCreationExpr().setType(
                            new ClassOrInterfaceType().setName("Error")).addArgument("e"));
            CatchClause catchClause = new CatchClause().setParameter(
                    new Parameter().setType(
                            new ClassOrInterfaceType().setName("NoSuchFieldException"))
                            .setName("e"));
            catchClause.getBody().addAndGetStatement(throwStmt);
            ts.getCatchClauses().add(catchClause);

            body.addAndGetStatement(new MethodCallExpr(new NameExpr("field"), "setAccessible").addArgument("true"));

            IfStmt ifStmt = new IfStmt().setCondition(new BinaryExpr(
                    new MethodCallExpr(
                            new NameExpr("field"), "get").addArgument(new MethodCallExpr(new NameExpr("joinPoint"), "getTarget")),
                    new NullLiteralExpr(), BinaryExpr.Operator.NOT_EQUALS));
            ifStmt.setThenStmt(new ReturnStmt(new MethodCallExpr(new NameExpr("joinPoint"), "proceed")));
            body.addAndGetStatement(ifStmt);

            ts = new TryStmt();
            body.addAndGetStatement(ts);
            blockStmt = new BlockStmt();
            ts.setTryBlock(blockStmt);

            blockStmt.addAndGetStatement(new MethodCallExpr(new NameExpr("field"), "set")
                                                 .addArgument(new MethodCallExpr(new NameExpr("joinPoint"), "getTarget"))
                                                 .addArgument(new MethodCallExpr(new NameExpr("instance"), "get")));
            throwStmt = new ThrowStmt(
                    new ObjectCreationExpr().setType(
                            new ClassOrInterfaceType().setName("Error")).addArgument("e"));
            catchClause = new CatchClause().setParameter(
                    new Parameter().setType(
                            new ClassOrInterfaceType().setName("IllegalAccessException"))
                            .setName("e"));
            catchClause.getBody().addAndGetStatement(throwStmt);
            ts.getCatchClauses().add(catchClause);

            body.addAndGetStatement(new ReturnStmt(new MethodCallExpr(new NameExpr("joinPoint"), "proceed")));
        });
    }

    private void generateFactoryFieldDeclaration(VariableElement fieldPoint) {
        ClassOrInterfaceType supplier = new ClassOrInterfaceType().setName(Supplier.class.getSimpleName());

        ClassOrInterfaceType type = new ClassOrInterfaceType();
        type.setName(Instance.class.getSimpleName());
        type.setTypeArguments(new ClassOrInterfaceType().setName(fieldPoint.asType().toString()));
        supplier.setTypeArguments(type);

        ClassOrInterfaceType beanManager = new ClassOrInterfaceType().setName("BeanManagerImpl");
        MethodCallExpr callForBeanManagerImpl = new MethodCallExpr(beanManager.getNameAsExpression(), "get");

        TypeElement typeElement = MoreTypes.asTypeElement(fieldPoint.asType());
        MethodCallExpr callForProducer = new MethodCallExpr(callForBeanManagerImpl, "lookupBean")
                .addArgument(new FieldAccessExpr(new NameExpr(typeElement.getQualifiedName().toString()), "class"));

        LambdaExpr lambda = new LambdaExpr().setEnclosingParameters(true);
        lambda.setBody(new ExpressionStmt(callForProducer));

        classBuilder.addFieldWithInitializer(supplier, fieldPoint.getSimpleName().toString(), lambda, Modifier.Keyword.PRIVATE);
    }

    private StringLiteralExpr getAnnotationValue(BeanDefinition bean, VariableElement fieldPoint) {
        StringBuffer sb = new StringBuffer();
        sb.append("get(")
                .append("*")
                //.append(fieldPoint.getType())
                .append(" ")
                .append(bean.getQualifiedName())
                .append(".")
                .append(fieldPoint.getSimpleName())
                .append(")");
        return new StringLiteralExpr(sb.toString());
    }
}

