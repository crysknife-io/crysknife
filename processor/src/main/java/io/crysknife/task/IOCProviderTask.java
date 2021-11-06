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
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.crysknife.client.InstanceFactory;
import io.crysknife.client.ioc.ContextualTypeProvider;
import io.crysknife.client.ioc.IOCProvider;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.IOCGenerator;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.validation.Check;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 11/5/21
 */
public class IOCProviderTask implements Task {

  private IOCContext context;
  private TreeLogger logger;
  private TypeMirror contextualTypeProvider;
  private Validator validator = new Validator();

  public IOCProviderTask(IOCContext context, TreeLogger logger) {
    this.context = context;
    this.logger = logger;
    this.contextualTypeProvider = context.getGenerationContext().getTypes()
        .erasure(context.getTypeMirror(ContextualTypeProvider.class));
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
        BeanDefinition beanDefinition = context.getBeanDefinitionOrCreateAndReturn(erased);
        beanDefinition.setHasFactory(false);
        beanDefinition.setIocGenerator(new IOCGenerator<BeanDefinition>(context) {
          @Override
          public void register() {

          }

          @Override
          public void generate(ClassBuilder clazz, BeanDefinition beanDefinition) {

          }

          @Override
          public Expression generateBeanLookupCall(ClassBuilder clazz,
              InjectableVariableDefinition fieldPoint) {
            ClassOrInterfaceType classOrInterfaceType = new ClassOrInterfaceType();
            classOrInterfaceType.setName(type.getQualifiedName().toString());
            MethodCallExpr methodCallExpr = new MethodCallExpr(
                new ObjectCreationExpr().setType(classOrInterfaceType), "provide");

            ArrayInitializerExpr withAssignableTypesValues = new ArrayInitializerExpr();
            ((DeclaredType) fieldPoint.getVariableElement().asType()).getTypeArguments().forEach(
                type -> withAssignableTypesValues.getValues().add(new NameExpr(type + ".class")));

            ArrayCreationExpr withAssignableTypes = new ArrayCreationExpr();
            withAssignableTypes.setElementType("Class<?>[]");
            withAssignableTypes.setInitializer(withAssignableTypesValues);

            methodCallExpr.addArgument(withAssignableTypes);
            methodCallExpr.addArgument(new NullLiteralExpr());

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
        });
      }
    }
  }

  private static class Validator {

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


    public void validate(TypeElement type) throws UnableToCompleteException {
      for (Check check : checks) {
        check.check(type);
      }
    }

  }
}
