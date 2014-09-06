package com.codepoetics.protonpack;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

class InterleavingSpliterator<T> implements Spliterator<T> {

    public static <T> Spliterator<T> interleaving(Spliterator<T>[] spliterators, Function<T[], Integer> selector) {
        Supplier<T[]> bufferedValues = () -> {
            T[] values = (T[]) new Object[spliterators.length];

            for (int i=0; i<spliterators.length; i++) {
                final int stableIndex = i;
                spliterators[i].tryAdvance(t -> values[stableIndex] = t);
            }

            return values;
        };

        return new InterleavingSpliterator<>(spliterators, bufferedValues, selector);
    }

    private final Spliterator<T>[] spliterators;
    private final Supplier<T[]> bufferSupplier;
    private T[] buffer = null;
    private final Function<T[], Integer> selector;

    private InterleavingSpliterator(Spliterator<T>[] spliterators, Supplier<T[]> bufferSupplier, Function<T[], Integer> selector) {
        this.spliterators = spliterators;
        this.bufferSupplier = bufferSupplier;
        this.selector = selector;
    }

    @Override
    public boolean tryAdvance(Consumer<? super T> action) {
        if (buffer == null) {
            buffer = bufferSupplier.get();
        }

        if (Stream.of(buffer).allMatch(Predicate.isEqual(null))) {
            return false;
        }

        int selected = selector.apply(buffer);
        action.accept(buffer[selected]);

        if (!spliterators[selected].tryAdvance(t -> buffer[selected] = t)) {
            buffer[selected] = null;
        }

        return true;
    }

    @Override
    public Spliterator<T> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        if (Stream.of(spliterators).anyMatch(s -> s.estimateSize() == Long.MAX_VALUE)) {
            return Long.MAX_VALUE;
        }
        return Stream.of(spliterators).mapToLong(Spliterator::estimateSize).sum();
    }

    @Override
    public long getExactSizeIfKnown() {
        if (Stream.of(spliterators).allMatch(s -> s.hasCharacteristics(Spliterator.SIZED))) {
            return Stream.of(spliterators).mapToLong(Spliterator::getExactSizeIfKnown).sum();
        }
        return -1;
    }

    @Override
    public int characteristics() {
        return Spliterator.NONNULL & Spliterator.ORDERED & Spliterator.IMMUTABLE;
    }
}
