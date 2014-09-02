package com.codepoetics.protonpack;

import java.util.Objects;

/**
 * A value combined with an index, indicating its position in an ordered sequence.
 *
 * @param <T> The type of the indexed value.
 */
public class Indexed<T> {

    /**
     * Combine an index and a value into an indexed value.
     * @param index The index of the value.
     * @param value The value indexed.
     * @param <T> The type of the value.
     * @return The indexed value.
     */
    public static <T> Indexed<T> index(int index, T value) {
        return new Indexed<>(index, value);
    }

    private final int index;
    private final T value;

    private Indexed(int index, T value) {
        this.index = index;
        this.value = value;
    }

    /**
     * @return The indexed value.
     */
    public int getIndex() {
        return index;
    }

    /**
     * @return The value indexed.
     */
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
