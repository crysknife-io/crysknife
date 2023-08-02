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

package io.crysknife.generator.api;

import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import io.crysknife.definition.Definition;
import io.crysknife.definition.InjectableVariableDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.util.GenerationUtils;

import javax.annotation.processing.FilerException;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/2/19
 */
public abstract class IOCGenerator<T extends Definition> {

  protected final IOCContext iocContext;

  protected final GenerationUtils generationUtils;

  protected final Types types;
  protected final Elements elements;

  protected final TreeLogger logger;

  public IOCGenerator(TreeLogger treeLogger, IOCContext iocContext) {
    this.iocContext = iocContext;
    this.logger = treeLogger;
    this.generationUtils = new GenerationUtils(iocContext);
    this.types = iocContext.getGenerationContext().getTypes();
    this.elements = iocContext.getGenerationContext().getElements();
  }

  public abstract void register();

  public void generate(ClassMetaInfo classMetaInfo, T beanDefinition) {

  }

  public String generateBeanLookupCall(InjectableVariableDefinition fieldPoint) {
    String typeQualifiedName = generationUtils.getActualQualifiedBeanName(fieldPoint);
    return new MethodCallExpr(new NameExpr("beanManager"), "lookupBean")
        .addArgument(new FieldAccessExpr(new NameExpr(typeQualifiedName), "class")).toString();
  }

  protected void writeJavaFile(String fileName, String source) {
    iocContext.addTask(() -> {
      try {
        JavaFileObject sourceFile = iocContext.getGenerationContext().getProcessingEnvironment()
            .getFiler().createSourceFile(fileName);
        try (Writer writer = sourceFile.openWriter()) {
          writer.write(source);
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      } catch (FilerException e) {
        // ignore
      } catch (IOException e) {
        throw new GenerationException(e);
      }
    });
  }

  public void before() {

  }

  public void after() {

  }

}
