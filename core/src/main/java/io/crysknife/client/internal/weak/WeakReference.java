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

import jsinterop.annotations.JsType;
import org.treblereel.gwt.utils.GwtIncompatible;

public class WeakReference<T> {

  private final WeakRefJ2CL<T> holder = new WeakRefJRE<>();

  public WeakReference(T value) {
    holder.set(value);
  }

  public T get() {
    return holder.get();
  }

  private static class WeakRefJRE<T> extends WeakRefJ2CL<T> {

    @GwtIncompatible
    private java.lang.ref.WeakReference<T> instance;

    @GwtIncompatible
    @Override
    protected void set(T instance) {
      this.instance = new java.lang.ref.WeakReference<T>(instance);
    }

    @GwtIncompatible
    @Override
    protected T get() {
      return instance.get();
    }
  }

  private static class WeakRefJ2CL<T> {

    private WeakRef<T> instance;

    protected void set(T instance) {
      this.instance = new WeakRef<>(instance);
    }

    protected T get() {
      return instance.deref();
    }
  }

  @JsType(isNative = true, name = "WeakRef", namespace = "<global>")
  private static class WeakRef<VALUE> {


    public WeakRef(VALUE value) {

    }

    public native VALUE deref();

  }
}
