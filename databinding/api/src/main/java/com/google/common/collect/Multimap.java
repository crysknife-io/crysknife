package com.google.common.collect;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/20/19
 */
public interface Multimap<K, V> {
    // Query Operations

    /**
     * Returns the number of key-value pairs in this multimap.
     *
     * <p><b>Note:</b> this method does not return the number of <i>distinct
     * keys</i> in the multimap, which is given by {@code keySet().size()} or
     * {@code asMap().size()}. See the opening section of the {@link Multimap}
     * class documentation for clarification.
     */
    int size();

    /**
     * Returns {@code true} if this multimap contains no key-value pairs.
     * Equivalent to {@code size() == 0}, but can in some cases be more efficient.
     */
    boolean isEmpty();

    /**
     * Returns {@code true} if this multimap contains at least one key-value pair
     * with the key {@code key}.
     */
    boolean containsKey(Object key);

    /**
     * Returns {@code true} if this multimap contains at least one key-value pair
     * with the value {@code value}.
     */
    boolean containsValue(Object value);

    /**
     * Returns {@code true} if this multimap contains at least one key-value pair
     * with the key {@code key} and the value {@code value}.
     */
    boolean containsEntry(
            Object key,
            Object value);

    // Modification Operations

    /**
     * Stores a key-value pair in this multimap.
     *
     * <p>Some multimap implementations allow duplicate key-value pairs, in which
     * case {@code put} always adds a new key-value pair and increases the
     * multimap size by 1. Other implementations prohibit duplicates, and storing
     * a key-value pair that's already in the multimap has no effect.
     *
     * @return {@code true} if the method increased the size of the multimap, or
     *     {@code false} if the multimap already contained the key-value pair and
     *     doesn't allow duplicates
     */
    boolean put(K key, V value);

    /**
     * Removes a single key-value pair with the key {@code key} and the value
     * {@code value} from this multimap, if such exists. If multiple key-value
     * pairs in the multimap fit this description, which one is removed is
     * unspecified.
     *
     * @return {@code true} if the multimap changed
     */
    boolean remove(
            Object key,
            Object value);

    // Bulk Operations

    /**
     * Stores a key-value pair in this multimap for each of {@code values}, all
     * using the same key, {@code key}. Equivalent to (but expected to be more
     * efficient than): <pre>   {@code
     *
     *   for (V value : values) {
     *     put(key, value);
     *   }}</pre>
     *
     * <p>In particular, this is a no-op if {@code values} is empty.
     *
     * @return {@code true} if the multimap changed
     */
    boolean putAll(K key, Iterable<? extends V> values);

    /**
     * Stores all key-value pairs of {@code multimap} in this multimap, in the
     * order returned by {@code multimap.entries()}.
     *
     * @return {@code true} if the multimap changed
     */
    boolean putAll(Multimap<? extends K, ? extends V> multimap);

    /**
     * Stores a collection of values with the same key, replacing any existing
     * values for that key.
     *
     * <p>If {@code values} is empty, this is equivalent to
     * {@link #removeAll(Object) removeAll(key)}.
     *
     * @return the collection of replaced values, or an empty collection if no
     *     values were previously associated with the key. The collection
     *     <i>may</i> be modifiable, but updating it will have no effect on the
     *     multimap.
     */
    Collection<V> replaceValues(K key, Iterable<? extends V> values);

    /**
     * Removes all values associated with the key {@code key}.
     *
     * <p>Once this method returns, {@code key} will not be mapped to any values,
     * so it will not appear in {@link #keySet()}, {@link #asMap()}, or any other
     * views.
     *
     * @return the values that were removed (possibly empty). The returned
     *     collection <i>may</i> be modifiable, but updating it will have no
     *     effect on the multimap.
     */
    Collection<V> removeAll(Object key);

    /**
     * Removes all key-value pairs from the multimap, leaving it {@linkplain
     * #isEmpty empty}.
     */
    void clear();

    // Views

    /**
     * Returns a view collection of the values associated with {@code key} in this
     * multimap, if any. Note that when {@code containsKey(key)} is false, this
     * returns an empty collection, not {@code null}.
     *
     * <p>Changes to the returned collection will update the underlying multimap,
     * and vice versa.
     */
    Collection<V> get(K key);

