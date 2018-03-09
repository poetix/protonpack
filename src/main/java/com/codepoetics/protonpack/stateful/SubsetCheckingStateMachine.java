package com.codepoetics.protonpack.stateful;

import java.util.HashSet;
import java.util.Set;

public final class SubsetCheckingStateMachine<T> implements StateMachine<Set<T>, T, T> {
  private final Set<T> subset;

  SubsetCheckingStateMachine(Set<T> subset) {
    this.subset = subset;
  }

  @Override
  public Transition<Set<T>, T> apply(Set<T> state, T element) {
    state.remove(element);
    return Transition.to(state, element);
  }

  @Override
  public boolean isTerminal(Set<T> state) {
    return state.isEmpty();
  }

  @Override
  public Set<T> getInitialState() {
    return new HashSet<>(subset);
  }
}
