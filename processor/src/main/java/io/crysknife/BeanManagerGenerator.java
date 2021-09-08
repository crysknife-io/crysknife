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

package io.crysknife;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import io.crysknife.annotation.Application;
import io.crysknife.client.BeanManager;
import io.crysknife.client.internal.AbstractBeanManager;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.nextstep.BeanProcessor;
import io.crysknife.nextstep.definition.BeanDefinition;
import io.crysknife.nextstep.definition.ProducesBeanDefinition;
import io.crysknife.nextstep.oracle.BeanOracle;
import io.crysknife.util.Utils;

import javax.enterprise.inject.Instance;
import javax.inject.Named;
import javax.inject.Provider;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static javax.lang.model.element.Modifier.ABSTRACT;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/28/19
 */
public class BeanManagerGenerator {

  private final IOCContext iocContext;

  private final BeanProcessor beanProcessor;

  private final BeanOracle oracle;

  BeanManagerGenerator(IOCContext iocContext, BeanProcessor beanProcessor) {
    this.iocContext = iocContext;
    this.beanProcessor = beanProcessor;
    this.oracle = new BeanOracle(iocContext, beanProcessor.getBeans());
  }

  void generate() {
    try {
      build();
    } catch (IOException e) {
      throw new GenerationException(e);
    }
  }

