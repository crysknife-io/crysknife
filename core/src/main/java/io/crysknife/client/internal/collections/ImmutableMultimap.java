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
package io.crysknife.client.internal.collections;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Dmitrii Tikhomirov Created by treblereel 2/22/20
 */
public class ImmutableMultimap<K, V> extends Multimap<K, V> {

  private ImmutableMultimap() {
    super(Collections.emptyMap());
  }

  private ImmutableMultimap(Map<K, List<V>> holder) {
    super(holder);
  }

  public static <K, V> ImmutableMultimap<K, V> of() {
    return new ImmutableMultimap();
  }

  public static <K, V> ImmutableMultimap<K, V> copyOf(Multimap<? extends K, ? extends V> multimap) {
    return new ImmutableMultimap<K, V>(
        (Map<K, List<V>>) new HashMap<K, V>((Map<? extends K, ? extends V>) multimap.holder));
  }

  public void put(K key, V value) {
    throw new UnsupportedOperationException();
  }

  public Collection<V> values() {
    return Collections.unmodifiableCollection(super.values());
  }

  public List<V> get(K key) {
    return super.get(key).stream().collect(Collectors.toList());
  }

  public void remove(K key, V value) {
    throw new UnsupportedOperationException();
  }

  public Collection<K> keys() {
    return Collections.unmodifiableCollection(super.keySet());
  }

  public Set<K> keySet() {
    return Collections.unmodifiableSet(super.keySet());
  }

  public void clear() {
    throw new UnsupportedOperationException();
  }

  public void removeAll(K key) {
    throw new UnsupportedOperationException();
  }

  public Collection<Map.Entry<K, V>> entries() {
    return Collections.unmodifiableCollection(super.entries());
  }
}
