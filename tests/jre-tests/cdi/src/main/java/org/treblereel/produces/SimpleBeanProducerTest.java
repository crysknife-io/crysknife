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

import org.treblereel.produces.staticproduces.MyStaticBean;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
@Singleton
public class SimpleBeanProducerTest {

  @Inject
  private SimpleBeanSingleton simpleBeanSingletonOne;
  @Inject
  private SimpleBeanSingleton simpleBeanSingletonTwo;
  @Inject
  private SimpleBeanDependent simpleBeanDependentOne;
  @Inject
  private SimpleBeanDependent simpleBeanDependentTwo;

  @Inject
  private MyStaticBean myStaticBean;

  public SimpleBeanSingleton getSimpleBeanSingletonOne() {
    return simpleBeanSingletonOne;
  }

  public SimpleBeanSingleton getSimpleBeanSingletonTwo() {
    return simpleBeanSingletonTwo;
  }

  public SimpleBeanDependent getSimpleBeanDependentOne() {
    return simpleBeanDependentOne;
  }

  public SimpleBeanDependent getSimpleBeanDependentTwo() {
    return simpleBeanDependentTwo;
  }

  public MyStaticBean getMyStaticBean() {
    return myStaticBean;
  }
}
