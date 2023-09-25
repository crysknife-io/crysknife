/*
 * Copyright (C) 2021
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

package io.crysknife.client.internal.weak;

import java.util.Collections;
import java.util.Set;

import elemental2.core.JsWeakSet;
import org.treblereel.gwt.utils.GwtIncompatible;

public class WeakSet<T> {

  private final WeakSetJ2CL<T> holder = new WeakSetJRE<>();

  public void add(T value) {
    holder.add(value);
  }

  public void clear() {
    holder.clear();
  }

  public boolean delete(T value) {
    return holder.delete(value);
  }

  public boolean has(T value) {
    return holder.has(value);
  }

  private class WeakSetJRE<T> extends WeakSetJ2CL<T> {

    @GwtIncompatible
    private final Set<T> holder = Collections.newSetFromMap(new java.util.WeakHashMap<>());

    @Override
    @GwtIncompatible
    public void add(T value) {
      holder.add(value);
    }

    @Override
    @GwtIncompatible
    public void clear() {
      holder.clear();
    }

    @Override
    @GwtIncompatible
    public boolean delete(T value) {
      return holder.remove(value);
    }

    @Override
    @GwtIncompatible
    public boolean has(T value) {
      return holder.contains(value);
    }

  }

  private class WeakSetJ2CL<T> {

    private final JsWeakSet<T> holder = new JsWeakSet<>();

    public void add(T value) {
      holder.add(value);
    }

    public void clear() {
      holder.clear();
    }

    public boolean delete(T value) {
      return holder.delete(value);
    }

    public boolean has(T value) {
      return holder.has(value);
    }

  }

}
