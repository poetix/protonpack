package com.codepoetics.protonpack;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * A sequence of values represented as nested cons pairs.
 * @param <T> The type of the values in the sequence.
 */
public interface Seq<T> extends Streamable<T> {
    /**
     * Creates an empty sequence.
     * @param <T> The type of the values in the sequence.
     * @return The empty sequence.
     */
    static <T> Seq<T> empty() {
        return new EmptySeq<>();
    }

    /**
     * Creates a sequence containing only one item.
     * @param item The item in the sequence.
     * @param <T> The type of the values in the sequence.
     * @return The created sequence.
     */
    static <T> Seq<T> singleton(T item) {
        return new PairSeq<>(item, empty());
    }

    /**
     * Creates a sequence containing zero or more items.
     * @param items The items in the sequence.
     * @param <T> The type of the items.
     * @return The created sequence.
     */
    @SafeVarargs
    static <T> Seq<T> of(T...items) {
        return of(Stream.of(items)).reverse();
    }

    /**
     * Creates a sequence from an ordered list
     * @param list The list of items to put in the sequence.
     * @param <T> The type of the items.
     * @return The created sequence.
     */
    static <T> Seq<T> of(List<T> list) {
        return of(list.stream()).reverse();
    }

    /**
     * Creates a sequence from a collection
     * @param collection The collection of items to put in the sequence.
     * @param <T> The type of the items.
     * @return The created sequence.
     */
    static <T> Seq<T> of(Collection<T> collection) {
        return of(collection.stream());
    }

    /**
     * Creates a sequence from a stream by consing each item onto an initially empty stream. Note that the resulting sequence will be in reverse order.
     * @param stream The stream of values to put in the sequence.
     * @param <T> The type of the items.
     * @return The created sequence.
     */
    static <T> Seq<T> of(Stream<T> stream) {
        return stream.reduce(empty(), Seq::cons, Seq::append);
    }

    /**
     * Returns the stream in reverse order.
     * @return The reversed stream.
     */
    default Seq<T> reverse() {
        return of(stream());
    }

    /**
     * Get the first item in the sequence.
     * @return The first item in the sequence.
     */
    T head();

    /**
     * Get the remaining items in the sequence.
     * @return The remaining items in the sequence.
     */
    Seq<T> tail();

    /**
     * Add an item to the start of the sequence.
     * @param item The item to add to the sequence.
     * @return The extended sequence.
     */
    Seq<T> cons(T item);

    /**
     * Append a second sequence to this sequence.
     * @param items The sequence to append to this sequence.
     * @return The two sequences concatenated.
     */
    default Seq<T> append(Seq<T> items) {
        return concat(items).toSeq().reverse();
    }

    /**
     * Test if the sequence is empty.
     * @return True if the sequence is empty, false otherwise.
     */
    boolean isEmpty();

    /**
     * Gets a stream of the items in the sequence.
     * @return A stream of the items in the sequence.
     */
    default Stream<T> get() {
        return isEmpty()
                ? Stream.empty()
                : StreamUtils.unfold(this, s -> s.tail().isEmpty() ? Optional.empty() : Optional.of(s.tail()))
                             .map(Seq::head);
    }

    @Override
    default Seq<T> toSeq() {
        return this;
    }
}
