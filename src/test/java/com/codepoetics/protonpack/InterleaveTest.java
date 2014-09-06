package com.codepoetics.protonpack;

import org.junit.Test;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class InterleaveTest {

    @Test public void
    round_robin_interleaving() {
        Stream<String> streamA = Stream.of("Peter", "Paul", "Mary");
        Stream<String> streamB = Stream.of("A", "B", "C", "D", "E");
        Stream<String> streamC = Stream.of("foo", "bar", "baz", "xyzzy");

        Stream<String> interleaved = StreamUtils.interleave(Selectors.roundRobin(), streamA, streamB, streamC);

        assertThat(interleaved.collect(Collectors.toList()), contains(
                "Peter", "A", "foo",
                "Paul", "B", "bar",
                "Mary", "C", "baz",
                "D", "xyzzy",
                "E"));
    }

    @Test public void
    sorted_interleaving() {
        Stream<String> streamA = Stream.of("Peter", "B", "xyzzy");
        Stream<String> streamB = Stream.of("A", "Paul", "C", "baz", "E");
        Stream<String> streamC = Stream.of("foo", "bar", "D", "Mary");

        Stream<String> interleaved = StreamUtils.interleave(Selectors.takeMin(),
                streamA.sorted(), streamB.sorted(), streamC.sorted());

        List<String> collected = interleaved.collect(Collectors.toList());

        assertThat(collected, contains("A", "B", "C", "D", "E", "Mary", "Paul", "Peter", "bar", "baz", "foo", "xyzzy"));
    }

    @Test public void
    prioritised_interleaving() {
        Stream<String> streamA = Stream.of("1 A1", "1 A2", "2 A3");
        Stream<String> streamB = Stream.of("2 B1", "1 B2", "1 B3");

        Function<String, Integer> priority = s -> Integer.valueOf(s.substring(0, 1));
        Stream<String> interleaved = StreamUtils.interleave(Selectors.takeMax(Comparator.comparing(priority)),
                streamA, streamB);

        List<String> collected = interleaved.collect(Collectors.toList());

        assertThat(collected, contains("2 B1", "1 A1", "1 B2", "1 A2", "2 A3", "1 B3"));
    }

}
