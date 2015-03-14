package com.codepoetics.protonpack;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;


final class AggregatingSpliterator<I> implements Spliterator<List<I>> {
    
    private final Spliterator<I> source;
    private final BiPredicate<List<I>, I> condition;
    
    private List<I> currentSlide = new ArrayList<>();

    AggregatingSpliterator(Spliterator<I> source, BiPredicate<List<I>, I> predicate) {
        this.source = requireNonNull(source);
        this.condition = requireNonNull(predicate);
    }

    @Override
    public boolean tryAdvance(Consumer<? super List<I>> action) {
        boolean hadElements = source.tryAdvance(curElem -> {
                if(!isSameSlide(curElem)) {
                    action.accept(currentSlide);
                    currentSlide = new ArrayList<>();
                }
                currentSlide.add(curElem);
            }
        );
        if (!hadElements && !currentSlide.isEmpty()) {
            action.accept(currentSlide);
            currentSlide = new ArrayList<>();
        }
        return hadElements;
    }

    private boolean isSameSlide(I curEl) {
        return currentSlide.isEmpty() || condition.test(currentSlide, curEl);
    }
    
    @Override
    public Spliterator<List<I>> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return source.estimateSize();
    }

    @Override
    public int characteristics() {
        return source.characteristics() & ~Spliterator.SIZED & ~Spliterator.CONCURRENT;
    }
}