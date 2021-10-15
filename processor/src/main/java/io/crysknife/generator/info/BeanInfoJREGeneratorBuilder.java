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

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
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
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import io.crysknife.client.BeanManager;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.util.GenerationUtils;
import io.crysknife.util.Utils;
import org.apache.commons.lang3.reflect.FieldUtils;

import javax.enterprise.inject.Instance;
import java.lang.reflect.Field;
import java.util.function.Supplier;

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
    addGetField();
    return classBuilder.toSourceCode();
  }

  private void initClass() {

    classBuilder
        .setClassName(MoreTypes.asTypeElement(bean.getType()).getSimpleName().toString() + "Info");
    classBuilder.getClassCompilationUnit()
        .setPackageDeclaration(Utils.getPackageName(MoreTypes.asTypeElement(bean.getType())));
    classBuilder.getClassDeclaration().getAnnotations()
        .add(new NormalAnnotationExpr().setName(new Name("Aspect")));
    classBuilder.getClassCompilationUnit().addImport("org.aspectj.lang.JoinPoint");
    classBuilder.getClassCompilationUnit().addImport("org.aspectj.lang.annotation.Before");
    classBuilder.getClassCompilationUnit().addImport("org.aspectj.lang.annotation.Aspect");
    classBuilder.getClassCompilationUnit().addImport("io.crysknife.client.BeanManagerImpl");
    classBuilder.getClassCompilationUnit().addImport(Field.class);
    classBuilder.getClassCompilationUnit().addImport(Supplier.class);
    classBuilder.getClassCompilationUnit().addImport(FieldUtils.class);
    classBuilder.getClassCompilationUnit().addImport(Instance.class);
    classBuilder.getClassCompilationUnit().addImport(BeanManager.class);

    classBuilder.addFieldWithInitializer(BeanManager.class.getSimpleName(), "beanManager",
        new MethodCallExpr(new NameExpr(BeanManager.class.getCanonicalName() + "Impl"), "get"),
        Modifier.Keyword.PRIVATE, Modifier.Keyword.FINAL);
  }

  private void addFields() {

    for (InjectableVariableDefinition fieldPoint : bean.getFields()) {
      String methodName =
          fieldPoint.getVariableElement().getEnclosingElement().toString().replaceAll("\\.", "_")
              + "_" + fieldPoint.getVariableElement().getSimpleName();
      boolean isLocal = isLocal(bean, fieldPoint);

      MethodDeclaration methodDeclaration =
          classBuilder.addMethod(methodName, Modifier.Keyword.PUBLIC);
      methodDeclaration.addParameter("JoinPoint", "joinPoint");
      methodDeclaration.addThrownException(NoSuchFieldException.class);
      methodDeclaration.addThrownException(IllegalAccessException.class);

      NormalAnnotationExpr annotationExpr = new NormalAnnotationExpr();
      annotationExpr.setName(new Name("Before"));
      annotationExpr.getPairs()
          .add(new MemberValuePair().setName("value").setValue(getAnnotationValue(fieldPoint)));
      methodDeclaration.addAnnotation(annotationExpr);

      Expression _beanCall;
      if (fieldPoint.getImplementation().isPresent()
          && fieldPoint.getImplementation().get().getIocGenerator().isPresent()) {
        _beanCall = fieldPoint.getImplementation().get().getIocGenerator().get()
            .generateBeanLookupCall(classBuilder, fieldPoint);
      } else if (fieldPoint.getGenerator().isPresent()) {
        _beanCall =
            fieldPoint.getGenerator().get().generateBeanLookupCall(classBuilder, fieldPoint);
      } else {
        String name = generationUtils.getActualQualifiedBeanName(fieldPoint);
        _beanCall = new MethodCallExpr(new NameExpr("beanManager"), "lookupBean")
            .addArgument(new FieldAccessExpr(new NameExpr(name), "class"));
      }

      Expression beanCall = new MethodCallExpr(_beanCall, "getInstance");

      ThrowStmt throwStmt = new ThrowStmt(new ObjectCreationExpr()
          .setType(new ClassOrInterfaceType().setName("Error")).addArgument("e"));

      TryStmt ts = new TryStmt();
      BlockStmt blockStmt = new BlockStmt();

      blockStmt.addAndGetStatement(new AssignExpr()
          .setTarget(new VariableDeclarationExpr(
              new ClassOrInterfaceType().setName(String.class.getSimpleName()), "fieldName"))
          .setValue(
              new StringLiteralExpr(fieldPoint.getVariableElement().getSimpleName().toString())));

      blockStmt.addAndGetStatement(new AssignExpr()
          .setTarget(new VariableDeclarationExpr(
              new ClassOrInterfaceType().setName(Field.class.getSimpleName()), "field"))
          .setValue(new MethodCallExpr("getField")
              .addArgument(new MethodCallExpr(
                  new MethodCallExpr(new NameExpr("joinPoint"), "getTarget"), "getClass"))
              .addArgument(new NameExpr("fieldName"))));

      blockStmt.addAndGetStatement(new MethodCallExpr("onInvoke").addArgument("joinPoint")
          .addArgument("field").addArgument(beanCall));

      CatchClause catchClause1 = new CatchClause().setParameter(new Parameter()
          .setType(new ClassOrInterfaceType().setName("NoSuchFieldException")).setName("e"));
      catchClause1.getBody().addAndGetStatement(throwStmt);

      CatchClause catchClause2 = new CatchClause().setParameter(new Parameter()
          .setType(new ClassOrInterfaceType().setName("IllegalAccessException")).setName("e"));
      catchClause2.getBody().addAndGetStatement(throwStmt);

      ts.getCatchClauses().add(catchClause1);
      ts.getCatchClauses().add(catchClause2);

      ts.setTryBlock(blockStmt);

      methodDeclaration.getBody().ifPresent(body -> {
        body.addAndGetStatement(ts);
      });
    }
  }

  private boolean isLocal(BeanDefinition bean, InjectableVariableDefinition fieldPoint) {
    return bean.getType()
        .equals(MoreElements.asType(fieldPoint.getVariableElement().getEnclosingElement()));
  }

  private StringLiteralExpr getAnnotationValue(InjectableVariableDefinition fieldPoint) {
    StringBuffer sb = new StringBuffer();
    sb.append("get(").append("*").append(" ")
        .append(fieldPoint.getVariableElement().getEnclosingElement()).append(".")
        .append(fieldPoint.getVariableElement().getSimpleName()).append(")");
    return new StringLiteralExpr(sb.toString());
  }

  private void addOnInvoke() {
    MethodDeclaration methodDeclaration =
        classBuilder.addMethod("onInvoke", Modifier.Keyword.PRIVATE);
    methodDeclaration.addParameter("JoinPoint", "joinPoint");
    methodDeclaration.addParameter("Field", "field");
    methodDeclaration.addParameter("Object", "instance");

    methodDeclaration.addThrownException(NoSuchFieldException.class);
    methodDeclaration.addThrownException(IllegalAccessException.class);

    methodDeclaration.getBody().ifPresent(body -> {
      body.addAndGetStatement(
          new MethodCallExpr(new NameExpr("field"), "setAccessible").addArgument("true"));

      BlockStmt thenStmt = new BlockStmt();

      IfStmt ifStmtLocal = new IfStmt().setCondition(new BinaryExpr(
          new MethodCallExpr(new NameExpr("field"), "get")
              .addArgument(new MethodCallExpr(new NameExpr("joinPoint"), "getTarget")),
          new NullLiteralExpr(), BinaryExpr.Operator.EQUALS)).setThenStmt(thenStmt);

      thenStmt.addAndGetStatement(new MethodCallExpr(new NameExpr("field"), "set")
          .addArgument(new MethodCallExpr(new NameExpr("joinPoint"), "getTarget"))
          .addArgument(new NameExpr("instance")));
      body.addAndGetStatement(ifStmtLocal);
    });
  }

  private void addGetField() {

    MethodDeclaration methodDeclaration =
        classBuilder.addMethod("getField", Modifier.Keyword.PRIVATE);
    methodDeclaration.addParameter("Class", "clazz");
    methodDeclaration.addParameter("String", "name");
    methodDeclaration.setType(Field.class);

    methodDeclaration.getBody().ifPresent(body -> {


      ClassOrInterfaceType consumerClassDecloration =
          new ClassOrInterfaceType().setName(Field.class.getCanonicalName());
      VariableDeclarationExpr variableDeclarationExpr =
          new VariableDeclarationExpr(consumerClassDecloration, "field");


      ForEachStmt forStmt = new ForEachStmt();
      forStmt.setVariable(variableDeclarationExpr);
      forStmt.setIterable(
          new MethodCallExpr(new NameExpr("FieldUtils"), "getAllFields").addArgument("clazz"));

      IfStmt ifStmt = new IfStmt().setCondition(
          new MethodCallExpr(new MethodCallExpr(new NameExpr("field"), "getName"), "equals")
              .addArgument("name"));
      ifStmt.setThenStmt(new BlockStmt().addAndGetStatement(new ReturnStmt(new NameExpr("field"))));

      BlockStmt blockStmt = new BlockStmt();
      blockStmt.addAndGetStatement(ifStmt);

      forStmt.setBody(blockStmt);

      body.addAndGetStatement(forStmt);
      ThrowStmt throwStmt = new ThrowStmt(
          new ObjectCreationExpr().setType(new ClassOrInterfaceType().setName("Error"))
              .addArgument("\"Error: no field named '\" + name + \"' at  \" + clazz + \" at \""));
      body.addAndGetStatement(throwStmt);

    });

  }

}
