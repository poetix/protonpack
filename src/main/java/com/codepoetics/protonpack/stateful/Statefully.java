package com.codepoetics.protonpack.stateful;

import com.codepoetics.protonpack.Indexed;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class Statefully {

  private static final Consumer<?> NO_OP = o -> {};
  private static <O> Consumer<O> noOp() {
    return (Consumer<O>) NO_OP;
  }

  public static <S, I, O> S traverse(Stream<I> input, StateMachine<S, I, O> stateMachine) {
    StatefulSpliterator<S, I, O> spliterator = StatefulSpliterator.over(input, stateMachine);
    StreamSupport.stream(spliterator, false).onClose(input::close).forEach(noOp());
    return spliterator.getState();
  }

  public static <S, I, O> Stream<O> transform(Stream<I> input, StateMachine<S, I, O> stateMachine) {
    StatefulSpliterator<S, I, O> spliterator = StatefulSpliterator.over(input, stateMachine);
    return StreamSupport.stream(spliterator, false).flatMap(s -> s).onClose(input::close);
  }

  public static <S, I, O> boolean terminates(Stream<I> input, StateMachine<S, I, O> stateMachine) {
    return stateMachine.isTerminal(traverse(input, stateMachine));
  }

  public static <T, R> Stream<R> window(Stream<T> input, int size, Function<Stream<T>, R> reducer) {
    return Statefully.transform(input, StateMachines.windowingStateMachine(size, reducer));
  }

  public static <T> Stream<Indexed<T>> index(Stream<T> input) {
    return Statefully.transform(
        input,
        StateMachine.create(() -> 0L, (index, item) -> Transition.to(index + 1, Indexed.index(index, item)))
    );
  }

  public static <S, T> Stream<TaggedValue<S, T>> tagging(Stream<T> input, S initialState, BiFunction<S, T, S> stateFunction) {
    return tagging(input, initialState, stateFunction, s -> false);
  }

  public static <S, T> Stream<TaggedValue<S, T>> tagging(Stream<T> input, S initialState, BiFunction<S, T, S> stateFunction, Predicate<S> isTerminal) {
    return Statefully.transform(input, StateMachines.tagging(initialState, stateFunction, isTerminal));
  }

  public static <T> Optional<T> findLastMatching(Stream<T> input, Predicate<T> condition) {
    return Statefully.transform(input, StateMachines.lastMatchingFinder(condition)).findFirst();
  }

  public static <T> boolean terminatingForEach(Stream<T> input, Function<T, Boolean> action) {
    return Statefully.traverse(input, StateMachines.terminatingForEach(action));
  }

  @SafeVarargs
  public static <T> boolean includesItems(Stream<T> input, T...items) {
    return includesItems(input, Stream.of(items).collect(Collectors.toSet()));
  }

  public static <T> boolean includesItems(Stream<T> input, Set<T> subset) {
    return Statefully.terminates(input, StateMachines.checkingSubset(subset));
  }

}
