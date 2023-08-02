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

package org.treblereel.injection.cycle.simple;

import io.crysknife.annotation.CircularDependency;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 9/26/21
 */
@ApplicationScoped
@CircularDependency
public class SimpleBeanOneImpl implements SimpleBeanOne {

  private SimpleBeanTwoImpl simpleBeanTwo;

  @Inject
  public FieldInjectBean fieldInjectBean;

  public String postConstruct;

  @Inject
  public SimpleBeanOneImpl(SimpleBeanTwoImpl simpleBeanTwo) {
    this.simpleBeanTwo = simpleBeanTwo;
  }

  @PostConstruct
  public void init() {
    postConstruct = getClass().getSimpleName() + ".init";
  }

  @Override
  public String whoAmI() {
    return getClass().getSimpleName();
  }

  public String whoIsDep() {
    return simpleBeanTwo.whoAmI();
  }

  @Override
  public String getPostConstruct() {
    return postConstruct;
  }
}
