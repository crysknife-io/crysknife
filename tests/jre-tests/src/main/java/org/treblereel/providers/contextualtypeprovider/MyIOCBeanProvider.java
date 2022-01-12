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

package org.treblereel.providers.contextualtypeprovider;

import io.crysknife.client.ioc.ContextualTypeProvider;
import io.crysknife.client.ioc.IOCProvider;

import java.lang.annotation.Annotation;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 11/5/21
 */
@IOCProvider
public class MyIOCBeanProvider implements ContextualTypeProvider<MyIOCBean> {

  @Override
  public MyIOCBean provide(Class<?>[] typeargs, Annotation[] qualifiers) {
    Class clazz1 = typeargs[0];
    Class clazz2 = typeargs[1];

    MyIOCBean myIOCBean = new MyIOCBean(clazz1, clazz2);
    return myIOCBean;
  }
}