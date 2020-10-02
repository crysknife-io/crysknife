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

package org.treblereel.gwt.crysknife.generator;

import org.treblereel.gwt.crysknife.generator.api.ClassBuilder;
import org.treblereel.gwt.crysknife.generator.context.IOCContext;
import org.treblereel.gwt.crysknife.generator.definition.Definition;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/2/19
 */
public abstract class IOCGenerator {

  protected final IOCContext iocContext;

  public IOCGenerator(IOCContext iocContext) {
    this.iocContext = iocContext;
  }

  public abstract void register();

  public abstract void generateBeanFactory(ClassBuilder clazz, Definition beanDefinition);

  public void before() {

  }

  public void after() {

  }

}
