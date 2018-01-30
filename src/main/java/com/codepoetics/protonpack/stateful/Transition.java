package com.codepoetics.protonpack.stateful;

import java.util.stream.Stream;

/**
 * Represents a transition to a new state, together with zero or more outputs.
 * @param <S> The type of the state to transition to.
 * @param <O> The type of the outputs from the transition.
 */
public final class Transition<S, O> {

  /**
   * Create a transition to a new state with zero or more outputs.
   * @param newState The state to transition to.
   * @param outputs The outputs from the transition.
   * @param <S> The type of the state to transition to.
   * @param <O> The type of the outputs from the transition.
   * @return The constructed transition.
   */
  @SafeVarargs
  public static <S, O> Transition<S, O> to(S newState, O...outputs) {
    return to(newState, Stream.of(outputs));
  }

  /**
   * Create a transition to a new state with the given outputs.
   * @param newState The state to transition to.
   * @param outputs The outputs from the transition.
   * @param <S> The type of the state to transition to.
   * @param <O> The type of the outputs.
   * @return The constructed transition.
   */
  public static <S, O> Transition<S, O> to(S newState, Stream<O> outputs) {
    return new Transition<>(newState, outputs);
  }

  private final S newState;
  private final Stream<O> outputs;

  private Transition(S newState, Stream<O> outputs) {
    this.newState = newState;
    this.outputs = outputs;
  }

  /**
   *
   * @return The new state to transition to.
   */
  public S getNewState() {
    return newState;
  }

  /**
   *
   * @return The outputs from the transition.
   */
  public Stream<O> getOutputs() {
    return outputs;
  }
}
