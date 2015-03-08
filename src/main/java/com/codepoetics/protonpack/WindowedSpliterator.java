package com.codepoetics.protonpack;

import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

class WindowedSpliterator<TSource> implements Spliterator<List<TSource>> {
    private final Spliterator<TSource> source;
    private final int windowSize;
    private int overlap;
    List<TSource> queue = new LinkedList<>();
    List<TSource> next = new LinkedList<>();
    private boolean windowSeeded;

    public WindowedSpliterator(Spliterator<TSource> input, int windowSize, int overlap) {
        source = input;

        this.windowSize = windowSize;
        this.overlap = overlap;
    }

    static <T> WindowedSpliterator<T> over(Spliterator<T> source, int windowSize, int overlap) {
        return new WindowedSpliterator<>(source, windowSize, overlap);
    }

    private boolean hasNext() {
        if (!windowSeeded) {
            seedWindow();

            windowSeeded = true;
        }

        return next.size() > 0;
    }

    private void nextWindow() {
        for (int i = 0; i < overlap; i++) {
            next.remove(0);

            source.tryAdvance(next::add);
        }
    }

    private void seedWindow() {
        int window = windowSize;

        while (source.tryAdvance(next::add)) {
            window--;

            if (window == 0) {
                return;
            }
        }
    }

    private List<TSource> next() {
        queue = new LinkedList<>(next);

        nextWindow();

        if (next.size() != windowSize) {
            next.clear();
        }

        return queue;
    }

    @Override
    public boolean tryAdvance(Consumer<? super List<TSource>> action) {
        if (hasNext()) {
            action.accept(next());

            return true;
        }

        return false;
    }

    @Override
    public Spliterator<List<TSource>> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return 0;
    }

    @Override
    public int characteristics() {
        return 0;
    }
}
