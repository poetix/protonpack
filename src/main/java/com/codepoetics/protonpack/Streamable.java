package com.codepoetics.protonpack;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A source of Streams that can be repeatedly streamed.
 * @param <T> The type over which the streamable's streams stream.
 */
public interface Streamable<T> extends Supplier<Stream<T>> {

    /**
     * Gets an empty streamable.
     * @param <T> The type of the values that aren't in the streamable's streams.
     * @return An empty streamable.
     */
    static <T> Streamable<T> empty() {
        return Stream::empty;
    }

    /**
     * For converting method references to no-arg methods that return streams into streamable.
     * @param streamable Anything that can be cast to a Streamable.
     * @param <T> The type over which the streamable's streams stream.
     * @return The streamable.
     */
    static <T> Streamable<T> of(Supplier<Stream<T>> streamable) {
        return streamable::get;
    }

    /**
     * Create a streamable that produces streams over an array of items.
     * @param items The items that the streamable's streams will stream.
     * @param <T> The type of the values in the array.
     * @return The streamable.
     */
    static <T> Streamable<T> of(T...items) {
        return () -> Stream.of(items);
    }

    /**
     * Create a streamable that produces streams over a collection of items.
     * @param collection The items that the streamable's streams will stream.
     * @param <T> The type of the values in the collection.
     * @return The streamable.
     */
    static <T> Streamable<T> of(Collection<T> collection) {
        return collection::stream;
    }

    /**
     * Create a streamable that produces streams over an iterable of items.
     * @param iterable The items that the streamable's streams will stream.
     * @param <T> The type of the values in the iterable.
     * @return The streamable.
     */
    static <T> Streamable<T> of(Iterable<T> iterable) {
        return () -> StreamUtils.ofNullable(iterable);
    }

    /**
     * Create a streamable that produces streams of 0 or 1 elements over an optional value.
     * @param optional The optional item that the streamable's streams will stream.
     * @param <T> The type of the optional.
     * @return The streamable.
     */
    static <T> Streamable<T> of(Optional<T> optional) {
        return () -> StreamUtils.stream(optional);
    }

    /**
     * Concatenate a series of streamables together.
     * @param streamables The streamables to concatenate.
     * @param <T> The type of the streamables.
     * @return A streamable which streams over the concatenation of the streams produced by all the source streamables.
     */
    @SafeVarargs
    static <T> Streamable<T> ofAll(Streamable<T>...streamables) {
        return Stream.of(streamables).reduce(Streamable::concat).orElseGet(Streamable::empty);
    }

    /**
     * Synonym for "get"
     * @return A stream over the streamable
     */
    default Stream<T> stream() {
        return get();
    }

    /**
     * Concatenate this streamable with another streamable.
     * @param streamable The streamable to concatenate.
     * @return A concatenated streamable, which streams over the concatenation of the streams produces by its source streamables.
     */
    default Streamable<T> concat(Streamable<T> streamable) {
        return () -> Stream.concat(stream(), streamable.stream());
    }

    /**
     * Create a streamable that transforms the streams produced by this streamable with a stream transformer.
     * @param transformer The transformer to apply to this streamable's streams.
     * @param <T2> The type of the streams produced by the transformation.
     * @return A streamable which produces the transformed streams.
     */
    default <T2> Streamable<T2> transform(Function<Stream<T>, Stream<T2>> transformer) {
        return () -> transformer.apply(stream());
    }

    /**
     * Transform this streamable's streams with the supplied map.
     * @param f The map to apply.
     * @param <T2> The mapped-to type.
     * @return A streamable which produces the transformed streams.
     */
    default <T2> Streamable<T2> map(Function<? super T, ? extends T2> f) {
        return transform(s -> s.map(f));
    }

    /**
     * Transform this streamable's streams with the supplied flatmap.
     * @param f The flatmap to apply.
     * @param <T2> The flatmapped-to type.
     * @return A streamable which produces the transformed streams.
     */
    default <T2> Streamable<T2> flatMap(Function<? super T, Stream<? extends T2>> f) {
        return transform(s -> s.flatMap(f));
    }

    /**
     * Transform this streamable's streams with the supplied filter predicate.
     * @param predicate The filter predicate to apply.
     * @return A streamable which produces the transformed streams.
     */
    default Streamable<T> filter(Predicate<? super T> predicate) {
        return transform(s -> s.filter(predicate));
    }


