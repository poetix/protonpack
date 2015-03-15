/*
 * Author: 		Alexis Cartier <alexcrt>
 * Date :  		24 déc. 2014
 */

package com.codepoetics.protonpack.maps;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Spliterator;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Default implementation of a {@code MapStream<K, V>}. 
 */
final class DefaultMapStream<K, V> implements MapStream<K, V> {

    private final Stream<Entry<K, V>> delegate;

    DefaultMapStream(Stream<Entry<K, V>> stream) {
        delegate = stream;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return delegate.iterator();
    }

    @Override
    public Spliterator<Entry<K, V>> spliterator() {
        return delegate.spliterator();
    }

    @Override
    public boolean isParallel() {
        return delegate.isParallel();
    }

    @Override
    public MapStream<K, V> sequential() {
        return new DefaultMapStream<>(delegate.sequential());
    }

    @Override
    public MapStream<K, V> parallel() {
        return new DefaultMapStream<>(delegate.parallel());
    }

    @Override
    public MapStream<K, V> unordered() {
        return new DefaultMapStream<>(delegate.unordered());
    }

    @Override
    public MapStream<K, V> onClose(Runnable closeHandler) {
        return new DefaultMapStream<>(delegate.onClose(closeHandler));
    }

    @Override
    public void close() {
        delegate.close();        
    }

    @Override
    public MapStream<K, V> filter(Predicate<? super Entry<K, V>> predicate) {
        return new DefaultMapStream<>(delegate.filter(predicate));
    }

    @Override
    public <R> Stream<R> map(Function<? super Entry<K, V>, ? extends R> mapper) {
        return delegate.map(mapper);
    }

    @Override
    public IntStream mapToInt(ToIntFunction<? super Entry<K, V>> mapper) {
        return delegate.mapToInt(mapper);
    }

    @Override
    public LongStream mapToLong(ToLongFunction<? super Entry<K, V>> mapper) {
        return delegate.mapToLong(mapper);
    }

    @Override
    public DoubleStream mapToDouble(ToDoubleFunction<? super Entry<K, V>> mapper) {
        return delegate.mapToDouble(mapper);
    }

    @Override
    public <R> Stream<R> flatMap(
            Function<? super Entry<K, V>, ? extends Stream<? extends R>> mapper) {
        return delegate.flatMap(mapper);
    }

    @Override
    public IntStream flatMapToInt(
            Function<? super Entry<K, V>, ? extends IntStream> mapper) {
        return delegate.flatMapToInt(mapper);
    }

    @Override
    public LongStream flatMapToLong(
            Function<? super Entry<K, V>, ? extends LongStream> mapper) {
        return delegate.flatMapToLong(mapper);
    }

    @Override
    public DoubleStream flatMapToDouble(
            Function<? super Entry<K, V>, ? extends DoubleStream> mapper) {
        return delegate.flatMapToDouble(mapper);
    }

    @Override
    public MapStream<K, V> distinct() {
        return new DefaultMapStream<>(delegate.distinct());
    }

    @Override
    public MapStream<K, V> sorted() {
        return new DefaultMapStream<>(delegate.sorted());
    }

    @Override
    public MapStream<K, V> sorted(Comparator<? super Entry<K, V>> comparator) {
        return new DefaultMapStream<>(delegate.sorted(comparator));
    }

    @Override
    public MapStream<K, V> peek(Consumer<? super Entry<K, V>> action) {
        return new DefaultMapStream<>(delegate.peek(action));
    }

    @Override
    public MapStream<K, V> limit(long maxSize) {
        return new DefaultMapStream<>(delegate.limit(maxSize));
    }

    @Override
    public MapStream<K, V> skip(long n) {
        return new DefaultMapStream<>(delegate.skip(n));
    }

    @Override
    public void forEach(Consumer<? super Entry<K, V>> action) {
        delegate.forEach(action);
    }

    @Override
    public void forEachOrdered(Consumer<? super Entry<K, V>> action) {
        delegate.forEachOrdered(action);
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <A> A[] toArray(IntFunction<A[]> generator) {
        return delegate.toArray(generator);
    }

    @Override
    public Entry<K, V> reduce(Entry<K, V> identity,
            BinaryOperator<Entry<K, V>> accumulator) {
        return delegate.reduce(identity, accumulator);
    }

    @Override
    public Optional<Entry<K, V>> reduce(BinaryOperator<Entry<K, V>> accumulator) {
        return delegate.reduce(accumulator);
    }

    @Override
    public <U> U reduce(U identity,
            BiFunction<U, ? super Entry<K, V>, U> accumulator,
            BinaryOperator<U> combiner) {
        return delegate.reduce(identity, accumulator, combiner);
    }

    @Override
    public <R> R collect(Supplier<R> supplier,
            BiConsumer<R, ? super Entry<K, V>> accumulator,
                    BiConsumer<R, R> combiner) {
        return delegate.collect(supplier, accumulator, combiner);
    }

    @Override
    public <R, A> R collect(Collector<? super Entry<K, V>, A, R> collector) {
        return delegate.collect(collector);
    }

    @Override
    public Optional<Entry<K, V>> min(Comparator<? super Entry<K, V>> comparator) {
        return delegate.min(comparator);
    }

    @Override
    public Optional<Entry<K, V>> max(Comparator<? super Entry<K, V>> comparator) {
        return delegate.max(comparator);
    }

    @Override
    public long count() {
        return delegate.count();
    }

    @Override
    public boolean anyMatch(Predicate<? super Entry<K, V>> predicate) {
        return delegate.anyMatch(predicate);
    }

    @Override
    public boolean allMatch(Predicate<? super Entry<K, V>> predicate) {
        return delegate.allMatch(predicate);
    }

    @Override
    public boolean noneMatch(Predicate<? super Entry<K, V>> predicate) {
        return delegate.noneMatch(predicate);
    }

    @Override
    public Optional<Entry<K, V>> findFirst() {
        return delegate.findFirst();
    }

    @Override
    public Optional<Entry<K, V>> findAny() {
        return delegate.findAny();
    }
}
