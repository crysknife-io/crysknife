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

import java.io.IOException;
import java.io.PrintWriter;

import javax.annotation.processing.FilerException;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.google.auto.common.MoreElements;
import com.google.auto.common.MoreTypes;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.GenerationContext;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.point.FieldPoint;
import io.crysknife.nextstep.definition.BeanDefinition;
import io.crysknife.nextstep.definition.InjectionPointDefinition;
import io.crysknife.util.Utils;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/4/19
 */
public abstract class BeanIOCGenerator extends IOCGenerator {

  public BeanIOCGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  /**
   * @param clazz
   * @param fieldPoint
   *
   * @return Expression, how to call instance of this bean ?
   */
  public abstract Expression generateBeanCall(ClassBuilder clazz, FieldPoint fieldPoint);

  public void write(ClassBuilder clazz, BeanDefinition beanDefinition, GenerationContext context) {
    try {
      String fileName = Utils.getQualifiedFactoryName(beanDefinition.getType());
      String source = clazz.toSourceCode();
      build(fileName, source, context);
    } catch (javax.annotation.processing.FilerException e1) {
      context.getProcessingEnvironment().getMessager().printMessage(Diagnostic.Kind.NOTE,
          e1.getMessage());
    } catch (IOException e1) {
      throw new GenerationException(e1);
    }
  }

  protected void build(String fileName, String source, GenerationContext context)
      throws IOException {
    JavaFileObject builderFile =
        context.getProcessingEnvironment().getFiler().createSourceFile(fileName);

    try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
      out.append(source);
    } catch (FilerException e) {
      throw new GenerationException(e);
    }
  }
}
