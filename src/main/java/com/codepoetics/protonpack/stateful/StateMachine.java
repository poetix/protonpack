package com.codepoetics.protonpack.stateful;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A state machine that receives items from an input stream and performs a state transition for each item, emitting zero or more outputs.
 * @param <S> The type of the machine's states.
 * @param <I> The type of the inputs.
 * @param <O> The type of the outputs.
 */
public interface StateMachine<S, I, O> {

  /**
   * Use the given transition function to create a state machine that never terminates, and releases no final outputs at the end of processing.
   * @param initialState The initial state of the state machine.
   * @param transitionFunction The transition function to use.
   * @param <S> The type of the machine's states.
   * @param <I> The type of the inputs.
   * @param <O> The type of the outputs.
   * @return The constructed state machine.
   */
  static <S, I, O> StateMachine<S, I, O> create(Supplier<S> initialState, BiFunction<S, I, Transition<S, O>> transitionFunction) {
    return create(initialState, transitionFunction, s -> false);
  }

  /**
   * Use the given transition function to create a state machine that terminates if the terminating condition is met, but releases no final outputs at the end of processing.
   * @param initialState The initial state of the state machine.
   * @param transitionFunction The transition function to use.
   * @param isTerminal The condition that determines when a state is terminal.
   * @param <S> The type of the machine's states.
   * @param <I> The type of the inputs.
   * @param <O> The type of the outputs.
   * @return The constructed state machine.
   */
  static <S, I, O> StateMachine<S, I, O> create(
      Supplier<S> initialState,
      BiFunction<S, I, Transition<S, O>> transitionFunction,
      Predicate<S> isTerminal) {
    return create(initialState, transitionFunction, isTerminal, s -> Stream.empty());
  }

  /**
   * Use the given transition function to create a state machine that terminates if the terminating condition is met, and may release final outputs at the end of processing.
   * @param initialState The initial state of the state machine.
   * @param transitionFunction The transition function to use.
   * @param isTerminal The condition that determines when a state is terminal.
   * @param finisher The function that releases final outputs at the end of processing.
   * @param <S> The type of the machine's states.
   * @param <I> The type of the inputs.
   * @param <O> The type of the outputs.
   * @return The constructed state machine.
   */
  static <S, I, O> StateMachine<S, I, O> create(
      Supplier<S> initialState,
      BiFunction<S, I, Transition<S, O>> transitionFunction,
      Predicate<S> isTerminal,
      Function<S, Stream<O>> finisher) {
    return new ComposedStateMachine<>(initialState, transitionFunction, isTerminal, finisher);
  }

  /**
   * Apply the given input to the given state, returning a transition to a new state with 0 or more outputs.
   * @param state The state to transition from.
   * @param input The input to apply to the state.
   * @return The transition.
   */
  Transition<S, O> apply(S state, I input);

  /**
   * Test whether a given state is terminal and stream traversal should stop.
   * @param state The state to test.
   * @return True if the state is terminal, false otherwise.
   */
  default boolean isTerminal(S state) {
    return false;
  }

  /**
   * Based on the final state (if non-terminal), obtain any "left-over" outputs when the end of the stream is reached.
   * @param finalState The state at the end of the stream.
   * @return The final outputs from the state machine.
   */
  default Stream<O> finish(S finalState) {
    return Stream.empty();
  }

  S getInitialState();

  default StateMachineRunner<S, I, O> runner() {
    return runnerWith(getInitialState());
  }

  default StateMachineRunner<S, I, O> runnerWith(S initialState) {
    return StateMachineRunner.create(initialState, this);
  }

}
