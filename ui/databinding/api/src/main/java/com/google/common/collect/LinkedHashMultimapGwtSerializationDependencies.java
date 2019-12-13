package com.google.common.collect;

import java.util.Collection;
import java.util.Map;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/20/19
 */
abstract class LinkedHashMultimapGwtSerializationDependencies<K, V>
        extends AbstractSetMultimap<K, V> {
    LinkedHashMultimapGwtSerializationDependencies(Map<K, Collection<V>> map) {
        super(map);
    }
}
