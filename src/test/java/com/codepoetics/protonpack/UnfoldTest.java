package com.codepoetics.protonpack;

import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;

public class UnfoldTest {

    @Test
    public void unfolds_a_seed_until_empty_is_returned() {
        Stream<Integer> unfolded = StreamUtils.unfold(1, i -> (i < 10) ? Optional.of(i + 1) : Optional.empty());

        List<Integer> collected = unfolded.collect(Collectors.toList());
        assertThat(collected, hasSize(10));
        assertThat(collected, hasItems(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test public void
    unfolds_a_seed_until_a_condition_is_met() {
        Stream<Integer> unfolded = StreamUtils.unfold(1, i -> i + 1, i -> i <= 10);

        List<Integer> collected = unfolded.collect(Collectors.toList());
        assertThat(collected, hasSize(10));
        assertThat(collected, hasItems(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test public void
    takes_from_a_supplier_until_a_condition_is_met() {
        AtomicInteger a = new AtomicInteger(0);
        Supplier<Integer> supplier = a::incrementAndGet;

        Stream<Integer> unfolded = StreamUtils.unfold(supplier, (Integer i) -> i <= 10);

        List<Integer> collected = unfolded.collect(Collectors.toList());
        assertThat(collected, hasSize(10));
        assertThat(collected, hasItems(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test public void
    unfold_with_generator_returns_an_empty_stream_if_seed_is_null() {
        Stream<Integer> unfolded = StreamUtils.unfold(null, (Integer i) -> Optional.of(i + 1));

        assertThat(unfolded.collect(Collectors.toList()), empty());
    }

    @Test public void
    unfold_with_condition_returns_an_empty_stream_if_seed_is_null() {
        Stream<Integer> unfolded = StreamUtils.unfold(null, i -> i + 1, i -> i <= 10);

        assertThat(unfolded.collect(Collectors.toList()), empty());
    }

    @Test public void
    returns_an_empty_stream_if_seed_fails_condition() {
        Stream<Integer> unfolded = StreamUtils.unfold(11, i -> i + 1, i -> i <= 10);

        assertThat(unfolded.collect(Collectors.toList()), empty());
    }

    @Test public void
    returns_an_empty_stream_if_supplier_returns_null() {
        Stream<Integer> unfolded = StreamUtils.unfold(() -> null, (Integer i) -> i <= 10);

        assertThat(unfolded.collect(Collectors.toList()), empty());
    }

    @Test public void
    returns_an_empty_stream_if_supplier_fails_condition() {
        AtomicInteger a = new AtomicInteger(11);
        Supplier<Integer> supplier = a::incrementAndGet;

        Stream<Integer> unfolded = StreamUtils.unfold(supplier, (Integer i) -> i <= 10);

        assertThat(unfolded.collect(Collectors.toList()), empty());
    }
}
