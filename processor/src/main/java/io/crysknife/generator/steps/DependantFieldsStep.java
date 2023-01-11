/*
 * Copyright © 2023 Treblereel
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

package io.crysknife.generator.steps;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.crysknife.client.InstanceFactory;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.definition.InjectionParameterDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.util.GenerationUtils;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Supplier;

public class DependantFieldsStep extends Step {

  private final BeanLookupCallGenerator beanLookupCallGenerator;

  public DependantFieldsStep(Step next, BeanLookupCallGenerator beanLookupCallGenerator) {
    super(next);
    this.beanLookupCallGenerator = beanLookupCallGenerator;
  }

  @Override
  public void execute(StepContext context) {
    BeanDefinition beanDefinition = context.beanDefinition;
    ClassBuilder classBuilder = context.clazz;
    GenerationUtils generationUtils = context.generationUtils;

    Set<InjectionParameterDefinition> params = beanDefinition.getConstructorParams();
    Iterator<InjectionParameterDefinition> injectionPointDefinitionIterator = params.iterator();
    while (injectionPointDefinitionIterator.hasNext()) {
      InjectableVariableDefinition argument = injectionPointDefinitionIterator.next();
      generateFactoryFieldDeclaration(classBuilder, argument, "constructor");
    }

    beanDefinition.getFields()
    .forEach(field -> generateFactoryFieldDeclaration(classBuilder, field, "field"));


  }

    private void generateFactoryFieldDeclaration(ClassBuilder classBuilder,
      InjectableVariableDefinition fieldPoint, String kind) {
    String varName = "_" + kind + "_" + fieldPoint.getVariableElement().getSimpleName().toString();
    String typeQualifiedName = generationUtils.getActualQualifiedBeanName(fieldPoint);
    ClassOrInterfaceType supplier =
        new ClassOrInterfaceType().setName(Supplier.class.getSimpleName());

    ClassOrInterfaceType type = new ClassOrInterfaceType();
    type.setName(InstanceFactory.class.getSimpleName());
    type.setTypeArguments(new ClassOrInterfaceType().setName(typeQualifiedName));
    supplier.setTypeArguments(type);


    Expression beanCall;
    if (fieldPoint.getImplementation().isPresent()
        && fieldPoint.getImplementation().get().getIocGenerator().isPresent()) {
      beanCall = fieldPoint.getImplementation().get().getIocGenerator().get()
          .generateBeanLookupCall(classBuilder, fieldPoint);
    } else if (fieldPoint.getGenerator().isPresent()) {
      beanCall = fieldPoint.getGenerator().get().generateBeanLookupCall(classBuilder, fieldPoint);
    } else {
      beanCall = generateBeanLookupCall(classBuilder, fieldPoint);
    }

    if (beanCall == null) {
      throw new GenerationException();
    }

    LambdaExpr lambda = new LambdaExpr().setEnclosingParameters(true);
    lambda.setBody(new ExpressionStmt(beanCall));

    classBuilder.addFieldWithInitializer(supplier, varName, lambda, Modifier.Keyword.PRIVATE);
  }

}
