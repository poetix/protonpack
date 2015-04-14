package com.codepoetics.protonpack.collectors;

import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;

/**
 * Utility class providing some collectors.
 */
public final class CollectorUtils {

    private CollectorUtils() {
    }

    /**
     * Find the item for which the supplied projection returns the maximum value.
     * @param projection The projection to apply to each item.
     * @param <T> The type of each item.
     * @param <Y> The type of the projected value to compare on.
     * @return The collector.
     */
    public static <T, Y extends Comparable<Y>> Collector<T, ?, Optional<T>> maxBy(Function<T, Y> projection) {
        return maxBy(projection, Comparable::compareTo);
    }

    /**
     * Find the item for which the supplied projection returns the maximum value (variant for non-naturally-comparable
     * projected values).
     * @param projection The projection to apply to each item.
     * @param comparator The comparator to use to compare the projected values.
     * @param <T> The type of each item.
     * @param <Y> The type of the projected value to compare on.
     * @return The collector.
     */
    public static <T, Y> Collector<T, ?, Optional<T>>
    maxBy(Function<T, Y> projection, Comparator<Y> comparator) {
        return java.util.stream.Collectors.maxBy((a, b) -> {
            Y element1 = projection.apply(a);
            Y element2 = projection.apply(b);

            return comparator.compare(element1, element2);
        });
    }

    /**
     * Find the item for which the supplied projection returns the minimum value.
     * @param projection The projection to apply to each item.
     * @param <T> The type of each item.
     * @param <Y> The type of the projected value to compare on.
     * @return The collector.
     */
    public static <T, Y extends Comparable<Y>> Collector<T, ?, Optional<T>> minBy(Function<T, Y> projection) {
        return minBy(projection, Comparable::compareTo);
    }

    /**
     * Find the item for which the supplied projection returns the minimum value (variant for non-naturally-comparable
     * projected values).
     * @param projection The projection to apply to each item.
     * @param comparator The comparator to use to compare the projected values.
     * @param <T> The type of each item.
     * @param <Y> The type of the projected value to compare on.
     * @return The collector.
     */
    public static <T, Y> Collector<T, ?, Optional<T>>
    minBy(Function<T, Y> projection, Comparator<Y> comparator) {
        return java.util.stream.Collectors.minBy((a, b) -> {
            Y element1 = projection.apply(a);
            Y element2 = projection.apply(b);

            return comparator.compare(element1, element2);
        });
    }

    /**
     * A collector that returns the single member of a stream (if present), or throws a
     * {@link com.codepoetics.protonpack.collectors.NonUniqueValueException} if more
     * than one item is found.
     * @param <T> The type of the items in the stream.
     * @return The collector.
     */
    public static <T> Collector<T, AtomicReference<T>, Optional<T>> unique() {
        return Collector.of(
                AtomicReference::new,
                CollectorUtils::uniqueAccumulate,
                CollectorUtils::uniqueCombine,
                ref -> Optional.ofNullable(ref.get())
        );
    }

    private static <T> void uniqueAccumulate(AtomicReference<T> a, T t) {
        if (t == null) {
            return;
        }
        if (!a.compareAndSet(null, t)) {
            throw new NonUniqueValueException(a.get(), t);
        }
    }

    private static <T> AtomicReference<T> uniqueCombine(AtomicReference<T> a1, AtomicReference<T> a2) {
        uniqueAccumulate(a1, a2.get());
        return a1;
    }

    /**
     * A combiner for all the cases when you don't intend to reduce/collect on a parallel stream.
     * Will throw an {@link java.lang.IllegalStateException} if it is ever called.
     * @param <T> The type of partial result you don't intend to combine.
     * @return A combiner that throws an exception instead of combining.
     */
    public static <T> BinaryOperator<T> noCombiner() {
        return (t1, t2) -> {
            throw new IllegalStateException("No combiner supplied for merging parallel results");
        };
    }
}
