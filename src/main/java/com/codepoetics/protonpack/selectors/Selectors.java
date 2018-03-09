package com.codepoetics.protonpack.selectors;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

public final class Selectors {
    private Selectors() {
    }

    public static <T> Selector<T> roundRobin() {
        return new Selector<T>() {
            private int startIndex = 0;
            @Override
            public Integer apply(T[] options) {
                int result = startIndex;
                while (options[result] == null) {
                    result = (result + 1) % options.length;
                }

                startIndex = (result + 1) % options.length;
                return result;
            }
        };
    }

    public static <T extends Comparable<T>> Selector<T> takeMin() {
        return takeMin(Comparator.naturalOrder());
    }

    public static <T> Selector<T> takeMin(Comparator<? super T> comparator) {
        return new Selector<T>() {

            private int startIndex = 0;

            @Override
            public Integer apply(T[] options) {
                T smallest = Stream.of(options).filter(Objects::nonNull).min(comparator).get();

                int result = startIndex;
                while (options[result] == null || comparator.compare(smallest, options[result]) != 0) {
                    result = (result + 1) % options.length;
                }

                startIndex = (result + 1) % options.length;
                return result;
            }
        };
    }

    public static <T extends Comparable<T>> Selector<T> takeMax() {
        return takeMax(Comparator.naturalOrder());
    }

    public static <T> Selector<T> takeMax(Comparator<? super T> comparator) {
        return takeMin(comparator.reversed());
    }
}
