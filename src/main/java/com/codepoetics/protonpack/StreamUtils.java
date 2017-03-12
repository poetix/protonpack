package com.codepoetics.protonpack;

import com.codepoetics.protonpack.functions.TriFunction;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * Utility class providing static methods for performing various operations on Streams.
 */
public final class StreamUtils {

    private StreamUtils() {

    }

    /**
     * Constructs an infinite (although in practice bounded by Long.MAX_VALUE) stream of longs 0, 1, 2, 3...
     * for use as indices.
     * @return A stream of longs.
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
     * @return A stream of zipped values.
     */
    public static <L, R, O> Stream<O> zip(Stream<L> lefts, Stream<R> rights, BiFunction<L, R, O> combiner) {
        return StreamSupport.stream(ZippingSpliterator.zipping(lefts.spliterator(), rights.spliterator(), combiner), false);
    }

    /**
     * Zip together the "left", "middle" and "right" streams until any stream runs out of values.
     * Each triple of values is combined into a single value using the supplied combiner function.
     * @param lefts The "left" stream to zip.
     * @param middles The "middle" stream to zip.
     * @param rights The "right" stream to zip.
     * @param combiner The function to combine "left", "middle" and "right" values.
     * @param <L> The type over which the "left" stream streams.
     * @param <M> The type over which the "middle" stream streams.
     * @param <R> The type over which the "right" stream streams.
     * @param <O> The type created by the combiner out of triples of "left", "middle" and "right" values, over which the resulting
     *           stream streams.
     * @return A stream of zipped values.
     */
    public static <L, M, R, O> Stream<O> zip(Stream<L> lefts, Stream<M> middles, Stream<R> rights, TriFunction<L, M, R, O> combiner) {
        return StreamSupport.stream(TriZippingSpliterator.zipping(
                lefts.spliterator(),
                middles.spliterator(),
                rights.spliterator(),
                combiner), false);
    }

