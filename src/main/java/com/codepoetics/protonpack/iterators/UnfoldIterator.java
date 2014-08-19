package com.codepoetics.protonpack.iterators;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;

public class UnfoldIterator<T> implements Iterator<T> {

    public static <T> Iterator<T> over(T seed, Function<T, Optional<T>> generator) {
        return new UnfoldIterator<T>(seed, generator);
    }

    public UnfoldIterator(T seed, Function<T, Optional<T>> generator) {
        this.current = Optional.ofNullable(seed);
        this.generator = generator;
    }

    private final Function<T, Optional<T>> generator;
    private Optional<T> current;

    @Override
    public boolean hasNext() {
        return current.isPresent();
    }

    @Override
    public T next() {
        T next = current.orElse(null);
        current = current.flatMap(generator);
        return next;
    }
}