    /**
     * Transform this streamable's streams with the supplied filter predicate, rejecting items which match the predicate.
     * @param predicate The filter predicate to apply.
     * @return A streamable which produces the transformed streams.
     */
    default Streamable<T> reject(Predicate<? super T> predicate) {
        return transform(s -> s.filter(predicate.negate()));
    }

    /**
     * Transform this streamable's streams by sorting them.
     * @param comparator The comparator to use in sorting.
     * @return A streamable which produces the transformed streams.
     */
    default Streamable<T> sorted(Comparator<? super T> comparator) {
        return () -> stream().sorted(comparator);
    }

    /**
     * Transform this streamable's streams by skipping elements
     * @param n The number of elements to skip
     * @return A streamable which produces the transformed streams.
     */
    default Streamable<T> skip(long n) {
        return () -> stream().skip(n);
    }

    /**
     * Transform this streamable's streams by limiting the number of elements they can contain.
     * @param n The number of elements to limit to.
     * @return A streamable which produces the transformed streams.
     */
    default Streamable<T> limit(long n) {
        return () -> stream().limit(n);
    }

    /**
     * Stream this streamable, and call forEach on the resulting stream with the supplied action.
     * @param action The action to apply to each stream element.
     */
    default void forEach(Consumer<T> action) {
        stream().forEach(action);
    }

    /**
     * Stream this streamable, and call forEach on the resulting stream in order with the supplied action.
     * @param action The action to apply to each stream element.
     */
    default void forEachOrdered(Consumer<T> action) {
        stream().forEachOrdered(action);
    }

    /**
     * Stream this streamable, and collect the stream with the supplied collector.
     * @param collector The collector to use to collect streamed values.
     * @param <O> The output type of the collector.
     * @return The collected result.
     */
    default <O> O collect(Collector<T, ?, O> collector) {
        return stream().collect(collector);
    }

    /**
     * Stream this streamable, and collect the stream to a list.
     * @return The collected result.
     */
    default List<T> toList() {
        return collect(Collectors.toList());
    }

    /**
     * Stream this streamable, and collect the stream to a set.
     * @return The collected result.
     */
    default Set<T> toSet() {
        return collect(Collectors.toSet());
    }

    /**
     * Stream this streamable, and collect the stream to a map, extracting keys with the supplied index function.
     * @param indexFunction The function to use to extract keys from the streamed values.
     * @param <K> The type of the keys.
     * @return The collected result.
     */
    default <K> Map<K, T> toMap(Function<? super T, ? extends K> indexFunction) {
        return collect(Collectors.toMap(indexFunction, v -> v));
    }

    /**
     * Stream this streamable, and collect the stream to a map, extracting keys and values with the supplied functions.
     * @param keyFunction The function to use to extract keys from the stream.
     * @param valueFunction The function to use to extract values from the stream.
     * @param <K> The type of the keys.
     * @param <V> The type of the values.
     * @return The collected result.
     */
    default <K, V> Map<K, V> toMap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction) {
        return collect(Collectors.toMap(keyFunction, valueFunction));
    }

    /**
     * Stream this streamable, and collect the stream to an array.
     * @param arrayConstructor A function that will construct a new empty array of the required size.
     * @return The collected result.
     */
    default T[] toArray(IntFunction<T[]> arrayConstructor) {
        return stream().toArray(arrayConstructor);
    }

    /**
     * Stream this streamable, and collect the stream into a Seq.
     * @return The collected result.
     */
    default Seq<T> toSeq() {
        return Seq.of(stream());
    }

    /**
     * Stream and reduce the streamable, using the supplied identity, accumulator and combiner.
     * @param identity The identity to use when reducing.
     * @param accumulator The accumulator to use when reducing.
     * @param combiner The combiner to use when reducing.
     * @param <U> The type of the result.
     * @return The reduced result.
     */
    default <U> U reduce(U identity, BiFunction<U, T, U> accumulator, BinaryOperator<U> combiner){
        return get().reduce(identity, accumulator, combiner);
    }

    /**
     * Stream and reduce the streamable, using the supplied accumulator.
     * @param accumulator The accumulator to use when reducing.
     * @return The reduced result.
     */
    default Optional<T> reduce(BinaryOperator<T> accumulator) {
        return get().reduce(accumulator);
    }

    /**
     * Stream and reduce the streamable, using the supplied identity and accumulator.
     * @param identity The identity to use when reducing.
     * @param accumulator The accumulator to use when reducing.
     * @return The reduced result.
     */
    default T reduce(T identity, BinaryOperator<T> accumulator) {
        return get().reduce(identity, accumulator);
    }

}
