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

package org.treblereel.providers;

import io.crysknife.client.BeanManager;
import io.crysknife.client.ioc.ContextualTypeProvider;
import io.crysknife.client.ioc.IOCProvider;
import io.crysknife.exception.GenerationException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.lang.annotation.Annotation;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 12/3/21
 */
@Singleton
@IOCProvider
public class MyIOCSingletonBeanProvider implements ContextualTypeProvider<MyIOCSingletonBean> {


  @Inject
  private BeanManager beanManager;

  @Override
  public MyIOCSingletonBean provide(Class<?>[] typeargs, Annotation[] qualifiers) {
    if (beanManager == null) {
      throw new Error();
    }

    Class clazz1 = typeargs[0];
    Class clazz2 = typeargs[1];

    MyIOCSingletonBean myIOCBean = new MyIOCSingletonBean(clazz1, clazz2);
    return myIOCBean;
  }
}
