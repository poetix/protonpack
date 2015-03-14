package com.codepoetics.protonpack;

import java.util.Optional;
import java.util.Spliterator;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

public class PeekableSpliterator<T> implements Spliterator<T> {

    public static <T> PeekableSpliterator<T> peeking(Spliterator<T> spliterator) {
        return new PeekableSpliterator<>(spliterator);
    }

    private final Spliterator<T> source;
    private Optional<T> buffer = Optional.empty();

    private PeekableSpliterator(Spliterator<T> source) {
        this.source = requireNonNull(source);
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        boolean hadNext = tryPeek(action);
        buffer = Optional.empty();
        return hadNext;
    }

    public boolean tryPeek(Consumer<? super T> action) {
        if (buffer.isPresent()) {
            buffer.ifPresent(action);
            return true;
        }
        return source.tryAdvance(e -> {
            buffer = Optional.of(e);
            action.accept(e);
        });
    }

    @Override
    public Spliterator<T> trySplit() {
        return source.trySplit();
    }

    @Override
    public long estimateSize() {
        return source.estimateSize();
    }

    @Override
    public int characteristics() {
        return source.characteristics();
    }
}
