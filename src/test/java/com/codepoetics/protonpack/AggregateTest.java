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
import static org.hamcrest.Matchers.equalTo;

public class AggregateTest {

    @Test public void
    aggregate_on_bi_element_predicate() {
        Stream<String> stream = Stream.of("a1", "b1", "b2", "c1");
        Stream<List<String>> aggregated = StreamUtils.aggregate(stream, (e1, e2) -> e1.charAt(0) == e2.charAt(0));
        assertThat(aggregated.collect(toList()), contains(
            Collections.singletonList("a1"),
                asList("b1", "b2"),
            Collections.singletonList("c1")));
    }
    
    @Test public void
    aggregate_on_size1() {
        Stream<String> stream = Stream.of("a1", "b1", "b2", "c1");
        Stream<List<String>> aggregated = StreamUtils.aggregate(stream, 1);
        assertThat(aggregated.collect(toList()), contains(
            Collections.singletonList("a1"), Collections.singletonList("b1"), Collections.singletonList("b2"), Collections.singletonList("c1")));
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
                asList("a1", "b1", "b2"), Collections.singletonList("c1")));
    }

    @Test public void
    find_first_on_size2() {
        Stream<String> stream = Stream.of("a1");
        Stream<List<String>> aggregated = StreamUtils.aggregate(stream, 2);
        assertThat(aggregated.findFirst().get(), equalTo(Collections.singletonList("a1")));
    }

    @Test public void
    works_with_iterator() throws Exception {
        Stream<String> stream = Stream.of("a", "a", "a", "b", "b", "c", "c", "c");
        Stream<List<String>> aggregated = StreamUtils.aggregate(stream, String::equals);
        List<List<String>> list = new ArrayList<>();
        aggregated.iterator().forEachRemaining(list::add);
        assertThat(list, contains(
                asList("a", "a", "a"),
                asList("b", "b"),
                asList("c", "c", "c")
        ));

        stream = Stream.of("a", "a", "a", "b", "b", "c", "c", "c");
        aggregated = StreamUtils.aggregate(stream, 3);
        list = new ArrayList<>();
        aggregated.iterator().forEachRemaining(list::add);
        assertThat(list, contains(
                asList("a", "a", "a"),
                asList("b", "b", "c"),
                asList("c", "c")
        ));
    }
}
