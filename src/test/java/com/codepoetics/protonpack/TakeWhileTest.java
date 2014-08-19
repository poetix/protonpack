package com.codepoetics.protonpack;

import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class TakeWhileTest {

    @Test public void
    take_while_takes_items_while_condition_is_met() {
        Stream<Integer> infiniteInts = Stream.iterate(0, i -> i + 1);
        Stream<Integer> finiteInts = StreamUtils.takeWhile(infiniteInts, i -> i < 10);

        assertThat(finiteInts.collect(Collectors.toList()), hasSize(10));
    }

    @Test public void
    take_until_takes_items_until_condition_is_met() {
        Stream<Integer> infiniteInts = Stream.iterate(0, i -> i + 1);
        Stream<Integer> finiteInts = StreamUtils.takeUntil(infiniteInts, i -> i > 10);

        assertThat(finiteInts.collect(Collectors.toList()), hasSize(11));
    }
}
