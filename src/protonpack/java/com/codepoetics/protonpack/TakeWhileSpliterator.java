package com.codepoetics.protonpack;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

final class TakeWhileSpliterator<T> implements Spliterator<T> {

    static <T> TakeWhileSpliterator<T> over(Spliterator<T> source, Predicate<T> condition) {
        return new TakeWhileSpliterator<>(source, condition);
    }

    private final Spliterator<T> source;
    private final Predicate<T> condition;
    private boolean conditionHolds = true;

    private TakeWhileSpliterator(Spliterator<T> source, Predicate<T> condition) {
        this.source = requireNonNull(source);
        this.condition = requireNonNull(condition);
    }


    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        return conditionHolds && source.tryAdvance(e -> {
            if (conditionHolds = condition.test(e)) {
                action.accept(e);
            }
        });
    }

    @Override
    public Spliterator<T> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return conditionHolds ? source.estimateSize() : 0;
    }

    @Override
    public int characteristics() {
        return source.characteristics() &~ Spliterator.SIZED;
    }
}
