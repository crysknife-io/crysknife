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

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.google.auto.common.MoreTypes;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.definition.Definition;
import io.crysknife.definition.InjectionPointDefinition;
import io.crysknife.util.GenerationUtils;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/2/19
 */
public abstract class IOCGenerator {

  protected final IOCContext iocContext;

  protected final GenerationUtils generationUtils;

  public IOCGenerator(IOCContext iocContext) {
    this.iocContext = iocContext;
    this.generationUtils = new GenerationUtils(iocContext);
  }

  public abstract void register();

  public abstract void generate(ClassBuilder clazz, Definition beanDefinition);

  public Expression generateBeanLookupCall(ClassBuilder clazz,
      InjectionPointDefinition fieldPoint) {
    MethodCallExpr callForProducer = new MethodCallExpr(new NameExpr("beanManager"), "lookupBean")
        .addArgument(new FieldAccessExpr(new NameExpr(MoreTypes
            .asTypeElement(fieldPoint.getVariableElement().asType()).getQualifiedName().toString()),
            "class"));
    generationUtils.maybeAddQualifiers(iocContext, callForProducer, fieldPoint);
    return callForProducer;
  }

  public void before() {

  }

  public void after() {

  }

}
