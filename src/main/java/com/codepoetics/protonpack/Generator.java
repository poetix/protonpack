package com.codepoetics.protonpack;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface Generator<T> extends Function<T, Optional<T>> {

    static <T> Generator<T> withCondition(UnaryOperator<T> operator, Predicate<T> condition) {
        return t -> {
            T newT = operator.apply(t);
            return newT != null && condition.test(newT) ? Optional.of(newT) : Optional.empty();
        };
    }

    static <T> Generator<T> withCondition(Supplier<T> supplier, Predicate<T> condition) {
        return withCondition(t -> supplier.get(), condition);
    }
}
