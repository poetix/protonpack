package com.codepoetics.protonpack;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.util.function.Predicate.isEqual;

class ListZippingSpliterator<T, O> implements Spliterator<O> {

    static <T, O> Spliterator<O> zipping(List<Spliterator<T>> spliterators, Function<List<T>, O> combiner) {
        return new ListZippingSpliterator<>(spliterators, combiner);
    }

    private final List<Spliterator<T>> spliterators;
    private final Function<List<T>, O> combiner;

    private ListZippingSpliterator(List<Spliterator<T>> spliterators, Function<List<T>, O> combiner) {
        this.spliterators = spliterators;
        this.combiner = combiner;
    }

    @Override
    public boolean tryAdvance(Consumer<? super O> action) {
        if (spliterators.isEmpty()) {
            return false;
        }
        List<T> acc = new ArrayList<>(spliterators.size());
        boolean hadNext = spliterators.stream()
                .map(s -> s.tryAdvance(acc::add))
                .allMatch(isEqual(Boolean.TRUE));
        if (hadNext) {
            action.accept(combiner.apply(acc));
        }
        return hadNext;
    }

    @Override
    public Spliterator<O> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        // TODO: benchmark and cache?
        return spliterators.stream()
                .mapToLong(Spliterator::estimateSize)
                .min().orElse(0);
    }

    @Override
    public int characteristics() {
        // TODO: benchmark and cache?
        int characteristics = spliterators.stream()
                .mapToInt(Spliterator::characteristics)
                .reduce(0, (i, j) -> i & j);
        return characteristics & ~(Spliterator.DISTINCT | Spliterator.SORTED);
    }
}
