package com.google.common.collect;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.google.common.base.Objects;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/20/19
 */
abstract class AbstractMultiset<E> extends AbstractCollection<E> implements Multiset<E> {
    // Query Operations

    @Override
    public int size() {
        return Multisets.sizeImpl(this);
    }

    @Override
    public boolean isEmpty() {
        return entrySet().isEmpty();
    }

    @Override
    public boolean contains(Object element) {
        return count(element) > 0;
    }

    @Override
    public Iterator<E> iterator() {
        return Multisets.iteratorImpl(this);
    }

    @Override
    public int count(Object element) {
        for (Entry<E> entry : entrySet()) {
            if (Objects.equal(entry.getElement(), element)) {
                return entry.getCount();
            }
        }
        return 0;
    }

    // Modification Operations
    @Override
    public boolean add(E element) {
        add(element, 1);
        return true;
    }

    @Override
    public int add(E element, int occurrences) {
        throw new UnsupportedOperationException(this.getClass().getCanonicalName() + " add");

    }

    @Override
    public boolean remove(Object element) {
        return remove(element, 1) > 0;
    }

    @Override
    public int remove(Object element, int occurrences) {
        throw new UnsupportedOperationException(this.getClass().getCanonicalName() + " remove");

    }

    @Override
    public int setCount(E element, int count) {
        return setCountImpl(this, element, count);
    }

    @Override
    public boolean setCount(E element, int oldCount, int newCount) {
        return setCountImpl(this, element, oldCount, newCount);
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

    static <E> boolean setCountImpl(Multiset<E> self, E element, int oldCount, int newCount) {
        if (self.count(element) == oldCount) {
            self.setCount(element, newCount);
            return true;
        } else {
            return false;
        }
    }


    // Bulk Operations

    /**
     * {@inheritDoc}
     *
     * <p>This implementation is highly efficient when {@code elementsToAdd}
     * is itself a {@link Multiset}.
     */
    @Override
    public boolean addAll(Collection<? extends E> elementsToAdd) {
        return Multisets.addAllImpl(this, elementsToAdd);
    }

    @Override
    public boolean removeAll(Collection<?> elementsToRemove) {
        return Multisets.removeAllImpl(this, elementsToRemove);
    }

    @Override
    public boolean retainAll(Collection<?> elementsToRetain) {
        return Multisets.retainAllImpl(this, elementsToRetain);
    }

    @Override
    public void clear() {
        Iterators.clear(entryIterator());
    }

    // Views

    private transient Set<E> elementSet;

    @Override
    public Set<E> elementSet() {
        Set<E> result = elementSet;
        if (result == null) {
            elementSet = result = createElementSet();
        }
        return result;
    }

    /**
     * Creates a new instance of this multiset's element set, which will be
     * returned by {@link #elementSet()}.
     */
    Set<E> createElementSet() {
        return new ElementSet();
    }

    class ElementSet extends Multisets.ElementSet<E> {
        @Override
        Multiset<E> multiset() {
            return AbstractMultiset.this;
        }
    }

    abstract Iterator<Entry<E>> entryIterator();

    abstract int distinctElements();

    private transient Set<Entry<E>> entrySet;

    @Override
    public Set<Entry<E>> entrySet() {
        Set<Entry<E>> result = entrySet;
        if (result == null) {
            entrySet = result = createEntrySet();
        }
        return result;
    }

    class EntrySet extends Multisets.EntrySet<E> {
        @Override
        Multiset<E> multiset() {
            return AbstractMultiset.this;
        }

        @Override
        public Iterator<Entry<E>> iterator() {
            return entryIterator();
        }

        @Override
        public int size() {
            return distinctElements();
        }
    }

    Set<Entry<E>> createEntrySet() {
        return new EntrySet();
    }

    // Object methods

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns {@code true} if {@code object} is a multiset
     * of the same size and if, for each element, the two multisets have the same
     * count.
     */
    @Override
    public boolean equals(Object object) {
        return Multisets.equalsImpl(this, object);
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns the hash code of {@link
     * Multiset#entrySet()}.
     */
    @Override
    public int hashCode() {
        return entrySet().hashCode();
    }

    /**
     * {@inheritDoc}
     *
     * <p>This implementation returns the result of invoking {@code toString} on
     * {@link Multiset#entrySet()}.
     */
    @Override
    public String toString() {
        return entrySet().toString();
    }
}

