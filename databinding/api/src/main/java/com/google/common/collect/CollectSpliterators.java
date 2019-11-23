package com.google.common.collect;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Dmitrii Tikhomirov
 * Created by treblereel 11/20/19
 */
public class CollectSpliterators {

    static <F, T> Spliterator<T> map(
            Spliterator<F> fromSpliterator, Function<? super F, ? extends T> function) {
        return new Spliterator<T>() {

            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                return fromSpliterator.tryAdvance(
                        fromElement -> action.accept(function.apply(fromElement)));
            }

            @Override
            public void forEachRemaining(Consumer<? super T> action) {
                fromSpliterator.forEachRemaining(fromElement -> action.accept(function.apply(fromElement)));
            }

            @Override
            public Spliterator<T> trySplit() {
                Spliterator<F> fromSplit = fromSpliterator.trySplit();
                return (fromSplit != null) ? map(fromSplit, function) : null;
            }

            @Override
            public long estimateSize() {
                return fromSpliterator.estimateSize();
            }

            @Override
            public int characteristics() {
                return fromSpliterator.characteristics()
                        & ~(Spliterator.DISTINCT | Spliterator.NONNULL | Spliterator.SORTED);
            }
        };
    }

    static <F, T> Spliterator<T> flatMap(
            Spliterator<F> fromSpliterator,
            Function<? super F, Spliterator<T>> function,
            int topCharacteristics,
            long topSize) {
        class FlatMapSpliterator implements Spliterator<T> {
            Spliterator<T> prefix;
            final Spliterator<F> from;
            int characteristics;
            long estimatedSize;

            FlatMapSpliterator(
                    Spliterator<T> prefix, Spliterator<F> from, int characteristics, long estimatedSize) {
                this.prefix = prefix;
                this.from = from;
                this.characteristics = characteristics;
                this.estimatedSize = estimatedSize;
            }

            @Override
            public boolean tryAdvance(Consumer<? super T> action) {
                while (true) {
                    if (prefix != null && prefix.tryAdvance(action)) {
                        if (estimatedSize != Long.MAX_VALUE) {
                            estimatedSize--;
                        }
                        return true;
                    } else {
                        prefix = null;
                    }
                    if (!from.tryAdvance(fromElement -> prefix = function.apply(fromElement))) {
                        return false;
                    }
                }
            }

            @Override
            public void forEachRemaining(Consumer<? super T> action) {
                if (prefix != null) {
                    prefix.forEachRemaining(action);
                    prefix = null;
                }
                from.forEachRemaining(fromElement -> function.apply(fromElement).forEachRemaining(action));
                estimatedSize = 0;
            }

            @Override
            public Spliterator<T> trySplit() {
                Spliterator<F> fromSplit = from.trySplit();
                if (fromSplit != null) {
                    int splitCharacteristics = characteristics & ~Spliterator.SIZED;
                    long estSplitSize = estimateSize();
                    if (estSplitSize < Long.MAX_VALUE) {
                        estSplitSize /= 2;
                        this.estimatedSize -= estSplitSize;
                        this.characteristics = splitCharacteristics;
                    }
                    Spliterator<T> result =
                            new FlatMapSpliterator(this.prefix, fromSplit, splitCharacteristics, estSplitSize);
                    this.prefix = null;
                    return result;
                } else if (prefix != null) {
                    Spliterator<T> result = prefix;
                    this.prefix = null;
                    return result;
                } else {
                    return null;
                }
            }

            @Override
            public long estimateSize() {
                if (prefix != null) {
                    estimatedSize = Math.max(estimatedSize, prefix.estimateSize());
                }
                return Math.max(estimatedSize, 0);
            }

            @Override
            public int characteristics() {
                return characteristics;
            }
        }
        return new FlatMapSpliterator(null, fromSpliterator, topCharacteristics, topSize);
    }

}
