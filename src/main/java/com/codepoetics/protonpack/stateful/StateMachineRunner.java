package com.codepoetics.protonpack.stateful;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class StateMachineRunner<S, I, O> {

  public static <S, I, O> StateMachineRunner<S, I, O> create(S initialState, StateMachine<S, I, O> stateMachine) {
    return new StateMachineRunner<>(initialState, stateMachine);
  }

  private S state;
  private boolean isFinished = false;
  private final StateMachine<S, I, O> stateMachine;

  private StateMachineRunner(S state, StateMachine<S, I, O> stateMachine) {
    this.state = state;
    this.stateMachine = stateMachine;
  }

  public List<O> accept(I input) {
    return _accept(input).collect(Collectors.toList());
  }

  @SafeVarargs
  public final List<O> accept(I first, I...remaining) {
    return accept(Stream.concat(Stream.of(first), Stream.of(remaining)));
  }

  public List<O> accept(Collection<I> inputs) {
    return accept(inputs.stream());
  }

  public List<O> accept(Stream<I> inputs) {
    return inputs.flatMap(this::_accept).collect(Collectors.toList());
  }


  public List<O> acceptAndFinish(I input) {
    List<O> results = accept(input);
    _finish().forEach(results::add);
    return results;
  }

  @SafeVarargs
  public final List<O> acceptAndFinish(I first, I...remaining) {
    return acceptAndFinish(Stream.concat(Stream.of(first), Stream.of(remaining)));
  }

  public List<O> acceptAndFinish(Collection<I> inputs) {
    return acceptAndFinish(inputs.stream());
  }

  public List<O> acceptAndFinish(Stream<I> inputs) {
    List<O> results = accept(inputs);
    _finish().forEach(results::add);
    return results;
  }

  private Stream<O> _accept(I input) {
    if (isFinished) {
      return Stream.empty();
    }

    Transition<S, O> transition = stateMachine.apply(state, input);
    state = transition.getNewState();
    return transition.getOutputs();
  }

  public List<O> finish() {
    return _finish().collect(Collectors.toList());
  }

  private Stream<O> _finish() {
    if (isFinished) {
      return Stream.empty();
    }

    isFinished = true;
    return stateMachine.finish(state);
  }

  public S getState() {
    return state;
  }
}