  private void build() throws IOException {
    JavaFileObject builderFile = iocContext.getGenerationContext().getProcessingEnvironment()
        .getFiler().createSourceFile(BeanManager.class.getCanonicalName() + "Impl");
    try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
      out.append(new BeanManagerGeneratorBuilder().build().toString());
    }
  }

  /*
   * private void maybeAddQualifiers(MethodCallExpr call, TypeElement field, String annotationName)
   * { if (annotationName != null) { boolean isNamed = field.getAnnotation(Named.class) != null;
   * annotationName = isNamed ? Named.class.getCanonicalName() : annotationName; ObjectCreationExpr
   * annotation = new ObjectCreationExpr(); annotation.setType(new ClassOrInterfaceType()
   * .setName(isNamed ? Named.class.getCanonicalName() : annotationName));
   * NodeList<BodyDeclaration<?>> anonymousClassBody = new NodeList<>();
   *
   * MethodDeclaration annotationType = new MethodDeclaration();
   * annotationType.setModifiers(Modifier.Keyword.PUBLIC); annotationType.setName("annotationType");
   * annotationType.setType(new ClassOrInterfaceType().setName("Class<? extends Annotation>"));
   * annotationType.getBody().get() .addAndGetStatement(new ReturnStmt(new NameExpr(annotationName +
   * ".class"))); anonymousClassBody.add(annotationType);
   *
   * if (isNamed) { MethodDeclaration value = new MethodDeclaration();
   * value.setModifiers(Modifier.Keyword.PUBLIC); value.setName("value"); value.setType(new
   * ClassOrInterfaceType().setName("String")); String namedValue =
   * (field.getAnnotation(Named.class).value().length() == 0) ? field.getQualifiedName().toString()
   * : field.getAnnotation(Named.class).value();
   *
   * value.getBody().get().addAndGetStatement(new ReturnStmt(new StringLiteralExpr(namedValue)));
   * anonymousClassBody.add(value); }
   *
   * annotation.setAnonymousClassBody(anonymousClassBody);
   *
   * call.addArgument(annotation); } }
   */

  public class BeanManagerGeneratorBuilder {

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
      clazz.addImport(AbstractBeanManager.class);

      ClassOrInterfaceType factory = new ClassOrInterfaceType();
      factory.setName("AbstractBeanManager");

      classDeclaration.getExtendedTypes().add(factory);
    }

    private void addFields() {
      addBeanInstance();
    }

    private void addBeanInstance() {
      ClassOrInterfaceType type = new ClassOrInterfaceType();
      type.setName(BeanManager.class.getSimpleName() + "Impl");
      classDeclaration.addField(type, "instance", Modifier.Keyword.STATIC,
          Modifier.Keyword.PRIVATE);
    }

    private void initInitMethod() {
      init = classDeclaration.addMethod("init", Modifier.Keyword.PRIVATE);

      addBeanManager(init);

      Set<TypeMirror> processed = new HashSet<>();

      iocContext.getOrderedBeans().stream()
          .filter(
              field -> (MoreTypes.asTypeElement(field).getAnnotation(Application.class) == null))
          .forEach(bean -> {
            System.out.println("TOP " + bean);
            if (!processed.contains(bean)) {
              processed.add(bean);
              TypeMirror erased = iocContext.getGenerationContext().getTypes().erasure(bean);
              BeanDefinition beanDefinition = beanProcessor.getBeans().get(erased);

              if (beanDefinition instanceof ProducesBeanDefinition) {

              } else {

                if (isSuitableBeanDefinition(beanDefinition)) {
                  generateInitEntry(init, beanDefinition.getType());
                } else {
                  Optional<BeanDefinition> maybe = oracle.guessDefaultImpl(erased);
                  maybe.ifPresent(candidate -> {
                    generateInitEntry(init, beanDefinition.getType(), candidate.getType());
                  });
                }

                beanDefinition.getSubclasses().forEach(subclass -> {
                  if (MoreTypes.asTypeElement(subclass.getType())
                      .getAnnotation(Named.class) != null) {
                    Named named =
                        MoreTypes.asTypeElement(subclass.getType()).getAnnotation(Named.class);
                    if (!"".equals(named.value())) {
                      if (isSuitableBeanDefinition(subclass)) {
                        addNamedInitEntry(init, beanDefinition.getType(), subclass.getType(),
                            named);
                      }
                    }
                  } else {
                    List<AnnotationMirror> qualifiers = Utils.getAllElementQualifierAnnotations(
                        iocContext, MoreTypes.asTypeElement(subclass.getType()));
                    qualifiers.forEach(qualifier -> {
                      if (isSuitableBeanDefinition(subclass)) {
                        addQualifierInitEntry(init, beanDefinition.getType(), subclass.getType(),
                            qualifier);
                      }
                    });
                  }
                });
              }
            }
          });
    }

    private void addQualifierInitEntry(MethodDeclaration init, TypeMirror type, TypeMirror factory,
        AnnotationMirror qualifier) {

      ObjectCreationExpr annotation = new ObjectCreationExpr();
      annotation
          .setType(new ClassOrInterfaceType().setName(qualifier.getAnnotationType().toString()));

      NodeList<BodyDeclaration<?>> anonymousClassBody = new NodeList<>();

      MethodDeclaration annotationType = new MethodDeclaration();
      annotationType.setModifiers(Modifier.Keyword.PUBLIC);
      annotationType.setName("annotationType");
      annotationType.setType(new ClassOrInterfaceType().setName("Class<? extends Annotation>"));
      annotationType.getBody().get().addAndGetStatement(
          new ReturnStmt(new NameExpr(qualifier.getAnnotationType().toString() + ".class")));
      anonymousClassBody.add(annotationType);

      annotation.setAnonymousClassBody(anonymousClassBody);

      generateInitEntry(init, type, factory, annotation);

    }

    private void addNamedInitEntry(MethodDeclaration init, TypeMirror type, TypeMirror factory,
        Named named) {

      ObjectCreationExpr annotation = new ObjectCreationExpr();
      annotation.setType(new ClassOrInterfaceType().setName(Named.class.getCanonicalName()));

      NodeList<BodyDeclaration<?>> anonymousClassBody = new NodeList<>();

      MethodDeclaration annotationType = new MethodDeclaration();
      annotationType.setModifiers(Modifier.Keyword.PUBLIC);
      annotationType.setName("annotationType");
      annotationType.setType(new ClassOrInterfaceType().setName("Class<? extends Annotation>"));
      annotationType.getBody().get().addAndGetStatement(
          new ReturnStmt(new NameExpr(Named.class.getCanonicalName() + ".class")));
      anonymousClassBody.add(annotationType);

      MethodDeclaration value = new MethodDeclaration();
      value.setModifiers(Modifier.Keyword.PUBLIC);
      value.setName("value");
      value.setType(new ClassOrInterfaceType().setName("String"));
      String namedValue = named.value();

      value.getBody().get().addAndGetStatement(new ReturnStmt(new StringLiteralExpr(namedValue)));
      anonymousClassBody.add(value);

      annotation.setAnonymousClassBody(anonymousClassBody);

      generateInitEntry(init, type, factory, annotation);

    }

    private void addBeanManager(MethodDeclaration init) {
      MethodCallExpr theCall = new MethodCallExpr(new ThisExpr(), "register")
          .addArgument(new NameExpr("io.crysknife.client.BeanManager.class"))
          .addArgument(new NameExpr("() -> this"));
      init.getBody().ifPresent(body -> body.addAndGetStatement(theCall));
    }

    private boolean isSuitableBeanDefinition(BeanDefinition beanDefinition) {
      return MoreTypes.asTypeElement(beanDefinition.getType()).getKind().isClass()
          && !MoreTypes.asTypeElement(beanDefinition.getType()).getModifiers().contains(ABSTRACT)
          && beanDefinition.getIocGenerator().isPresent();
    }

    private void generateInitEntry(MethodDeclaration init, TypeMirror field) {
      generateInitEntry(init, field, field, null);
    }

    private void generateInitEntry(MethodDeclaration init, TypeMirror field, TypeMirror factory) {
      generateInitEntry(init, field, factory, null);
    }

    private void generateInitEntry(MethodDeclaration init, TypeMirror field, TypeMirror factory,
        Expression annotation) {
      String qualifiedName =
          MoreTypes.asTypeElement(iocContext.getGenerationContext().getTypes().erasure(field))
              .getQualifiedName().toString();
      String factoryQualifiedName =
          MoreTypes.asTypeElement(iocContext.getGenerationContext().getTypes().erasure(factory))
              .getQualifiedName().toString();

      if (!iocContext.getBlacklist().contains(qualifiedName)) {


        Expression factoryCreationExpr;

        if (iocContext.getGenerationContext().getTypes().isSubtype(field, factory)) {
          factoryCreationExpr = new ObjectCreationExpr()
              .setType(new ClassOrInterfaceType().setName(Utils.getQualifiedFactoryName(factory)))
              .addArgument(new ThisExpr());

          MethodCallExpr call = new MethodCallExpr(new ThisExpr(), "register")
              .addArgument(new FieldAccessExpr(new NameExpr(qualifiedName), "class"))
              .addArgument(factoryCreationExpr);


          if (annotation != null) {
            call.addArgument(annotation);
          }
          init.getBody().ifPresent(body -> body.addAndGetStatement(call));

        } else {
          /*
           * factoryCreationExpr = new LambdaExpr().setEnclosingParameters(true).setBody(new
           * ExpressionStmt( new MethodCallExpr("lookupBean").addArgument(factoryQualifiedName +
           * ".class")));
           */
          Expression temp =
              new MethodCallExpr("lookupBean").addArgument(factoryQualifiedName + ".class");

          MethodCallExpr call = new MethodCallExpr(new ThisExpr(), "register")
              .addArgument(new FieldAccessExpr(new NameExpr(qualifiedName), "class"))
              .addArgument(temp);
          if (annotation != null) {
            call.addArgument(annotation);
          }
          qualifiers.add(call);

        }


        /*
         * if (annotation != null) { call.addArgument(annotation); } init.getBody().ifPresent(body
         * -> body.addAndGetStatement(call));
         */
      }
    }

    private void addGetInstanceMethod() {
      getMethodDeclaration =
          classDeclaration.addMethod("get", Modifier.Keyword.PUBLIC, Modifier.Keyword.STATIC);
      getMethodDeclaration.setType(BeanManager.class.getSimpleName());
      addGetBody();
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
