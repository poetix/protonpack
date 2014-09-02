package com.codepoetics.protonpack;

import com.codepoetics.protonpack.iterators.SkipWhileIterator;
import com.codepoetics.protonpack.iterators.TakeWhileIterator;
import com.codepoetics.protonpack.iterators.UnfoldIterator;
import com.codepoetics.protonpack.iterators.ZippingIterator;

import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.*;
import java.util.stream.Stream;

/**
 * Utility class providing static methods for performing various operations on Streams.
 */
public final class StreamUtils {

    private StreamUtils() {

    }

    /**
     * Constructs an infinite (although in practice bounded by Integer.MAX_VALUE) stream of integers 0, 1, 2, 3...
     * for use as indices.
     * @return The stream of integers.
     */
    public static Stream<Integer> indices() {
        return Stream.iterate(0, l -> l + 1);
    }

    /**
     * Zip the source stream together with the stream of indices() to provide a stream of indexed values.
     * @param source  The source stream.
     * @param <T> The type over which the source stream streams.
     * @return A stream of indexed values.
     */
    public static <T> Stream<Indexed<T>> zipWithIndex(Stream<T> source) {
        return zip(indices(), source, Indexed::index);
    }

    /**
     * Zip together the "left" and "right" streams until either runs out of values.
     * Each pair of values is combined into a single value using the supplied combiner function.
     * @param lefts The "left" stream to zip.
     * @param rights The "right" stream to zip.
     * @param combiner The function to combine "left" and "right" values.
     * @param <L> The type over which the "left" stream streams.
     * @param <R> The type over which the "right" stream streams.
     * @param <O> The type created by the combiner out of pairs of "left" and "right" values, over which the resulting
     *           stream streams.
     * @return The constructed stream of zipped values.
     */
    public static <L, R, O> Stream<O> zip(Stream<L> lefts, Stream<R> rights, BiFunction<L, R, O> combiner) {
        Spliterator<L> leftSpliterator = lefts.spliterator();
        Spliterator<R> rightSpliterator = rights.spliterator();

        int sharedCharacteristics =
                leftSpliterator.characteristics()
                        & rightSpliterator.characteristics()
                        & ~(Spliterator.DISTINCT | Spliterator.SORTED);

        boolean isParallel = lefts.isParallel() || rights.isParallel();

        Streamifier streamifier = (isSized(sharedCharacteristics)
                ? Streamifier.streamifier(isParallel).sized(
                    Math.min(leftSpliterator.getExactSizeIfKnown(),
                             rightSpliterator.getExactSizeIfKnown()))
                : Streamifier.streamifier(isParallel).unsized())
            .withCharacteristics(sharedCharacteristics);

        return streamifier.<O>streamify(ZippingIterator.over(
                Spliterators.iterator(leftSpliterator),
                Spliterators.iterator(rightSpliterator),
                combiner));
    }

    private static boolean isSized(int characteristics) {
        return (characteristics & Spliterator.SIZED) != 0;
    }

    /**
     * Construct a stream which takes values from the source stream for as long as they meet the supplied condition, and stops
     * as soon as a value is encountered which does not meet the condition.
     * @param source The source stream.
     * @param condition The condition to apply to elements of the source stream.
     * @param <T> The type over which the stream streams.
     * @return The constructed stream.
     */
    public static <T> Stream<T> takeWhile(Stream<T> source, Predicate<T> condition) {
        Spliterator<T> spliterator = source.spliterator();
        int characteristics = spliterator.characteristics() & ~(Spliterator.SIZED);
        return Streamifier
                .streamifier(source.isParallel())
                .unsized()
                .withCharacteristics(characteristics)
                .streamify(TakeWhileIterator.over(
                        Spliterators.iterator(spliterator),
                        condition));
    }

    /**
     * Construct a stream which takes values from the source stream until one of them meets the supplied condition,
     * and then stops.
     * @param source The source stream.
     * @param condition The condition to apply to elements of the source stream.
     * @param <T> The type over which the stream streams.
     * @return The constructed stream.
     */
    public static <T> Stream<T> takeUntil(Stream<T> source, Predicate<T> condition) {
        return takeWhile(source, condition.negate());
    }

    /**
     * Construct a stream which skips values from the source stream for as long as they meet the supplied condition,
     * then streams every remaining value as soon as the first value is found which does not meet the condition.
     * @param source The source stream.
     * @param condition The condition to apply to elements of the source stream.
     * @param <T> The type over which the stream streams.
     * @return The constructed stream.
     */
    public static <T> Stream<T> skipWhile(Stream<T> source, Predicate<T> condition) {
        Spliterator<T> spliterator = source.spliterator();
        int characteristics = spliterator.characteristics() & ~(Spliterator.SIZED);
        return Streamifier
                .streamifier(source.isParallel())
                .unsized()
                .withCharacteristics(characteristics)
                .streamify(SkipWhileIterator.over(
                        Spliterators.iterator(spliterator),
                        condition));
    }

    /**
     * Construct a stream which skips values from the source stream for as long as they do not meet the supplied condition,
     * then streams every remaining value as soon as the first value is found which does meet the condition.
     * @param source The source stream.
     * @param condition The condition to apply to elements of the source stream.
     * @param <T> The type over which the stream streams.
     * @return The constructed stream.
     */
    public static <T> Stream<T> skipUntil(Stream<T> source, Predicate<T> condition) {
        return skipWhile(source, condition.negate());
    }

    /**
     * Construct a stream which takes the seed value and applies the generator to create the next value, feeding each
     * new value back into the generator to create subsequent values. If the generator returns Optional.empty(), then
     * the stream has no more values.
     * @param seed The seed value.
     * @param generator The generator to use to create new values.
     * @param <T> The type over which the stream streams.
     * @return The constructed stream.
     */
    public static <T> Stream<T> unfold(T seed, Function<T, Optional<T>> generator) {
        return Streamifier.toStream(UnfoldIterator.over(seed, generator));
    }
}
