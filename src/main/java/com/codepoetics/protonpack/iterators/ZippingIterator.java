package com.codepoetics.protonpack.iterators;

import java.util.Iterator;
import java.util.function.BiFunction;

public class ZippingIterator<L, R, O> implements Iterator<O> {

    public static <L, R, O> Iterator<O> over(Iterator<L> lefts, Iterator<R> rights, BiFunction<L, R, O> combiner) {
        return new ZippingIterator<>(lefts, rights, combiner);
    }

    private final Iterator<L> lefts;
    private final Iterator<R> rights;
    private final BiFunction<L, R, O> combiner;

    private ZippingIterator(Iterator<L> lefts, Iterator<R> rights, BiFunction<L, R, O> combiner) {
        this.lefts = lefts;
        this.rights = rights;
        this.combiner = combiner;
    }

    @Override
    public boolean hasNext() {
        return lefts.hasNext() && rights.hasNext();
    }

    @Override
    public O next() {
        return combiner.apply(lefts.next(), rights.next());
    }
}
