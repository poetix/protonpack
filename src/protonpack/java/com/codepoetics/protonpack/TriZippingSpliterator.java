package com.codepoetics.protonpack;

import com.codepoetics.protonpack.function.TriFunction;

import java.util.Spliterator;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

final class TriZippingSpliterator<L, M, R, O> implements Spliterator<O> {

    static <L, M, R, O> Spliterator<O> zipping(Spliterator<L> lefts, Spliterator<M> middles, Spliterator<R> rights, TriFunction<L, M, R, O> combiner) {
        return new TriZippingSpliterator<>(lefts, middles, rights, combiner);
    }

    private final Spliterator<L> lefts;
    private final Spliterator<M> middles;
    private final Spliterator<R> rights;
    private final TriFunction<L, M, R, O> combiner;
    private boolean middleHadNext = false;
    private boolean rightHadNext = false;

    private TriZippingSpliterator(Spliterator<L> lefts, Spliterator<M> middles, Spliterator<R> rights, TriFunction<L, M, R, O> combiner) {
        this.lefts = requireNonNull(lefts);
        this.rights = requireNonNull(rights);
        this.middles = requireNonNull(middles);
        this.combiner = requireNonNull(combiner);
    }

    @Override
    public boolean tryAdvance(Consumer<? super O> action) {
        middleHadNext = false;
        rightHadNext = false;
        boolean leftHadNext = lefts.tryAdvance(l ->
            middles.tryAdvance(m -> {
                middleHadNext = true;
                rights.tryAdvance(r -> {
                    rightHadNext = true;
                    action.accept(combiner.apply(l, m, r));
                });
            }));

        return leftHadNext && middleHadNext && rightHadNext;
    }

    @Override
    public Spliterator<O> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return Math.min(lefts.estimateSize(), Math.min(middles.estimateSize(), rights.estimateSize()));
    }

    @Override
    public int characteristics() {
        return lefts.characteristics() & rights.characteristics()
                & ~(Spliterator.DISTINCT | Spliterator.SORTED);
    }
}
