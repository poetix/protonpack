package com.codepoetics.protonpack;

import java.util.Objects;
import java.util.function.Function;

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
    public static <T> Indexed<T> index(long index, T value) {
        return new Indexed<>(index, value);
    }

    /**
     * A factory that produces a function mapping values from indexed objects ready to use
     * in <code>Stream.map</code>. The index is kept.
     * @param mapper Value transformation function.
     * @param <T> Original indexed value type.
     * @param <U> New indexed value type.
     * @return A function that transforms one indexed value to another, keeping the same index.
     */
    public static <T, U> Function<Indexed<T>, Indexed<U>> mapping(Function<T, U> mapper) {
        return i -> i.map(mapper);
    }

    /**
     * Map this indexed value to another value keeping the same index.
     * @param mapper A non-interfering, stateless function to apply to the value.
     * @param <U> The element type of the new indexed value.
     * @return A new indexed element of the transformed value and the same index.
     */
    public <U> Indexed<U> map(Function<T, U> mapper) {
        return index(index, mapper.apply(value));
    }

    private final long index;
    private final T value;

    private Indexed(long index, T value) {
        this.index = index;
        this.value = value;
    }

    /**
     * @return The indexed value.
     */
    public long getIndex() {
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
        return Objects.equals(index, other.index) && Objects.equals(value, other.value);
    }

    @Override
    public String toString() {
        return String.format("Indexed { index: %d, value: %s }", index, value);
    }

}
