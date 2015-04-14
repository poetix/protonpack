package com.codepoetics.protonpack;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.IntStream;

import static com.codepoetics.protonpack.collectors.CollectorUtils.noCombiner;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class NoCombinerTest {

    @Test(expected = IllegalStateException.class) public void
    throws_illegal_state_exception_when_called() {
        Long sum = IntStream.range(0, 10000).parallel().mapToObj(Long::valueOf).reduce(0L, (l1, l2) -> l1 + l2, noCombiner());
    }

    @Test public void
    is_never_called_for_sequential_stream() {
        Long sum = IntStream.range(0, 10000).mapToObj(Long::valueOf).reduce(0L, (l1, l2) -> l1 + l2, noCombiner());
    }

    @Test public void
    can_be_used_in_a_collector() {
        assertThat(
                IntStream.range(0, 10000).mapToObj(Long::valueOf).collect(Collector.of(HashSet<Long>::new, Set::add, noCombiner())).size(),
                equalTo(10000));
    }
}
