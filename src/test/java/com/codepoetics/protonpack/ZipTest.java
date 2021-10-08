package com.codepoetics.protonpack;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ZipTest {

    @Test public void
    zips_a_pair_of_streams_of_same_length() {
        Stream<String> streamA = Stream.of("A", "B", "C");
        Stream<String> streamB  = Stream.of("Apple", "Banana", "Carrot");

        List<String> zipped = StreamUtils.zip(streamA, streamB, (a, b) -> a + " is for " + b).collect(Collectors.toList());

        assertThat(zipped, contains("A is for Apple", "B is for Banana", "C is for Carrot"));
    }

    @Test public void
    zips_a_pair_of_streams_where_first_stream_is_longer() {
        Stream<String> streamA = Stream.of("A", "B", "C", "D");
        Stream<String> streamB  = Stream.of("Apple", "Banana", "Carrot");

        List<String> zipped = StreamUtils.zip(streamA, streamB, (a, b) -> a + " is for " + b).collect(Collectors.toList());

        assertThat(zipped, contains("A is for Apple", "B is for Banana", "C is for Carrot"));
    }

    @Test public void
    zips_a_pair_of_streams_where_second_stream_is_longer() {
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

    @Test public void
    zips_a_stream_with_index_and_map() {
        Stream<String> source = Stream.of("Foo", "Bar", "Baz");

        List<Indexed<String>> zipped = StreamUtils.zipWithIndex(source).map(Indexed.mapping(String::toLowerCase)).collect(Collectors.toList());

        assertThat(zipped, contains(
                Indexed.index(0, "foo"),
                Indexed.index(1, "bar"),
                Indexed.index(2, "baz")));
    }

    @Test public void
    zips_a_trio_of_streams_of_same_length() {
        Stream<String> streamA = Stream.of("A", "B", "C");
        Stream<String> streamB = Stream.of("aggravating", "banausic", "complaisant");
        Stream<String> streamC  = Stream.of("Apple", "Banana", "Carrot");

        List<String> zipped = StreamUtils.zip(streamA, streamB, streamC, (a, b, c) -> a + " is for " + b + " " + c).collect(Collectors.toList());

        assertThat(zipped, contains("A is for aggravating Apple", "B is for banausic Banana", "C is for complaisant Carrot"));
    }

    @Test public void
    zips_a_list_of_streams_of_same_length() {
        Stream<String> streamA = Stream.of("A", "B", "C");
        Stream<String> streamB  = Stream.of("Apple", "Banana", "Carrot");

        List<String> zipped = StreamUtils.zip(Arrays.asList(streamA, streamB), l -> l.get(0) + " is for " + l.get(1)).collect(Collectors.toList());

        assertThat(zipped, contains("A is for Apple", "B is for Banana", "C is for Carrot"));
    }

    @Test public void
    zips_a_list_of_streams_where_first_stream_is_longer() {
        Stream<String> streamA = Stream.of("A", "B", "C", "D");
        Stream<String> streamB  = Stream.of("Apple", "Banana", "Carrot");

        List<String> zipped = StreamUtils.zip(Arrays.asList(streamA, streamB), l -> l.get(0) + " is for " + l.get(1)).collect(Collectors.toList());

        assertThat(zipped, contains("A is for Apple", "B is for Banana", "C is for Carrot"));
    }

    @Test public void
    zips_a_list_of_streams_where_second_stream_is_longer() {
        Stream<String> streamA = Stream.of("A", "B", "C");
        Stream<String> streamB  = Stream.of("Apple", "Banana", "Carrot", "Doughnut");

        List<String> zipped = StreamUtils.zip(Arrays.asList(streamA, streamB), l -> l.get(0) + " is for " + l.get(1)).collect(Collectors.toList());

        assertThat(zipped, contains("A is for Apple", "B is for Banana", "C is for Carrot"));
    }

    @Test public void
    zips_an_empty_list_of_streams_without_calling_combiner() {
        List<String> zipped = StreamUtils.zip(Collections.emptyList(),
                l -> {if (true) throw new AssertionError(); else return ".";})
                .collect(Collectors.toList());

        assertThat(zipped, is(empty()));
    }
}
