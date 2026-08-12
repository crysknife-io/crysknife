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
import java.util.List;
import java.util.ListIterator;
import java.util.function.Consumer;

/**
 * A wrapper around a {@link List} that fires property change notifications on the owning
 * {@link BindingAgent} whenever the list is mutated.
 *
 * @param <E> element type
 * @author Dmitrii Tikhomirov
 */
public class ObservableList<E> implements List<E> {

  private final List<E> delegate;
  private final String propertyName;
  private final Consumer<String> changeNotifier;

  public ObservableList(List<E> delegate, String propertyName, Consumer<String> changeNotifier) {
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
    return listIterator();
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
  public boolean addAll(int index, Collection<? extends E> c) {
    boolean result = delegate.addAll(index, c);
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
  public boolean retainAll(Collection<?> c) {
    boolean result = delegate.retainAll(c);
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

  @Override
  public E get(int index) {
    return delegate.get(index);
  }

  @Override
  public E set(int index, E element) {
    E old = delegate.set(index, element);
    fireChange();
    return old;
  }

  @Override
  public void add(int index, E element) {
    delegate.add(index, element);
    fireChange();
  }

  @Override
  public E remove(int index) {
    E removed = delegate.remove(index);
    fireChange();
    return removed;
  }

  @Override
  public int indexOf(Object o) {
    return delegate.indexOf(o);
  }

  @Override
  public int lastIndexOf(Object o) {
    return delegate.lastIndexOf(o);
  }

  @Override
  public ListIterator<E> listIterator() {
    return listIterator(0);
  }

  @Override
  public ListIterator<E> listIterator(int index) {
    ListIterator<E> it = delegate.listIterator(index);
    return new ListIterator<E>() {
      @Override
      public boolean hasNext() {
        return it.hasNext();
      }

      @Override
      public E next() {
        return it.next();
      }

      @Override
      public boolean hasPrevious() {
        return it.hasPrevious();
      }

      @Override
      public E previous() {
        return it.previous();
      }

      @Override
      public int nextIndex() {
        return it.nextIndex();
      }

      @Override
      public int previousIndex() {
        return it.previousIndex();
      }

      @Override
      public void remove() {
        it.remove();
        fireChange();
      }

      @Override
      public void set(E e) {
        it.set(e);
        fireChange();
      }

      @Override
      public void add(E e) {
        it.add(e);
        fireChange();
      }
    };
  }

  @Override
  public List<E> subList(int fromIndex, int toIndex) {
    return new ObservableList<>(delegate.subList(fromIndex, toIndex), propertyName, changeNotifier);
  }

  public List<E> unwrap() {
    return delegate;
  }

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    return delegate.equals(o);
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }
}
