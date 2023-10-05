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

package io.crysknife.task;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Specializes;
import jakarta.enterprise.inject.Typed;
import jakarta.inject.Named;
import jakarta.inject.Provider;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypesException;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
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
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreTypes;
import io.crysknife.annotation.Application;
import io.crysknife.client.BeanManager;
import io.crysknife.client.internal.AbstractBeanManager;
import io.crysknife.client.internal.BeanFactory;
import io.crysknife.client.internal.InstanceImpl;
import io.crysknife.client.internal.ProducesBeanFactory;
import io.crysknife.client.internal.QualifierUtil;
import io.crysknife.client.internal.SyncBeanDefImpl;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.ProducesBeanDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.util.GenerationUtils;
import io.crysknife.util.TypeUtils;

import static javax.lang.model.element.Modifier.ABSTRACT;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/28/19
 */
// TODO this class must be refactored
public class BeanManagerGeneratorTask implements Task {

  private final IOCContext iocContext;

  private final GenerationUtils generationUtils;

  private final TypeMirror OBJECT;

  private TreeLogger logger;

  public BeanManagerGeneratorTask(IOCContext iocContext, TreeLogger logger) {
    this.iocContext = iocContext;
    this.logger = logger;
    this.generationUtils = new GenerationUtils(iocContext);
    OBJECT = iocContext.getGenerationContext().getElements()
        .getTypeElement(Object.class.getCanonicalName()).asType();
  }

  public void execute() throws UnableToCompleteException {
    try {
      build();
    } catch (IOException e) {
      throw new GenerationException(e);
    }
  }

