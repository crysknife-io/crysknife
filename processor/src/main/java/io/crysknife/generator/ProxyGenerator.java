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


package io.crysknife.generator;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreTypes;
import io.crysknife.annotation.CircularDependency;
import io.crysknife.annotation.Generator;
import io.crysknife.client.Interceptor;
import io.crysknife.client.internal.CircularDependencyProxy;
import io.crysknife.client.internal.ProxyBeanFactory;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.definition.InjectionParameterDefinition;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.ExecutionEnv;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.util.Utils;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.type.TypeKind;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/30/21
 */
@Generator
public class ProxyGenerator extends ScopedBeanGenerator<BeanDefinition> {

  private static List<String> OBJECT_METHODS = new ArrayList<String>() {
    {
      add("wait");
      add("finalize");
    }
  };
  private MethodDeclaration initDelegate;

  public ProxyGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  public void register() {
    iocContext.register(CircularDependency.class, WiringElementType.CLASS_DECORATOR, this);
  }

  @Override
  public void generate(ClassBuilder builder, BeanDefinition beanDefinition) {
    initDelegate(builder, beanDefinition);

    createInstance(builder, beanDefinition);
    getInstance(builder, beanDefinition);
    setExtendsProxyBeanFactory(builder, beanDefinition);
    addDependantBeanReadyMethod(builder, beanDefinition);
    addDependantClassHolder(builder, beanDefinition);
    generateProxy(builder, beanDefinition);
  }

  private void initDelegate(ClassBuilder builder, BeanDefinition beanDefinition) {
    initDelegate = builder.addMethod("initDelegate", Modifier.Keyword.PUBLIC);
    initDelegate.addParameter(Utils.getSimpleClassName(beanDefinition.getType()), "instance");
  }

  private void getInstance(ClassBuilder builder, BeanDefinition beanDefinition) {
    BlockStmt body = new BlockStmt();
    BlockStmt ifBody = new BlockStmt();
    ifBody.addAndGetStatement(new MethodCallExpr("createInstance"));
    ifBody.addAndGetStatement(new MethodCallExpr("initDelegate"));
    body.addAndGetStatement(new IfStmt().setCondition(
        new BinaryExpr(new NameExpr("instance"), new NullLiteralExpr(), BinaryExpr.Operator.EQUALS))
        .setThenStmt(ifBody));
    body.addAndGetStatement(new ReturnStmt(new NameExpr("instance")));


    // builder.getClassDeclaration().getMethodsByName("getInstance").get(0).setBody(body);
  }

  private void createInstance(ClassBuilder builder, BeanDefinition beanDefinition) {

    initDelegate.getBody().ifPresent(body -> {

      if (iocContext.getGenerationContext().getExecutionEnv().equals(ExecutionEnv.J2CL)) {
        ObjectCreationExpr newInstance = generateNewInstanceCreationExpr(beanDefinition);
        Set<InjectionParameterDefinition> params = beanDefinition.getConstructorParams();
        Iterator<InjectionParameterDefinition> injectionPointDefinitionIterator = params.iterator();
        while (injectionPointDefinitionIterator.hasNext()) {
          InjectableVariableDefinition argument = injectionPointDefinitionIterator.next();
          newInstance.addArgument(
              getFieldAccessorExpression(builder, beanDefinition, argument, "constructor"));
        }
        VariableDeclarationExpr interceptor = new VariableDeclarationExpr(
            new ClassOrInterfaceType().setName(Interceptor.class.getSimpleName()), "interceptor");

        ObjectCreationExpr interceptorCreationExpr = new ObjectCreationExpr();
        interceptorCreationExpr.setType(Interceptor.class.getSimpleName());
        interceptorCreationExpr.addArgument(newInstance);

        body.addAndGetStatement(
            new AssignExpr().setTarget(interceptor).setValue(interceptorCreationExpr));
      }


      body.addAndGetStatement(new VariableDeclarationExpr(new VariableDeclarator()
          .setType(Utils.getSimpleClassName(beanDefinition.getType())).setName("delegate")
          .setInitializer(generateInstanceInitializerNewObjectExpr(builder, beanDefinition))));

      body.addAndGetStatement(new MethodCallExpr(new EnclosedExpr(new CastExpr(
          new ClassOrInterfaceType()
              .setName("Proxy" + Utils.getSimpleClassName(beanDefinition.getType())),
          new NameExpr("instance"))), "setInstance").addArgument("delegate"));

      body.addAndGetStatement(new MethodCallExpr("initInstance").addArgument("delegate"));
      if (!iocContext.getGenerationContext().getExecutionEnv().equals(ExecutionEnv.JRE)) {
        beanDefinition.getFields().forEach(fieldPoint -> {
          Expression expr =
              getFieldAccessorExpression(builder, beanDefinition, fieldPoint, "field");
          body.addStatement(expr);
        });
      }
    });

    String proxyName = "Proxy" + Utils.getSimpleClassName(beanDefinition.getType());

    BlockStmt body = new BlockStmt();

    AssignExpr assignExpr = new AssignExpr().setTarget(
        new VariableDeclarationExpr(new ClassOrInterfaceType().setName(proxyName), "instance"));
    assignExpr.setValue(new ObjectCreationExpr().setType(proxyName));

    body.addAndGetStatement(assignExpr);
    MethodDeclaration existingCreateInstance =
        builder.getClassDeclaration().getMethodsByName("createInstance").get(0);
    existingCreateInstance.setBody(body);
  }

