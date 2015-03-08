package com.codepoetics.protonpack;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.maxBy;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.iterableWithSize;

public class WindowedTest {
    @Test
    public void
    windowing_on_list() {
        Stream<Integer> integerStream = Stream.of(1, 2, 3, 4, 5);

        List<List<Integer>> windows = StreamUtils.windowed(integerStream, 2).collect(toList());

        assertThat(windows, contains(
                asList(1, 2),
                asList(2, 3),
                asList(3, 4),
                asList(4, 5)));
    }

    @Test
    public void
    windowing_on_list_two_overlap() {
        Stream<Integer> integerStream = Stream.of(1, 2, 3, 4, 5);

        List<List<Integer>> windows = StreamUtils.windowed(integerStream, 3, 2).collect(toList());

        assertThat(windows, contains(
                asList(1, 2, 3),
                asList(3, 4, 5)));
    }

    @Test
    public void
    windowing_on__empty_list() {
        ArrayList<Integer> ints = new ArrayList<>();

        ints.stream().collect(maxBy((a, b) -> a.toString().compareTo(b.toString())));

        List<List<Integer>> windows = StreamUtils.windowed(ints.stream(), 2).collect(toList());

        assertThat(windows, iterableWithSize(0));
    }
}
