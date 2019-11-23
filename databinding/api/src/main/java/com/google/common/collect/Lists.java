package com.google.common.collect;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/20/19
 */
public class Lists {

    static <E> List<E> subListImpl(final List<E> list, int fromIndex, int toIndex) {
        List<E> wrapper;
        if (list instanceof RandomAccess) {
            wrapper =
                    new RandomAccessListWrapper<E>(list) {
                        @Override
                        public ListIterator<E> listIterator(int index) {
                            return backingList.listIterator(index);
                        }

                        private static final long serialVersionUID = 0;
                    };
        } else {
            wrapper =
                    new AbstractListWrapper<E>(list) {
                        @Override
                        public ListIterator<E> listIterator(int index) {
                            return backingList.listIterator(index);
                        }

                        private static final long serialVersionUID = 0;
                    };
        }
        return wrapper.subList(fromIndex, toIndex);
    }

    private static class RandomAccessListWrapper<E> extends AbstractListWrapper<E>
            implements RandomAccess {
        RandomAccessListWrapper(List<E> backingList) {
            super(backingList);
        }
    }

    private static class AbstractListWrapper<E> extends AbstractList<E> {
        final List<E> backingList;

        AbstractListWrapper(List<E> backingList) {
            this.backingList = backingList;
        }

        @Override
        public void add(int index, E element) {
            backingList.add(index, element);
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            return backingList.addAll(index, c);
        }

        @Override
        public E get(int index) {
            return backingList.get(index);
        }

        @Override
        public E remove(int index) {
            return backingList.remove(index);
        }

        @Override
        public E set(int index, E element) {
            return backingList.set(index, element);
        }

        @Override
        public boolean contains(Object o) {
            return backingList.contains(o);
        }

        @Override
        public int size() {
            return backingList.size();
        }
    }


}
