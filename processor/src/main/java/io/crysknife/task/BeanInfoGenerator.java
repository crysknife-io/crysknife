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

package io.crysknife.task;

import java.io.IOException;
import java.io.Writer;

import javax.annotation.processing.FilerException;
import javax.lang.model.type.TypeMirror;
import javax.tools.JavaFileObject;

import com.google.auto.common.MoreTypes;
import io.crysknife.definition.BeanDefinition;
import io.crysknife.definition.ProducesBeanDefinition;
import io.crysknife.exception.GenerationException;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.context.ExecutionEnv;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.generator.helpers.FreemarkerTemplateGenerator;
import io.crysknife.generator.info.AbstractBeanInfoGenerator;
import io.crysknife.generator.info.BeanInfoJREGeneratorBuilder;
import io.crysknife.generator.info.InterceptorGenerator;
import io.crysknife.logger.TreeLogger;

import static javax.lang.model.element.Modifier.ABSTRACT;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
public class BeanInfoGenerator implements Task {

  private IOCContext iocContext;
  private AbstractBeanInfoGenerator generator;

  private InterceptorGenerator interceptorGenerator;


  public BeanInfoGenerator(IOCContext iocContext, TreeLogger logger) {
    this.iocContext = iocContext;
    if (iocContext.getGenerationContext().getExecutionEnv().equals(ExecutionEnv.JRE)) {
      generator = new BeanInfoJREGeneratorBuilder(iocContext);
      interceptorGenerator = new InterceptorGenerator(iocContext);
    }
  }

  public void execute() throws UnableToCompleteException {
    if (generator == null) {
      return;
    }

    new InfoGenerator().generate();

    for (TypeMirror bean : iocContext.getOrderedBeans()) {
      TypeMirror erased = iocContext.getGenerationContext().getTypes().erasure(bean);
      BeanDefinition beanDefinition = iocContext.getBean(erased);
      if (beanDefinition instanceof ProducesBeanDefinition) {
        continue;
      }
      if (isSuitableBeanDefinition(beanDefinition)) {
        beanDefinition.getIocGenerator()
            .ifPresent(iocGenerator -> interceptorGenerator.generate(beanDefinition));
      }
    }
  }

  private boolean isSuitableBeanDefinition(BeanDefinition beanDefinition) {
    if (beanDefinition.getIocGenerator().isPresent()) {
      return true;
    }
    return MoreTypes.asTypeElement(beanDefinition.getType()).getKind().isClass()
        && !MoreTypes.asTypeElement(beanDefinition.getType()).getModifiers().contains(ABSTRACT);
  }

  private class InfoGenerator {

    private final FreemarkerTemplateGenerator freemarkerTemplateGenerator =
        new FreemarkerTemplateGenerator("jre/parent.ftlh");

    private void generate() {

      try {
        String source = freemarkerTemplateGenerator.toSource(new Object());
        String fileName = "io.crysknife.generator.info.Info";
        write(iocContext, fileName, source);
      } catch (IOException e) {
        throw new GenerationException(e);
      }
    }

    private void write(IOCContext iocContext, String fileName, String source) throws IOException {

      try {
        JavaFileObject sourceFile = iocContext.getGenerationContext().getProcessingEnvironment()
            .getFiler().createSourceFile(fileName);
        try (Writer writer = sourceFile.openWriter()) {
          writer.write(source);
        }
      } catch (FilerException ignored) {
      }
    }
  }
}
