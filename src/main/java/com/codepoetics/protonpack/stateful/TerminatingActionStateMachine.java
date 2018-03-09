package com.codepoetics.protonpack.stateful;

import java.util.function.Function;

public final class TerminatingActionStateMachine<T> implements StateMachine<Boolean, T, Void> {

  private final Function<T, Boolean> action;

  TerminatingActionStateMachine(Function<T, Boolean> action) {
    this.action = action;
  }

  @Override
  public Transition<Boolean, Void> apply(Boolean shouldContinue, T item) {
    return Transition.to(shouldContinue && action.apply(item));
  }

  @Override
  public boolean isTerminal(Boolean state) {
    return !state;
  }

  @Override
  public Boolean getInitialState() {
    return true;
  }
}
