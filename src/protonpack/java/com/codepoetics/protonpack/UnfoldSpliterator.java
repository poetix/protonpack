package com.codepoetics.protonpack;

import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;

class UnfoldSpliterator<T> implements Spliterator<T> {

    static <T> UnfoldSpliterator<T> over(T seed, Function<T, Optional<T>> generator) {
        return new UnfoldSpliterator<>(seed, generator);
    }

    private Optional<T> current;
    private final Function<T, Optional<T>> generator;

    private UnfoldSpliterator(T seed, Function<T, Optional<T>> generator) {
        this.current = Optional.of(seed);
        this.generator = generator;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        current.ifPresent(action);
        current = current.flatMap(generator);
        return current.isPresent();
    }

    @Override
    public Spliterator<T> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return Spliterator.IMMUTABLE & Spliterator.NONNULL & Spliterator.ORDERED;
    }
}
