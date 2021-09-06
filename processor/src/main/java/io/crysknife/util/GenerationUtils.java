/*
 * Copyright © 2021 Treblereel
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

package io.crysknife.util;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreTypes;
import io.crysknife.client.Reflect;
import io.crysknife.client.internal.InstanceImpl;
import io.crysknife.client.internal.OnFieldAccessed;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.definition.BeanDefinition;
import io.crysknife.generator.point.FieldPoint;
import jsinterop.base.Js;

import javax.inject.Named;
import javax.inject.Qualifier;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 8/19/21
 */
public class GenerationUtils {

  private final IOCContext context;
  private final TypeMirror qualifier;

  public GenerationUtils(IOCContext context) {
    this.context = context;
    qualifier = context.getGenerationContext().getElements()
        .getTypeElement(Qualifier.class.getCanonicalName()).asType();

  }

  public MethodCallExpr getFieldAccessCallExpr(BeanDefinition beanDefinition,
      VariableElement field) {
    if (context.getGenerationContext().isGwt2()) {
      return new MethodCallExpr(new NameExpr(beanDefinition.getClassName() + "Info"),
          field.getSimpleName().toString()).addArgument("instance");
    }

    return new MethodCallExpr(
        new MethodCallExpr(new NameExpr(Js.class.getSimpleName()), "asPropertyMap")
            .addArgument("instance"),
        "get").addArgument(
            new MethodCallExpr(new NameExpr(Reflect.class.getSimpleName()), "objectProperty")
                .addArgument(new StringLiteralExpr(Utils.getJsFieldName(field)))
                .addArgument("instance"));
  }

  public Expression beanManagerLookupBeanCall(FieldPoint fieldPoint) {
    MethodCallExpr callForProducer = new MethodCallExpr(new NameExpr("beanManager"), "lookupBean")
        .addArgument(new FieldAccessExpr(
            new NameExpr(fieldPoint.getType().getQualifiedName().toString()), "class"));

    maybeAddQualifiers(context, callForProducer, fieldPoint);
    return callForProducer;
  }

  public void maybeAddQualifiers(IOCContext context, MethodCallExpr call, FieldPoint field) {

    String annotationName = null;

    if (field.isNamed()) {
      annotationName = Named.class.getCanonicalName();
    } else if (isQualifier(field) != null) {
      annotationName = isQualifier(field);
    }

    if (annotationName != null) {
      ObjectCreationExpr annotation = new ObjectCreationExpr();
      annotation.setType(new ClassOrInterfaceType().setName(annotationName));
      NodeList<BodyDeclaration<?>> anonymousClassBody = new NodeList<>();

      MethodDeclaration annotationType = new MethodDeclaration();
      annotationType.setModifiers(Modifier.Keyword.PUBLIC);
      annotationType.setName("annotationType");
      annotationType.setType(
          new ClassOrInterfaceType().setName("Class<? extends java.lang.annotation.Annotation>"));
      annotationType.getBody().get()
          .addAndGetStatement(new ReturnStmt(new NameExpr(annotationName + ".class")));
      anonymousClassBody.add(annotationType);

      if (field.isNamed()) {
        MethodDeclaration value = new MethodDeclaration();
        value.setModifiers(Modifier.Keyword.PUBLIC);
        value.setName("value");
        value.setType(new ClassOrInterfaceType().setName("String"));
        value.getBody().get()
            .addAndGetStatement(new ReturnStmt(new StringLiteralExpr(field.getNamed())));
        anonymousClassBody.add(value);
      }

      annotation.setAnonymousClassBody(anonymousClassBody);

      call.addArgument(annotation);


    }
  }


  public String isQualifier(FieldPoint field) {
    for (AnnotationMirror ann : field.getField().getAnnotationMirrors()) {
      for (AnnotationMirror e : context.getGenerationContext().getProcessingEnvironment()
          .getElementUtils()
          .getAllAnnotationMirrors(MoreTypes.asElement(ann.getAnnotationType()))) {
        boolean same =
            context.getGenerationContext().getTypes().isSameType(e.getAnnotationType(), qualifier);
        if (same) {
          return ann.getAnnotationType().toString();
        }
      }
    }
    return null;
  }

  public Expression wrapCallInstanceImpl(ClassBuilder classBuilder, Expression call) {
    classBuilder.getClassCompilationUnit().addImport(InstanceImpl.class);
    LambdaExpr lambda = new LambdaExpr();
    lambda.setEnclosingParameters(true);
    lambda.setBody(new ExpressionStmt(call));

    return new ObjectCreationExpr().setType(InstanceImpl.class).addArgument(lambda);
  }
}