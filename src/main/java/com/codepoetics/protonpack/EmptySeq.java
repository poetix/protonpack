package com.codepoetics.protonpack;

final class EmptySeq<T> implements Seq<T> {
    @Override
    public T head() {
        throw new UnsupportedOperationException("Empty sequence has no head");
    }

    @Override
    public Seq<T> tail() {
        throw new UnsupportedOperationException("Empty sequence has no tail");
    }

    @Override
    public Seq<T> cons(T item) {
        return new PairSeq<>(item, this);
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Seq) && ((Seq<?>) o).isEmpty();
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        return "()";
    }

}
