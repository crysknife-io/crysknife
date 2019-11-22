package com.google.common.collect;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/20/19
 */
public interface SetMultimap<K, V> extends Multimap<K, V> {
    /**
     * {@inheritDoc}
     *
     * <p>Because a {@code SetMultimap} has unique values for a given key, this
     * method returns a {@link Set}, instead of the {@link java.util.Collection}
     * specified in the {@link Multimap} interface.
     */
    @Override
    Set<V> get(K key);

    /**
     * {@inheritDoc}
     *
     * <p>Because a {@code SetMultimap} has unique values for a given key, this
     * method returns a {@link Set}, instead of the {@link java.util.Collection}
     * specified in the {@link Multimap} interface.
     */
    @Override
    Set<V> removeAll(Object key);

    /**
     * {@inheritDoc}
     *
     * <p>Because a {@code SetMultimap} has unique values for a given key, this
     * method returns a {@link Set}, instead of the {@link java.util.Collection}
     * specified in the {@link Multimap} interface.
     *
     * <p>Any duplicates in {@code values} will be stored in the multimap once.
     */
    @Override
    Set<V> replaceValues(K key, Iterable<? extends V> values);

    /**
     * {@inheritDoc}
     *
     * <p>Because a {@code SetMultimap} has unique values for a given key, this
     * method returns a {@link Set}, instead of the {@link java.util.Collection}
     * specified in the {@link Multimap} interface.
     */
    @Override
    Set<Map.Entry<K, V>> entries();

    /**
     * {@inheritDoc}
     *
     * <p><b>Note:</b> The returned map's values are guaranteed to be of type
     * {@link Set}. To obtain this map with the more specific generic type
     * {@code Map<K, Set<V>>}, call {@link Multimaps#asMap(SetMultimap)} instead.
     */
    @Override
    Map<K, Collection<V>> asMap();

    /**
     * Compares the specified object to this multimap for equality.
     *
     * <p>Two {@code SetMultimap} instances are equal if, for each key, they
     * contain the same values. Equality does not depend on the ordering of keys
     * or values.
     *
     * <p>An empty {@code SetMultimap} is equal to any other empty {@code
     * Multimap}, including an empty {@code ListMultimap}.
     */
    @Override
    boolean equals(Object obj);
}

