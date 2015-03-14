package com.codepoetics.protonpack;

import java.util.Spliterator;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

class TappedSpliterator<T> implements Spliterator<T> {

    public static <T> TappedSpliterator<T> tapping(Spliterator<T> source, Consumer<? super T> tap) {
        return new TappedSpliterator<>(source, tap);
    }

    private final Spliterator<T> source;
    private final Consumer<? super T> tap;

    TappedSpliterator(Spliterator<T> source, Consumer<? super T> tap) {
        this.source = requireNonNull(source);
        this.tap = requireNonNull(tap);
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        return source.tryAdvance(tapped(action));
    }

    private Consumer<? super T> tapped(Consumer<? super T> action) {
        return ((Consumer<T>) action).andThen(tap);
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        source.forEachRemaining(tapped(action));
    }

    @Override
    public Spliterator<T> trySplit() {
        Spliterator<T> maybeSplit = source.trySplit();
        if (maybeSplit == null) {
            return null;
        }
        return tapping(maybeSplit, tap);
    }

    @Override
    public long estimateSize() {
        return source.estimateSize();
    }

    @Override
    public long getExactSizeIfKnown() {
        return source.getExactSizeIfKnown();
    }

    @Override
    public int characteristics() {
        return source.characteristics();
    }
}
