package com.codepoetics.protonpack.stateful;

import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class LastMatchingFinderStateMachine<T> implements StateMachine<Supplier<Stream<T>>, T, T> {

  private final Predicate<T> condition;

  LastMatchingFinderStateMachine(Predicate<T> condition) {
    this.condition = condition;
  }

  @Override
  public Transition<Supplier<Stream<T>>, T> apply(Supplier<Stream<T>> state, T item) {
    return condition.test(item)
        ? Transition.to(() -> Stream.of(item))
        : Transition.to(null, state.get());
  }

  @Override
  public boolean isTerminal(Supplier<Stream<T>> state) {
    return state == null;
  }

  @Override
  public Stream<T> finish(Supplier<Stream<T>> finalState) {
    return finalState.get();
  }

  @Override
  public Supplier<Stream<T>> getInitialState() {
    return Stream::empty;
  }
}
