package com.codepoetics.protonpack.comparators;

import com.codepoetics.protonpack.StreamUtils;

import java.util.Comparator;
import java.util.List;

public final class Comparators {

    private Comparators() {
    }

    public static <T> Comparator<? super List<T>> toListComparator(Comparator<? super T> itemComparator) {
        return (o1, o2) -> StreamUtils.zip(o1.stream(), o2.stream(), itemComparator::compare)
                .filter(c -> c != 0)
                .findFirst()
                .orElseGet(() -> Integer.compare(o1.size(), o2.size()));
    }
}
