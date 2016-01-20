package com.codepoetics.protonpack;

import java.util.Comparator;
import java.util.Spliterator;

/**
 * A Spliterator that is SORTED if its underlying source is SORTED, and that therefore needs to provide the source's Comparator.
 * @param <T>
 */
abstract class SortedInheritingSpliterator<T> implements Spliterator<T> {

    protected final Spliterator<T> source;

    protected SortedInheritingSpliterator(Spliterator<T> source) {
        this.source = source;
    }

    public int characteristics() {
        return modifiedCharacteristics(source.characteristics());
    }

    abstract int modifiedCharacteristics(int sourceCharacteristics);

    public Comparator<? super T> getComparator() {
        return source.getComparator();
    }
}
