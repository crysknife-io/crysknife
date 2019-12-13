package com.google.common.collect;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Function;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/20/19
 */
public class Iterators {

    private enum EmptyModifiableIterator implements Iterator<Object> {
        INSTANCE;

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {

        }
    }


    public static <F, T> Iterator<T> transform(
            final Iterator<F> fromIterator, final Function<? super F, ? extends T> function) {
        return new TransformedIterator<F, T>(fromIterator) {
            @Override
            T transform(F from) {
                return function.apply(from);
            }
        };
    }

    static <T> T pollNext(Iterator<T> iterator) {
        if (iterator.hasNext()) {
            T result = iterator.next();
            iterator.remove();
            return result;
        } else {
            return null;
        }
    }

    public static boolean removeAll(Iterator<?> removeFrom, Collection<?> elementsToRemove) {
        boolean result = false;
        while (removeFrom.hasNext()) {
            if (elementsToRemove.contains(removeFrom.next())) {
                removeFrom.remove();
                result = true;
            }
        }
        return result;
    }

    public static <T> boolean addAll(Collection<T> addTo, Iterator<? extends T> iterator) {
        boolean wasModified = false;
        while (iterator.hasNext()) {
            wasModified |= addTo.add(iterator.next());
        }
        return wasModified;
    }

    static void clear(Iterator<?> iterator) {
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }

    public static <T> UnmodifiableIterator<T> unmodifiableIterator(
            final Iterator<? extends T> iterator) {
        if (iterator instanceof UnmodifiableIterator) {
            @SuppressWarnings("unchecked") // Since it's unmodifiable, the covariant cast is safe
                    UnmodifiableIterator<T> result = (UnmodifiableIterator<T>) iterator;
            return result;
        }
        return new UnmodifiableIterator<T>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public T next() {
                return iterator.next();
            }
        };
    }

    static <T> Iterator<T> emptyModifiableIterator() {
        return (Iterator<T>) EmptyModifiableIterator.INSTANCE;
    }
}
