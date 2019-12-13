package com.google.common.collect;

import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/20/19
 */
class RegularImmutableList<E> extends ForwardingImmutableList<E> {
    private final List<E> delegate;
    E forSerialization;

    RegularImmutableList(List<E> delegate) {
        // TODO(cpovirk): avoid redundant unmodifiableList wrapping
        this.delegate = unmodifiableList(delegate);
    }

    @Override List<E> delegateList() {
        return delegate;
    }
}
