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

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.api.ClassMetaInfo;
import io.crysknife.generator.context.IOCContext;

import jakarta.enterprise.context.Dependent;
import jakarta.ejb.Startup;

import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 10/6/21
 */
public class StartupGenerator {

  private final IOCContext context;

  StartupGenerator(IOCContext context) {
    this.context = context;
  }

  void generate(MethodDeclaration methodDeclaration) {
    methodDeclaration.getBody().ifPresent(body -> {
      context.getTypeElementsByAnnotation(Startup.class.getCanonicalName()).stream()
          .collect(Collectors.toSet()).forEach(type -> {
            if (isDependent(context.getBean(type.asType()))) {
              throw new GenerationException(
                  "Bean, annotated with @Startup, must be @Singleton or @ApplicationScoped : "
                      + type);
            }
            body.addAndGetStatement(
                new MethodCallExpr(new MethodCallExpr(new NameExpr("beanManager"), "lookupBean")
                    .addArgument(type.asType() + ".class"), "getInstance"));
          });
    });
  }

  private boolean isDependent(BeanDefinition beanDefinition) {
    String annotation = beanDefinition.getScope().annotationType().getCanonicalName();
    String dependent = Dependent.class.getCanonicalName();
    return annotation.equals(dependent);
  }
}
