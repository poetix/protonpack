package com.codepoetics.protonpack;

import java.util.Comparator;
import java.util.Spliterator;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class JoinSpliterator<I, O> implements Spliterator<O> {

    final private Comparator<I> comparator;
    final private Spliterator<I> left;
    final private Spliterator<I> right;
    final private BiFunction<I, I, O> compute;
    private Pair pair = null;
    private boolean hasNext = false;

    public JoinSpliterator(Comparator<I> comparator, Spliterator<I> left, Spliterator<I> right, BiFunction<I, I, O> compute) {
        this.comparator = comparator;
        this.right = right;
        this.left = left;
        this.compute = compute;
    }

    boolean advanceLeft() {
        return left.tryAdvance(v -> pair.left = v);
    }

    boolean advanceRight() {
        return right.tryAdvance(v -> pair.right = v);
    }

    @Override
    public boolean tryAdvance(Consumer<? super O> action) {

        if (pair == null) {
            pair = new Pair();
            hasNext = advanceLeft() && advanceRight();
        }

        while (hasNext) {
            int compare = comparator.compare(pair.left, pair.right);
            if (compare == 0) {
                // generate.
                action.accept(compute.apply(pair.left, pair.right));
                hasNext = advanceLeft() && advanceRight();
                return hasNext;
            } else if (compare < 0) {
                hasNext = advanceLeft();
            } else /* if (compare > 0) */ {
                hasNext = advanceRight();
            }
        }
        return false;
    }

    @Override
    public Spliterator<O> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return Long.max(left.estimateSize(), right.estimateSize());
    }

    @Override
    public int characteristics() {
        return 0;
    }

    class Pair {
        I left;
        I right;
    }
}
