package com.codepoetics.protonpack.stateful;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ComposedStateMachine<S, I, O> implements StateMachine<S, I, O> {

  private final Supplier<S> initialStateSupplier;
  private final BiFunction<S, I, Transition<S, O>> transitionFunction;
  private final Predicate<S> isTerminal;
  private final Function<S, Stream<O>> finisher;

  ComposedStateMachine(Supplier<S> initialStateSupplier, BiFunction<S, I, Transition<S, O>> transitionFunction, Predicate<S> isTerminal, Function<S, Stream<O>> finisher) {
    this.initialStateSupplier = initialStateSupplier;
    this.transitionFunction = transitionFunction;
    this.isTerminal = isTerminal;
    this.finisher = finisher;
  }

  @Override
  public S getInitialState() {
    return initialStateSupplier.get();
  }

  @Override
  public Transition<S, O> apply(S state, I input) {
    return transitionFunction.apply(state, input);
  }

  @Override
  public boolean isTerminal(S state) {
    return isTerminal.test(state);
  }

  @Override
  public Stream<O> finish(S finalState) {
    return finisher.apply(finalState);
  }

}
