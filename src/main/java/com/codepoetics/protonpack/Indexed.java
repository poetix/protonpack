package com.codepoetics.protonpack;

import java.util.Objects;

public class Indexed<T> {

    public static <T> Indexed<T> index(int index, T value) {
        return new Indexed<>(index, value);
    }

    private final int index;
    private final T value;

    private Indexed(int index, T value) {
        this.index = index;
        this.value = value;
    }

    public int getIndex() {
        return index;
    }

    public T getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Indexed other = (Indexed) obj;
        return Objects.equals(this.index, other.index) && Objects.equals(this.value, other.value);
    }
}
