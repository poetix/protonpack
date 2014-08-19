package com.codepoetics.protonpack;

import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class ZipTest {

    @Test public void
    zips_a_pair_of_streams_with_custom_combiner() {
        Stream<String> streamA = Stream.of("A is for ", "B is for ", "C is for ");
        Stream<String> streamB  = Stream.of("Apple", "Banana", "Carrot", "Doughnut");

        List<String> zipped = StreamUtils.zip(streamA, streamB, String::concat).collect(Collectors.toList());

        assertThat(zipped, hasSize(3));
        assertThat(zipped, hasItems("A is for Apple", "B is for Banana", "C is for Carrot"));
    }

    @Test public void
    zips_a_pair_of_streams() {
        Stream<String> streamA = Stream.of("A is for ", "B is for ", "C is for ");
        Stream<String> streamB  = Stream.of("Apple", "Banana", "Carrot", "Doughnut");

        List<ZippedPair<String, String>> zipped = StreamUtils.zip(streamA, streamB).collect(Collectors.toList());

        assertThat(zipped, hasSize(3));
        assertThat(zipped, hasItems(
                ZippedPair.of("A is for ", "Apple"),
                ZippedPair.of("B is for ", "Banana"),
                ZippedPair.of("C is for ", "Carrot")));
    }

}
