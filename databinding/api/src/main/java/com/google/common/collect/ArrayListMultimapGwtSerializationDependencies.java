package com.google.common.collect;

import java.util.Collection;
import java.util.Map;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/20/19
 */
abstract class ArrayListMultimapGwtSerializationDependencies<K, V>
        extends AbstractListMultimap<K, V> {
    ArrayListMultimapGwtSerializationDependencies(Map<K, Collection<V>> map) {
        super(map);
    }

    K dummyKey;
    V dummyValue;
}