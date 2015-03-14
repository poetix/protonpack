package com.codepoetics.protonpack.selector;

import java.util.function.Function;

@FunctionalInterface
public interface Selector<T> extends Function<T[], Integer> {
}
