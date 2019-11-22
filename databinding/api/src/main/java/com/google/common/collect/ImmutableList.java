package com.google.common.collect;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/20/19
 */
public abstract class ImmutableList<E> extends ImmutableCollection<E>
        implements List<E>,
                   RandomAccess {

    static final ImmutableList<Object> EMPTY =
            new RegularImmutableList<Object>(Collections.emptyList());

    public static <E> ImmutableList<E> of() {
        return (ImmutableList<E>) EMPTY;
    }

    public static <E> ImmutableList<E> of(E element) {
        return new SingletonImmutableList<E>(element);
    }

    static <E> ImmutableList<E> unsafeDelegateList(List<? extends E> list) {
        switch (list.size()) {
            case 0:
                return of();
            case 1:
                return of(list.get(0));
            default:
                @SuppressWarnings("unchecked")
                List<E> castedList = (List<E>) list;
                return new RegularImmutableList<E>(castedList);
        }
    }

    public final boolean addAll(int index, Collection<? extends E> newElements) {
        throw new UnsupportedOperationException();
    }

    public final E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    public final void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    public final E remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override public UnmodifiableIterator<E> iterator() {
        return listIterator();
    }

    @Override public ImmutableList<E> subList(int fromIndex, int toIndex) {
        return unsafeDelegateList(Lists.subListImpl(this, fromIndex, toIndex));
    }

    @Override public UnmodifiableListIterator<E> listIterator() {
        return listIterator(0);
    }

    @Override public UnmodifiableListIterator<E> listIterator(int index) {
        return new AbstractIndexedListIterator<E>(size(), index) {
            @Override
            protected E get(int index) {
                return ImmutableList.this.get(index);
            }
        };
    }

}
