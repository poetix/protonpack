package com.codepoetics.protonpack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.*;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Utility class providing static methods for performing various operations on Streams.
 */
public final class StreamUtils {

    private StreamUtils() {

    }

    /**
     * Constructs an infinite (although in practice bounded by Long.MAX_VALUE) stream of longs 0, 1, 2, 3...
     * for use as indices.
     * @return The stream of longs.
     */
    public static LongStream indices() {
        return LongStream.iterate(0L, l -> l + 1);
    }

    /**
     * Zip the source stream together with the stream of indices() to provide a stream of indexed values.
     * @param source  The source stream.
     * @param <T> The type over which the source stream streams.
     * @return A stream of indexed values.
     */
    public static <T> Stream<Indexed<T>> zipWithIndex(Stream<T> source) {
        return zip(indices().mapToObj(Long::valueOf), source, Indexed::index);
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
        return StreamSupport.stream(ZippingSpliterator.zipping(lefts.spliterator(), rights.spliterator(), combiner), false);
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
        return StreamSupport.stream(TakeWhileSpliterator.over(source.spliterator(), condition), false);
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
     * @return The constructed element-skipping stream.
     */
    public static <T> Stream<T> skipWhile(Stream<T> source, Predicate<T> condition) {
        return StreamSupport.stream(SkipUntilSpliterator.over(source.spliterator(), condition.negate()), false);
    }

    /**
     * Construct a stream which skips values from the source stream for as long as they do not meet the supplied condition,
     * then streams every remaining value as soon as the first value is found which does meet the condition.
     * @param source The source stream.
     * @param condition The condition to apply to elements of the source stream.
     * @param <T> The type over which the stream streams.
     * @return The constructed element-skipping stream.
     */
    public static <T> Stream<T> skipUntil(Stream<T> source, Predicate<T> condition) {
        return StreamSupport.stream(SkipUntilSpliterator.over(source.spliterator(), condition), false);
    }

    /**
     * Construct a stream which takes the seed value and applies the generator to create the next value, feeding each
     * new value back into the generator to create subsequent values. If the generator returns Optional.empty(), then
     * the stream has no more values.
     * @param seed The seed value.
     * @param generator The generator to use to create new values.
     * @param <T> The type over which the stream streams.
     * @return The constructed unfolding stream.
     */
    public static <T> Stream<T> unfold(T seed, Function<T, Optional<T>> generator) {
        return StreamSupport.stream(UnfoldSpliterator.over(seed, generator), false);
    }

    /**
     * Construct a stream which interleaves the supplied streams, picking items using the supplied selector function.
     *
     * The selector function will be passed an array containing one value from each stream, or null if that stream
     * has no more values, and must return the integer index of the value to accept. That value will become part of the
     * interleaved stream, and the source stream at that index will advance to the next value.
     *
     * See the {@link com.codepoetics.protonpack.Selectors} class for ready-made selectors for round-robin and sorted
     * item selection.
     * @param selector The selector function to use.
     * @param streams The streams to interleave.
     * @param <T> The type over which the interleaved streams stream.
     * @return The constructed interleaved stream.
     */
    public static <T> Stream<T> interleave(Function<T[], Integer> selector, Stream<T>... streams) {
        Spliterator<T>[] spliterators = (Spliterator<T>[]) Stream.of(streams).map(s -> s.spliterator()).toArray(Spliterator[]::new);
        return StreamSupport.stream(InterleavingSpliterator.interleaving(spliterators, selector), false);
    }

    /**
     * Construct a stream which interleaves the supplied streams, picking items using the supplied selector function.
     *
     * The selector function will be passed an array containing one value from each stream, or null if that stream
     * has no more values, and must return the integer index of the value to accept. That value will become part of the
     * interleaved stream, and the source stream at that index will advance to the next value.
     *
     * See the {@link com.codepoetics.protonpack.Selectors} class for ready-made selectors for round-robin and sorted
     * item selection.
     * @param selector The selector function to use.
     * @param streams The streams to interleave.
     * @param <T> The type over which the interleaved streams stream.
     * @return The constructed interleaved stream.
     */
    public static <T> Stream<T> interleave(Function<T[], Integer> selector, List<Stream<T>> streams) {
        Spliterator<T>[] spliterators = (Spliterator<T>[]) streams.stream().map(s -> s.spliterator()).toArray(Spliterator[]::new);
        return StreamSupport.stream(InterleavingSpliterator.interleaving(spliterators, selector), false);
    }

    /**
     * Construct a stream which merges together values from the supplied streams, somewhat in the manner of the
     * stream constructed by {@link com.codepoetics.protonpack.StreamUtils#zip(java.util.stream.Stream, java.util.stream.Stream, java.util.function.BiFunction)},
     * but for an arbitrary number of streams and using a merger to merge the values from multiple streams
     * into an accumulator.
     *
     * @param unitSupplier Supplies the initial "zero" or "unit" value for the accumulator.
     * @param merger Merges each item from the collection of values taken from the source streams into the accumulator value.
     * @param streams The streams to merge.
     * @param <T> The type over which the merged streams stream.
     * @param <O> The type of the accumulator, over which the constructed stream streams.
     * @return The constructed merging stream.
     */
    public static <T, O> Stream<O> merge(Supplier<O> unitSupplier, BiFunction<O, T, O> merger, Stream<T>...streams) {
        Spliterator<T>[] spliterators = (Spliterator<T>[]) Stream.of(streams).map(s -> s.spliterator()).toArray(Spliterator[]::new);
        return StreamSupport.stream(MergingSpliterator.merging(spliterators, unitSupplier, merger), false);
    }

    /**
     * Construct a stream which merges together values from the supplied streams into lists of values, somewhat in the manner of the
     * stream constructed by {@link com.codepoetics.protonpack.StreamUtils#zip(java.util.stream.Stream, java.util.stream.Stream, java.util.function.BiFunction)},
     * but for an arbitrary number of streams.
     *
     * @param streams The streams to merge.
     * @param <T> The type over which the merged streams stream.
     * @return The constructed merging stream of lists of T.
     */
    public static <T> Stream<List<T>> mergeToList(Stream<T>...streams) {
        return merge(ArrayList::new, (l, x) -> { l.add(x); return l; }, streams);
    }

    /**
     * Filter with the condition negated. Will throw away any members of the source stream that match the condition.
     *
     * @param source The source stream.
     * @param predicate The filter condition.
     * @param <T> The type over which the stream streams.
     * @return The constructed rejecting stream.
     */
    public static <T> Stream<T> reject(Stream<T> source, Predicate<? super T> predicate) {
        return source.filter(predicate.negate());
    }
}
