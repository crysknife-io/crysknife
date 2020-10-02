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

package org.treblereel.gwt.crysknife.generator.info;

import java.io.IOException;
import java.io.PrintWriter;

import javax.tools.JavaFileObject;

import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.BeanDefinition;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
public class BeanInfoGenerator {

  private IOCContext iocContext;

  private AbstractBeanInfoGenerator generator;

  public BeanInfoGenerator(IOCContext iocContext) {
    this.iocContext = iocContext;
    if (iocContext.getGenerationContext().isGwt2()) {
      generator = new BeanInfoGWT2GeneratorBuilder(iocContext);
    } else if (iocContext.getGenerationContext().isJre()) {
      generator = new BeanInfoJREGeneratorBuilder(iocContext);
    } else {
      generator = new BeanInfoJ2CLGeneratorBuilder(iocContext);
    }
  }

  public void generate() {
    iocContext.getBeans().forEach((k, bean) -> {
      try {
        generate(bean);
      } catch (IOException e) {
        throw new Error(e);
      }
    });
  }

  private void generate(BeanDefinition bean) throws IOException {
    if (!bean.getFieldInjectionPoints().isEmpty()) {
      JavaFileObject builderFile = iocContext.getGenerationContext().getProcessingEnvironment()
          .getFiler().createSourceFile(bean.getQualifiedName() + "Info");
      try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
        out.append(generator.build(bean));
      }
    }
  }
}
