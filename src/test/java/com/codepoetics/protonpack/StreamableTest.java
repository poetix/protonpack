package com.codepoetics.protonpack;

import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;

public class StreamableTest {

    @Test public void
    can_stream_repeatedly() {
        Streamable<Integer> streamable = Streamable.of(1, 2, 3);

        assertEquals(streamable.stream().collect(Collectors.toList()), streamable.stream().collect(Collectors.toList()));
    }

    @Test public void
    captures_stream_transformations() {
        Streamable<String> modified = Streamable.of(1, 2, 3).filter(i -> i > 1).map(i -> "Number " + i);

        assertThat(modified.toList(), contains("Number 2", "Number 3"));
    }

    @Test public void
    concat_multiple_streamables() {
        Streamable<Integer> concatenated = Streamable.of(
            Streamable.of(1, 2, 3), Streamable.of(5, 6, 7), Streamable.of(9, 10, 11)
        );

        assertThat(concatenated.toList(), contains(1, 2, 3, 5, 6, 7, 9, 10, 11));
    }

    @Test public void
    streamable_of_optional() {
        Streamable<Integer> streamableWithItem = Streamable.of(Optional.of(123));
        Streamable<Integer> streamableEmpty = Streamable.of(Optional.empty());
        assertEquals(Arrays.asList(123), streamableWithItem.toList());
        assertEquals(Arrays.asList(), streamableEmpty.toList());
    }
}
