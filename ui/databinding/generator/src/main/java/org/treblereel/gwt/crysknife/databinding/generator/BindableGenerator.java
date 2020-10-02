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

package org.treblereel.gwt.crysknife.databinding.generator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.lang.model.element.TypeElement;

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
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreTypes;
import org.treblereel.gwt.crysknife.annotation.Generator;
import org.treblereel.gwt.crysknife.databinding.client.BindableProxy;
import org.treblereel.gwt.crysknife.databinding.client.BindableProxyAgent;
import org.treblereel.gwt.crysknife.databinding.client.BindableProxyFactory;
import org.treblereel.gwt.crysknife.databinding.client.BindableProxyProvider;
import org.treblereel.gwt.crysknife.databinding.client.NonExistingPropertyException;
import org.treblereel.gwt.crysknife.databinding.client.PropertyType;
import org.treblereel.gwt.crysknife.databinding.client.api.Bindable;
import org.treblereel.gwt.crysknife.databinding.client.api.DataBinder;
import org.treblereel.gwt.crysknife.generator.ScopedBeanGenerator;
import org.treblereel.gwt.crysknife.generator.WiringElementType;
import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;
import org.treblereel.gwt.crysknife.generator.definition.Definition;
import org.treblereel.gwt.crysknife.generator.point.FieldPoint;

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
    // iocContext.register(Bindable.class, WiringElementType.CLASS_DECORATOR, this); //PARAMETER
    iocContext.register(Inject.class, DataBinder.class, WiringElementType.BEAN, this); // PARAMETER
    TypeElement type = iocContext.getGenerationContext().getElements()
        .getTypeElement(DataBinder.class.getCanonicalName());
    BeanDefinition beanDefinition = iocContext.getBeanDefinitionOrCreateAndReturn(type);
    beanDefinition.setGenerator(this);
    iocContext.getBlacklist().add(DataBinder.class.getCanonicalName());
  }

  @Override
  public void generateBeanFactory(ClassBuilder clazz, Definition definition) {
    if (definition instanceof BeanDefinition) {
      clazz.getClassCompilationUnit().addImport(((BeanDefinition) definition).getType().toString());

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

      BeanDefinition beanDefinition = (BeanDefinition) definition;
      initClassBuilder(clazz, beanDefinition);
      clazz.getImplementedTypes().clear();

      MethodDeclaration methodDeclaration =
          clazz.addMethod("loadBindableProxies", Modifier.Keyword.PRIVATE);

      iocContext.getTypeElementsByAnnotation(Bindable.class.getCanonicalName()).forEach(c -> {
        clazz.getClassCompilationUnit().addImport(c.toString());
        generateBindableProxy(methodDeclaration, MoreTypes.asTypeElement(c.asType()));
      });

      generateFactoryCreateMethod(clazz, beanDefinition);
      generateFactoryForTypeMethod(clazz, beanDefinition);
      write(clazz, beanDefinition, iocContext.getGenerationContext());
    }
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

  private void generateBindableProxy(MethodDeclaration methodDeclaration, TypeElement type) {
    new BindableProxyGenerator(
        iocContext.getGenerationContext().getProcessingEnvironment().getTypeUtils(),
        methodDeclaration, type).generate();
  }

  @Override
  public Expression generateBeanCall(ClassBuilder classBuilder, FieldPoint fieldPoint,
      BeanDefinition beanDefinition) {
    classBuilder.getClassCompilationUnit().addImport(DataBinder.class);
    MoreTypes.asDeclared(fieldPoint.getField().asType()).getTypeArguments();
    return new NameExpr(
        "org.treblereel.gwt.crysknife.databinding.client.api.DataBinder_Factory.get().forType("
            + MoreTypes.asDeclared(fieldPoint.getField().asType()).getTypeArguments().get(0)
            + ".class)");
  }
}
