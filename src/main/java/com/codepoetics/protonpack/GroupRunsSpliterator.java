package com.codepoetics.protonpack;

import java.util.*;
import java.util.function.Consumer;

class GroupRunsSpliterator<T> implements Spliterator<List<T>> {
    private final Spliterator<T> source;
    private final Comparator<T> comparator;

    Optional<T> last = Optional.empty();
    T current = null;

    public GroupRunsSpliterator(Spliterator<T> source, Comparator<T> comparator) {
        this.source = source;
        this.comparator = comparator;
    }

    @Override
    public boolean tryAdvance(Consumer<? super List<T>> action) {
        List<T> neighbors = new LinkedList<>();

        last.ifPresent(neighbors::add);

        while (source.tryAdvance(i -> current = i)) {
          boolean runBroken = !(itemBelongsToRun(current));
          if (!runBroken) {
              neighbors.add(current);
          }

          last = Optional.of(current);

          if (runBroken){
            action.accept(neighbors);

            return true;
          }
        }
      return flushRemainingNeighbours(action, neighbors);
    }

  private boolean flushRemainingNeighbours(Consumer<? super List<T>> action, List<T> neighbors) {
    if (!neighbors.isEmpty()) {
      action.accept(neighbors);

      last = Optional.empty();

      return true;
    }
    return false;
  }

  private boolean itemBelongsToRun(T item) {
    return last.map(lastItem -> comparator.compare(item, lastItem) == 0)
        .orElse(true);
  }

  @Override
    public Spliterator<List<T>> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return source.estimateSize();
    }

    @Override
    public int characteristics() {
        return source.characteristics() &
                ~(Spliterator.SIZED | Spliterator.ORDERED);
    }
}
