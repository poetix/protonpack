package com.codepoetics.protonpack.stateful;

import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class StateMachines {

  private StateMachines() {
  }

  public static <T> LastMatchingFinderStateMachine<T> lastMatchingFinder(Predicate<T> condition) {
    return new LastMatchingFinderStateMachine<>(condition);
  }

  public static <T, R> WindowingStateMachine<T, R> windowingStateMachine(int size, Function<Stream<T>, R> reducer) {
    return new WindowingStateMachine<>(size, reducer);
  }

  public static <T> SubsetCheckingStateMachine<T> checkingSubset(Set<T> subset) {
    return new SubsetCheckingStateMachine<>(subset);
  }

  public static <T> TerminatingActionStateMachine<T> terminatingForEach(Function<T, Boolean> action) {
    return new TerminatingActionStateMachine<>(action);
  }

  public static <S, T> TaggingStateMachine<S, T> tagging(S initialState, BiFunction<S, T, S> stateTransitionFunction) {
    return tagging(initialState, stateTransitionFunction, s -> false);
  }

  public static <S, T> TaggingStateMachine<S, T> tagging(S initialState, BiFunction<S, T, S> stateTransitionFunction, Predicate<S> isTerminal) {
    return StateMachines.<S, T>tagging(() -> initialState, stateTransitionFunction, isTerminal);
  }

  public static <S, T> TaggingStateMachine<S, T> tagging(Supplier<S> initialStateSupplier, BiFunction<S, T, S> stateTransitionFunction) {
    return tagging(initialStateSupplier, stateTransitionFunction, s -> false);
  }

  public static <S, T> TaggingStateMachine<S, T> tagging(Supplier<S> initialStateSupplier, BiFunction<S, T, S> stateTransitionFunction, Predicate<S> isTerminal) {
    return new TaggingStateMachine<>(initialStateSupplier, stateTransitionFunction, isTerminal);
  }

}
