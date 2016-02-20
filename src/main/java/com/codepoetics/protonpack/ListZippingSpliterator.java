package com.codepoetics.protonpack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;

class ListZippingSpliterator<T, O> implements Spliterator<O> {

    static <T, O> Spliterator<O> zipping(List<Spliterator<T>> spliterators, Function<List<T>, O> combiner) {
        return new ListZippingSpliterator<>(spliterators, combiner);
    }

    private final List<Spliterator<T>> spliterators;
    private final Function<List<T>, O> combiner;
    private boolean hadNext;

    private ListZippingSpliterator(List<Spliterator<T>> spliterators, Function<List<T>, O> combiner) {
        this.spliterators = spliterators;
        this.combiner = combiner;
    }

    @Override
    public boolean tryAdvance(Consumer<? super O> action) {
        hadNext = false;
        Iterator<Spliterator<T>> si = spliterators.iterator();
        if(si.hasNext()) {
            tryAdvance(si, new ArrayList<>(spliterators.size()), action);
        }
        return hadNext;
    }

    private void tryAdvance(Iterator<Spliterator<T>> si, List<T> acc, Consumer<? super O> action) {
        if (si.hasNext()) {
            si.next().tryAdvance(s -> {
                acc.add(s);
                tryAdvance(si, acc, action);
            });
        } else {
            action.accept(combiner.apply(acc));
            hadNext = true;
        }
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
