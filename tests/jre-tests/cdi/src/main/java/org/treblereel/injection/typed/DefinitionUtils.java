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

package org.treblereel.injection.typed;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 10/9/21
 */
@ApplicationScoped
public class DefinitionUtils {

  public DefinitionsCacheRegistry definitionsRegistry;

  @Inject
  @SuppressWarnings("all")
  public DefinitionUtils(final DefinitionsCacheRegistry definitionsRegistry) {
    this.definitionsRegistry = definitionsRegistry;
  }
}
