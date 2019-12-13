package com.google.common.collect;

import java.util.Collection;
import java.util.Set;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/20/19
 */
public interface Multiset<E> extends Collection<E> {

    Set<E> elementSet();

    Set<Entry<E>> entrySet();

    int count(Object element);

    int setCount(E element, int count);

    boolean setCount(E element, int oldCount, int newCount);

    int remove(Object element, int occurrences);

    int add(E element, int occurrences);




    interface Entry<E> {

        /**
         * Returns the multiset element corresponding to this entry. Multiple calls
         * to this method always return the same instance.
         *
         * @return the element corresponding to this entry
         */
        E getElement();

        /**
         * Returns the count of the associated element in the underlying multiset.
         * This count may either be an unchanging snapshot of the count at the time
         * the entry was retrieved, or a live view of the current count of the
         * element in the multiset, depending on the implementation. Note that in
         * the former case, this method can never return zero, while in the latter,
         * it will return zero if all occurrences of the element were since removed
         * from the multiset.
         *
         * @return the count of the element; never negative
         */
        int getCount();

        /**
         * {@inheritDoc}
         *
         * <p>Returns {@code true} if the given object is also a multiset entry and
         * the two entries represent the same element and count. That is, two
         * entries {@code a} and {@code b} are equal if: <pre>   {@code
         *
         *   Objects.equal(a.getElement(), b.getElement())
         *       && a.getCount() == b.getCount()}</pre>
         */
        @Override
        // TODO(kevinb): check this wrt TreeMultiset?
        boolean equals(Object o);

        /**
         * {@inheritDoc}
         *
         * <p>The hash code of a multiset entry for element {@code element} and
         * count {@code count} is defined as: <pre>   {@code
         *
         *   ((element == null) ? 0 : element.hashCode()) ^ count}</pre>
         */
        @Override
        int hashCode();

        /**
         * Returns the canonical string representation of this entry, defined as
         * follows. If the count for this entry is one, this is simply the string
         * representation of the corresponding element. Otherwise, it is the string
         * representation of the element, followed by the three characters {@code
         * " x "} (space, letter x, space), followed by the count.
         */
        @Override
        String toString();
    }

}
