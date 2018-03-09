package com.codepoetics.protonpack;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.iterableWithSize;

public class GroupRunsTest {
    @Test
    public void
    group_runs() {
        Stream<Integer> integerStream = Stream.of(1, 1, 2, 2, 3, 4, 5);

        List<List<Integer>> runs = StreamUtils.groupRuns(integerStream).collect(toList());

        assertThat(runs, contains(
                asList(1, 1),
                asList(2, 2),
            Collections.singletonList(3),
            Collections.singletonList(4),
            Collections.singletonList(5)));
    }

    @Test
    public void
    group_runs_end_has_run() {
        Stream<Integer> integerStream = Stream.of(1, 1, 2, 2, 3, 4, 5, 5);

        List<List<Integer>> runs = StreamUtils.groupRuns(integerStream).collect(toList());

        assertThat(runs, contains(
                asList(1, 1),
                asList(2, 2),
            Collections.singletonList(3),
            Collections.singletonList(4),
                asList(5, 5)));
    }

    @Test
    public void
    group_is_empty() {
        List<List<Integer>> runs = StreamUtils.groupRuns(new ArrayList<Integer>().stream()).collect(toList());

        assertThat(runs, iterableWithSize(0));
    }

}
