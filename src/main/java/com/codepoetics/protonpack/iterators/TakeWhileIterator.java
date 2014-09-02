package com.codepoetics.protonpack.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * An iterator wrapping a source iterator, which returns elements from the source until it finds
 * one that does not satisfy the supplied predicate.
 * @param <T> The type over which the iterator iterates.
 */
public class TakeWhileIterator<T> implements Iterator<T> {
    /**
     * Wrap the supplied source iterator with a TakeWhileIterator, which returns elements from the source until it finds
     * one that does not satisfy the supplied predicate.
     * @param source The source iterator.
     * @param predicate The predicate used to test each element of the source iterator.
     * @param <T> The type over which the iterator iterates.
     * @return The wrapping iterator.
     */
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
