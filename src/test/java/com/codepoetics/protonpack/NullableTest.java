package com.codepoetics.protonpack;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
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

    @Test public void
    stream_converts_null_iterable_to_empty_stream() {
        assertThat(StreamUtils.stream((Iterable<Integer>) null).collect(Collectors.toList()).size(), equalTo(0));
    }

    @Test public void
    stream_converts_non_null_iterable_value_to_stream() {
        List<Integer> list = new ArrayList<>();
        list.add(22);
        list.add(33);
        assertThat(StreamUtils.stream(list).collect(Collectors.toList()), hasItems(22, 33));
    }

    @Test public void
    stream_converts_null_int_array_to_empty_stream() {
        assertThat(StreamUtils.stream((int[]) null).sum(), equalTo(0));
    }

    @Test public void
    stream_converts_non_null_int_array_to_stream_with_single_value() {
        int [] array = new int[] {22, 33};
        assertThat(StreamUtils.stream(array).sum(), equalTo(55));
    }

    @Test public void
    stream_converts_null_obj_array_to_empty_stream() {
        assertThat(StreamUtils.stream((String [])null).collect(Collectors.toList()).size(), equalTo(0));
    }

    @Test public void
    stream_converts_non_null_obj_array_to_stream_with_single_value() {
        String [] array = new String[] {"hello", "world"};
        assertThat(StreamUtils.stream(array).collect(Collectors.toList()), hasItems("hello", "world"));
    }
}
