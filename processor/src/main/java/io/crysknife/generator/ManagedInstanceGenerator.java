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

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.lang.model.type.TypeMirror;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.google.auto.common.MoreTypes;
import io.crysknife.annotation.Generator;
import io.crysknife.client.ManagedInstance;
import io.crysknife.client.internal.InstanceImpl;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.definition.BeanDefinition;
import io.crysknife.generator.definition.Definition;
import io.crysknife.generator.point.FieldPoint;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/27/21
 */
@Generator
public class ManagedInstanceGenerator extends BeanIOCGenerator {

  public ManagedInstanceGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  public Expression generateBeanCall(ClassBuilder clazz, FieldPoint fieldPoint) {

    TypeMirror param =
        MoreTypes.asDeclared(fieldPoint.getField().asType()).getTypeArguments().get(0);

    StringBuffer sb = new StringBuffer();
    sb.append("new ").append("io.crysknife.client.internal.ManagedInstanceImpl");
    sb.append("(").append(param.toString()).append(".class").append(", ");
    sb.append("io.crysknife.client.BeanManagerImpl.get()");
    sb.append(")");

    return new NameExpr(sb.toString());
  }

  @Override
  public void register() {
    iocContext.register(Inject.class, ManagedInstance.class, WiringElementType.FIELD_TYPE, this);
    iocContext.register(Inject.class, Instance.class, WiringElementType.FIELD_TYPE, this);
    iocContext.getBlacklist().add(ManagedInstance.class.getCanonicalName());
    iocContext.getBlacklist().add(Instance.class.getCanonicalName());
  }

  @Override
  public void generateBeanFactory(ClassBuilder clazz, Definition beanDefinition) {
    // do nothing
  }
}
