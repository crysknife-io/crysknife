package com.google.common.collect;

import java.util.Set;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/20/19
 */
public abstract class ImmutableSet<E> extends ImmutableCollection<E> implements Set<E> {

    public static <E> ImmutableSet<E> of() {
        return (ImmutableSet<E>) RegularImmutableSet.EMPTY;
    }

}
