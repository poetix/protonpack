package com.codepoetics.protonpack.iterators;

import java.util.Iterator;
import java.util.function.Predicate;

public class TakeWhileIterator<T> implements Iterator<T> {

    public static <T> Iterator<T> over(Iterator<T> source, Predicate<T> predicate) {
        return new TakeWhileIterator<>(
            PeekableIterator.peeking(source),
            predicate);
    }

    private final PeekableIterator<T> source;
    private final Predicate<T> predicate;

    private TakeWhileIterator(PeekableIterator<T> source, Predicate<T> predicate) {
        this.source = source;
        this.predicate = predicate;
    }

    @Override
    public boolean hasNext() {
        T peeked = source.peek();
        return peeked != null && predicate.test(peeked);
    }

    @Override
    public T next() {
        T next = source.next();
        return predicate.test(next) ? next : null;
    }
}
