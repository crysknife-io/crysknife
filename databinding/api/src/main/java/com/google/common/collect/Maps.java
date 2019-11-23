package com.google.common.collect;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.base.Objects;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/20/19
 */
public class Maps {

    private enum EntryFunction implements Function<Map.Entry<?, ?>, Object> {
        KEY {
            @Override
            public Object apply(Map.Entry<?, ?> entry) {
                return entry.getKey();
            }
        },
        VALUE {
            @Override
            public Object apply(Map.Entry<?, ?> entry) {
                return entry.getValue();
            }
        };
    }

    static class KeySet<K, V> extends Sets.ImprovedAbstractSet<K> {
        final Map<K, V> map;

        KeySet(Map<K, V> map) {
            this.map = map;
        }

        Map<K, V> map() {
            return map;
        }

        @Override
        public Iterator<K> iterator() {
            return keyIterator(map().entrySet().iterator());
        }

        @Override
        public void forEach(Consumer<? super K> action) {
            // avoids entry allocation for those maps that allocate entries on iteration
            map.forEach((k, v) -> action.accept(k));
        }

        @Override
        public int size() {
            return map().size();
        }

        @Override
        public boolean isEmpty() {
            return map().isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return map().containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            if (contains(o)) {
                map().remove(o);
                return true;
            }
            return false;
        }

        @Override
        public void clear() {
            map().clear();
        }
    }

