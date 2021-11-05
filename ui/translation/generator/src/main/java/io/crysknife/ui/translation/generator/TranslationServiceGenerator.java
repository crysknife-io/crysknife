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

package io.crysknife.ui.translation.generator;

import io.crysknife.annotation.Generator;
import io.crysknife.definition.Definition;
import io.crysknife.generator.ScopedBeanGenerator;
import io.crysknife.generator.WiringElementType;
import io.crysknife.generator.api.ClassBuilder;
import io.crysknife.generator.context.IOCContext;
import io.crysknife.ui.translation.api.spi.TranslationService;

import javax.inject.Inject;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 11/5/21
 */
@Generator(priority = 100002)
public class TranslationServiceGenerator extends ScopedBeanGenerator {

  public TranslationServiceGenerator(IOCContext iocContext) {
    super(iocContext);
  }

  @Override
  public void register() {
    iocContext.register(Inject.class, TranslationService.class, WiringElementType.BEAN, this);
  }

  @Override
  public void generate(ClassBuilder clazz, Definition beanDefinition) {

  }
}
