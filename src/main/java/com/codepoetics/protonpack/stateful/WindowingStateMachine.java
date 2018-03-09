package com.codepoetics.protonpack.stateful;

import java.util.function.Function;
import java.util.stream.Stream;

public final class WindowingStateMachine<T, R> implements StateMachine<Window<T>, T, R> {

  private final int size;
  private final Function<Stream<T>, R> reducer;

  WindowingStateMachine(int size, Function<Stream<T>, R> reducer) {
    this.size = size;
    this.reducer = reducer;
  }

  @Override
  public Transition<Window<T>, R> apply(Window<T> window, T item) {
    Window<T> newWindow = window.add(item);
    return Transition.to(newWindow, newWindow.reduce(reducer));
  }

  @Override
  public Window<T> getInitialState() {
    return Window.initialise(size);
  }
}
