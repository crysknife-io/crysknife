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

package org.treblereel.produces;

import java.util.Random;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
public class SimpleBeanProducer {

  @Inject
  private RandomGenerator randomGenerator;

  @Produces
  @Singleton
  public SimpleBeanSingleton getSimpleBeanSingleton() {
    SimpleBeanSingleton bean = new SimpleBeanSingleton();
    bean.setFoo(this.getClass().getSimpleName());
    bean.setBar(new Random().nextInt());
    bean.setStaticValue(randomGenerator.getRandom());
    return bean;
  }

  @Produces
  @Dependent
  public SimpleBeanDependent getSimpleBeanDependent() {
    SimpleBeanDependent bean = new SimpleBeanDependent();
    bean.setFoo(this.getClass().getSimpleName());
    bean.setBar(new Random().nextInt());
    bean.setStaticValue(randomGenerator.getRandom());
    return bean;
  }
}
