package com.codepoetics.protonpack.iterators;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

public interface PeekableIterator<T> extends Iterator<T> {

    public static <T> PeekableIterator<T> peeking(Iterator<T> source) {
        return new PeekableIterator<T>() {

            private T buffer = null;

            @Override
            public T peek() {
                if (buffer == null && source.hasNext()) {
                    buffer = source.next();
                }
                return buffer;
            }

            @Override
            public boolean hasNext() {
                return peek() != null;
            }

            @Override
            public T next() {
                T next = buffer == null ? source.next() : buffer;
                buffer = null;
                return next;
            }
        };
    }

    T peek();

}
