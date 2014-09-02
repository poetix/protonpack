package com.codepoetics.protonpack;

import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class ZipTest {

    @Test public void
    zips_a_pair_of_streams_with_custom_combiner() {
        Stream<String> streamA = Stream.of("A", "B", "C");
        Stream<String> streamB  = Stream.of("Apple", "Banana", "Carrot", "Doughnut");

        List<String> zipped = StreamUtils.zip(streamA, streamB, (a, b) -> a + " is for " + b).collect(Collectors.toList());

        assertThat(zipped, contains("A is for Apple", "B is for Banana", "C is for Carrot"));
    }

    @Test public void
    zips_a_stream_with_index() {
        Stream<String> source = Stream.of("Foo", "Bar", "Baz");

        List<Indexed<String>> zipped = StreamUtils.zipWithIndex(source).collect(Collectors.toList());

        assertThat(zipped, contains(
                Indexed.index(0, "Foo"),
                Indexed.index(1, "Bar"),
                Indexed.index(2, "Baz")));
    }

}
