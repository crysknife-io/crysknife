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
import io.crysknife.definition.Definition;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.util.GenerationUtils;

import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/2/19
 */
public abstract class IOCGenerator<T extends Definition>
    extends io.crysknife.generator.refactoring.IOCGenerator<T> {

  protected final IOCContext iocContext;

  protected final GenerationUtils generationUtils;

  protected final Types types;
  protected final Elements elements;

  protected final TreeLogger logger;

  public IOCGenerator(TreeLogger treeLogger, IOCContext iocContext) {

    this.iocContext = iocContext;
    this.logger = treeLogger;

    this.generationUtils = new GenerationUtils(iocContext);

    types = iocContext.getGenerationContext().getTypes();
    elements = iocContext.getGenerationContext().getElements();
    super.init(treeLogger, iocContext);
  }

  public abstract void register();

  public abstract void generate(ClassBuilder clazz, T beanDefinition);

  public Expression generateBeanLookupCall(ClassBuilder clazz,
      InjectableVariableDefinition fieldPoint) {
    String typeQualifiedName = generationUtils.getActualQualifiedBeanName(fieldPoint);
    MethodCallExpr callForProducer = new MethodCallExpr(new NameExpr("beanManager"), "lookupBean")
        .addArgument(new FieldAccessExpr(new NameExpr(typeQualifiedName), "class"));
    return callForProducer;
  }

  public void before() {

  }

  public void after() {

  }

}
