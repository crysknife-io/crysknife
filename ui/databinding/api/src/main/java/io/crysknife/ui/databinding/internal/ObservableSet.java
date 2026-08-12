/*
 * Copyright © 2025 Treblereel
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
package io.crysknife.ui.databinding.internal;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A wrapper around a {@link Set} that fires property change notifications on the owning
 * {@link BindingAgent} whenever the set is mutated.
 *
 * @param <E> element type
 * @author Dmitrii Tikhomirov
 */
public class ObservableSet<E> implements Set<E> {

  private final Set<E> delegate;
  private final String propertyName;
  private final Consumer<String> changeNotifier;

  public ObservableSet(Set<E> delegate, String propertyName, Consumer<String> changeNotifier) {
    this.delegate = delegate;
    this.propertyName = propertyName;
    this.changeNotifier = changeNotifier;
  }

  private void fireChange() {
    changeNotifier.accept(propertyName);
  }

  @Override
  public int size() {
    return delegate.size();
  }

  @Override
  public boolean isEmpty() {
    return delegate.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return delegate.contains(o);
  }

  @Override
  public Iterator<E> iterator() {
    Iterator<E> it = delegate.iterator();
    return new Iterator<E>() {
      @Override
      public boolean hasNext() {
        return it.hasNext();
      }

      @Override
      public E next() {
        return it.next();
      }

      @Override
      public void remove() {
        it.remove();
        fireChange();
      }
    };
  }

  @Override
  public Object[] toArray() {
    return delegate.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return delegate.toArray(a);
  }

  @Override
  public boolean add(E e) {
    boolean result = delegate.add(e);
    if (result) {
      fireChange();
    }
    return result;
  }

  @Override
  public boolean remove(Object o) {
    boolean result = delegate.remove(o);
    if (result) {
      fireChange();
    }
    return result;
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return delegate.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    boolean result = delegate.addAll(c);
    if (result) {
      fireChange();
    }
    return result;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    boolean result = delegate.retainAll(c);
    if (result) {
      fireChange();
    }
    return result;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    boolean result = delegate.removeAll(c);
    if (result) {
      fireChange();
    }
    return result;
  }

  @Override
  public void clear() {
    if (!delegate.isEmpty()) {
      delegate.clear();
      fireChange();
    }
  }

  public Set<E> unwrap() {
    return delegate;
  }
}