  private void addDependantClassHolder(ClassBuilder builder, BeanDefinition beanDefinition) {
    builder.getClassCompilationUnit().addImport(List.class);
    builder.getClassCompilationUnit().addImport(ArrayList.class);

    ClassOrInterfaceType addDependantClass = new ClassOrInterfaceType();
    addDependantClass.setName(List.class.getCanonicalName());
    addDependantClass
        .setTypeArguments(new ClassOrInterfaceType().setName(List.class.getSimpleName()));

    builder.addFieldWithInitializer(addDependantClass, "dependantBeans",
        new ObjectCreationExpr()
            .setType(new ClassOrInterfaceType().setName(ArrayList.class.getSimpleName())),
        Modifier.Keyword.PRIVATE, Modifier.Keyword.FINAL);


    // builder.addStatementToConstructor(new MethodCallExpr(new
    // NameExpr("dependantBeans"),"add").addArgument())

  }

  private void generateProxy(ClassBuilder builder, BeanDefinition beanDefinition) {
    ClassOrInterfaceDeclaration wrapper = new ClassOrInterfaceDeclaration();
    wrapper.setName(
        "Proxy" + MoreTypes.asTypeElement(beanDefinition.getType()).getSimpleName().toString());
    wrapper.addExtendedType(Utils.getSimpleClassName(beanDefinition.getType()));

    ClassOrInterfaceType implementsCircularDependencyProxy = new ClassOrInterfaceType();
    implementsCircularDependencyProxy.setName(CircularDependencyProxy.class.getCanonicalName());
    implementsCircularDependencyProxy.setTypeArguments(
        new ClassOrInterfaceType().setName(Utils.getSimpleClassName(beanDefinition.getType())));
    wrapper.getImplementedTypes().add(implementsCircularDependencyProxy);

    wrapper.setModifier(com.github.javaparser.ast.Modifier.Keyword.FINAL, true);

    wrapper.addField(
        new ClassOrInterfaceType().setName(Utils.getSimpleClassName(beanDefinition.getType())),
        "instance", Modifier.Keyword.PRIVATE);

    ConstructorDeclaration constructor = wrapper.addConstructor(Modifier.Keyword.PRIVATE);
    MethodCallExpr _super = new MethodCallExpr("super");
    beanDefinition.getConstructorParams().forEach(param -> {
      _super.addArgument(new CastExpr(
          new ClassOrInterfaceType().setName(param.getVariableElement().asType().toString()),
          new NullLiteralExpr()));
    });

    constructor.getBody().addAndGetStatement(_super);

    builder.getClassDeclaration().addMember(wrapper);

    Utils.getAllMethodsIn(elements, MoreTypes.asTypeElement(beanDefinition.getType())).stream()
        .filter(elm -> !elm.getModifiers().contains(javax.lang.model.element.Modifier.STATIC))
        .filter(elm -> !elm.getModifiers().contains(javax.lang.model.element.Modifier.PRIVATE))
        .filter(elm -> !elm.getModifiers().contains(javax.lang.model.element.Modifier.ABSTRACT))
        .filter(elm -> !elm.getModifiers().contains(javax.lang.model.element.Modifier.NATIVE))
        .filter(elm -> !elm.getModifiers().contains(javax.lang.model.element.Modifier.FINAL))
        .filter(elm -> !OBJECT_METHODS.contains(elm.getSimpleName().toString())).forEach(elm -> {
          addMethod(wrapper, beanDefinition, elm);
        });

    addUnwrapMethod(wrapper, beanDefinition);
    addSetInstance(wrapper, beanDefinition);

  }

