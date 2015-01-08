package com.codepoetics.protonpack;

import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.function.Consumer;

class ZippingSpliterator<L, R, O> implements Spliterator<O> {

    static <L, R, O> Spliterator<O> zipping(Spliterator<L> lefts, Spliterator<R> rights, BiFunction<L, R, O> combiner, boolean isParallel) {
        return new ZippingSpliterator<>(lefts, rights, combiner, isParallel);
    }

    private final Spliterator<L> lefts;
    private final Spliterator<R> rights;
    private final BiFunction<L, R, O> combiner;
    private boolean rightHadNext = false;
    private boolean isParallel;

    private ZippingSpliterator(Spliterator<L> lefts, Spliterator<R> rights, BiFunction<L, R, O> combiner, boolean isParallel) {
        this.lefts = lefts;
        this.rights = rights;
        this.combiner = combiner;
        this.isParallel = isParallel;
    }

    @Override
    public boolean tryAdvance(Consumer<? super O> action) {
        rightHadNext = false;
        boolean leftHadNext = lefts.tryAdvance(l ->
            rights.tryAdvance(r -> {
                rightHadNext = true;
                action.accept(combiner.apply(l, r));
            }));
        return leftHadNext && rightHadNext;
    }

    @Override
    public Spliterator<O> trySplit() {
    	if(!isParallel){
    		return null;
    	}
    	Spliterator<L> newLefts = lefts.trySplit();
    	if(newLefts == null){
    		return null;
    	}
        Spliterator<R> newRights = rights.trySplit();
    	if(newRights == null){
    		return null;
    	}
    	return zipping(newLefts, newRights, combiner, isParallel);
    }

    @Override
    public long estimateSize() {
        return Math.min(lefts.estimateSize(), rights.estimateSize());
    }

    @Override
    public int characteristics() {
        return lefts.characteristics() & rights.characteristics()
                & ~(Spliterator.DISTINCT | Spliterator.SORTED);
    }
}
