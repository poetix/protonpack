package com.codepoetics.protonpack.stateful;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

final class StatefulSpliterator<S, I, O> implements Spliterator<Stream<O>> {

  static <S, I, O> StatefulSpliterator<S, I, O> over(Stream<I> inputStream, S initialState, TraversalStateMachine<S, I, O> stateMachine, Predicate<S> isTerminal, Consumer<S> stateObserver) {
    return new StatefulSpliterator<>(inputStream.spliterator(), initialState, stateMachine, isTerminal, stateObserver);
  }

  private final Spliterator<I> inputSpliterator;
  private S state;
  private final TraversalStateMachine<S, I, O> stateMachine;
  private final Predicate<S> isTerminal;
  private final Consumer<S> stateObserver;

  private StatefulSpliterator(Spliterator<I> inputSpliterator, S initialState, TraversalStateMachine<S, I, O> stateMachine, Predicate<S> isTerminal, Consumer<S> stateObserver) {
    this.inputSpliterator = inputSpliterator;
    this.state = initialState;
    this.stateMachine = stateMachine;
    this.isTerminal = isTerminal;
    this.stateObserver = stateObserver;
  }

  public S getState() {
    return state;
  }

  @Override
  public boolean tryAdvance(Consumer<? super Stream<O>> action) {
    return !isTerminal.test(state) && inputSpliterator.tryAdvance(input -> {
      Transition<S, O> transition = stateMachine.apply(state, input);
      action.accept(transition.getOutputs());
      state = transition.getNewState();
      stateObserver.accept(state);
    });
  }

  @Override
  public Spliterator<Stream<O>> trySplit() {
    return null;
  }

  @Override
  public long estimateSize() {
    return inputSpliterator.estimateSize();
  }

  @Override
  public int characteristics() {
    return inputSpliterator.characteristics();
  }
}
