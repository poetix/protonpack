package com.codepoetics.protonpack.stateful;

import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class TaggingStateMachine<S, T> implements StateMachine<S, T, TaggedValue<S, T>> {
  private final Supplier<S> initialStateSupplier;
  private final BiFunction<S, T, S> stateTransitionFunction;
  private final Predicate<S> isTerminal;

  TaggingStateMachine(Supplier<S> initialStateSupplier, BiFunction<S, T, S> stateTransitionFunction, Predicate<S> isTerminal) {
    this.initialStateSupplier = initialStateSupplier;
    this.stateTransitionFunction = stateTransitionFunction;
    this.isTerminal = isTerminal;
  }

  @Override
  public Transition<S, TaggedValue<S, T>> apply(S state, T input) {
    S newState = stateTransitionFunction.apply(state, input);
    return Transition.to(newState, TaggedValue.of(newState, input));
  }

  @Override
  public boolean isTerminal(S state) {
    return isTerminal.test(state);
  }

  @Override
  public S getInitialState() {
    return initialStateSupplier.get();
  }
}
