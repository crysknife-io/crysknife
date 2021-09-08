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

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import io.crysknife.annotation.Generator;
import io.crysknife.client.internal.InstanceImpl;
import io.crysknife.generator.BeanIOCGenerator;
import io.crysknife.generator.WiringElementType;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.point.FieldPoint;
import io.crysknife.nextstep.definition.Definition;
import io.crysknife.ui.databinding.client.components.DefaultListComponent;
import io.crysknife.ui.databinding.client.components.ListComponent;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.lang.model.type.TypeMirror;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 8/27/21
 */
@Generator(priority = 100000)
public class ListComponentGenerator extends BeanIOCGenerator {

  public ListComponentGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  public Expression generateBeanCall(ClassBuilder classBuilder, FieldPoint fieldPoint) {
    classBuilder.getClassCompilationUnit().addImport(ListComponent.class);
    classBuilder.getClassCompilationUnit().addImport(DefaultListComponent.class);

    if (fieldPoint.getType().getTypeParameters().size() != 2) {
      throw new Error(ListComponent.class.getCanonicalName() + " must be typed");
    }

    TypeMirror type1 = fieldPoint.getType().getTypeParameters().get(0).asType();
    TypeMirror type2 = fieldPoint.getType().getTypeParameters().get(1).asType();


    ClassOrInterfaceType classOrInterfaceType = new ClassOrInterfaceType();
    classOrInterfaceType.setName(DefaultListComponent.class.getSimpleName());
    classOrInterfaceType.setTypeArguments(new ClassOrInterfaceType().setName(type1.toString()),
        new ClassOrInterfaceType().setName(type1.toString()));



    // supplier.setTypeArguments(type);



    LambdaExpr lambda = new LambdaExpr();
    lambda.setEnclosingParameters(true);
    lambda.setBody(new ExpressionStmt(new ObjectCreationExpr().setType(classOrInterfaceType)));

    return new ObjectCreationExpr().setType(InstanceImpl.class).addArgument(lambda);
  }

  @Override
  public void register() {
    iocContext.register(Inject.class, ListComponent.class, WiringElementType.FIELD_TYPE, this);
    iocContext.getBlacklist().add(ListComponent.class.getCanonicalName());
  }

  @Override
  public void generate(ClassBuilder clazz, Definition beanDefinition) {

  }

}
