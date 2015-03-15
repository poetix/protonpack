package com.codepoetics.protonpack.selectors;

import java.util.function.Function;

@FunctionalInterface
public interface Selector<T> extends Function<T[], Integer> {
}
