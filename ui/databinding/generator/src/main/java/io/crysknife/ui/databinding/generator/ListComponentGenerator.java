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

package io.crysknife.ui.databinding.generator;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreTypes;
import io.crysknife.annotation.Generator;
import io.crysknife.client.internal.InstanceImpl;
import io.crysknife.definition.Definition;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.generator.BeanIOCGenerator;
import io.crysknife.generator.WiringElementType;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.ui.databinding.client.components.DefaultListComponent;
import io.crysknife.ui.databinding.client.components.ListComponent;
import io.crysknife.ui.databinding.client.components.ListComponentProvider;
import io.crysknife.ui.databinding.client.components.ListContainer;

import javax.enterprise.inject.Default;
import javax.inject.Inject;
import javax.inject.Named;
import java.lang.annotation.Annotation;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 8/27/21
 */
@Generator(priority = 100000)
public class ListComponentGenerator extends BeanIOCGenerator {

  public ListComponentGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  public void register() {
    iocContext.register(Inject.class, ListComponent.class, WiringElementType.FIELD_TYPE, this);
  }

  @Override
  public void generate(ClassBuilder clazz, Definition beanDefinition) {

  }

  @Override
  public Expression generateBeanLookupCall(ClassBuilder classBuilder,
      InjectableVariableDefinition fieldPoint) {
    classBuilder.getClassCompilationUnit().addImport(DefaultListComponent.class);
    classBuilder.getClassCompilationUnit().addImport(ListComponentProvider.class);

    ArrayCreationExpr types = new ArrayCreationExpr().setElementType("Class[]");
    types.getInitializer().ifPresent(init -> {
      MoreTypes.asDeclared(fieldPoint.getVariableElement().asType()).getTypeArguments()
          .forEach(typeArguments -> {
            init.getValues()
                .add(new FieldAccessExpr(new NameExpr(typeArguments.toString()), "class"));
          });
    });

    ArrayCreationExpr annotations =
        new ArrayCreationExpr().setElementType("java.lang.annotation.Annotation[]");

    annotations.getInitializer().ifPresent(init -> {
      init.getValues().add(addDefaultAnnotation());
      maybeAddListContainerAnnotation(init.getValues(), fieldPoint);
    });


    MethodCallExpr instance = new MethodCallExpr(new ObjectCreationExpr()
        .setType(ListComponentProvider.class).addArgument(new NameExpr("beanManager")), "provide")
            .addArgument(types).addArgument(annotations);
    return new ObjectCreationExpr().setType(InstanceImpl.class).addArgument(instance);
  }

  private void maybeAddListContainerAnnotation(NodeList<Expression> annotations,
      InjectableVariableDefinition fieldPoint) {

    if (fieldPoint.getVariableElement().getAnnotation(ListContainer.class) != null) {
      ObjectCreationExpr annotation = new ObjectCreationExpr();
      annotation
          .setType(new ClassOrInterfaceType().setName(ListContainer.class.getCanonicalName()));
      NodeList<BodyDeclaration<?>> anonymousClassBody = new NodeList<>();

      MethodDeclaration annotationType = new MethodDeclaration();
      annotationType.setModifiers(Modifier.Keyword.PUBLIC);
      annotationType.addAnnotation(Override.class);
      annotationType.setName("annotationType");
      annotationType.setType(
          new ClassOrInterfaceType().setName("Class<? extends java.lang.annotation.Annotation>"));
      annotationType.getBody().get().addAndGetStatement(
          new ReturnStmt(new NameExpr(ListContainer.class.getCanonicalName() + ".class")));
      anonymousClassBody.add(annotationType);

      MethodDeclaration value = new MethodDeclaration();
      value.setModifiers(Modifier.Keyword.PUBLIC);
      value.addAnnotation(Override.class);
      value.setName("value");
      value.setType(new ClassOrInterfaceType().setName("String"));
      value.getBody().get().addAndGetStatement(new ReturnStmt(new StringLiteralExpr(
          fieldPoint.getVariableElement().getAnnotation(ListContainer.class).value())));
      anonymousClassBody.add(value);

      annotation.setAnonymousClassBody(anonymousClassBody);

      annotations.add(annotation);
    }
  }

  private ObjectCreationExpr addDefaultAnnotation() {
    ObjectCreationExpr annotation = new ObjectCreationExpr();
    annotation.setType(new ClassOrInterfaceType().setName(Default.class.getCanonicalName()));
    NodeList<BodyDeclaration<?>> anonymousClassBody = new NodeList<>();

    MethodDeclaration annotationType = new MethodDeclaration();
    annotationType.setModifiers(Modifier.Keyword.PUBLIC);
    annotationType.addAnnotation(Override.class);
    annotationType.setName("annotationType");
    annotationType.setType(
        new ClassOrInterfaceType().setName("Class<? extends java.lang.annotation.Annotation>"));
    annotationType.getBody().get().addAndGetStatement(
        new ReturnStmt(new NameExpr(Default.class.getCanonicalName() + ".class")));
    anonymousClassBody.add(annotationType);

    annotation.setAnonymousClassBody(anonymousClassBody);
    return annotation;
  }

}
