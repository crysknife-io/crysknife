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

package io.crysknife.task;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
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
import io.crysknife.client.InstanceFactory;
import io.crysknife.client.ioc.ContextualTypeProvider;
import io.crysknife.client.ioc.IOCProvider;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.DependentGenerator;
import io.crysknife.generator.IOCGenerator;
import io.crysknife.generator.SingletonGenerator;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.GenerationContext;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.util.Utils;
import io.crysknife.validation.Check;
import io.crysknife.validation.Validator;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 11/5/21
 */
public class IOCProviderTask implements Task {

  private IOCContext context;
  private TreeLogger logger;
  private TypeMirror contextualTypeProvider;
  private Validator validator;

  public IOCProviderTask(IOCContext context, TreeLogger logger) {
    this.context = context;
    this.logger = logger;
    this.contextualTypeProvider = context.getGenerationContext().getTypes()
        .erasure(context.getTypeMirror(ContextualTypeProvider.class));

    validator = new ProviderValidator(context);
  }

  @Override
  public void execute() throws UnableToCompleteException {
    for (TypeElement typeElement : context
        .getTypeElementsByAnnotation(IOCProvider.class.getCanonicalName())) {
      process(typeElement);
    }
  }

  private void process(TypeElement type) throws UnableToCompleteException {
    for (TypeMirror iface : type.getInterfaces()) {
      if (context.getGenerationContext().getTypes().isSameType(contextualTypeProvider,
          context.getGenerationContext().getTypes().erasure(iface))) {
        DeclaredType asDeclaredType = (DeclaredType) iface;
        TypeMirror provided = asDeclaredType.getTypeArguments().get(0);
        TypeMirror erased = context.getGenerationContext().getTypes().erasure(provided);
        validator.validate(type);

        logger.log(TreeLogger.Type.INFO, String.format("registered @IOCProvider for %s", erased));

        boolean isSingleton = Utils.containsAnnotation(type, Singleton.class.getCanonicalName(),
            ApplicationScoped.class.getCanonicalName());

        BeanDefinition beanDefinitionContextualTypeProvider =
            context.getBeanDefinitionOrCreateAndReturn(type.asType());
        if (isSingleton) {
          beanDefinitionContextualTypeProvider.setIocGenerator(new SingletonGenerator(context) {

            @Override
            public void write(ClassBuilder clazz, BeanDefinition beanDefinition,
                GenerationContext context) {
              addProxy(clazz, beanDefinition, erased);
              super.write(clazz, beanDefinition, iocContext.getGenerationContext());
            }
          });
        } else {
          beanDefinitionContextualTypeProvider.setIocGenerator(new DependentGenerator(context));
        }

        BeanDefinition beanDefinition = context.getBeanDefinitionOrCreateAndReturn(erased);
        beanDefinition.setHasFactory(false);
        beanDefinition
            .setIocGenerator(new ProviderStatelessIOCGenerator(context, type, erased, isSingleton));
      }
    }
  }

  private void addProxy(ClassBuilder clazz, BeanDefinition beanDefinition, TypeMirror erased) {
    ClassOrInterfaceDeclaration wrapper = new ClassOrInterfaceDeclaration();
    wrapper.setName(MoreTypes.asTypeElement(beanDefinition.getType()).getSimpleName().toString());
    wrapper.addExtendedType(beanDefinition.getType().toString());
    wrapper.setModifier(com.github.javaparser.ast.Modifier.Keyword.FINAL, true);
    clazz.getClassDeclaration().addMember(wrapper);

    wrapper.addField(erased.toString(), "instance",
        com.github.javaparser.ast.Modifier.Keyword.PRIVATE);

    MethodDeclaration provide =
        wrapper.addMethod("provide", com.github.javaparser.ast.Modifier.Keyword.PUBLIC);
    provide.addAnnotation(Override.class);
    provide.setType(erased.toString());
    provide.addParameter(new Parameter().setType("Class<?>[]").setName("typeargs"));
    provide.addParameter(
        new Parameter().setType("java.lang.annotation.Annotation[]").setName("qualifiers"));

    IfStmt ifStmt = new IfStmt().setCondition(new BinaryExpr(new NameExpr("instance"),
        new NullLiteralExpr(), BinaryExpr.Operator.EQUALS));
    BlockStmt blockStmt = new BlockStmt();

    blockStmt.addAndGetStatement(new AssignExpr().setTarget(new NameExpr("instance"))
        .setValue(new MethodCallExpr(new NameExpr("super"), "provide").addArgument("typeargs")
            .addArgument("qualifiers")));
    ifStmt.setThenStmt(blockStmt);

    provide.getBody().get().addAndGetStatement(ifStmt);
    provide.getBody().get().addAndGetStatement(new ReturnStmt("instance"));
  }

