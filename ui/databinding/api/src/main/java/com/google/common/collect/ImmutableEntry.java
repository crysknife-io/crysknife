package com.google.common.collect;

import java.io.Serializable;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/20/19
 */
class ImmutableEntry<K, V> extends AbstractMapEntry<K, V> implements Serializable {
    final K key;
    final V value;

    ImmutableEntry(K key,
                   V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public final K getKey() {
        return key;
    }

    @Override
    public final V getValue() {
        return value;
    }

    @Override
    public final V setValue(V value) {
        throw new UnsupportedOperationException(this.getClass().getCanonicalName() + " setValue");

    }

    private static final long serialVersionUID = 0;
}

