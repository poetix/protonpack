package com.codepoetics.protonpack.stateful;

import com.codepoetics.protonpack.Indexed;
import com.codepoetics.protonpack.StreamUtils;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class Statefully {

  private static final Consumer<?> NO_OP = o -> {};
  private static <O> Consumer<O> noOp() {
    return (Consumer<O>) NO_OP;
  }

  public static <S, I, O> S traverse(Stream<I> input, S initialState, TraversalStateMachine<S, I, O> stateMachine, Predicate<S> isTerminal) {
    StatefulSpliterator<S, I, O> spliterator = StatefulSpliterator.over(input, initialState, stateMachine, isTerminal, noOp());
    StreamSupport.stream(spliterator, false).onClose(input::close).forEach(noOp());
    return spliterator.getState();
  }

  public static <S, I, O> Stream<O> transform(Stream<I> input, S initialState, TraversalStateMachine<S, I, O> stateMachine, Predicate<S> isTerminal) {
    StatefulSpliterator<S, I, O> spliterator = StatefulSpliterator.over(input, initialState, stateMachine, isTerminal, noOp());
    return StreamSupport.stream(spliterator, false).flatMap(s -> s).onClose(input::close);
  }

  public static <S, I, O> Stream<O> transform(Stream<I> input, S initialState, TraversalStateMachine<S, I, O> stateMachine, Predicate<S> isTerminal, Consumer<S> stateObserver) {
    StatefulSpliterator<S, I, O> spliterator = StatefulSpliterator.over(input, initialState, stateMachine, isTerminal, stateObserver);
    return StreamSupport.stream(spliterator, false).flatMap(s -> s).onClose(input::close);
  }

  public static <S, I, O> boolean terminates(Stream<I> input, S initialState, TraversalStateMachine<S, I, O> stateMachine, Predicate<S> isTerminal) {
    return isTerminal.test(traverse(input, initialState, stateMachine, isTerminal));
  }

  public static <T, R> Stream<R> window(Stream<T> input, int size, Function<Stream<T>, R> windowFunction) {
    return Statefully.transform(
        input,
        Window.<T>initialise(size),
        (window, item) -> {
          Window<T> newWindow = window.add(item);
          return Transition.to(newWindow, newWindow.reduce(windowFunction));
        },
        s -> false);
  }

  public static <T> Stream<Indexed<T>> index(Stream<T> input) {
    return Statefully.transform(
        input,
        0L,
        (index, item) -> Transition.to(index + 1, Indexed.index(index, item)),
        i -> false
    );
  }

  public static final class TaggedValue<S, T> {

    public static <S, T> TaggedValue<S, T> of(S tag, T value) {
      return new TaggedValue<>(tag, value);
    }

    private final S tag;
    private final T value;

    private TaggedValue(S tag, T value) {
      this.tag = tag;
      this.value = value;
    }

    public S getTag() {
      return tag;
    }

    public T getValue() {
      return value;
    }

  }

  public static <S, T> Stream<TaggedValue<S, T>> tagging(Stream<T> input, S initialState, BiFunction<S, T, S> stateMachine, Predicate<S> isTerminal) {
    return Statefully.transform(
        input,
        initialState,
        (state, item) -> {
          S newState = stateMachine.apply(state, item);
          return Transition.to(newState, TaggedValue.of(newState, item));
        },
        isTerminal
    );
  }

  public static <T> Optional<T> findLastMatching(Stream<T> input, Predicate<T> condition) {
    return Statefully.transform(
        input,
        (Supplier<Stream<T>>) Stream::empty,
        (state, item) -> condition.test(item)
            ? Transition.to(() -> Stream.of(item))
            : Transition.to(null, state.get()),
        Objects::isNull).findFirst();
  }

  public static <T> boolean terminatingForEach(Stream<T> input, Function<T, Boolean> action) {
    return Statefully.traverse(
        input,
        true,
        (shouldContinue, item) -> Transition.to(shouldContinue && action.apply(item)),
        shouldContinue -> shouldContinue
    );
  }

  @SafeVarargs
  public static <T> boolean includesItems(Stream<T> input, T...items) {
    return includesItems(input, Stream.of(items).collect(Collectors.toSet()));
  }

  public static <T> boolean includesItems(Stream<T> input, Set<T> subset) {
    return Statefully.traverse(
        input,
        new HashSet<>(subset),
        (state, element) -> {
          state.remove(element);
          return Transition.to(state, element);
        },
        Set::isEmpty).isEmpty();
  }
}
