package com.codepoetics.protonpack;

import org.junit.Test;

import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;

public class NullableTest {

    @Test public void
    streamNullable_converts_null_to_empty_stream() {
        assertThat(StreamUtils.streamNullable(null).collect(Collectors.toList()).size(), equalTo(0));
    }

    @Test public void
    streamNullable_converts_non_null_value_to_stream_with_single_value() {
        assertThat(StreamUtils.streamNullable(3).collect(Collectors.toList()), hasItems(3));
    }
}
