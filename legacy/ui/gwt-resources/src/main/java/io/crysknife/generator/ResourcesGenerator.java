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

package io.crysknife.generator;

import jakarta.inject.Provider;

import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreElements;
import org.gwtproject.resources.client.Resource;
import org.gwtproject.resources.ext.ResourceGeneratorUtil;
import io.crysknife.annotation.Generator;
import io.crysknife.client.internal.InstanceImpl;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.definition.BeanDefinition;
import io.crysknife.generator.definition.Definition;
import io.crysknife.generator.point.FieldPoint;
import io.crysknife.util.Utils;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/7/19
 */
@Generator(priority = 101)
public class ResourcesGenerator extends BeanIOCGenerator {

  public ResourcesGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  public void register() {
    iocContext.register(Resource.class, WiringElementType.BEAN, this);
  }

  @Override
  public void generate(ClassBuilder classBuilder, Definition definition) {

  }

  public void addFactoryFieldDeclaration(ClassBuilder classBuilder, BeanDefinition beanDefinition) {
    String varName = Utils.toVariableName(beanDefinition.getQualifiedName());
    ClassOrInterfaceType type = new ClassOrInterfaceType();
    type.setName("io.crysknife.client.Instance");
    type.setTypeArguments(new ClassOrInterfaceType().setName(beanDefinition.getQualifiedName()));

    Parameter param = new Parameter();
    param.setName(varName);
    param.setType(type);

    classBuilder.addField(type, varName, Modifier.Keyword.FINAL, Modifier.Keyword.PRIVATE);
  }

  public String getFactoryVariableName() {
    return "";
  }

  public void addFactoryFieldInitialization(ClassBuilder classBuilder,
      BeanDefinition beanDefinition) {
    String theName = ResourceGeneratorUtil.generateSimpleSourceName(null, beanDefinition.getType());
    String qualifiedImplName = MoreElements.getPackage(beanDefinition.getType()) + "." + theName;

    classBuilder.getClassCompilationUnit().addImport(InstanceImpl.class);
    classBuilder.getClassCompilationUnit().addImport(Provider.class);
    classBuilder.getClassCompilationUnit()
        .addImport(beanDefinition.getType().getQualifiedName().toString());
    classBuilder.getClassCompilationUnit().addImport(qualifiedImplName);

    String varName = Utils.toVariableName(beanDefinition.getQualifiedName());
    FieldAccessExpr field = new FieldAccessExpr(new ThisExpr(), varName);

    AssignExpr assign = new AssignExpr().setTarget(field)
        .setValue(new NameExpr("new InstanceImpl(new Provider<"
            + beanDefinition.getType().getSimpleName() + ">() {" + "        @Override"
            + "        public " + beanDefinition.getType().getSimpleName() + " get() {"
            + "            return new " + theName + "();" + "        }" + "    })"));
    classBuilder.addStatementToConstructor(assign);
  }

  @Override
  public Expression generateBeanCall(ClassBuilder classBuilder, FieldPoint fieldPoint,
      BeanDefinition beanDefinition) {
    String theName = ResourceGeneratorUtil.generateSimpleSourceName(null, beanDefinition.getType());
    String qualifiedImplName = MoreElements.getPackage(beanDefinition.getType()) + "." + theName;
    classBuilder.getClassCompilationUnit()
        .addImport(beanDefinition.getType().getQualifiedName().toString());
    classBuilder.getClassCompilationUnit().addImport(qualifiedImplName);

    return new NameExpr("new " + theName + "()");
  }
}
