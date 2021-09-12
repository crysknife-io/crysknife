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

package io.crysknife.task;

import com.google.auto.common.MoreElements;
import io.crysknife.annotation.ComponentScan;
import io.crysknife.exception.UnableToCompleteException;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;

import javax.lang.model.element.TypeElement;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/10/21
 */
public class ProcessComponentScanAnnotationTask implements Task {

  private IOCContext context;
  private TreeLogger logger;
  private TypeElement application;

  private Set<String> packages;

  public ProcessComponentScanAnnotationTask(IOCContext context, TreeLogger logger,
      TypeElement application) {
    this.context = context;
    this.logger = logger;
    this.application = application;
  }

  @Override
  public void execute() throws UnableToCompleteException {
    packages = new HashSet<>();
    context.getGenerationContext().getRoundEnvironment()
        .getElementsAnnotatedWith(ComponentScan.class).forEach(componentScan -> {
          String[] values = componentScan.getAnnotation(ComponentScan.class).value();
          for (String aPackage : values) {
            packages.add(aPackage);
          }
        });

    if (packages.isEmpty()) {
      packages.add(MoreElements.getPackage(application).getQualifiedName().toString());
    }
  }
}
