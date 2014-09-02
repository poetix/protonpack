package com.codepoetics.protonpack.iterators;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;

/**
 * An iterator which generates elements from a seed and a generator function. The iterator stops when the function
 * returns Optional.empty().
 *
 * @param <T> The type over which the iterator iterates.
 */
public class UnfoldIterator<T> implements Iterator<T> {

    /**
     * Creates an iterator which takes the seed value and applies the generator to create the next value, feeding each
     * new value back into the generator to create subsequent values. If the generator returns Optional.empty(), then
     * the iterator has no more values.
     *
     * @param seed The seed value.
     * @param generator The generator to use to create new values.
     * @param <T> The type over which the iterator iterates.
     * @return The constructed iterator.
     */
    public static <T> Iterator<T> over(T seed, Function<T, Optional<T>> generator) {
        return new UnfoldIterator<T>(seed, generator);
    }

    private UnfoldIterator(T seed, Function<T, Optional<T>> generator) {
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
