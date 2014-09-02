package com.codepoetics.protonpack.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface PeekableIterator<T> extends Iterator<T> {

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

    Optional<T> peek();

}
