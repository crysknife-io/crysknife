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

package io.crysknife.ui.databinding.generator;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreTypes;
import io.crysknife.annotation.Generator;
import io.crysknife.client.BeanManager;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.ScopedBeanGenerator;
import io.crysknife.generator.WiringElementType;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.ui.databinding.client.BindableProxy;
import io.crysknife.ui.databinding.client.BindableProxyAgent;
import io.crysknife.ui.databinding.client.BindableProxyFactory;
import io.crysknife.ui.databinding.client.BindableProxyProvider;
import io.crysknife.ui.databinding.client.NonExistingPropertyException;
import io.crysknife.ui.databinding.client.PropertyType;
import io.crysknife.ui.databinding.client.api.Bindable;
import io.crysknife.ui.databinding.client.api.Convert;
import io.crysknife.ui.databinding.client.api.Converter;
import io.crysknife.ui.databinding.client.api.DataBinder;
import io.crysknife.ui.databinding.client.api.DefaultConverter;
import io.crysknife.util.Utils;

import javax.inject.Inject;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/7/19
 */
@Generator(priority = 100002)
public class BindableGenerator extends ScopedBeanGenerator {

  public BindableGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  public void register() {
    iocContext.register(Inject.class, DataBinder.class, WiringElementType.BEAN, this); // PARAMETER
  }

  @Override
  public Expression generateBeanLookupCall(ClassBuilder classBuilder,
      InjectableVariableDefinition fieldPoint) {
    classBuilder.getClassCompilationUnit().addImport(DataBinder.class);
    return generationUtils.wrapCallInstanceImpl(classBuilder, new MethodCallExpr(
        new MethodCallExpr(
            new NameExpr("io.crysknife.ui.databinding.client.api.DataBinder_Factory"), "get"),
        "forType")
            .addArgument(new NameExpr(iocContext
                .getGenerationContext().getTypes().erasure(MoreTypes
                    .asDeclared(fieldPoint.getVariableElement().asType()).getTypeArguments().get(0))
                + ".class")));
  }

  @Override
  public void generate(ClassBuilder clazz, BeanDefinition beanDefinition)
      throws GenerationException {
    clazz.getClassCompilationUnit().addImport((beanDefinition).getType().toString());

    clazz.getClassCompilationUnit().addImport(DataBinder.class);
    clazz.getClassCompilationUnit().addImport(Collections.class);
    clazz.getClassCompilationUnit().addImport(HashMap.class);
    clazz.getClassCompilationUnit().addImport(Map.class);
    clazz.getClassCompilationUnit().addImport(PropertyType.class);
    clazz.getClassCompilationUnit().addImport(NonExistingPropertyException.class);
    clazz.getClassCompilationUnit().addImport(BindableProxyAgent.class);
    clazz.getClassCompilationUnit().addImport(BindableProxy.class);
    clazz.getClassCompilationUnit().addImport(BindableProxyProvider.class);
    clazz.getClassCompilationUnit().addImport(BindableProxyFactory.class);

    initClassBuilder(clazz, beanDefinition);
    clazz.getImplementedTypes().clear();

    MethodDeclaration methodDeclaration =
        clazz.addMethod("loadBindableProxies", Modifier.Keyword.PRIVATE);

    Set<UnableToCompleteException> errors = new HashSet<>();

    iocContext.getTypeElementsByAnnotation(Bindable.class.getCanonicalName()).forEach(c -> {
      clazz.getClassCompilationUnit().addImport(c.toString());
      try {
        generateBindableProxy(methodDeclaration, MoreTypes.asTypeElement(c.asType()));
      } catch (UnableToCompleteException e) {
        errors.add(e);
      }
    });

    if (!errors.isEmpty()) {
      printErrors(errors);
      throw new GenerationException(
          "at " + beanDefinition.getType() + " during Databining generation");
    }

    maybeAddDefaultConverters(clazz, methodDeclaration);
    generateFactoryCreateMethod(clazz, beanDefinition);
    generateFactoryForTypeMethod(clazz, beanDefinition);
    // TODO
    clazz.addConstructorDeclaration()
        .addParameter(new ClassOrInterfaceType().setName(BeanManager.class.getCanonicalName()),
            "beanManager")
        .getBody().addAndGetStatement(new MethodCallExpr("super").addArgument("beanManager"));

    MethodDeclaration getMethodDeclaration =
        clazz.addMethod("getInstance", Modifier.Keyword.PUBLIC);

    getMethodDeclaration.addAnnotation(Override.class);
    getMethodDeclaration.setType(Utils.getSimpleClassName(clazz.beanDefinition.getType()));
    getMethodDeclaration.getBody().get().addAndGetStatement(new ReturnStmt(new NullLiteralExpr()));

    write(clazz, beanDefinition, iocContext.getGenerationContext());
  }

