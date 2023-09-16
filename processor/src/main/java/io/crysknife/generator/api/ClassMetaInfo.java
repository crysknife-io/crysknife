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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;

public class ClassMetaInfo {

  private final Set<String> imports = new TreeSet<>();
  private final List<Supplier<String>> onDestroy = new ArrayList<>();
  private final List<Supplier<String>> doInitInstance = new ArrayList<>();
  private final List<Supplier<String>> doCreateInstance = new ArrayList<>();
  private final List<Supplier<String>> body = new ArrayList<>();

  private final List<Supplier<String>> factoryConstructor = new ArrayList<>();


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

  public void addToFactoryConstructor(Supplier<String> supplier) {
    factoryConstructor.add(supplier);
  }

  public void addToDoCreateInstance(Supplier<String> supplier) {
    doCreateInstance.add(supplier);
  }

  public Set<String> getImports() {
    return imports;
  }

  public List<String> getOnDestroy() {
    return onDestroy.stream().map(Supplier::get).collect(java.util.stream.Collectors.toList());
  }

  public List<String> getDoInitInstance() {
    return doInitInstance.stream().map(Supplier::get).collect(java.util.stream.Collectors.toList());
  }

  public List<String> getBodyStatements() {
    return body.stream().map(Supplier::get).collect(java.util.stream.Collectors.toList());
  }

  public List<String> getDoCreateInstance() {
    return doCreateInstance.stream().map(Supplier::get)
        .collect(java.util.stream.Collectors.toList());
  }

  public List<String> getFactoryConstructor() {
    return factoryConstructor.stream().map(Supplier::get)
        .collect(java.util.stream.Collectors.toList());
  }

}
