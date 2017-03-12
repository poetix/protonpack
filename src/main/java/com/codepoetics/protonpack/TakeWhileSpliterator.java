package com.codepoetics.protonpack;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

class TakeWhileSpliterator<T> implements Spliterator<T> {

    static <T> TakeWhileSpliterator<T> over(Spliterator<T> source, Predicate<T> condition) {
        return new TakeWhileSpliterator<>(source, condition, false);
    }

    static <T> TakeWhileSpliterator<T> overInclusive(Spliterator<T> source, Predicate<T> condition) {
        return new TakeWhileSpliterator<>(source, condition, true);
    }

    private final Spliterator<T> source;
    private final Predicate<T> condition;
    private final boolean inclusive;
    private boolean conditionHeldSoFar = true;

    private TakeWhileSpliterator(Spliterator<T> source, Predicate<T> condition, boolean inclusive) {
        this.source = source;
        this.condition = condition;
        this.inclusive = inclusive;
    }


    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        return conditionHeldSoFar && source.tryAdvance(e -> {
            if (condition.test(e)) {
                action.accept(e);
            } else {
                if (inclusive && conditionHeldSoFar) {
                    action.accept(e);
                }
                conditionHeldSoFar = false;
            }
        });
    }

    @Override
    public Spliterator<T> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return conditionHeldSoFar ? source.estimateSize() : 0;
    }

    @Override
    public int characteristics() {
        return source.characteristics() &~ Spliterator.SIZED;
    }

    @Override
    public Comparator<? super T> getComparator() {
        return source.getComparator();
    }
}
