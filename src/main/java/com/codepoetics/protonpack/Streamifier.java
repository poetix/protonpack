package com.codepoetics.protonpack;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;

import static java.util.stream.StreamSupport.stream;

public interface Streamifier {

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

    interface CharacteristicsCapture {
        Streamifier withCharacteristics(int characteristics);
    }

    interface SizednessCapture {
        CharacteristicsCapture unsized();
        CharacteristicsCapture sized(long size);
    }

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

    <T> Stream<T> streamify(Iterator<T> iterator);

}
