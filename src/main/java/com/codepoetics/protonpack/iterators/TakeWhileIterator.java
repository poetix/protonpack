package com.codepoetics.protonpack.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;
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
        return source.peek().map(predicate::test).orElse(false);
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return source.next();
    }
}
