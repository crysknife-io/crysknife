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

package org.treblereel.gwt.crysknife.generator.point;

import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.TypeElement;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 3/3/19
 */
public class ConstructorPoint extends Point {

  private final List<FieldPoint> arguments = new LinkedList<>();

  public ConstructorPoint(String name, TypeElement type) {
    super(type, name);
  }

  public void addArgument(FieldPoint arg) {
    arguments.add(arg);
  }

  public List<FieldPoint> getArguments() {
    return arguments;
  }

  @Override
  public String toString() {
    return "ConstructorPoint{" + "arguments=" + arguments + ", name='" + name + '\'' + ", type="
        + type + '}';
  }
}
