package com.codepoetics.protonpack.stateful;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

final class Window<T> {

  static <T> Window<T> initialise(int size) {
    List<T> initialContents = new ArrayList<>(size);
    IntStream.range(0, size).forEach(i -> initialContents.add(null));
    return new Window<>(size, 0, 0, initialContents);
  }

  private final int size;
  private final int count;
  private final int index;
  private final List<T> contents;

  private Window(int size, int count, int index, List<T> contents) {
    this.size = size;
    this.count = count;
    this.index = index;
    this.contents = contents;
  }

  Window<T> add(T item) {
    List<T> newContents = new ArrayList<>(contents);
    newContents.set(index, item);

    int newIndex = index + 1;
    if (newIndex == size) {
      newIndex = 0;
    }

    int newCount = count + 1;
    if (newCount > size) {
      newCount = size;
    }

    return new Window<>(size, newCount, newIndex, newContents);
  }

  <R> Stream<R> reduce(Function<Stream<T>, R> reducer) {
    if (count < size) {
      return Stream.empty();
    }
    try (Stream<T> windowStream = Stream.concat(
        IntStream.range(index, size).mapToObj(contents::get),
        IntStream.range(0, index).mapToObj(contents::get))) {
      return Stream.of(reducer.apply(windowStream));
    }
  }
}
