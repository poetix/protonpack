package com.codepoetics.protonpack.collectors;

import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collector;

public class CollectorUtils {
    public static <T, Y extends Comparable<Y>> Collector<T, ?, Optional<T>> maxBy(Function<T, Y> projection) {
        return maxBy(projection, Comparable::compareTo);
    }

    public static <T, Y> Collector<T, ?, Optional<T>>
    maxBy(Function<T, Y> projection, Comparator<Y> comparator) {
        return java.util.stream.Collectors.maxBy((a, b) -> {
            Y element1 = projection.apply(a);
            Y element2 = projection.apply(b);

            return comparator.compare(element1, element2);
        });
    }
    public static <T, Y extends Comparable<Y>> Collector<T, ?, Optional<T>> minBy(Function<T, Y> projection) {
        return minBy(projection, Comparable::compareTo);
    }

    public static <T, Y> Collector<T, ?, Optional<T>>
    minBy(Function<T, Y> projection, Comparator<Y> comparator) {
        return java.util.stream.Collectors.minBy((a, b) -> {
            Y element1 = projection.apply(a);
            Y element2 = projection.apply(b);

            return comparator.compare(element1, element2);
        });
    }
}