  private void maybeAddDefaultConverters(ClassBuilder clazz, MethodDeclaration methodDeclaration) {
    TypeMirror converter =
        iocContext.getGenerationContext().getTypes().erasure(iocContext.getGenerationContext()
            .getElements().getTypeElement(Converter.class.getCanonicalName()).asType());

    iocContext.getTypeElementsByAnnotation(DefaultConverter.class.getCanonicalName()).stream()
        .filter(type -> iocContext.getGenerationContext().getTypes().isAssignable(type.asType(),
            converter))
        .collect(Collectors.toSet()).forEach(con -> {
          con.getInterfaces().forEach(i -> {
            if (iocContext.getGenerationContext().getTypes().isSameType(converter,
                iocContext.getGenerationContext().getTypes().erasure(i))) {
              methodDeclaration.getBody().ifPresent(body -> {
                body.addAndGetStatement(new ExpressionStmt(
                    new MethodCallExpr(new NameExpr(Convert.class.getCanonicalName()),
                        "registerDefaultConverter")
                            .addArgument(new NameExpr(
                                MoreTypes.asDeclared(i).getTypeArguments().get(0).toString()
                                    + ".class"))
                            .addArgument(new NameExpr(
                                MoreTypes.asDeclared(i).getTypeArguments().get(1).toString()
                                    + ".class"))
                            .addArgument(new ObjectCreationExpr()
                                .setType(con.getQualifiedName().toString()))));
              });
            }
          });
        });
  }

  public void generateFactoryCreateMethod(ClassBuilder classBuilder,
      BeanDefinition beanDefinition) {
    MethodDeclaration getMethodDeclaration =
        classBuilder.addMethod("get", Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);
    getMethodDeclaration.setType("DataBinder_Factory");
    classBuilder.setGetMethodDeclaration(getMethodDeclaration);

    BlockStmt body = classBuilder.getGetMethodDeclaration().getBody().get();
    IfStmt ifStmt = new IfStmt().setCondition(new BinaryExpr(new NameExpr("instance"),
        new NullLiteralExpr(), BinaryExpr.Operator.EQUALS));
    ObjectCreationExpr newInstance = new ObjectCreationExpr();
    newInstance.setType(new ClassOrInterfaceType().setName("DataBinder_Factory"));
    newInstance.addArgument(new NullLiteralExpr());

    BlockStmt initialization = new BlockStmt();
    initialization.addAndGetStatement(
        new AssignExpr().setTarget(new NameExpr("instance")).setValue(newInstance));
    initialization
        .addAndGetStatement(new MethodCallExpr(new NameExpr("instance"), "loadBindableProxies"));
    ifStmt.setThenStmt(initialization);
    body.addAndGetStatement(ifStmt);
    body.addAndGetStatement(new ReturnStmt(new NameExpr("instance")));
    classBuilder.addField("DataBinder_Factory", "instance", Modifier.Keyword.PRIVATE,
        Modifier.Keyword.STATIC);

    classBuilder.addMethod("initInstance", Modifier.Keyword.PUBLIC)
        .addParameter("DataBinder", "fake").addAnnotation(Override.class);
  }

  public void generateFactoryForTypeMethod(ClassBuilder classBuilder,
      BeanDefinition beanDefinition) {
    MethodDeclaration forTypMethodDeclaration =
        classBuilder.addMethod("forType", Modifier.Keyword.PUBLIC);
    forTypMethodDeclaration.addParameter("Class", "modelType");

    forTypMethodDeclaration.setType(DataBinder.class);
    forTypMethodDeclaration.getBody().get().addAndGetStatement(new ReturnStmt(
        new MethodCallExpr(new NameExpr("DataBinder"), "forType").addArgument("modelType")));
  }

  private void generateBindableProxy(MethodDeclaration methodDeclaration, TypeElement type)
      throws UnableToCompleteException {
    new BindableProxyGenerator(
        iocContext.getGenerationContext().getProcessingEnvironment().getElementUtils(),
        iocContext.getGenerationContext().getProcessingEnvironment().getTypeUtils(),
        methodDeclaration, type).generate();
  }

  private void printErrors(Set<UnableToCompleteException> errors) {

    errors.forEach(error -> {
      if (error.errors != null) {
        printErrors(error.errors);
      } else if (error.getMessage() != null) {
        System.out.println("Error: " + error.getMessage());
        iocContext.getGenerationContext().getProcessingEnvironment().getMessager()
            .printMessage(Diagnostic.Kind.ERROR, error.getMessage());
      }
    });
  }

}
