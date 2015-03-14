package com.codepoetics.protonpack;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

final class SkipUntilSpliterator<T> implements Spliterator<T> {

    static <T> SkipUntilSpliterator<T> over(Spliterator<T> source, Predicate<T> condition) {
        return new SkipUntilSpliterator<>(source, condition);
    }

    private final Spliterator<T> source;
    private final Predicate<T> condition;
    private boolean conditionMet = false;

    private SkipUntilSpliterator(Spliterator<T> source, Predicate<T> condition) {
        this.source = requireNonNull(source);
        this.condition = requireNonNull(condition);
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (conditionMet) {
            return source.tryAdvance(action);
        }
        while (!conditionMet && source.tryAdvance(e -> {
            if (conditionMet = condition.test(e)) {
                action.accept(e);
            }
        }));
        return conditionMet;
    }

    @Override
    public void forEachRemaining(Consumer<? super T> action) {
        if (!conditionMet) {
            tryAdvance(action);
        }
        if (conditionMet) {
            source.forEachRemaining(action);
        }
    }

    @Override
    public Spliterator<T> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return conditionMet ? source.estimateSize() : Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return source.characteristics() &~ Spliterator.SIZED;
    }
}
