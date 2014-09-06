package com.codepoetics.protonpack;

import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class MergeTest {

    @Test public void
    merges_streams_to_list_with_unit_merger_and_combiner() {
        Stream<String> streamA = Stream.of("A", "B", "C");
        Stream<String> streamB = Stream.of("apple", "banana", "carrot", "date");
        Stream<String> streamC = Stream.of("fritter", "split", "cake", "roll", "pastry");

        Stream<List<String>> merged = StreamUtils.mergeToList(
                streamA, streamB, streamC);

        assertThat(merged.collect(toList()), contains(
                asList("A", "apple", "fritter"),
                asList("B", "banana", "split"),
                asList("C", "carrot", "cake"),
                asList("date", "roll"),
                asList("pastry")));
    }
}