  private void build() throws IOException {
    try {
      JavaFileObject builderFile = iocContext.getGenerationContext().getProcessingEnvironment()
          .getFiler().createSourceFile(BeanManager.class.getCanonicalName() + "Impl");
      try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
        out.append(new BeanManagerGeneratorBuilder().build().toString());
      }
    } catch (javax.annotation.processing.FilerException e) {
      // we can ignore it
    }
  }

  private class BeanManagerGeneratorBuilder {

    private CompilationUnit clazz = new CompilationUnit();

    private ClassOrInterfaceDeclaration classDeclaration;

    private MethodDeclaration getMethodDeclaration;

    private Set<Expression> qualifiers = new HashSet<>();

    private MethodDeclaration init;

    public CompilationUnit build() {
      initClass();
      addFields();
      initInitMethod();
      addGetInstanceMethod();

      for (Expression qualifier : qualifiers) {
        init.getBody().ifPresent(body -> body.addAndGetStatement(qualifier));

      }

      return clazz;
    }

    private void initClass() {
      clazz.setPackageDeclaration(BeanManager.class.getPackage().getName());
      classDeclaration = clazz.addClass(BeanManager.class.getSimpleName() + "Impl");
      clazz.addImport(Provider.class);
      clazz.addImport(Map.class);
      clazz.addImport(HashMap.class);
      clazz.addImport(Annotation.class);
      clazz.addImport(Instance.class);
      clazz.addImport(InstanceImpl.class);
      clazz.addImport(AbstractBeanManager.class);
      clazz.addImport(SyncBeanDefImpl.class);
      clazz.addImport(BeanFactory.class);
      clazz.addImport(QualifierUtil.class);
      clazz.addImport("io.crysknife.client.internal.QualifierUtil.DEFAULT_ANNOTATION", true, false);
      clazz.addImport("io.crysknife.client.internal.QualifierUtil.SPECIALIZES_ANNOTATION", true,
          false);
      clazz.addImport("io.crysknife.client.internal.SyncBeanDefImpl.Builder", true, false);

      ClassOrInterfaceType factory = new ClassOrInterfaceType();
      factory.setName("AbstractBeanManager");

      classDeclaration.getExtendedTypes().add(factory);
    }

    private void addFields() {
      addBeanInstance();
    }

    private void initInitMethod() {
      init = classDeclaration.addMethod("init", Modifier.Keyword.PRIVATE);
      addBeanManager(init);
      addEventManager(init);


      Set<TypeMirror> processed = new HashSet<>();
      iocContext.getOrderedBeans().stream()
          .filter(
              field -> (MoreTypes.asTypeElement(field).getAnnotation(Application.class) == null))
          .forEach(bean -> {
            TypeMirror erased = iocContext.getGenerationContext().getTypes().erasure(bean);

            if (!processed.contains(bean) && !iocContext.getBuildIn().contains(erased.toString())) {
              processed.add(bean);
              BeanDefinition beanDefinition = iocContext.getBean(erased);

              if (beanDefinition instanceof ProducesBeanDefinition) {
                addProducesBeanDefinition((ProducesBeanDefinition) beanDefinition);
              } else {

                if (isSuitableBeanDefinition(beanDefinition)) {
                  Annotation scope = beanDefinition.getScope();
                  List<TypeMirror> assignableTypes = new ArrayList<>();
                  assignableTypes.add(erased);

                  TypeUtils.getSuperTypes(iocContext.getGenerationContext().getElements(),
                      MoreTypes.asTypeElement(erased)).forEach(spr -> {
                        if (!iocContext.getGenerationContext().getTypes().isSameType(spr.asType(),
                            OBJECT)) {
                          assignableTypes.add(
                              iocContext.getGenerationContext().getTypes().erasure(spr.asType()));
                        }
                      });

                  MethodCallExpr registerCallExpr = new MethodCallExpr("register");

                  Expression builderCallExpr =
                      new ObjectCreationExpr().setType("Builder").addArgument(erased + ".class")
                          .addArgument(scope.annotationType().getCanonicalName() + ".class");

                  builderCallExpr = addAssignableTypes(assignableTypes, builderCallExpr);
                  builderCallExpr =
                      maybeAddQualifierExpression(MoreTypes.asElement(erased), builderCallExpr);
                  builderCallExpr = maybeAddTypedExpression(bean, builderCallExpr);
                  builderCallExpr = maybeAddAlternativeExpression(bean, builderCallExpr);

                  builderCallExpr = new MethodCallExpr(builderCallExpr, "withFactory").addArgument(
                      new ObjectCreationExpr().setType(TypeUtils.getQualifiedFactoryName(erased))
                          .addArgument(new ThisExpr()));

                  builderCallExpr = new MethodCallExpr(builderCallExpr, "build");
                  registerCallExpr.addArgument(builderCallExpr);
                  init.getBody().ifPresent(body -> body.addAndGetStatement(registerCallExpr));
                }
              }
            }
          });
    }

    private Expression maybeAddAlternativeExpression(TypeMirror bean, Expression builderCallExpr) {
      if (MoreTypes.asTypeElement(bean).getAnnotation(Alternative.class) != null) {
        builderCallExpr = new MethodCallExpr(builderCallExpr, "isAlternative");
      }
      return builderCallExpr;
    }

    private Expression maybeAddTypedExpression(TypeMirror bean, Expression builderCallExpr) {
      if (MoreTypes.asTypeElement(bean).getAnnotation(Typed.class) != null) {
        Typed typed = MoreTypes.asTypeElement(bean).getAnnotation(Typed.class);
        MethodCallExpr createTyped =
            new MethodCallExpr(new NameExpr("QualifierUtil"), "createTyped");
        try {
          typed.value();
        } catch (MirroredTypesException types) {
          List<DeclaredType> mirrors = (List<DeclaredType>) types.getTypeMirrors();
          mirrors.forEach(mirror -> createTyped.addArgument(mirror.toString() + ".class"));

          builderCallExpr =
              new MethodCallExpr(builderCallExpr, "withTyped").addArgument(createTyped);
        }
      }
      return builderCallExpr;
    }

    private Expression addAssignableTypes(List<TypeMirror> assignableTypes,
        Expression builderCallExpr) {
      ArrayInitializerExpr withAssignableTypesValues = new ArrayInitializerExpr();
      assignableTypes.forEach(
          type -> withAssignableTypesValues.getValues().add(new NameExpr(type + ".class")));

      ArrayCreationExpr withAssignableTypes = new ArrayCreationExpr();
      withAssignableTypes.setElementType(Class.class);
      withAssignableTypes.setInitializer(withAssignableTypesValues);

      builderCallExpr = new MethodCallExpr(builderCallExpr, "withAssignableTypes")
          .addArgument(withAssignableTypes);
      return builderCallExpr;
    }

    private void addGetInstanceMethod() {
      getMethodDeclaration =
          classDeclaration.addMethod("get", Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);
      getMethodDeclaration.setType(BeanManager.class.getSimpleName());
      addGetBody();
    }

    private void addBeanInstance() {
      ClassOrInterfaceType type = new ClassOrInterfaceType();
      type.setName(BeanManager.class.getSimpleName() + "Impl");
      classDeclaration.addField(type, "instance", Modifier.Keyword.STATIC,
          Modifier.Keyword.PRIVATE);
    }

    private void addBeanManager(MethodDeclaration init) {
      ArrayInitializerExpr withAssignableTypesValues = new ArrayInitializerExpr();
      withAssignableTypesValues.getValues().add(new NameExpr("BeanManager.class"));

      ArrayCreationExpr withAssignableTypes = new ArrayCreationExpr();
      withAssignableTypes.setElementType(Class.class);
      withAssignableTypes.setInitializer(withAssignableTypesValues);


      MethodCallExpr registerCallExpr = new MethodCallExpr("register");

      Expression builderCallExpr =
          new ObjectCreationExpr().setType("Builder").addArgument("BeanManager.class")
              .addArgument("jakarta.enterprise.context.ApplicationScoped.class");

      builderCallExpr = new MethodCallExpr(builderCallExpr, "withAssignableTypes")
          .addArgument(withAssignableTypes);

      builderCallExpr = new MethodCallExpr(builderCallExpr, "withQualifiers")
          .addArgument(new NameExpr("new Annotation[] { DEFAULT_ANNOTATION }"));


      builderCallExpr = new MethodCallExpr(builderCallExpr, "withFactory").addArgument(new NameExpr(
          "new BeanFactory<BeanManager>(this){\n" + "\n" + "                @Override\n"
              + "                public BeanManager getInstance() {\n"
              + "                  return BeanManagerImpl.this;\n" + "                }\n"
              + "              }"));

      builderCallExpr = new MethodCallExpr(builderCallExpr, "build");
      registerCallExpr.addArgument(builderCallExpr);


      init.getBody().ifPresent(body -> body.addAndGetStatement(registerCallExpr));
    }

    private void addEventManager(MethodDeclaration init) {
      String statement =
          "register(new Builder(io.crysknife.client.internal.event.EventManager.class, jakarta.inject.Singleton.class)"
              + ".withAssignableTypes(new Class[] { io.crysknife.client.internal.event.EventManager.class, io.crysknife.client.internal.AbstractEventFactory.class })"
              + ".withFactory(new io.crysknife.client.internal.event.EventManager.EventManagerFactory(this)).build());";
      init.getBody().ifPresent(body -> body.addAndGetStatement(statement));

    }

    private void addProducesBeanDefinition(ProducesBeanDefinition producesBeanDefinition) {
      producesBeanDefinition.getSubtypes().forEach(this::addProducesBeanDefinition);

      TypeMirror erased =
          iocContext.getGenerationContext().getTypes().erasure(producesBeanDefinition.getType());

      Annotation scope = producesBeanDefinition.getScope();
      List<TypeMirror> assignableTypes = new ArrayList<>();
      assignableTypes.add(erased);

      TypeUtils.getSuperTypes(iocContext.getGenerationContext().getElements(),
          MoreTypes.asTypeElement(erased)).forEach(spr -> {
            if (!iocContext.getGenerationContext().getTypes().isSameType(spr.asType(), OBJECT)) {
              assignableTypes
                  .add(iocContext.getGenerationContext().getTypes().erasure(spr.asType()));
            }
          });

      ArrayInitializerExpr withAssignableTypesValues = new ArrayInitializerExpr();
      assignableTypes.forEach(
          type -> withAssignableTypesValues.getValues().add(new NameExpr(type + ".class")));

      MethodCallExpr registerCallExpr = new MethodCallExpr("register");

      Expression builderCallExpr =
          new ObjectCreationExpr().setType("Builder").addArgument(erased + ".class")
              .addArgument(scope.annotationType().getCanonicalName() + ".class");

      builderCallExpr = addAssignableTypes(assignableTypes, builderCallExpr);
      builderCallExpr =
          maybeAddQualifierExpression(producesBeanDefinition.getMethod(), builderCallExpr);

      ClassOrInterfaceType producerType = new ClassOrInterfaceType();
      producerType.setName(ProducesBeanFactory.class.getCanonicalName())
          .setTypeArguments(new ClassOrInterfaceType().setName(erased.toString()));

      ClassOrInterfaceType supplierType =
          new ClassOrInterfaceType().setName(Supplier.class.getCanonicalName())
              .setTypeArguments(new ClassOrInterfaceType().setName(erased.toString()));


      ObjectCreationExpr supplier = new ObjectCreationExpr().setType(supplierType);

      NodeList<BodyDeclaration<?>> supplierClassBody = new NodeList<>();

      MethodDeclaration annotationType = new MethodDeclaration();
      annotationType.setModifiers(Modifier.Keyword.PUBLIC);
      annotationType.setName("get");
      annotationType.setType(new ClassOrInterfaceType().setName(erased.toString()));

      annotationType.getBody()
          .ifPresent(body -> body.addAndGetStatement(new ReturnStmt(new MethodCallExpr(
              new MethodCallExpr(
                  new MethodCallExpr(new FieldAccessExpr(new NameExpr("BeanManagerImpl"), "this"),
                      "lookupBean").addArgument(
                          producesBeanDefinition.getProducer().getQualifiedName().toString()
                              + ".class"),
                  "getInstance"),
              producesBeanDefinition.getMethod().getSimpleName().toString()))));

      supplierClassBody.add(annotationType);
      supplier.setAnonymousClassBody(supplierClassBody);

      ObjectCreationExpr factory = new ObjectCreationExpr().setType(producerType)
          .addArgument(new ThisExpr()).addArgument(supplier);

      builderCallExpr = new MethodCallExpr(builderCallExpr, "withFactory").addArgument(factory);
      builderCallExpr = new MethodCallExpr(builderCallExpr, "build");

      registerCallExpr.addArgument(builderCallExpr);
      init.getBody().ifPresent(body -> body.addAndGetStatement(registerCallExpr));

      String qualifiedName = MoreTypes.asTypeElement(
          iocContext.getGenerationContext().getTypes().erasure(producesBeanDefinition.getType()))
          .getQualifiedName().toString();


      ObjectCreationExpr newInstance = new ObjectCreationExpr();
      newInstance.setType(new ClassOrInterfaceType().setName(InstanceImpl.class.getSimpleName()));

      ObjectCreationExpr provider = new ObjectCreationExpr();
      provider.setType(new ClassOrInterfaceType().setName(Provider.class.getSimpleName()));

      newInstance.addArgument(provider);

      NodeList<BodyDeclaration<?>> anonymousClassBody = new NodeList<>();

      MethodDeclaration get = new MethodDeclaration();
      get.setModifiers(Modifier.Keyword.PUBLIC);
      get.addAnnotation(Override.class);
      get.setName("get");
      get.setType(new ClassOrInterfaceType().setName(qualifiedName));

      String producerClass = producesBeanDefinition.getProducer().toString();

      ClassOrInterfaceType instance =
          new ClassOrInterfaceType().setName(Instance.class.getSimpleName());
      instance.setTypeArguments(new ClassOrInterfaceType().setName(producerClass));

      Expression getNewInstance;

      if (producesBeanDefinition.getMethod().getModifiers()
          .contains(javax.lang.model.element.Modifier.STATIC)) {
        getNewInstance = new MethodCallExpr(
            new NameExpr(TypeUtils.getQualifiedName(producesBeanDefinition.getProducer())),
            producesBeanDefinition.getMethod().getSimpleName().toString());
      } else {
        getNewInstance = new MethodCallExpr(new EnclosedExpr(new CastExpr(
            new ClassOrInterfaceType().setName(producerClass),
            new MethodCallExpr(
                new MethodCallExpr("lookupBean").addArgument(producerClass + ".class"), "get"))),
            producesBeanDefinition.getMethod().getSimpleName().toString());
      }

      if (producesBeanDefinition.isSingleton()) {

        IfStmt ifStmt = new IfStmt().setCondition(new BinaryExpr(new NameExpr("holder"),
            new NullLiteralExpr(), BinaryExpr.Operator.EQUALS));
        BlockStmt blockStmt = new BlockStmt();

        blockStmt.addAndGetStatement(
            new AssignExpr().setTarget(new NameExpr("holder")).setValue(getNewInstance));

        ifStmt.setThenStmt(blockStmt);
        get.getBody().ifPresent(body -> body.addAndGetStatement(ifStmt));
        VariableDeclarator holder =
            new VariableDeclarator(new ClassOrInterfaceType().setName(qualifiedName), "holder");
        FieldDeclaration field = new FieldDeclaration();
        field.getVariables().add(holder);
        anonymousClassBody.add(field);

        get.getBody()
            .ifPresent(body -> body.addAndGetStatement(new ReturnStmt(new NameExpr("holder"))));
      } else {
        get.getBody().ifPresent(body -> body.addAndGetStatement(new ReturnStmt(getNewInstance)));
      }
      anonymousClassBody.add(get);
      provider.setAnonymousClassBody(anonymousClassBody);
    }

    private boolean isSuitableBeanDefinition(BeanDefinition beanDefinition) {
      return MoreTypes.asTypeElement(beanDefinition.getType()).getKind().isClass()
          && !MoreTypes.asTypeElement(beanDefinition.getType()).getModifiers().contains(ABSTRACT)
          && beanDefinition.getIocGenerator().isPresent() && beanDefinition.hasFactory();
    }

    private Expression maybeAddQualifierExpression(Element bean, Expression builderCallExpr) {
      List<AnnotationMirror> qualifiers =
          new ArrayList<>(TypeUtils.getAllElementQualifierAnnotations(iocContext, bean));
      Set<Expression> qualifiersExpression = new HashSet<>();

      qualifiers.forEach(
          type -> qualifiersExpression.add(generationUtils.createQualifierExpression(type)));
      Named named = bean.getAnnotation(Named.class);
      if (named != null) {
        qualifiersExpression.add(new MethodCallExpr(new NameExpr("QualifierUtil"), "createNamed")
            .addArgument(new StringLiteralExpr(bean.getAnnotation(Named.class).value())));
      }

      if (bean.getAnnotation(Specializes.class) != null) {
        qualifiersExpression.add(new NameExpr("SPECIALIZES_ANNOTATION"));
      }

      if (bean.getAnnotation(Default.class) != null) {
        qualifiersExpression.add(new NameExpr("DEFAULT_ANNOTATION"));
      }

      ArrayInitializerExpr withQualifiersValues = new ArrayInitializerExpr();
      qualifiersExpression.forEach(type -> withQualifiersValues.getValues().add(type));
      ArrayCreationExpr withQualifiers = new ArrayCreationExpr();
      withQualifiers.setElementType(Annotation.class);
      withQualifiers.setInitializer(withQualifiersValues);

      if (!qualifiersExpression.isEmpty()) {
        builderCallExpr =
            new MethodCallExpr(builderCallExpr, "withQualifiers").addArgument(withQualifiers);
      }
      return builderCallExpr;
    }

    private void addGetBody() {
      NameExpr instance = new NameExpr("instance");
      IfStmt ifStmt = new IfStmt().setCondition(
          new BinaryExpr(instance, new NullLiteralExpr(), BinaryExpr.Operator.EQUALS));
      BlockStmt blockStmt = new BlockStmt();
      blockStmt.addAndGetStatement(generateInstanceInitializer());
      blockStmt.addAndGetStatement(new MethodCallExpr(instance, "init"));
      ifStmt.setThenStmt(blockStmt);
      getMethodDeclaration.getBody().ifPresent(body -> body.addAndGetStatement(ifStmt));

      getMethodDeclaration.getBody()
          .ifPresent(body -> body.getStatements().add(new ReturnStmt(instance)));
    }

    protected Expression generateInstanceInitializer() {
      ObjectCreationExpr newInstance = new ObjectCreationExpr();
      newInstance
          .setType(new ClassOrInterfaceType().setName(BeanManager.class.getSimpleName() + "Impl"));
      return new AssignExpr().setTarget(new NameExpr("instance")).setValue(newInstance);
    }
  }
}
