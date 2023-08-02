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

import java.util.Objects;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 4/26/20
 */
public class SimpleBeanSingleton {

  private String foo;
  private int bar;
  private int staticValue;

  @Override
  public int hashCode() {
    return Objects.hash(getFoo(), getBar(), getStaticValue());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof SimpleBeanSingleton)) {
      return false;
    }
    SimpleBeanSingleton that = (SimpleBeanSingleton) o;
    return getBar() == that.getBar() && getStaticValue() == that.getStaticValue()
        && Objects.equals(getFoo(), that.getFoo());
  }

  public int getBar() {
    return bar;
  }

  public void setBar(int bar) {
    this.bar = bar;
  }

  public int getStaticValue() {
    return staticValue;
  }

  public String getFoo() {
    return foo;
  }

  public void setFoo(String foo) {
    this.foo = foo;
  }

  public void setStaticValue(int staticValue) {
    this.staticValue = staticValue;
  }
}
