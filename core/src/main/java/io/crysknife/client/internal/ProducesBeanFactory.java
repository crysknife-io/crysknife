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

package io.crysknife.client.internal;

import io.crysknife.client.BeanManager;

import javax.enterprise.context.Dependent;
import java.util.function.Supplier;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/29/21
 */
public class ProducesBeanFactory<T> extends BeanFactory<T> {

  private final Supplier<T> producer;

  public ProducesBeanFactory(BeanManager beanManager, Supplier<T> producer) {
    super(beanManager);
    this.producer = producer;
  }

  @Override()
  public T createInstance() {
    this.instance = producer.get();
    return instance;
  }

  @Override()
  public T getInstance() {
    if (beanDef.getScope().equals(Dependent.class)) {
      return producer.get();
    } else {
      if (instance == null) {
        instance = producer.get();
      }
      return instance;
    }
  }

  @Override
  public void initInstance() {

  }
}
