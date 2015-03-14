package com.codepoetics.protonpack;

import java.util.LinkedList;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

class WindowedSpliterator<T> implements Spliterator<List<T>> {
    private final Spliterator<T> source;
    private final int windowSize;
    private int overlap;
    List<T> queue = new LinkedList<>();
    List<T> next = new LinkedList<>();
    private boolean windowSeeded;

    public WindowedSpliterator(Spliterator<T> input, int windowSize, int overlap) {
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
            if(next.isEmpty()){
                return;
            }

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

    private List<T> next() {
        queue = new LinkedList<>(next);

        nextWindow();

        if (next.size() != windowSize) {
            next.clear();
        }

        return queue;
    }

    @Override
    public boolean tryAdvance(Consumer<? super List<T>> action) {
        if (hasNext()) {
            action.accept(next());

            return true;
        }

        return false;
    }

    @Override
    public Spliterator<List<T>> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        long sourceSize = source.estimateSize();
        if (sourceSize == 0) {
            return 0;
        }
        if (sourceSize <= windowSize) {
            return 1;
        }
        return sourceSize - windowSize;
    }

    @Override
    public int characteristics() {
        return source.characteristics() & ~(Spliterator.SIZED | Spliterator.ORDERED);
    }
}
