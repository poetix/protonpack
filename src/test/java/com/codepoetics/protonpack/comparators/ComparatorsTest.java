package com.codepoetics.protonpack.comparators;

import org.junit.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ComparatorsTest {

    @Test
    public void compareLists() {
        List<String> first = Arrays.asList("A", "B", "C");
        List<String> same = Arrays.asList("A", "B", "C");
        List<String> second = Arrays.asList("A", "B", "D");
        List<String> longer = Arrays.asList("A", "B", "C", "D");
        List<String> shorter = Arrays.asList("A", "B");

        Comparator<? super List<String>> unit = Comparators.toListComparator(String::compareTo);

        assertThat(unit.compare(first, same), equalTo(0));

        assertThat(unit.compare(first, second), equalTo(-1));
        assertThat(unit.compare(second, first), equalTo(1));

        assertThat(unit.compare(first, longer), equalTo(-1));
        assertThat(unit.compare(longer, first), equalTo(1));

        assertThat(unit.compare(first, shorter), equalTo(1));
        assertThat(unit.compare(shorter, first), equalTo(-1));
    }
}
