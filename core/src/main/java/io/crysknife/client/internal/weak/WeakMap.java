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

import elemental2.core.JsWeakMap;

import java.util.Map;

public class WeakMap<K, V> {

  private final WeakMapJ2CL<K, V> holder = new WeakMapJRE<>();

  public void clear() {
    holder.clear();
  }

  public boolean delete(K key) {
    return holder.delete(key);
  }

  public V get(K key) {
    return holder.get(key);
  }

  public boolean has(K key) {
    return holder.has(key);
  }

  public void set(K key, V value) {
    holder.set(key, value);
  }

  private class WeakMapJRE<K, V> extends WeakMapJ2CL<K, V> {

    private final Map<K, V> holder = new java.util.WeakHashMap<>();

    public void clear() {
      holder.clear();
    }

    public boolean delete(K key) {
      return holder.remove(key) != null;
    }

    public V get(K key) {
      return holder.get(key);
    }

    public boolean has(K key) {
      return holder.containsKey(key);
    }

    public void set(K key, V value) {
      holder.put(key, value);
    }
  }

  private class WeakMapJ2CL<K, V> {

    private final JsWeakMap<K, V> holder = new JsWeakMap<>();

    public void clear() {
      holder.clear();
    }

    public boolean delete(K key) {
      return holder.delete(key);
    }

    public V get(K key) {
      return holder.get(key);
    }

    public boolean has(K key) {
      return holder.has(key);
    }

    public void set(K key, V value) {
      holder.set(key, value);
    }

  }
}
