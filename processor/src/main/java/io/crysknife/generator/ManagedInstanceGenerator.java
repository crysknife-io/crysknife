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

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.google.auto.common.MoreTypes;
import io.crysknife.annotation.Generator;
import io.crysknife.client.ManagedInstance;
import io.crysknife.client.internal.InstanceImpl;
import io.crysknife.client.internal.ManagedInstanceImpl;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.lang.model.type.TypeMirror;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/27/21
 */
@Generator
public class ManagedInstanceGenerator extends BeanIOCGenerator {

  private final TypeMirror instanceTypeMirror;
  private final TypeMirror managedInstanceTypeMirror;

  public ManagedInstanceGenerator(IOCContext iocContext) {
    super(iocContext);
    instanceTypeMirror = iocContext.getGenerationContext().getTypes()
        .erasure(iocContext.getTypeMirror(Instance.class.getCanonicalName()));
    managedInstanceTypeMirror = iocContext.getGenerationContext().getTypes()
        .erasure(iocContext.getTypeMirror(ManagedInstance.class.getCanonicalName()));

  }

  @Override
  public void register() {
    iocContext.register(Inject.class, ManagedInstance.class, WiringElementType.FIELD_TYPE, this);
    iocContext.register(Inject.class, Instance.class, WiringElementType.FIELD_TYPE, this);
  }

  @Override
  public void generate(ClassBuilder clazz, io.crysknife.definition.Definition beanDefinition) {

  }

  @Override
  public Expression generateBeanLookupCall(ClassBuilder clazz,
      InjectableVariableDefinition fieldPoint) {

    clazz.getClassCompilationUnit().addImport(ManagedInstance.class);
    clazz.getClassCompilationUnit().addImport(ManagedInstanceImpl.class);
    clazz.getClassCompilationUnit().addImport(InstanceImpl.class);

    TypeMirror erased = iocContext.getGenerationContext().getTypes()
        .erasure(fieldPoint.getVariableElement().asType());

    System.out.println("QQQ " + erased + " and " + instanceTypeMirror);

    Expression result;

    if (iocContext.getGenerationContext().getTypes().isSameType(instanceTypeMirror, erased)) {
      TypeMirror param =
          MoreTypes.asDeclared(fieldPoint.getVariableElement().asType()).getTypeArguments().get(0);

      ObjectCreationExpr instance = new ObjectCreationExpr().setType(InstanceImpl.class)
          .addArgument(new MethodCallExpr(new NameExpr("beanManager"), "lookupBean").addArgument(
              new NameExpr(iocContext.getGenerationContext().getTypes().erasure(param).toString()
                  + ".class")));

      result = instance;
    } else {

      TypeMirror param =
          MoreTypes.asDeclared(fieldPoint.getVariableElement().asType()).getTypeArguments().get(0);

      ObjectCreationExpr instance = new ObjectCreationExpr().setType(ManagedInstanceImpl.class)
          .addArgument(new NameExpr("beanManager")).addArgument(new NameExpr(
              iocContext.getGenerationContext().getTypes().erasure(param).toString() + ".class"));

      result = instance;
    }


    ObjectCreationExpr rtrn =
        new ObjectCreationExpr().setType(InstanceImpl.class).addArgument(result);

    return rtrn;
  }

}
