package com.codepoetics.protonpack;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;

public class AggregateTest {

    @Test public void
    aggregate_on_bi_element_predicate() {
        Stream<String> stream = Stream.of("a1", "b1", "b2", "c1");
        Stream<List<String>> aggregated = StreamUtils.aggregate(stream, (e1, e2) -> e1.charAt(0) == e2.charAt(0));
        assertThat(aggregated.collect(toList()), contains(
                asList("a1"),
                asList("b1", "b2"),
                asList("c1")));
    }
    
    @Test public void
    aggregate_on_size1() {
        Stream<String> stream = Stream.of("a1", "b1", "b2", "c1");
        Stream<List<String>> aggregated = StreamUtils.aggregate(stream, 1);
        assertThat(aggregated.collect(toList()), contains(
                asList("a1"), asList("b1"), asList("b2"), asList("c1")));
    }
    
    @Test public void
    aggregate_on_size2() {
        Stream<String> stream = Stream.of("a1", "b1", "b2", "c1");
        Stream<List<String>> aggregated = StreamUtils.aggregate(stream, 2);
        assertThat(aggregated.collect(toList()), contains(
                asList("a1", "b1"), asList("b2", "c1")));
    }
    
    @Test public void
    aggregate_on_size3() {
        Stream<String> stream = Stream.of("a1", "b1", "b2", "c1");
        Stream<List<String>> aggregated = StreamUtils.aggregate(stream, 3);
        assertThat(aggregated.collect(toList()), contains(
                asList("a1", "b1", "b2"), asList("c1")));
    }
}