    /**
     * Returns a view collection of all <i>distinct</i> keys contained in this
     * multimap. Note that the key set contains a key if and only if this multimap
     * maps that key to at least one value.
     *
     * <p>Changes to the returned set will update the underlying multimap, and
     * vice versa. However, <i>adding</i> to the returned set is not possible.
     */
    Set<K> keySet();

    /**
     * Returns a view collection containing the key from each key-value pair in
     * this multimap, <i>without</i> collapsing duplicates. This collection has
     * the same size as this multimap, and {@code keys().count(k) ==
     * get(k).size()} for all {@code k}.
     *
     * <p>Changes to the returned multiset will update the underlying multimap,
     * and vice versa. However, <i>adding</i> to the returned collection is not
     * possible.
     */
    Multiset<K> keys();

    /**
     * Returns a view collection containing the <i>value</i> from each key-value
     * pair contained in this multimap, without collapsing duplicates (so {@code
     * values().size() == size()}).
     *
     * <p>Changes to the returned collection will update the underlying multimap,
     * and vice versa. However, <i>adding</i> to the returned collection is not
     * possible.
     */
    Collection<V> values();

    /**
     * Returns a view collection of all key-value pairs contained in this
     * multimap, as {@link Map.Entry} instances.
     *
     * <p>Changes to the returned collection or the entries it contains will
     * update the underlying multimap, and vice versa. However, <i>adding</i> to
     * the returned collection is not possible.
     */
    Collection<Map.Entry<K, V>> entries();

    /**
     * Performs the given action for all key-value pairs contained in this multimap. If an ordering is
     * specified by the {@code Multimap} implementation, actions will be performed in the order of
     * iteration of {@link #entries()}. Exceptions thrown by the action are relayed to the caller.
     *
     * <p>To loop over all keys and their associated value collections, write
     * {@code Multimaps.asMap(multimap).forEach((key, valueCollection) -> action())}.
     *
     * @since 21.0
     */
    default void forEach(BiConsumer<? super K, ? super V> action) {
        entries().forEach(entry -> action.accept(entry.getKey(), entry.getValue()));
    }

    /**
     * Returns a view of this multimap as a {@code Map} from each distinct key
     * to the nonempty collection of that key's associated values. Note that
     * {@code this.asMap().get(k)} is equivalent to {@code this.get(k)} only when
     * {@code k} is a key contained in the multimap; otherwise it returns {@code
     * null} as opposed to an empty collection.
     *
     * <p>Changes to the returned map or the collections that serve as its values
     * will update the underlying multimap, and vice versa. The map does not
     * support {@code put} or {@code putAll}, nor do its entries support {@link
     * Map.Entry#setValue setValue}.
     */
    Map<K, Collection<V>> asMap();

    // Comparison and hashing

    /**
     * Compares the specified object with this multimap for equality. Two
     * multimaps are equal when their map views, as returned by {@link #asMap},
     * are also equal.
     *
     * <p>In general, two multimaps with identical key-value mappings may or may
     * not be equal, depending on the implementation. For example, two
     * {@link SetMultimap} instances with the same key-value mappings are equal,
     * but equality of two {@link ListMultimap} instances depends on the ordering
     * of the values for each key.
     *
     * <p>A non-empty {@link SetMultimap} cannot be equal to a non-empty
     * {@link ListMultimap}, since their {@link #asMap} views contain unequal
     * collections as values. However, any two empty multimaps are equal, because
     * they both have empty {@link #asMap} views.
     */
    @Override
    boolean equals(Object obj);

    /**
     * Returns the hash code for this multimap.
     *
     * <p>The hash code of a multimap is defined as the hash code of the map view,
     * as returned by {@link Multimap#asMap}.
     *
     * <p>In general, two multimaps with identical key-value mappings may or may
     * not have the same hash codes, depending on the implementation. For
     * example, two {@link SetMultimap} instances with the same key-value
     * mappings will have the same {@code hashCode}, but the {@code hashCode}
     * of {@link ListMultimap} instances depends on the ordering of the values
     * for each key.
     */
    @Override
    int hashCode();
}