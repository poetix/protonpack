package com.codepoetics.protonpack;

import org.junit.Test;

import java.util.stream.IntStream;

import static com.codepoetics.protonpack.collectors.CollectorUtils.noCombiner;

public class NoCombinerTest {

    @Test(expected = IllegalStateException.class) public void
    throws_illegal_state_exception_when_called() {
        Long sum = IntStream.range(0, 10000).parallel().mapToObj(Long::valueOf).reduce(0L, (l1, l2) -> l1 + l2, noCombiner());
    }

    @Test public void
    is_never_called_for_sequential_stream() {
        Long sum = IntStream.range(0, 10000).mapToObj(Long::valueOf).reduce(0L, (l1, l2) -> l1 + l2, noCombiner());
    }

}
