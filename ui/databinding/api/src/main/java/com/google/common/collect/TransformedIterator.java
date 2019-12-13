package com.google.common.collect;

import java.util.Iterator;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/20/19
 */
abstract class TransformedIterator<F, T> implements Iterator<T> {
    final Iterator<? extends F> backingIterator;

    TransformedIterator(Iterator<? extends F> backingIterator) {
        this.backingIterator = backingIterator;
    }

    abstract T transform(F from);

    @Override
    public final boolean hasNext() {
        return backingIterator.hasNext();
    }

    @Override
    public final T next() {
        return transform(backingIterator.next());
    }

    @Override
    public final void remove() {
        backingIterator.remove();
    }
}

