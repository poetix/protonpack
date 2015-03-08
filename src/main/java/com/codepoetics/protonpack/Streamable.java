package com.codepoetics.protonpack;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public interface Streamable<T> {

    static <T> Streamable<T> empty() {
        return Stream::empty;
    }

    static <T> Streamable<T> of(Streamable<T> streamable) {
        return streamable;
    }

    static <T> Streamable<T> of(T...items) {
        return () -> Stream.of(items);
    }

    static <T> Streamable<T> of(Collection<T> collection) {
        return collection::stream;
    }

    static <T> Streamable<T> of(Iterable<T> iterable) {
        return () -> StreamSupport.stream(iterable.spliterator(), false);
    }

    @SafeVarargs
    static <T> Streamable<T> of(Streamable<T>...streamables) {
        return Stream.of(streamables).reduce(Streamable::concat).orElseGet(Streamable::empty);
    }

    Stream<T> stream();

    default Streamable<T> concat(Streamable<T> streamable) {
        return () -> Stream.concat(stream(), streamable.stream());
    }

    default <T2> Streamable<T2> transform(Function<Stream<T>, Stream<T2>> transformer) {
        return () -> transformer.apply(stream());
    }

    default <T2> Streamable<T2> map(Function<? super T, ? extends T2> f) {
        return transform(s -> s.map(f));
    }

    default <T2> Streamable<T2> flatMap(Function<? super T, Stream<? extends T2>> f) {
        return transform(s -> s.flatMap(f));
    }

    default Streamable<T> filter(Predicate<? super T> predicate) {
        return transform(s -> s.filter(predicate));
    }

    default Streamable<T> sorted(Comparator<? super T> comparator) {
        return () -> stream().sorted(comparator);
    }

    default Streamable<T> skip(long n) {
        return () -> stream().skip(n);
    }

    default void forEach(Consumer<T> action) {
        stream().forEach(action);
    }

    default void forEachOrdered(Consumer<T> action) {
        stream().forEachOrdered(action);
    }

    default <O> O collect(Collector<T, ?, O> collector) {
        return stream().collect(collector);
    }

    default List<T> toList() {
        return collect(Collectors.toList());
    }

    default Set<T> toSet() {
        return collect(Collectors.toSet());
    }

    default <K> Map<K, T> toMap(Function<? super T, ? extends K> indexFunction) {
        return collect(Collectors.toMap(indexFunction, v -> v));
    }

    default <K, V> Map<K, V> toMap(Function<? super T, ? extends K> keyFunction, Function<? super T, ? extends V> valueFunction) {
        return collect(Collectors.toMap(keyFunction, valueFunction));
    }

    default T[] toArray(IntFunction<T[]> arrayConstructor) {
        return stream().toArray(arrayConstructor);
    }

}
