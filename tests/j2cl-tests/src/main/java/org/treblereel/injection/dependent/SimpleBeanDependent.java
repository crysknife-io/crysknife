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

import java.util.Random;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
@Dependent
public class SimpleBeanDependent {

  private String postConstruct;

  private int random;

  public String getName() {
    return this.getClass().getSimpleName();
  }

  @PostConstruct
  public void init() {
    postConstruct = "done";
    random = new Random().nextInt();
  }

  public String getPostConstruct() {
    return postConstruct;
  }

  public int getRandom() {
    return random;
  }
}
