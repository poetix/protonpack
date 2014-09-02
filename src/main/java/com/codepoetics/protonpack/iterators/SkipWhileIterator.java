package com.codepoetics.protonpack.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * An iterator wrapping a source iterator, which advances to the first element that does not satisfy the supplied predicate
 * and then continues iterating from there.
 *
 * @param <T> The type over which the iterator iterates.
 */
public class SkipWhileIterator<T> implements Iterator<T> {

    /**
     * Wrap the supplied source iterator with a SkipWhileIterator, which advances to the first element
     * that does not satisfy the supplied predicate and then continues iterating from there.
     * @param source The source iterator.
     * @param predicate The predicate used to test each element of the source iterator.
     * @param <T> The type over which the iterator iterates.
     * @return The wrapping iterator.
     */
    public static <T> Iterator<T> over(Iterator<T> source, Predicate<T> predicate) {
        return new SkipWhileIterator<>(
            PeekableIterator.peeking(source),
            predicate);
    }

    private final PeekableIterator<T> source;
    private final Predicate<T> predicate;
    private boolean foundFirst = false;

    private SkipWhileIterator(PeekableIterator<T> source, Predicate<T> predicate) {
        this.source = source;
        this.predicate = predicate;
    }

    private boolean seekFirst() {
        while(source.hasNext()) {
            if (!source.peek().map(predicate::test).orElse(false)) {
                return true;
            }
            source.next();
        }
        return false;
    }

    @Override
    public boolean hasNext() {
        if (!foundFirst) {
            return foundFirst = seekFirst();
        }
        return source.hasNext();
    }

    @Override
    public T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        return source.next();
    }
}
