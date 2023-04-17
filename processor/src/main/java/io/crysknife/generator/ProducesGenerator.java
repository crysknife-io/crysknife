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

import io.crysknife.generator.api.Generator;
import io.crysknife.definition.MethodDefinition;
import io.crysknife.generator.api.IOCGenerator;
import io.crysknife.generator.api.WiringElementType;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.logger.TreeLogger;

import jakarta.enterprise.inject.Produces;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/4/19
 */
@Generator(priority = 500)
public class ProducesGenerator extends IOCGenerator<MethodDefinition> {

  public ProducesGenerator(TreeLogger treeLogger, IOCContext iocContext) {
    super(treeLogger, iocContext);
  }

  @Override
  public void register() {
    iocContext.register(Produces.class, WiringElementType.METHOD_DECORATOR, this);
  }

}
