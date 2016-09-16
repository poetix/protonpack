package com.codepoetics.protonpack;

import com.codepoetics.protonpack.comparators.Comparators;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.BiPredicate;
import java.util.function.Consumer;


class AggregatingSpliterator<I> implements Spliterator<List<I>> {

    private final Spliterator<I> source;
    private final BiPredicate<List<I>, I> condition;
    private boolean wasSameSlide = false;

    private List<I> currentSlide = new ArrayList<>();

    AggregatingSpliterator(Spliterator<I> source, BiPredicate<List<I>, I> predicate) {
        this.source = source;
        this.condition = predicate;
    }

    @Override
    public boolean tryAdvance(Consumer<? super List<I>> action) {
        boolean hadElements;
        do {
            hadElements = source.tryAdvance(curElem -> {
                        wasSameSlide = isSameSlide(curElem);
                        if(!wasSameSlide) {
                            action.accept(currentSlide);
                            currentSlide = new ArrayList<>();
                        }
                        currentSlide.add(curElem);
                    }
            );
        } while (wasSameSlide && hadElements);

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

    @Override
    public Comparator<? super List<I>> getComparator() {
        Comparator<? super I> comparator = source.getComparator();
        return comparator == null ? null : Comparators.toListComparator(comparator);
    }
}
