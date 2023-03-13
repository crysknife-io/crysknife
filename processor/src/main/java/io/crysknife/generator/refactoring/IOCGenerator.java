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

package io.crysknife.generator.refactoring;

import io.crysknife.definition.Definition;
import io.crysknife.generator.api.ClassMetaInfo;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;
import io.crysknife.util.GenerationUtils;

public abstract class IOCGenerator<T extends Definition> {

  protected IOCContext iocContext;
  protected GenerationUtils generationUtils;
  protected TreeLogger logger;

  public void init(TreeLogger treeLogger, IOCContext iocContext) {
    this.iocContext = iocContext;
    this.logger = treeLogger;
    this.generationUtils = new GenerationUtils(iocContext);
  }

  public abstract void generate(ClassMetaInfo classMetaInfo, T beanDefinition);


  public void before() {}

  public void after() {}
}
