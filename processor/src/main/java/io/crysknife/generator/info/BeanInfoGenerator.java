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

package io.crysknife.generator.info;

import io.crysknife.definition.BeanDefinition;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.context.ExecutionEnv;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.task.Task;

import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
public class BeanInfoGenerator implements Task {

  private IOCContext iocContext;
  private AbstractBeanInfoGenerator generator;

  public BeanInfoGenerator(IOCContext iocContext) {
    this.iocContext = iocContext;
    if (iocContext.getGenerationContext().getExecutionEnv().equals(ExecutionEnv.GWT2)) {
      generator = new BeanInfoGWT2GeneratorBuilder(iocContext);
    } else if (iocContext.getGenerationContext().getExecutionEnv().equals(ExecutionEnv.JRE)) {
      generator = new BeanInfoJREGeneratorBuilder(iocContext);
    } else {
      generator = new BeanInfoJ2CLGeneratorBuilder(iocContext);
    }
  }

  public void execute() throws UnableToCompleteException {
    iocContext.getBeans().forEach((k, bean) -> {
      try {
        generate(bean);
      } catch (javax.annotation.processing.FilerException e) {
        // Attempt to recreate a file for type
      } catch (IOException e) {
        throw new Error(e);
      }
    });
  }

  private void generate(BeanDefinition bean) throws IOException {
    if (!bean.getFields().isEmpty()) {
      JavaFileObject builderFile = iocContext.getGenerationContext().getProcessingEnvironment()
          .getFiler().createSourceFile(bean.getQualifiedName() + "Info");
      try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
        out.append(generator.build(bean));
      }
    }
  }
}
