package com.google.common.collect;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/20/19
 */
public class Multimaps {

    static boolean equalsImpl(Multimap<?, ?> multimap, Object object) {
        if (object == multimap) {
            return true;
        }
        if (object instanceof Multimap) {
            Multimap<?, ?> that = (Multimap<?, ?>) object;
            return multimap.asMap().equals(that.asMap());
        }
        return false;
    }

    static class Keys<K, V> extends AbstractMultiset<K> {
        final Multimap<K, V> multimap;

        Keys(Multimap<K, V> multimap) {
            this.multimap = multimap;
        }

        @Override
        Iterator<Multiset.Entry<K>> entryIterator() {
            return new TransformedIterator<Map.Entry<K, Collection<V>>, Multiset.Entry<K>>(
                    multimap.asMap().entrySet().iterator()) {
                @Override
                Multiset.Entry<K> transform(final Map.Entry<K, Collection<V>> backingEntry) {
                    return new Multisets.AbstractEntry<K>() {
                        @Override
                        public K getElement() {
                            return backingEntry.getKey();
                        }

                        @Override
                        public int getCount() {
                            return backingEntry.getValue().size();
                        }
                    };
                }
            };
        }

        @Override
        public Spliterator<K> spliterator() {
            return CollectSpliterators.map(multimap.entries().spliterator(), Map.Entry::getKey);
        }

        @Override
        public void forEach(Consumer<? super K> consumer) {
            multimap.entries().forEach(entry -> consumer.accept(entry.getKey()));
        }

        @Override
        int distinctElements() {
            return multimap.asMap().size();
        }

        @Override
        Set<Multiset.Entry<K>> createEntrySet() {
            return new KeysEntrySet();
        }

        class KeysEntrySet extends Multisets.EntrySet<K> {
            @Override
            Multiset<K> multiset() {
                return Keys.this;
            }

            @Override
            public Iterator<Multiset.Entry<K>> iterator() {
                return entryIterator();
            }

            @Override
            public int size() {
                return distinctElements();
            }

            @Override
            public boolean isEmpty() {
                return multimap.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Multiset.Entry) {
                    Multiset.Entry<?> entry = (Multiset.Entry<?>) o;
                    Collection<V> collection = multimap.asMap().get(entry.getElement());
                    return collection != null && collection.size() == entry.getCount();
                }
                return false;
            }

            @Override
            public boolean remove(Object o) {
                if (o instanceof Multiset.Entry) {
                    Multiset.Entry<?> entry = (Multiset.Entry<?>) o;
                    Collection<V> collection = multimap.asMap().get(entry.getElement());
                    if (collection != null && collection.size() == entry.getCount()) {
                        collection.clear();
                        return true;
                    }
                }
                return false;
            }
        }

        @Override
        public boolean contains(Object element) {
            return multimap.containsKey(element);
        }

        @Override
        public Iterator<K> iterator() {
            return Maps.keyIterator(multimap.entries().iterator());
        }

        @Override
        public int count(Object element) {
            Collection<V> values = Maps.safeGet(multimap.asMap(), element);
            return (values == null) ? 0 : values.size();
        }

        @Override
        public int remove(Object element, int occurrences) {
            if (occurrences == 0) {
                return count(element);
            }

            Collection<V> values = Maps.safeGet(multimap.asMap(), element);

            if (values == null) {
                return 0;
            }

            int oldCount = values.size();
            if (occurrences >= oldCount) {
                values.clear();
            } else {
                Iterator<V> iterator = values.iterator();
                for (int i = 0; i < occurrences; i++) {
                    iterator.next();
                    iterator.remove();
                }
            }
            return oldCount;
        }

        @Override
        public void clear() {
            multimap.clear();
        }

        @Override
        public Set<K> elementSet() {
            return multimap.keySet();
        }
    }

    abstract static class Entries<K, V> extends AbstractCollection<Map.Entry<K, V>> {
        abstract Multimap<K, V> multimap();

        @Override
        public int size() {
            return multimap().size();
        }

        @Override
        public boolean contains(Object o) {
            if (o instanceof Map.Entry) {
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                return multimap().containsEntry(entry.getKey(), entry.getValue());
            }
            return false;
        }

        @Override
        public boolean remove(Object o) {
            if (o instanceof Map.Entry) {
                Map.Entry<?, ?> entry = (Map.Entry<?, ?>) o;
                return multimap().remove(entry.getKey(), entry.getValue());
            }
            return false;
        }

        @Override
        public void clear() {
            multimap().clear();
        }
    }



}
