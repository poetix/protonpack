package com.codepoetics.protonpack;

import org.junit.Test;

import java.util.Optional;
import java.util.stream.Stream;

import static com.codepoetics.protonpack.collectors.CollectorUtils.maxBy;
import static com.codepoetics.protonpack.collectors.CollectorUtils.minBy;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MinMaxByProjectionTest {
    @Test
    public void
    max_by_with_projection_and_default_comparer() {
        Stream<String> integerStream = Stream.of("a", "bb", "ccc", "1");

        Optional<String> max = integerStream.collect(maxBy(String::length));

        assertThat(max.get(), is("ccc"));
    }

    @Test
    public void
    min_by_with_projection_and_default_comparer() {
        Stream<String> integerStream = Stream.of("abc", "bb", "ccc", "1");

        Optional<String> min = integerStream.collect(minBy(String::length));

        assertThat(min.get(), is("1"));
    }
}
