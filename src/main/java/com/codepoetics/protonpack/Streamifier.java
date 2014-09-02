package com.codepoetics.protonpack;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;

import static java.util.stream.StreamSupport.stream;

/**
 * Interface providing a small DSL for constructing streams out of iterators.
 */
public interface Streamifier {

    /**
     * Directly convert the supplied iterator into a synchronous, unsized, immutable, non-null, ordered stream.
     *
     * @param iterator The iterator to convert.
     * @param <T> The type over which the iterator iterates and the resulting stream streams.
     * @return A stream streaming over the values provided by the iterator.
     */
    static <T> Stream<T> toStream(Iterator<T> iterator) {
        return Streamifier
                .streamifier(false)
                .unsized()
                .<T>withCharacteristics(
                        Spliterator.IMMUTABLE &
                                Spliterator.NONNULL &
                                Spliterator.ORDERED)
                .streamify(iterator);
    }

    /**
     * Interface used in the Streamifier DSL. This would not normally be used outside of a chain of DSL methods.
     */
    interface CharacteristicsCapture {
        /**
         * Accepts the characteristics of the stream to be constructed.
         *
         * @param characteristics The characteristics to be imparted to the stream to be constructed.
         * @return A Streamifier which can convert an iterator into a stream.
         */
        Streamifier withCharacteristics(int characteristics);
    }

    /**
     * Interface used in the Streamifier DSL. This would not normally be used outside of a chain of DSL methods.
     */
    interface SizednessCapture {
        /**
         * Specifies that the stream to be constructed will be unsized.
         * @return An object used to capture the characteristics of the stream to be constructed.
         */
        CharacteristicsCapture unsized();

        /**
         * Specifies that the stream to be constructed will be of the given size.
         * @param size The size of the stream to be constructed.
         * @return An object used to capture the characteristics of the stream to be constructed.
         */
        CharacteristicsCapture sized(long size);
    }

    /**
     * Start constructing a Streamifier that can convert an Iterator&lt;T&gt; into a Stream&lt;T&gt;
     * @param isParallel Whether the stream to be constructed will be a parallel stream.
     * @return An object used to capture the sizedness of the stream to be constructed.
     */
    static SizednessCapture streamifier(boolean isParallel) {
        return new SizednessCapture() {
            @Override
            public CharacteristicsCapture unsized() {
                return characteristics -> new Streamifier() {
                    @Override
                    public <T> Stream<T> streamify(Iterator<T> iterator) {
                        return stream(Spliterators.spliteratorUnknownSize(iterator, characteristics), isParallel);
                    }
                };
            }

            @Override
            public CharacteristicsCapture sized(long size) {
                return characteristics -> new Streamifier() {
                    @Override
                    public <T> Stream<T> streamify(Iterator<T> iterator) {
                        return stream(Spliterators.spliterator(iterator, size, characteristics & Spliterator.SIZED), isParallel);
                    }
                };
            }
        };
    }

    /**
     * Convert the supplied iterator into a stream with the required parallelism, sizedness and characteristics.
     * @param iterator The iterator to convert.
     * @return A stream streaming over the values provided by the iterator.
     */
    <T> Stream<T> streamify(Iterator<T> iterator);

}
