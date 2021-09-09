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

import javax.annotation.PostConstruct;

import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import io.crysknife.annotation.Generator;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.definition.Definition;
import io.crysknife.generator.definition.ExecutableDefinition;
import io.crysknife.nextstep.definition.MethodDefinition;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/3/19
 */
@Generator(priority = Integer.MAX_VALUE)
public class PostConstructGenerator extends IOCGenerator {

  public PostConstructGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  public void register() {
    iocContext.register(PostConstruct.class, WiringElementType.METHOD_DECORATOR, this);
  }

  @Override
  public void generate(ClassBuilder clazz, io.crysknife.nextstep.definition.Definition definition) {
    if (definition instanceof MethodDefinition) {
      MethodDefinition postConstract = (MethodDefinition) definition;
      FieldAccessExpr instance = new FieldAccessExpr(new ThisExpr(), "instance");
      MethodCallExpr method = new MethodCallExpr(instance,
          postConstract.getExecutableElement().getSimpleName().toString());
      clazz.getGetMethodDeclaration().getBody().get().addAndGetStatement(method);
    }

  }

  public void generate(ClassBuilder builder, Definition definition) {
    if (definition instanceof ExecutableDefinition) {
      ExecutableDefinition postConstract = (ExecutableDefinition) definition;
      FieldAccessExpr instance = new FieldAccessExpr(new ThisExpr(), "instance");
      MethodCallExpr method = new MethodCallExpr(instance,
          postConstract.getExecutableElement().getSimpleName().toString());
      builder.getGetMethodDeclaration().getBody().get().addAndGetStatement(method);
    }
  }
}
