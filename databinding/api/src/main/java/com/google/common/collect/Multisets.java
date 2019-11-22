package com.google.common.collect;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;

import com.google.common.base.Objects;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/20/19
 */
public class Multisets {

    static int sizeImpl(Multiset<?> multiset) {
        long size = 0;
        for (Multiset.Entry<?> entry : multiset.entrySet()) {
            size += entry.getCount();
        }
        return saturatedCast(size);
    }

    static <E> Iterator<E> iteratorImpl(Multiset<E> multiset) {
        return new MultisetIteratorImpl<E>(multiset, multiset.entrySet().iterator());
    }

    public static int saturatedCast(long value) {
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (value < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) value;
    }

    abstract static class AbstractEntry<E> implements Multiset.Entry<E> {
        /**
         * Indicates whether an object equals this entry, following the behavior
         * specified in {@link Multiset.Entry#equals}.
         */
        @Override
        public boolean equals(Object object) {
            if (object instanceof Multiset.Entry) {
                Multiset.Entry<?> that = (Multiset.Entry<?>) object;
                return this.getCount() == that.getCount()
                        && Objects.equal(this.getElement(), that.getElement());
            }
            return false;
        }

        /**
         * Return this entry's hash code, following the behavior specified in
         * {@link Multiset.Entry#hashCode}.
         */
        @Override
        public int hashCode() {
            E e = getElement();
            return ((e == null) ? 0 : e.hashCode()) ^ getCount();
        }

        /**
         * Returns a string representation of this multiset entry. The string
         * representation consists of the associated element if the associated count
         * is one, and otherwise the associated element followed by the characters
         * " x " (space, x and space) followed by the count. Elements and counts are
         * converted to strings as by {@code String.valueOf}.
         */
        @Override
        public String toString() {
            String text = String.valueOf(getElement());
            int n = getCount();
            return (n == 1) ? text : (text + " x " + n);
        }
    }

    abstract static class ElementSet<E> extends Sets.ImprovedAbstractSet<E> {
        abstract Multiset<E> multiset();

        @Override
        public void clear() {
            multiset().clear();
        }

        @Override
        public boolean contains(Object o) {
            return multiset().contains(o);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return multiset().containsAll(c);
        }

        @Override
        public boolean isEmpty() {
            return multiset().isEmpty();
        }

        @Override
        public Iterator<E> iterator() {
            return new TransformedIterator<Multiset.Entry<E>, E>(multiset().entrySet().iterator()) {
                @Override
                E transform(Multiset.Entry<E> entry) {
                    return entry.getElement();
                }
            };
        }

        @Override
        public boolean remove(Object o) {
            return multiset().remove(o, Integer.MAX_VALUE) > 0;
        }

        @Override
        public int size() {
            return multiset().entrySet().size();
        }
    }

    abstract static class EntrySet<E> extends Sets.ImprovedAbstractSet<Multiset.Entry<E>> {
        abstract Multiset<E> multiset();

        @Override
        public boolean contains(Object o) {
            if (o instanceof Multiset.Entry) {
                /*
                 * The GWT compiler wrongly issues a warning here.
                 */
                @SuppressWarnings("cast")
                Multiset.Entry<?> entry = (Multiset.Entry<?>) o;
                if (entry.getCount() <= 0) {
                    return false;
                }
                int count = multiset().count(entry.getElement());
                return count == entry.getCount();
            }
            return false;
        }

        // GWT compiler warning; see contains().
        @SuppressWarnings("cast")
        @Override
        public boolean remove(Object object) {
            if (object instanceof Multiset.Entry) {
                Multiset.Entry<?> entry = (Multiset.Entry<?>) object;
                Object element = entry.getElement();
                int entryCount = entry.getCount();
                if (entryCount != 0) {
                    // Safe as long as we never add a new entry, which we won't.
                    @SuppressWarnings("unchecked")
                    Multiset<Object> multiset = (Multiset) multiset();
                    return multiset.setCount(element, entryCount, 0);
                }
            }
            return false;
        }

        @Override
        public void clear() {
            multiset().clear();
        }
    }

