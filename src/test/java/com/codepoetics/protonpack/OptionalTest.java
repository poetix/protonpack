package com.codepoetics.protonpack;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import static org.junit.Assert.*;

public class OptionalTest {

    @Test
    public void
    stream_of_nonempty_optionals() {
        Stream<Integer> numbers = Stream.of(123, 456);
        Stream<Integer> results = numbers.flatMap(n -> StreamUtils.of(maybeAdd3(n)));
        List<Integer> resultList = results.collect(Collectors.toList());
        assertEquals(Arrays.asList(126, 459), resultList);
    }

    @Test
    public void
    stream_of_empty_optionals() {
        Stream<String> names = Stream.of("John", "Susan");
        Stream<String> transformed = names.flatMap(s -> StreamUtils.of(Optional.empty()));
        List<String> results = transformed.collect(Collectors.toList());
        assertEquals(Collections.emptyList(), results);
    }

    private Optional<Integer> maybeAdd3(final Integer number) {
        return Optional.of(number + 3);
    }

}
