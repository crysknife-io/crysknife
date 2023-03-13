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

import io.crysknife.generator.api.ClassMetaInfo;
import jakarta.inject.Inject;

import io.crysknife.annotation.Generator;
import io.crysknife.client.BeanManager;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.logger.TreeLogger;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/30/19
 */
@Generator()
public class BeanManagerProducerGenerator extends ScopedBeanGenerator {

  public BeanManagerProducerGenerator(TreeLogger treeLogger, IOCContext iocContext) {
    super(treeLogger, iocContext);
  }

  @Override
  public void register() {
    iocContext.register(Inject.class, BeanManager.class, WiringElementType.FIELD_TYPE, this);

  }

  public void generate(ClassBuilder clazz, BeanDefinition beanDefinition) {

  }



  @Override
  public void generateInstanceGetMethodReturn(ClassBuilder classBuilder,
      BeanDefinition beanDefinition) {
    /*    MethodCallExpr methodCallExpr =
        new MethodCallExpr(new NameExpr(BeanManager.class.getCanonicalName() + "Impl"), "get");
    classBuilder.getGetMethodDeclaration().getBody().get()
        .addAndGetStatement(new ReturnStmt(methodCallExpr));*/


  }

  @Override
  public void generate(ClassMetaInfo classMetaInfo, BeanDefinition beanDefinition) {

  }
}
