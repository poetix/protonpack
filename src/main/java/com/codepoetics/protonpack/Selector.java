package com.codepoetics.protonpack;

import java.util.function.Function;

@FunctionalInterface
public interface Selector<T> extends Function<T[], Integer> {
}