    /**
     * Delegates to {@link Map#remove}. Returns {@code null} on {@code
     * ClassCastException} and {@code NullPointerException}.
     */
    static <V> V safeRemove(Map<?, V> map, Object key) {
        try {
            return map.remove(key);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }

    /**
     * Delegates to {@link Map#containsKey}. Returns {@code false} on {@code
     * ClassCastException} and {@code NullPointerException}.
     */
    static boolean safeContainsKey(Map<?, ?> map, Object key) {
        try {
            return map.containsKey(key);
        } catch (ClassCastException | NullPointerException e) {
            return false;
        }
    }

    public static <V, K> Iterator<V> valueIterator(Iterator<Map.Entry<K,V>> entryIterator) {
        return Iterators.transform(entryIterator, Maps.<V>valueFunction());
    }

    static <V> Function<Map.Entry<?, V>, V> valueFunction() {
        return (Function) EntryFunction.VALUE;
    }

    public static final int MAX_POWER_OF_TWO = 1 << (Integer.SIZE - 2);


    static int capacity(int expectedSize) {
        if (expectedSize < 3) {
            return expectedSize + 1;
        }
        if (expectedSize < MAX_POWER_OF_TWO) {
            // This is the calculation used in JDK8 to resize when a putAll
            // happens; it seems to be the most conservative calculation we
            // can make.  0.75 is the default load factor.
            return (int) ((float) expectedSize / 0.75F + 1.0F);
        }
        return Integer.MAX_VALUE; // any large value
    }

    static <K, V> Iterator<K> keyIterator(Iterator<Map.Entry<K, V>> entryIterator) {
        return Iterators.transform(entryIterator, Maps.<K>keyFunction());
    }

    static <K> Function<Map.Entry<K, ?>, K> keyFunction() {
        return (Function) EntryFunction.KEY;
    }

    static <V> V safeGet(Map<?, V> map, Object key) {
        try {
            return map.get(key);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }

    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<>();
    }

    public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(int expectedSize) {
        return new HashMap<>(capacity(expectedSize));
    }

    public static <K, V> Map.Entry<K, V> immutableEntry(K key,V value) {
        return new ImmutableEntry<>(key, value);
    }


    abstract static class ViewCachingAbstractMap<K, V> extends AbstractMap<K, V> {
        /**
         * Creates the entry set to be returned by {@link #entrySet()}. This method
         * is invoked at most once on a given map, at the time when {@code entrySet}
         * is first called.
         */
        abstract Set<Entry<K, V>> createEntrySet();

        private transient Set<Entry<K, V>> entrySet;

        @Override
        public Set<Entry<K, V>> entrySet() {
            Set<Entry<K, V>> result = entrySet;
            return (result == null) ? entrySet = createEntrySet() : result;
        }

        private transient Set<K> keySet;

        @Override
        public Set<K> keySet() {
            Set<K> result = keySet;
            return (result == null) ? keySet = createKeySet() : result;
        }

        Set<K> createKeySet() {
            return new KeySet<>(this);
        }

        private transient Collection<V> values;

        @Override
        public Collection<V> values() {
            Collection<V> result = values;
            return (result == null) ? values = createValues() : result;
        }

        Collection<V> createValues() {
            return new Values<>(this);
        }
    }

    static class Values<K, V> extends AbstractCollection<V> {
        final Map<K, V> map;

        Values(Map<K, V> map) {
            this.map = map;
        }

        final Map<K, V> map() {
            return map;
        }

        @Override
        public Iterator<V> iterator() {
            return valueIterator(map().entrySet().iterator());
        }

        @Override
        public void forEach(Consumer<? super V> action) {
            // avoids allocation of entries for those maps that generate fresh entries on iteration
            map.forEach((k, v) -> action.accept(v));
        }

        @Override
        public boolean remove(Object o) {
            try {
                return super.remove(o);
            } catch (UnsupportedOperationException e) {
                for (Map.Entry<K, V> entry : map().entrySet()) {
                    if (Objects.equal(o, entry.getValue())) {
                        map().remove(entry.getKey());
                        return true;
                    }
                }
                return false;
            }
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            try {
                return super.removeAll(c);
            } catch (UnsupportedOperationException e) {
                Set<K> toRemove = Sets.newHashSet();
                for (Map.Entry<K, V> entry : map().entrySet()) {
                    if (c.contains(entry.getValue())) {
                        toRemove.add(entry.getKey());
                    }
                }
                return map().keySet().removeAll(toRemove);
            }
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            try {
                return super.retainAll(c);
            } catch (UnsupportedOperationException e) {
                Set<K> toRetain = Sets.newHashSet();
                for (Map.Entry<K, V> entry : map().entrySet()) {
                    if (c.contains(entry.getValue())) {
                        toRetain.add(entry.getKey());
                    }
                }
                return map().keySet().retainAll(toRetain);
            }
        }

        @Override
        public int size() {
            return map().size();
        }

        @Override
        public boolean isEmpty() {
            return map().isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return map().containsValue(o);
        }

        @Override
        public void clear() {
            map().clear();
        }
    }

    abstract static class EntrySet<K, V> extends Sets.ImprovedAbstractSet<Map.Entry<K, V>> {
        abstract Map<K, V> map();

        @Override
        public int size() {
            return map().size();
        }

        @Override
        public void clear() {
            map().clear();
        }

        @Override
        public boolean contains(Object o) {
            if (o instanceof Map.Entry) {
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                Object key = entry.getKey();
                V value = Maps.safeGet(map(), key);
                return Objects.equal(value, entry.getValue()) && (value != null || map().containsKey(key));
            }
            return false;
        }

        @Override
        public boolean isEmpty() {
            return map().isEmpty();
        }

        @Override
        public boolean remove(Object o) {
            if (contains(o)) {
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                return map().keySet().remove(entry.getKey());
            }
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            try {
                return super.removeAll(c);
            } catch (UnsupportedOperationException e) {
                // if the iterators don't support remove
                return Sets.removeAllImpl(this, c.iterator());
            }
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            try {
                return super.retainAll(c);
            } catch (UnsupportedOperationException e) {
                // if the iterators don't support remove
                Set<Object> keys = Sets.newHashSetWithExpectedSize(c.size());
                for (Object o : c) {
                    if (contains(o)) {
                        Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                        keys.add(entry.getKey());
                    }
                }
                return map().keySet().retainAll(keys);
            }
        }
    }

    static class SortedKeySet<K, V> extends KeySet<K, V> implements SortedSet<K> {
        SortedKeySet(SortedMap<K, V> map) {
            super(map);
        }

        @Override
        SortedMap<K, V> map() {
            return (SortedMap<K, V>) super.map();
        }

        @Override
        public Comparator<? super K> comparator() {
            return map().comparator();
        }

        @Override
        public SortedSet<K> subSet(K fromElement, K toElement) {
            return new SortedKeySet<>(map().subMap(fromElement, toElement));
        }

        @Override
        public SortedSet<K> headSet(K toElement) {
            return new SortedKeySet<>(map().headMap(toElement));
        }

        @Override
        public SortedSet<K> tailSet(K fromElement) {
            return new SortedKeySet<>(map().tailMap(fromElement));
        }

        @Override
        public K first() {
            return map().firstKey();
        }

        @Override
        public K last() {
            return map().lastKey();
        }
    }

    static <K> K keyOrNull(Map.Entry<K, ?> entry) {
        return (entry == null) ? null : entry.getKey();
    }

    static class NavigableKeySet<K, V> extends SortedKeySet<K, V> implements NavigableSet<K> {
        NavigableKeySet(NavigableMap<K, V> map) {
            super(map);
        }

        @Override
        NavigableMap<K, V> map() {
            return (NavigableMap<K, V>) map;
        }

        @Override
        public K lower(K e) {
            return map().lowerKey(e);
        }

        @Override
        public K floor(K e) {
            return map().floorKey(e);
        }

        @Override
        public K ceiling(K e) {
            return map().ceilingKey(e);
        }

        @Override
        public K higher(K e) {
            return map().higherKey(e);
        }

        @Override
        public K pollFirst() {
            return keyOrNull(map().pollFirstEntry());
        }

        @Override
        public K pollLast() {
            return keyOrNull(map().pollLastEntry());
        }

        @Override
        public NavigableSet<K> descendingSet() {
            return map().descendingKeySet();
        }

        @Override
        public Iterator<K> descendingIterator() {
            return descendingSet().iterator();
        }

        @Override
        public NavigableSet<K> subSet(
                K fromElement, boolean fromInclusive, K toElement, boolean toInclusive) {
            return map().subMap(fromElement, fromInclusive, toElement, toInclusive).navigableKeySet();
        }

        @Override
        public NavigableSet<K> headSet(K toElement, boolean inclusive) {
            return map().headMap(toElement, inclusive).navigableKeySet();
        }

        @Override
        public NavigableSet<K> tailSet(K fromElement, boolean inclusive) {
            return map().tailMap(fromElement, inclusive).navigableKeySet();
        }

        @Override
        public SortedSet<K> subSet(K fromElement, K toElement) {
            return subSet(fromElement, true, toElement, false);
        }

        @Override
        public SortedSet<K> headSet(K toElement) {
            return headSet(toElement, false);
        }

        @Override
        public SortedSet<K> tailSet(K fromElement) {
            return tailSet(fromElement, true);
        }
    }

}
