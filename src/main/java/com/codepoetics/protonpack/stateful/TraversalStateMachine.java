package com.codepoetics.protonpack.stateful;

import java.util.function.BiFunction;

public interface TraversalStateMachine<S, I, O> extends BiFunction<S, I, Transition<S, O>> {
}
