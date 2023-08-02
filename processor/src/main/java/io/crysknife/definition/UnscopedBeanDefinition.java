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

package io.crysknife.definition;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.google.auto.common.MoreTypes;
import io.crysknife.client.internal.InstanceImpl;
import io.crysknife.generator.api.IOCGenerator;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.util.TypeUtils;

import javax.lang.model.type.TypeMirror;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/9/21
 */
public class UnscopedBeanDefinition extends BeanDefinition {

  public UnscopedBeanDefinition(TypeMirror type, TreeLogger logger, IOCContext context) {
    super(type);
    setIocGenerator(new UnscopedIOCGenerator(logger, context));
  }

  private static class UnscopedIOCGenerator extends IOCGenerator<BeanDefinition> {

    private UnscopedIOCGenerator(TreeLogger logger, IOCContext context) {
      super(logger, context);
    }

    @Override
    public void register() {

    }

    public String generateBeanLookupCall(InjectableVariableDefinition fieldPoint) {

      String clazzName = TypeUtils
          .getQualifiedName(MoreTypes.asTypeElement(fieldPoint.getVariableElement().asType()));
      return new ObjectCreationExpr().setType(InstanceImpl.class.getCanonicalName())
          .addArgument(new ObjectCreationExpr().setType(clazzName)).toString();

    }
  }

}
