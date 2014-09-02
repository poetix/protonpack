package com.codepoetics.protonpack.iterators;

import java.util.Iterator;
import java.util.function.BiFunction;

/**
 * An iterator which wraps a pair of iterators, "zipping" together their values until either iterator runs out of values.
 * Each pair of values is combined into a single value using the supplied combiner function
 * @param <L> The type over which the "left" iterator iterates.
 * @param <R> The type over which the "right" iterator iterates.
 * @param <O> The type over which the resulting ZippingIterator iterates.
 */
public class ZippingIterator<L, R, O> implements Iterator<O> {

    /**
     * Wrap the supplied iterators and combiner function into a Zipping iterator, which "zips" together the values from
     * the "left" and "right" iterators until either iterator runs out of values. Each pair of values is combined into
     * a single value using the supplied combiner function
     * @param lefts The "left" iterator to zip.
     * @param rights The "right" iterator to zip.
     * @param combiner The function to combine "left" and "right" values.
     * @param <L> The type over which the "left" iterator iterates.
     * @param <R> The type over which the "right" iterator iterates.
     * @param <O> The type created by the combiner out of pairs of "left" and "right" values, over which the resulting
     *           iterator iterates.
     * @return The constructed zipping iterator.
     */
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
