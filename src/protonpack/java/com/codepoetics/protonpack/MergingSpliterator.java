package com.codepoetics.protonpack;

import java.util.ArrayList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;

final class MergingSpliterator<T, O> implements Spliterator<O> {

    public static <T, O> Spliterator<O> merging(Spliterator<T>[] sources, Supplier<O> unitSupplier, BiFunction<O, T, O> merger) {
        return new MergingSpliterator<>(sources, unitSupplier, merger);
    }

    private final Spliterator<T>[] sources;
    private final Supplier<O> unitSupplier;
    private final BiFunction<O, T, O> merger;

    private MergingSpliterator(Spliterator<T>[] sources, Supplier<O> unitSupplier, BiFunction<O, T, O> merger) {
        this.sources = requireNonNull(sources);
        this.unitSupplier = requireNonNull(unitSupplier);
        this.merger = requireNonNull(merger);
    }

    @Override
    public boolean tryAdvance(Consumer<? super O> action) {
        List<T> mergeables = new ArrayList<>(sources.length);
        Stream.of(sources).forEach(s -> s.tryAdvance(mergeables::add));

        if (mergeables.isEmpty()) {
            return false;
        }

        O unit = unitSupplier.get();

        action.accept(mergeables.stream().reduce(unit, merger,
                (l1, l2) -> l1)); // We never do this in parallel, so fuck it.
        return true;
    }

    @Override
    public Spliterator<O> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return Stream.of(sources).map(Spliterator::estimateSize).max(Long::compare).orElse(0L);
    }

    @Override
    public int characteristics() {
        return Spliterator.NONNULL & Spliterator.ORDERED & Spliterator.IMMUTABLE;
    }
}