  private static class ProviderStatelessIOCGenerator
      extends IOCGenerator<io.crysknife.definition.BeanDefinition> {

    private final TypeElement type;
    private final TypeMirror erased;

    public ProviderStatelessIOCGenerator(IOCContext iocContext, TypeElement type, TypeMirror erased,
        boolean isSingleton) {
      super(iocContext);
      this.type = type;
      this.erased = erased;
    }

    @Override
    public void register() {

    }

    @Override
    public void generate(ClassBuilder clazz,
        io.crysknife.definition.BeanDefinition beanDefinition) {}

    @Override
    public Expression generateBeanLookupCall(ClassBuilder clazz,
        InjectableVariableDefinition fieldPoint) {
      clazz.getClassCompilationUnit().addImport(Annotation.class.getCanonicalName());

      MethodCallExpr methodCallExpr = new MethodCallExpr(
          new MethodCallExpr(new MethodCallExpr(new NameExpr("beanManager"), "lookupBean")
              .addArgument(type.getQualifiedName().toString() + ".class"), "getInstance"),
          "provide");

      ArrayInitializerExpr withAssignableTypesValues = new ArrayInitializerExpr();
      ((DeclaredType) fieldPoint.getVariableElement().asType()).getTypeArguments().forEach(
          type -> withAssignableTypesValues.getValues().add(new NameExpr(type + ".class")));

      ArrayCreationExpr withAssignableTypes = new ArrayCreationExpr();
      withAssignableTypes.setElementType("Class<?>[]");
      withAssignableTypes.setInitializer(withAssignableTypesValues);

      methodCallExpr.addArgument(withAssignableTypes);

      List<AnnotationMirror> qualifiers = new ArrayList<>(
          Utils.getAllElementQualifierAnnotations(iocContext, fieldPoint.getVariableElement()));
      Set<Expression> qualifiersExpression = new HashSet<>();

      qualifiers.forEach(
          type -> qualifiersExpression.add(generationUtils.createQualifierExpression(type)));
      ArrayInitializerExpr withQualifiersValues = new ArrayInitializerExpr();
      qualifiersExpression.forEach(type -> withQualifiersValues.getValues().add(type));
      ArrayCreationExpr withQualifiers = new ArrayCreationExpr();
      withQualifiers.setElementType("Annotation[]");
      withQualifiers.setInitializer(withQualifiersValues);
      methodCallExpr.addArgument(withQualifiers);

      ClassOrInterfaceType type = new ClassOrInterfaceType();
      type.setName(InstanceFactory.class.getCanonicalName());
      type.setTypeArguments(new ClassOrInterfaceType().setName(erased.toString()));

      ObjectCreationExpr factory = new ObjectCreationExpr().setType(type);
      NodeList<BodyDeclaration<?>> supplierClassBody = new NodeList<>();

      MethodDeclaration getInstance = new MethodDeclaration();
      getInstance.setModifiers(com.github.javaparser.ast.Modifier.Keyword.PUBLIC);
      getInstance.setName("getInstance");
      getInstance.addAnnotation(Override.class);
      getInstance.setType(new ClassOrInterfaceType().setName(erased.toString()));

      getInstance.getBody().get().addAndGetStatement(new ReturnStmt(methodCallExpr));
      supplierClassBody.add(getInstance);

      factory.setAnonymousClassBody(supplierClassBody);

      return factory;
    }
  }

  private class ProviderValidator extends Validator<TypeElement> {

    private Set<Check> checks = new HashSet<Check>() {
      {
        add(new Check<TypeElement>() {
          @Override
          public void check(TypeElement element) throws UnableToCompleteException {
            if (element.getModifiers().contains(Modifier.ABSTRACT)) {
              log(element, "IOCProvider must not be abstract");
            }
          }
        });

        add(new Check<TypeElement>() {
          @Override
          public void check(TypeElement element) throws UnableToCompleteException {
            if (!element.getModifiers().contains(Modifier.PUBLIC)) {
              log(element, "IOCProvider must not be public");
            }
          }
        });

      }
    };

    public ProviderValidator(IOCContext context) {
      super(context);
      checks.forEach(check -> addCheck(check));
    }
  }
}
