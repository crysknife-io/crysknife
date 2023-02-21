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

package io.crysknife.generator.api;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class ClassMetaInfo {

  private final Set<String> imports = new HashSet<>();
  private final Set<Supplier<String>> onDestroy = new HashSet<>();
  private final Set<Supplier<String>> doInitInstance = new HashSet<>();
  private final Set<Supplier<String>> body = new HashSet<>();


  public void addImport(String _import) {
    imports.add(_import);
  }

  public void addImport(Class _import) {
    imports.add(_import.getCanonicalName());
  }

  public void addToBody(Supplier<String> supplier) {
    body.add(supplier);
  }

  public void addToOnDestroy(Supplier<String> supplier) {
    onDestroy.add(supplier);
  }

  public void addToDoInitInstance(Supplier<String> supplier) {
    doInitInstance.add(supplier);
  }

  public Set<String> getImports() {
    return imports;
  }
}
