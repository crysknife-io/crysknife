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
import io.crysknife.generator.context.IOCContext;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
public class BeanInfoJREGeneratorBuilder extends AbstractBeanInfoGenerator {

  private InterceptorGenerator interceptorGenerator;

  public BeanInfoJREGeneratorBuilder(IOCContext iocContext) {
    super(iocContext);
    this.interceptorGenerator = new InterceptorGenerator(iocContext);
  }

  @Override
  public String build(BeanDefinition bean) {
    interceptorGenerator.generate(bean);
    return "";
  }

}
