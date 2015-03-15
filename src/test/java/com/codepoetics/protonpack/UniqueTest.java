package com.codepoetics.protonpack;

import com.codepoetics.protonpack.collectors.CollectorUtils;
import com.codepoetics.protonpack.collectors.NonUniqueValueException;
import org.junit.Test;

import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class UniqueTest {

    @Test public void
    returns_empty_for_empty_stream() {
        assertThat(Stream.empty().collect(CollectorUtils.unique()), equalTo(Optional.empty()));
    }

    @Test public void
    returns_unique_item() {
        assertThat(Stream.of(1, 2, 3).filter(i -> i > 2).collect(CollectorUtils.unique()), equalTo(Optional.of(3)));
    }

    @Test(expected=NonUniqueValueException.class) public void
    throws_exception_if_item_is_not_unique() {
        Stream.of(1, 2, 3).filter(i -> i > 1).collect(CollectorUtils.unique());
    }
}
