package com.codepoetics.protonpack.stateful;

import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

final class StatefulSpliterator<S, I, O> implements Spliterator<Stream<O>> {

  static <S, I, O> StatefulSpliterator<S, I, O> over(Stream<I> inputStream, StateMachine<S, I, O> stateMachine) {
    return new StatefulSpliterator<>(inputStream.spliterator(), stateMachine.getInitialState(), stateMachine);
  }

  private final Spliterator<I> inputSpliterator;
  private S state;
  private boolean isFinished = false;
  private final StateMachine<S, I, O> stateMachine;

  private StatefulSpliterator(Spliterator<I> inputSpliterator, S initialState, StateMachine<S, I, O> stateMachine) {
    this.inputSpliterator = inputSpliterator;
    this.state = initialState;
    this.stateMachine = stateMachine;
  }

  public S getState() {
    return state;
  }

  @Override
  public boolean tryAdvance(Consumer<? super Stream<O>> action) {
    return !isFinished
        && !stateMachine.isTerminal(state)
        && (inputSpliterator.tryAdvance(input -> acceptInput(input, action))
        || finish(action));

  }

  private void acceptInput(I input, Consumer<? super Stream<O>> action) {
    Transition<S, O> transition = stateMachine.apply(state, input);
    action.accept(transition.getOutputs());
    state = transition.getNewState();
  }

  private boolean finish(Consumer<? super Stream<O>> action) {
    action.accept(stateMachine.finish(state));
    isFinished = true;
    return true;
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
