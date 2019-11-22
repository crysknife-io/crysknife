package com.google.common.collect;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gwtproject.core.shared.GwtIncompatible;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/20/19
 */
public final class ArrayListMultimap<K, V>
        extends ArrayListMultimapGwtSerializationDependencies<K, V> {
    // Default from ArrayList
    private static final int DEFAULT_VALUES_PER_KEY = 3;

    transient int expectedValuesPerKey;

    /**
     * Creates a new, empty {@code ArrayListMultimap} with the default initial capacities.
     *
     * <p>This method will soon be deprecated in favor of {@code
     * MultimapBuilder.hashKeys().arrayListValues().build()}.
     */
    public static <K, V> ArrayListMultimap<K, V> create() {
        return new ArrayListMultimap<>();
    }

    /**
     * Constructs an empty {@code ArrayListMultimap} with enough capacity to hold the specified
     * numbers of keys and values without resizing.
     *
     * <p>This method will soon be deprecated in favor of {@code
     * MultimapBuilder.hashKeys(expectedKeys).arrayListValues(expectedValuesPerKey).build()}.
     *
     * @param expectedKeys the expected number of distinct keys
     * @param expectedValuesPerKey the expected average number of values per key
     * @throws IllegalArgumentException if {@code expectedKeys} or {@code expectedValuesPerKey} is
     *     negative
     */
    public static <K, V> ArrayListMultimap<K, V> create(int expectedKeys, int expectedValuesPerKey) {
        return new ArrayListMultimap<>(expectedKeys, expectedValuesPerKey);
    }

    /**
     * Constructs an {@code ArrayListMultimap} with the same mappings as the specified multimap.
     *
     * <p>This method will soon be deprecated in favor of {@code
     * MultimapBuilder.hashKeys().arrayListValues().build(multimap)}.
     *
     * @param multimap the multimap whose contents are copied to this multimap
     */
    public static <K, V> ArrayListMultimap<K, V> create(Multimap<? extends K, ? extends V> multimap) {
        return new ArrayListMultimap<>(multimap);
    }

    private ArrayListMultimap() {
        super(new HashMap<K, Collection<V>>());
        expectedValuesPerKey = DEFAULT_VALUES_PER_KEY;
    }

    private ArrayListMultimap(int expectedKeys, int expectedValuesPerKey) {
        super(Maps.<K, Collection<V>>newHashMapWithExpectedSize(expectedKeys));
        this.expectedValuesPerKey = expectedValuesPerKey;
    }

    private ArrayListMultimap(Multimap<? extends K, ? extends V> multimap) {
        this(
                multimap.keySet().size(),
                (multimap instanceof ArrayListMultimap)
                        ? ((ArrayListMultimap<?, ?>) multimap).expectedValuesPerKey
                        : DEFAULT_VALUES_PER_KEY);
        putAll(multimap);
    }

    /**
     * Creates a new, empty {@code ArrayList} to hold the collection of values for
     * an arbitrary key.
     */
    @Override
    List<V> createCollection() {
        return new ArrayList<V>(expectedValuesPerKey);
    }

    /**
     * Reduces the memory used by this {@code ArrayListMultimap}, if feasible.
     *
     * @deprecated For a {@link ListMultimap} that automatically trims to size, use {@link
     *     ImmutableListMultimap}. If you need a mutable collection, remove the {@code trimToSize}
     *     call, or switch to a {@code HashMap<K, ArrayList<V>>}.
     */
    @Deprecated
    public void trimToSize() {
        for (Collection<V> collection : backingMap().values()) {
            ArrayList<V> arrayList = (ArrayList<V>) collection;
            arrayList.trimToSize();
        }
    }

    /**
     * @serialData expectedValuesPerKey, number of distinct keys, and then for
     *     each distinct key: the key, number of values for that key, and the
     *     key's values
     */
    @GwtIncompatible
    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        Serialization.writeMultimap(this, stream);
    }

    @GwtIncompatible
    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        expectedValuesPerKey = DEFAULT_VALUES_PER_KEY;
        int distinctKeys = Serialization.readCount(stream);
        Map<K, Collection<V>> map = Maps.newHashMap();
        setMap(map);
        Serialization.populateMultimap(this, stream, distinctKeys);
    }

    private static final long serialVersionUID = 0;

}
