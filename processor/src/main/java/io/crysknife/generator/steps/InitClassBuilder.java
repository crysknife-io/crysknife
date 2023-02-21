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

import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import io.crysknife.client.BeanManager;
import io.crysknife.client.InstanceFactory;
import io.crysknife.client.Reflect;
import io.crysknife.client.SyncBeanDef;
import io.crysknife.client.internal.BeanFactory;
import io.crysknife.client.internal.proxy.OnFieldAccessed;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.util.Utils;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Provider;

import javax.lang.model.element.TypeElement;
import java.util.function.Supplier;

public class InitClassBuilder implements Step<BeanDefinition> {

  @Override
  public void execute(IOCContext iocContext, ClassBuilder classBuilder,
      BeanDefinition beanDefinition) {
    String pkg = beanDefinition.getPackageName();
    String simpleName =
        MoreTypes.asTypeElement(beanDefinition.getType()).getSimpleName().toString();
    TypeElement asTypeElement = MoreTypes.asTypeElement(beanDefinition.getType());


    StringBuffer sb = new StringBuffer();
    if (asTypeElement.getEnclosingElement().getKind().isClass()) {
      String parent =
          MoreElements.asType(asTypeElement.getEnclosingElement()).getSimpleName().toString();
      sb.append(parent);
      sb.append("_");
    }
    sb.append(simpleName);
    sb.append("_Factory");

    classBuilder.getClassCompilationUnit().setPackageDeclaration(pkg);
    classBuilder.getClassCompilationUnit().addImport(BeanFactory.class);
    classBuilder.getClassCompilationUnit().addImport(SyncBeanDef.class);
    classBuilder.getClassCompilationUnit().addImport(InstanceFactory.class);
    classBuilder.getClassCompilationUnit().addImport(Provider.class);
    classBuilder.getClassCompilationUnit().addImport(OnFieldAccessed.class);
    classBuilder.getClassCompilationUnit().addImport(Reflect.class);
    classBuilder.getClassCompilationUnit().addImport(Supplier.class);
    classBuilder.getClassCompilationUnit().addImport(BeanManager.class);
    classBuilder.getClassCompilationUnit().addImport(Dependent.class);

    String classFactoryName = sb.toString();
    classBuilder.setClassName(classFactoryName);

    ClassOrInterfaceType factory = new ClassOrInterfaceType();
    factory.setName(BeanFactory.class.getSimpleName());
    factory.setTypeArguments(
        new ClassOrInterfaceType().setName(Utils.getSimpleClassName(beanDefinition.getType())));
    classBuilder.getExtendedTypes().add(factory);
  }
}
