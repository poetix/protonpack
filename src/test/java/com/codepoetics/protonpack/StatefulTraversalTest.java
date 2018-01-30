package com.codepoetics.protonpack;

import com.codepoetics.protonpack.stateful.Statefully;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class StatefulTraversalTest {

  @Test
  public void includesSubset() {
    Set<String> items = Stream.of("red", "green", "blue").collect(Collectors.toSet());

    assertTrue(Statefully.includesItems(Stream.of("yellow", "blue", "orange", "red", "purple", "green", "chartreuse"), "red", "green", "blue"));
    assertFalse(Statefully.includesItems(Stream.of("yellow", "blue", "orange", "red", "purple", "indigo", "chartreuse"), "red", "green", "blue"));
  }

  @Test
  public void findLastMatching() {
    assertEquals(Optional.of(6), Statefully.findLastMatching(Stream.of(3, 5, 5, 6, 7, 8), i -> i  < 7));
    assertEquals(Optional.empty(), Statefully.findLastMatching(Stream.<Integer>empty(), i -> i < 7));
    assertEquals(Optional.empty(), Statefully.findLastMatching(Stream.of(8, 3, 2, 6, 7), i -> i < 7));
  }

  @Test
  public void windowing() {
    assertThat(
        Statefully.window(
            Stream.of("cat", "dog", "mouse", "horse", "sloth","rabbit","giraffe"),
            3,
            s -> s.collect(Collectors.joining(","))).collect(Collectors.toList()),
        contains("cat,dog,mouse", "dog,mouse,horse", "mouse,horse,sloth","horse,sloth,rabbit","sloth,rabbit,giraffe"));
  }
}