    static final class MultisetIteratorImpl<E> implements Iterator<E> {
        private final Multiset<E> multiset;
        private final Iterator<Multiset.Entry<E>> entryIterator;
        private Multiset.Entry<E> currentEntry;

        /** Count of subsequent elements equal to current element */
        private int laterCount;

        /** Count of all elements equal to current element */
        private int totalCount;

        private boolean canRemove;

        MultisetIteratorImpl(Multiset<E> multiset, Iterator<Multiset.Entry<E>> entryIterator) {
            this.multiset = multiset;
            this.entryIterator = entryIterator;
        }

        @Override
        public boolean hasNext() {
            return laterCount > 0 || entryIterator.hasNext();
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            if (laterCount == 0) {
                currentEntry = entryIterator.next();
                totalCount = laterCount = currentEntry.getCount();
            }
            laterCount--;
            canRemove = true;
            return currentEntry.getElement();
        }

        @Override
        public void remove() {
            if (totalCount == 1) {
                entryIterator.remove();
            } else {
                multiset.remove(currentEntry.getElement());
            }
            totalCount--;
            canRemove = false;
        }
    }

    static <E> Spliterator<E> spliteratorImpl(Multiset<E> multiset) {
        Spliterator<Multiset.Entry<E>> entrySpliterator = multiset.entrySet().spliterator();
        return CollectSpliterators.flatMap(
                entrySpliterator,
                entry -> Collections.nCopies(entry.getCount(), entry.getElement()).spliterator(),
                Spliterator.SIZED
                        | (entrySpliterator.characteristics()
                        & (Spliterator.ORDERED | Spliterator.NONNULL | Spliterator.IMMUTABLE)),
                multiset.size());
    }

    static <T> Multiset<T> cast(Iterable<T> iterable) {
        return (Multiset<T>) iterable;
    }

    static <E> boolean addAllImpl(Multiset<E> self, Collection<? extends E> elements) {
        if (elements.isEmpty()) {
            return false;
        }
        if (elements instanceof Multiset) {
            Multiset<? extends E> that = cast(elements);
            for (Multiset.Entry<? extends E> entry : that.entrySet()) {
                self.add(entry.getElement(), entry.getCount());
            }
        } else {
            Iterators.addAll(self, elements.iterator());
        }
        return true;
    }

    static boolean removeAllImpl(Multiset<?> self, Collection<?> elementsToRemove) {
        Collection<?> collection =
                (elementsToRemove instanceof Multiset)
                        ? ((Multiset<?>) elementsToRemove).elementSet()
                        : elementsToRemove;

        return self.elementSet().removeAll(collection);
    }

    static boolean retainAllImpl(Multiset<?> self, Collection<?> elementsToRetain) {
        Collection<?> collection =
                (elementsToRetain instanceof Multiset)
                        ? ((Multiset<?>) elementsToRetain).elementSet()
                        : elementsToRetain;

        return self.elementSet().retainAll(collection);
    }

    static boolean equalsImpl(Multiset<?> multiset, Object object) {
        if (object == multiset) {
            return true;
        }
        if (object instanceof Multiset) {
            Multiset<?> that = (Multiset<?>) object;
            /*
             * We can't simply check whether the entry sets are equal, since that
             * approach fails when a TreeMultiset has a comparator that returns 0
             * when passed unequal elements.
             */

            if (multiset.size() != that.size() || multiset.entrySet().size() != that.entrySet().size()) {
                return false;
            }
            for (Multiset.Entry<?> entry : that.entrySet()) {
                if (multiset.count(entry.getElement()) != entry.getCount()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    static <E> boolean setCountImpl(Multiset<E> self, E element, int oldCount, int newCount) {
        if (self.count(element) == oldCount) {
            self.setCount(element, newCount);
            return true;
        } else {
            return false;
        }
    }

    static <E> int setCountImpl(Multiset<E> self, E element, int count) {
        int oldCount = self.count(element);

        int delta = count - oldCount;
        if (delta > 0) {
            self.add(element, delta);
        } else if (delta < 0) {
            self.remove(element, -delta);
        }

        return oldCount;
    }

}
