package com.codepoetics.protonpack;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;

public class TapTest {

    @Test public void
    elements_from_a_tapped_stream_are_sent_to_the_tap_as_they_are_released() {
        Stream<String> source = Stream.of("foo", "bar", "baz");
        List<String> receivedByTap = new ArrayList<>();
        Stream<String> tapped = StreamUtils.tap(source, receivedByTap::add);
        List<String> collected = tapped.collect(Collectors.toList());

        assertThat(receivedByTap, equalTo(collected));
    }

    @Test public void
    tap_receives_all_elements_from_the_tapped_stream() {
        Stream<String> source = Stream.of("foo", "bar", "baz");
        List<String> receivedByTap = new ArrayList<>();
        Stream<String> tapped = StreamUtils.tap(source, receivedByTap::add);
        List<String> collected = tapped.filter(s -> s.startsWith("b")).collect(Collectors.toList());

        assertThat(collected, contains("bar", "baz"));
        assertThat(receivedByTap, contains("foo", "bar", "baz"));
    }

    @Test public void
    tap_receives_modified_elements_from_modified_stream() {
        Stream<String> source = Stream.of("foo", "bar", "baz");
        List<String> receivedByFirstTap = new ArrayList<>();
        List<String> receivedBySecondTap = new ArrayList<>();

        Stream<String> tappedOriginal = StreamUtils.tap(source, receivedByFirstTap::add);
        Stream<String> modified = tappedOriginal.map(String::toUpperCase);
        Stream<String> tappedModified = StreamUtils.tap(modified, receivedBySecondTap::add);
        List<String> collectedModified = tappedModified.collect(Collectors.toList());

        assertThat(receivedBySecondTap, equalTo(collectedModified));
        assertThat(receivedByFirstTap, contains("foo", "bar", "baz"));
    }
}
