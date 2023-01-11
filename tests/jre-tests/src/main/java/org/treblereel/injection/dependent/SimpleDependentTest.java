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

package org.treblereel.injection.dependent;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
@Singleton
public class SimpleDependentTest {

  @Inject
  private SimpleBeanDependent fieldOne;
  @Inject
  private SimpleBeanDependent fieldTwo;
  private SimpleBeanDependent constrOne;
  private SimpleBeanDependent constrTwo;

  @Inject
  public SimpleDependentTest(SimpleBeanDependent constrOne, SimpleBeanDependent constrTwo) {
    this.constrOne = constrOne;
    this.constrTwo = constrTwo;
  }

  public SimpleBeanDependent getFieldOne() {
    return fieldOne;
  }

  public SimpleBeanDependent getFieldTwo() {
    return fieldTwo;
  }

  public SimpleBeanDependent getConstrOne() {
    return constrOne;
  }

  public SimpleBeanDependent getConstrTwo() {
    return constrTwo;
  }
}