    /**
     * Zip together a list of streams until one of them runs out of values.
     * Each tuple of values is combined into a single value using the supplied combiner function.
     * @param streams The streams to zip.
     * @param combiner The function to combine the values.
     * @param <T> The type over which the streams stream.
     * @param <O> The type created by the combiner out of groups of values, over
     * which the resulting stream streams.
     * @return A stream of zipped values.
     */
    public static <T, O> Stream<O> zip(List<Stream<T>> streams, Function<List<T>, O> combiner) {
        List<Spliterator<T>> spliterators = streams.stream().map(Stream::spliterator).collect(Collectors.toList());
        return StreamSupport.stream(ListZippingSpliterator.zipping(spliterators, combiner), false);
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
     * @return A condition-bounded stream.
     */
    public static <T> Stream<T> takeWhile(Stream<T> source, Predicate<T> condition) {
        return StreamSupport.stream(TakeWhileSpliterator.over(source.spliterator(), condition), false);
    }

    /**
     * Construct a stream which takes values from the source stream until but including the first value that is
     * encountered which does not meet the condition.
     * @param source The source stream.
     * @param condition The condition to apply to elements of the source stream.
     * @param <T> The type over which the stream streams.
     * @return A condition-bounded stream.
     */
    public static <T> Stream<T> takeWhileInclusive(Stream<T> source, Predicate<T> condition) {
        return StreamSupport.stream(TakeWhileSpliterator.overInclusive(source.spliterator(), condition), false);
    }

    /**
     * Construct a stream which takes values from the source stream until one of them meets the supplied condition,
     * and then stops.
     * @param source The source stream.
     * @param condition The condition to apply to elements of the source stream.
     * @param <T> The type over which the stream streams.
     * @return A condition-bounded stream.
     */
    public static <T> Stream<T> takeUntil(Stream<T> source, Predicate<T> condition) {
        return takeWhile(source, condition.negate());
    }

    /**
     * Construct a stream which takes values from the source stream until but including the first value that is
     * encountered which meets the supplied condition.
     * @param source The source stream.
     * @param condition The condition to apply to elements of the source stream.
     * @param <T> The type over which the stream streams.
     * @return A condition-bounded stream.
     */
    public static <T> Stream<T> takeUntilInclusive(Stream<T> source, Predicate<T> condition) {
        return takeWhileInclusive(source, condition.negate());
    }

    /**
     * Construct a stream which skips values from the source stream for as long as they meet the supplied condition,
     * then streams every remaining value as soon as the first value is found which does not meet the condition.
     * @param source The source stream.
     * @param condition The condition to apply to elements of the source stream.
     * @param <T> The type over which the stream streams.
     * @return An element-skipping stream.
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
     * @return An element-skipping stream.
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
     * @return An unfolding stream.
     */
    public static <T> Stream<T> unfold(T seed, Function<T, Optional<T>> generator) {
        return StreamSupport.stream(UnfoldSpliterator.over(seed, generator), false);
    }

    /**
     * Constructs a stream that is a windowed view of the source stream of the size window size
     * with a default overlap of one item
     *
     * @param source The source stream
     * @param windowSize The window size
     * @param <T> The type over which to stream
     * @return A stream of lists representing the window
     */
    public static <T> Stream<List<T>> windowed(Stream<T> source, int windowSize){
        return windowed(source, windowSize, 1);
    }

    /**
     * Constructs a windowed stream where each element is a list of the window size
     * and the skip is the offset from the start of each window.
     *
     * For example, a skip of size 1 is a traditional window a la ([1, 2, 3], [2, 3, 4] ...).
     *
     * A skip of size 2 for a window of size 3 would look like
     * ([1, 2, 3], [3, 4, 5], ...)
     *
     * If the stream finishes, the last window is guaranteed to be of the desired size (possible data loss).
     *
     * A stream [1, 2, 3] with a size 2 and skip 2 will result in ([1,2])
     *
     * @param source The input stream
     * @param windowSize The window size
     * @param skip The skip amount between windows
     * @param <T> The type over which to stream
     * @return A stream of lists representing the windows
     */
    public static <T> Stream<List<T>> windowed(Stream<T> source, int windowSize, int skip){
        return windowed(source, windowSize, skip, false);
    }

    /**
     * Constructs a windowed stream where each element is a list of the window size
     * and the skip is the offset from the start of each window.
     *
     * For example, a skip of size 1 is a traditional window a la ([1, 2, 3], [2, 3, 4] ...).
     *
     * A skip of size 2 for a window of size 3 would look like
     * ([1, 2, 3], [3, 4, 5], ...)
     *
     * If the stream finishes, the last windows may have a window size lesser than the desired size. This is allowed
     * via the allowLesserSize parameter.
     *
     * @param source The input stream
     * @param windowSize The window size
     * @param skip The skip amount between windows
     * @param allowLesserSize Allow end of stream windows to have a lower size for completion
     * @param <T> The type over which to stream
     * @return A stream of lists representing the windows
     */
    public static <T> Stream<List<T>> windowed(Stream<T> source, int windowSize, int skip, boolean allowLesserSize){
        return StreamSupport.stream(WindowedSpliterator.over(source.spliterator(), windowSize, skip, allowLesserSize), false);
    }

    /**
     * Constructs a stream that represents grouped run using the default comparator. This means
     * that similar elements will get grouped into a list. I.e. given a list of [1,1,2,3,4,4]
     * you will get a stream of ([1,1], [2], [3], [4, 4])
     *
     * @param source The input stream
     * @param <T> The type over which to stream
     * @return A stream of lists of grouped runs
     */
    public static <T extends Comparable<T>> Stream<List<T>> groupRuns(Stream<T> source){
        return groupRuns(source, Comparable::compareTo);
    }

    /**
     * Constructs a stream that represents grouped run using the custom comparator. This means
     * that similar elements will get grouped into a list. I.e. given a list of [1,1,2,3,4,4]
     * you will get a stream of ([1,1], [2], [3], [4, 4])
     *
     * @param source The input stream
     * @param comparator The comparator to determine if neighbor elements are the same
     * @param <T> The type over which to stream
     * @return A stream of lists of grouped runs
     */
    public static <T> Stream<List<T>> groupRuns(Stream<T> source, Comparator<T> comparator){
        return StreamSupport.stream(new GroupRunsSpliterator<T>(source.spliterator(), comparator), false);
    }

    /**
     * Construct a stream which interleaves the supplied streams, picking items using the supplied selector function.
     *
     * The selector function will be passed an array containing one value from each stream, or null if that stream
     * has no more values, and must return the integer index of the value to accept. That value will become part of the
     * interleaved stream, and the source stream at that index will advance to the next value.
     *
     * See the {@link com.codepoetics.protonpack.selectors.Selectors} class for ready-made selectors for round-robin and sorted
     * item selection.
     * @param selector The selector function to use.
     * @param streams The streams to interleave.
     * @param <T> The type over which the interleaved streams stream.
     * @return An interleaved stream.
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
     * See the {@link com.codepoetics.protonpack.selectors.Selectors} class for ready-made selectors for round-robin and sorted
     * item selection.
     * @param selector The selector function to use.
     * @param streams The streams to interleave.
     * @param <T> The type over which the interleaved streams stream.
     * @return An interleaved stream.
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
     * @return A merging stream.
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
     * @return A merging stream of lists of T.
     */
    public static <T> Stream<List<T>> mergeToList(Stream<T>...streams) {
        return merge(ArrayList::new, (l, x) -> {
            l.add(x);
            return l;
        }, streams);
    }

    /**
     * Filter with the condition negated. Will throw away any members of the source stream that match the condition.
     *
     * @param source The source stream.
     * @param predicate The filter condition.
     * @param <T> The type over which the stream streams.
     * @return A rejecting stream.
     */
    public static <T> Stream<T> reject(Stream<T> source, Predicate<? super T> predicate) {
        return source.filter(predicate.negate());
    }

    /**
     * Aggregates items from source stream into list of items while supplied predicate is true when evaluated on previous and current item.
     * Can by seen as streaming alternative to Collectors.groupingBy when source stream is sorted by key.
     * @param source - source stream
     * @param predicate - predicate specifying boundary between groups of items
     * @param <T> The type over which the stream streams.
     * @return Stream of List&lt;T&gt; aggregated according to predicate
     */
    public static <T> Stream<List<T>> aggregate(Stream<T> source, BiPredicate<T, T> predicate) {
        return StreamSupport.stream(new AggregatingSpliterator<T>(source.spliterator(),
                (a, e) -> a.isEmpty() || predicate.test(a.get(a.size() - 1), e)), false);
    }

    /**
     * Aggregates items from source stream into list of items with fixed size
     * @param source - source stream
     * @param size - size of the aggregated list
     * @param <T> The type over which the stream streams.
     * @return Stream of List&lt;T&gt; with all list of size @size with possible exception of last List&lt;T&gt;
     */
    public static <T> Stream<List<T>> aggregate(Stream<T> source, int size) {
        if (size <= 0) throw new IllegalArgumentException("Positive size expected, was: "+size);
        return StreamSupport.stream(new AggregatingSpliterator<T>(source.spliterator(), (a, e) -> a.size() < size), false);
    }

    /**
     * Aggregates items from source stream. Similar to @aggregate, but uses different predicate, evaluated on all items aggregated so far
     * and next item from source stream.
     * @param source - source stream
     * @param predicate - predicate specifying boundary between groups of items
     * @param <T> The type over which the stream streams.
     * @return Stream of List&lt;T&gt; aggregated according to predicate
     */
    public static <T> Stream<List<T>> aggregateOnListCondition(Stream<T> source, BiPredicate<List<T>, T> predicate) {
        return StreamSupport.stream(new AggregatingSpliterator<T>(source.spliterator(), predicate), false);
    }

    /**
     * Converts nulls into an empty stream, and non-null values into a stream with one element.
     * @param nullable The nullable value to convert.
     * @param <T> The type of the value.
     * @return A stream of zero or one values.
     * @deprecated use {@link StreamUtils#ofSingleNullable(Object)}
     */
    public static <T> Stream<T> streamNullable(T nullable) {
        return ofSingleNullable(nullable);
    }

    // can't be named ofNullable() due to overloading difficulty with erasure of generic type
    /**
     * Converts nulls into an empty stream, and non-null values into a stream with one element.
     * @param nullable The nullable value to convert.
     * @param <T> The type of the value.
     * @return A stream of zero or one values.
     */
    public static <T> Stream<T> ofSingleNullable(T nullable) {
        return null == nullable ? Stream.empty() : Stream.of(nullable);
    }

    /**
     * Converts an Optional value to a stream of 0..1 values
     * @param optional source optional value
     * @param <T> The type of the optional value
     * @return Stream of a single item of type T or an empty stream
     */
    public static <T> Stream<T> stream(Optional<T> optional) {
        return optional.map(Stream::of).orElseGet(Stream::empty);
    }

    /**
     * Converts an Iterable into a Stream.
     * @param iterable The iterable to stream.
     * @param <T> The type of the iterable
     * @return Stream of the values returned by the iterable
     */
    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    /**
     * Converts a nullable Iterable into a Stream.
     * @param iterable The iterable to stream.
     * @param <T> The type of the iterable
     * @return Stream of the values returned by the iterable, or an empty stream if the iterable is null
     */
    public static <T> Stream<T> ofNullable(Iterable<T> iterable) {
        return null == iterable ? Stream.empty() : stream(iterable);
    }

    /**
     * Converts nullable int array into an empty stream, and non-null array into a stream.
     * @param nullable The nullable array to convert.
     * @return A stream of zero or more values.
     */
    public static IntStream ofNullable(int[] nullable) {
        return null == nullable ? IntStream.empty() : Arrays.stream(nullable);
    }

    /**
     * Converts nullable long array into an empty stream, and non-null array into a stream.
     * @param nullable The nullable array to convert.
     * @return A stream of zero or more values.
     */
    public static LongStream ofNullable(long[] nullable) {
        return null == nullable ? LongStream.empty() : Arrays.stream(nullable);
    }

    /**
     * Converts nullable float array into an empty stream, and non-null array into a stream.
     * @param nullable The nullable array to convert.
     * @return A stream of zero or more values.
     */
    public static DoubleStream ofNullable(double[] nullable) {
        return null == nullable ? DoubleStream.empty() : Arrays.stream(nullable);
    }

    /**
     * Converts nullable array into an empty stream, and non-null array into a stream.
     * @param nullable The nullable array to convert.
     * @param <T> The type of the value.
     * @return A stream of zero or more values.
     */
    public static <T> Stream<T> ofNullable(T[] nullable) {
        return null == nullable ? Stream.empty() : Stream.of(nullable);
    }

}
