package com.codepoetics.protonpack;

import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class SkipWhileTest {

    @Test public void
    skip_while_skips_items_while_condition_is_met() {
        Stream<Integer> ints = Stream.of(1,2,3,4,5,6,7,8,9,10);
        Stream<Integer> skipped = StreamUtils.skipWhile(ints, i -> i < 4);

        List<Integer> collected = skipped.collect(Collectors.toList());

        assertThat(collected, contains(4, 5, 6, 7, 8, 9, 10));
    }

    @Test public void
    skip_until_takes_items_until_condition_is_met() {
        Stream<Integer> ints = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        Stream<Integer> skipped = StreamUtils.skipUntil(ints, i -> i > 4);

        List<Integer> collected = skipped.collect(Collectors.toList());

        assertThat(collected, contains(5, 6, 7, 8, 9, 10));
    }

    @Test public void
    skip_until_retains_sorted_property() {
        Stream<Integer> sortedInts = Stream.of(5, 4, 3, 2, 1).sorted();
        Stream<Integer> skipped = StreamUtils.skipUntil(sortedInts, i -> i > 3);

        List<Integer> collected = skipped.collect(Collectors.toList());

        assertThat(collected, contains(4, 5));
    }
}
