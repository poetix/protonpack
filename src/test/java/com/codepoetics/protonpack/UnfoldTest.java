package com.codepoetics.protonpack;

import org.junit.Test;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class UnfoldTest {

    @Test
    public void unfolds_a_seed_until_empty_is_returned() {
        Stream<Integer> unfolded = StreamUtils.unfold(1, i -> (i < 10) ? Optional.of(i + 1) : Optional.empty());

        assertThat(unfolded.collect(Collectors.toList()), contains(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

}
