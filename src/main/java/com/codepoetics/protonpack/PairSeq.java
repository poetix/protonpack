package com.codepoetics.protonpack;

import java.util.Objects;
import java.util.stream.Collectors;

final class PairSeq<T> implements Seq<T> {
    private final T head;
    private final Seq<T> tail;

    public PairSeq(T head, Seq<T> tail) {
        this.head = head;
        this.tail = tail;
    }

    @Override
    public T head() {
        return head;
    }

    @Override
    public Seq<T> tail() {
        return tail;
    }

    @Override
    public Seq<T> cons(T item) {
        return new PairSeq<>(item, this);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PairSeq<?> pairSeq = (PairSeq<?>) o;
        return Objects.equals(head, pairSeq.head) &&
                Objects.equals(tail, pairSeq.tail);
    }

    @Override
    public int hashCode() {
        return Objects.hash(head, tail);
    }

    @Override
    public String toString() {
        return stream().map(Object::toString).collect(Collectors.joining(",", "(", ")"));
    }
}
