/*
 * Copyright © 2020 Treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.crysknife.generator.info;

import java.lang.reflect.Field;
import java.util.function.Supplier;

import javax.enterprise.inject.Instance;
import javax.lang.model.element.TypeElement;

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
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.definition.BeanDefinition;
import io.crysknife.generator.point.FieldPoint;
import io.crysknife.util.GenerationUtils;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
public class BeanInfoJREGeneratorBuilder extends AbstractBeanInfoGenerator {

  private BeanDefinition bean;
  private ClassBuilder classBuilder;
  private GenerationUtils generationUtils;

  BeanInfoJREGeneratorBuilder(IOCContext iocContext) {
    super(iocContext);
    this.generationUtils = new GenerationUtils(iocContext);
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
    classBuilder.setClassName(bean.getClassName() + "Info");
    classBuilder.getClassCompilationUnit().setPackageDeclaration(bean.getPackageName());
    classBuilder.getClassDeclaration().getAnnotations()
        .add(new NormalAnnotationExpr().setName(new Name("Aspect")));
    classBuilder.getClassCompilationUnit().addImport("org.aspectj.lang.ProceedingJoinPoint");
    classBuilder.getClassCompilationUnit().addImport("org.aspectj.lang.annotation.Around");
    classBuilder.getClassCompilationUnit().addImport("org.aspectj.lang.annotation.Aspect");
    classBuilder.getClassCompilationUnit().addImport("io.crysknife.client.BeanManagerImpl");
    classBuilder.getClassCompilationUnit().addImport(Field.class);
    classBuilder.getClassCompilationUnit().addImport(Supplier.class);
    classBuilder.getClassCompilationUnit().addImport(Instance.class);
  }

  private void addFields() {
    for (FieldPoint fieldPoint : bean.getFieldInjectionPoints()) {
      generateFactoryFieldDeclaration(fieldPoint);
      MethodDeclaration methodDeclaration =
          classBuilder.addMethod(fieldPoint.getName(), Modifier.Keyword.PUBLIC);
      methodDeclaration.setType(Object.class.getSimpleName());
      methodDeclaration.addParameter("ProceedingJoinPoint", "joinPoint");
      methodDeclaration.addThrownException(Throwable.class);

      NormalAnnotationExpr annotationExpr = new NormalAnnotationExpr();
      annotationExpr.setName(new Name("Around"));
      annotationExpr.getPairs().add(
          new MemberValuePair().setName("value").setValue(getAnnotationValue(bean, fieldPoint)));
      methodDeclaration.addAnnotation(annotationExpr);

      methodDeclaration.getBody().ifPresent(body -> {
        body.addAndGetStatement(new ReturnStmt(new MethodCallExpr("onInvoke")
            .addArgument("joinPoint").addArgument(new StringLiteralExpr(fieldPoint.getName()))
            .addArgument(new MethodCallExpr(new NameExpr(fieldPoint.getName()), "get"))));
      });
    }
  }

  private void addOnInvoke() {
    MethodDeclaration methodDeclaration =
        classBuilder.addMethod("onInvoke", Modifier.Keyword.PRIVATE);
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
          .setValue(new MethodCallExpr(
              new MethodCallExpr(new MethodCallExpr(new NameExpr("joinPoint"), "getTarget"),
                  "getClass"),
              "getDeclaredField").addArgument(new NameExpr("fieldName"))));
      ThrowStmt throwStmt = new ThrowStmt(new ObjectCreationExpr()
          .setType(new ClassOrInterfaceType().setName("Error")).addArgument("e"));
      CatchClause catchClause = new CatchClause().setParameter(new Parameter()
          .setType(new ClassOrInterfaceType().setName("NoSuchFieldException")).setName("e"));
      catchClause.getBody().addAndGetStatement(throwStmt);
      ts.getCatchClauses().add(catchClause);

      body.addAndGetStatement(
          new MethodCallExpr(new NameExpr("field"), "setAccessible").addArgument("true"));

      IfStmt ifStmt = new IfStmt().setCondition(new BinaryExpr(
          new MethodCallExpr(new NameExpr("field"), "get")
              .addArgument(new MethodCallExpr(new NameExpr("joinPoint"), "getTarget")),
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
      throwStmt = new ThrowStmt(new ObjectCreationExpr()
          .setType(new ClassOrInterfaceType().setName("Error")).addArgument("e"));
      catchClause = new CatchClause().setParameter(new Parameter()
          .setType(new ClassOrInterfaceType().setName("IllegalAccessException")).setName("e"));
      catchClause.getBody().addAndGetStatement(throwStmt);
      ts.getCatchClauses().add(catchClause);

      body.addAndGetStatement(
          new ReturnStmt(new MethodCallExpr(new NameExpr("joinPoint"), "proceed")));
    });
  }

  private void generateFactoryFieldDeclaration(FieldPoint fieldPoint) {
    ClassOrInterfaceType supplier =
        new ClassOrInterfaceType().setName(Supplier.class.getSimpleName());

    ClassOrInterfaceType type = new ClassOrInterfaceType();
    type.setName(Instance.class.getSimpleName());
    type.setTypeArguments(
        new ClassOrInterfaceType().setName(fieldPoint.getType().getQualifiedName().toString()));
    supplier.setTypeArguments(type);

    ClassOrInterfaceType beanManager = new ClassOrInterfaceType().setName("BeanManagerImpl");
    MethodCallExpr callForBeanManagerImpl =
        new MethodCallExpr(beanManager.getNameAsExpression(), "get");

    MethodCallExpr callForProducer =
        new MethodCallExpr(callForBeanManagerImpl, "lookupBean").addArgument(new FieldAccessExpr(
            new NameExpr(fieldPoint.getType().getQualifiedName().toString()), "class"));

    generationUtils.maybeAddQualifiers(iocContext, callForProducer, fieldPoint);


    LambdaExpr lambda = new LambdaExpr().setEnclosingParameters(true);
    lambda.setBody(new ExpressionStmt(callForProducer));

    classBuilder.addFieldWithInitializer(supplier, fieldPoint.getName(), lambda,
        Modifier.Keyword.PRIVATE);
  }

  private StringLiteralExpr getAnnotationValue(BeanDefinition bean, FieldPoint fieldPoint) {
    StringBuffer sb = new StringBuffer();
    sb.append("get(").append("*")
        // .append(fieldPoint.getType())
        .append(" ").append(bean.getQualifiedName()).append(".").append(fieldPoint.getName())
        .append(")");
    return new StringLiteralExpr(sb.toString());
  }
}
