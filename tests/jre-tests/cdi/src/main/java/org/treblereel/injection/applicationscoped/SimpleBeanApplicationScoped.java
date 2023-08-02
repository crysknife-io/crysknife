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

package org.treblereel.injection.applicationscoped;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/21/20
 */
@ApplicationScoped
public class SimpleBeanApplicationScoped {

  private String postConstruct;

  public String getName() {
    return this.getClass().getSimpleName();
  }

  @PostConstruct
  public void init() {
    postConstruct = "done";
  }

  public String getPostConstruct() {
    return postConstruct;
  }
}
