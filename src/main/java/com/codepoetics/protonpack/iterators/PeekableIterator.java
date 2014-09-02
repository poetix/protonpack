package com.codepoetics.protonpack.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * An iterator wrapping a source iterator, that buffers one element and makes it available for lookahead.
 *
 * @param <T> The type over which the iterator iterates.
 */
public interface PeekableIterator<T> extends Iterator<T> {

    /**
     * Wrap the source iterator, returning a peekable iterator that buffers one element and makes it available for lookahead.
     *
     * @param source The source iterator.
     * @param <T> The type over which the iterator iterates.
     * @return The wrapping, peekable iterator.
     */
    public static <T> PeekableIterator<T> peeking(Iterator<T> source) {
        return new PeekableIterator<T>() {

            private Optional<T> buffer = Optional.empty();

            @Override
            public Optional<T> peek() {
                if (!buffer.isPresent() && source.hasNext()) {
                    buffer = Optional.of(source.next());
                }
                return buffer;
            }

            @Override
            public boolean hasNext() {
                return peek().isPresent();
            }

            @Override
            public T next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                T next = buffer.get();
                buffer = Optional.empty();
                return next;
            }
        };
    }

    /**
     * Look ahead to the next element in the iterator without consuming it.
     *
     * @return Optional.empty if there is no next element, otherwise an Optional wrapping the next element.
     */
    Optional<T> peek();

}