  private void addSetInstance(ClassOrInterfaceDeclaration wrapper, BeanDefinition beanDefinition) {
    MethodDeclaration setInstance = wrapper.addMethod("setInstance", Modifier.Keyword.PUBLIC);
    setInstance.addAnnotation(Override.class);
    Parameter parameter = new Parameter();
    parameter.setType(Utils.getSimpleClassName(beanDefinition.getType()));
    parameter.setName("delegate");
    setInstance.addParameter(parameter);

    setInstance.getBody().get().addAndGetStatement(
        new AssignExpr().setTarget(new NameExpr("instance")).setValue(new NameExpr("delegate")));
  }

  private void addUnwrapMethod(ClassOrInterfaceDeclaration wrapper, BeanDefinition beanDefinition) {
    MethodDeclaration unwrapMethod = wrapper.addMethod("unwrap", Modifier.Keyword.PUBLIC);
    unwrapMethod.addAnnotation(Override.class);
    unwrapMethod.setType(Utils.getSimpleClassName(beanDefinition.getType()));
    unwrapMethod.getBody().get().addAndGetStatement(new ReturnStmt(new NameExpr("instance")));
  }

  private void addMethod(ClassOrInterfaceDeclaration wrapper, BeanDefinition beanDefinition,
      ExecutableElement elm) {
    List<Modifier.Keyword> modifierList = elm.getModifiers().stream()
        .map(m -> Modifier.Keyword.valueOf(m.name())).collect(Collectors.toList());

    MethodDeclaration methodDeclaration = wrapper.addMethod(elm.getSimpleName().toString(),
        modifierList.toArray(new Modifier.Keyword[modifierList.size()]));

    methodDeclaration.setType(elm.getReturnType().toString());
    MethodCallExpr methodCallExpr =
        new MethodCallExpr(new NameExpr("this.instance"), elm.getSimpleName().toString());


    elm.getParameters().forEach(param -> {
      Parameter parameter = new Parameter();
      String type =
          param.asType().getKind().equals(TypeKind.TYPEVAR) ? "Object" : param.asType().toString();

      parameter.setType(type);
      parameter.setName(param.getSimpleName().toString());

      methodDeclaration.addParameter(parameter);
      methodCallExpr.addArgument(param.getSimpleName().toString());
    });

    ExpressionStmt call = new ExpressionStmt(methodCallExpr);
    if (!elm.getReturnType().toString().equals("void")) {
      methodDeclaration.getBody().get().addAndGetStatement(new ReturnStmt(call.getExpression()));
    } else {
      methodDeclaration.getBody().get().addAndGetStatement(call);

    }
  }

  private void setExtendsProxyBeanFactory(ClassBuilder builder, BeanDefinition beanDefinition) {
    builder.getClassCompilationUnit().addImport(ProxyBeanFactory.class);

    ClassOrInterfaceType factory = new ClassOrInterfaceType();
    factory.setName(ProxyBeanFactory.class.getSimpleName());
    factory.setTypeArguments(
        new ClassOrInterfaceType().setName(Utils.getSimpleClassName(beanDefinition.getType())));
    NodeList<ClassOrInterfaceType> extendsTypes = new NodeList<>();
    extendsTypes.add(factory);

    builder.getClassDeclaration().setExtendedTypes(extendsTypes);
  }

  private void addDependantBeanReadyMethod(ClassBuilder builder, BeanDefinition beanDefinition) {
    MethodDeclaration dependantBeanReadyMethod =
        builder.addMethod("dependantBeanReady", Modifier.Keyword.PUBLIC);
    dependantBeanReadyMethod.addAndGetParameter(Class.class, "clazz");
  }
}
