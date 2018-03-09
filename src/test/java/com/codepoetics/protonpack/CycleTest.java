package com.codepoetics.protonpack;

import org.junit.Test;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.codepoetics.protonpack.StreamUtils.cycle;
import static com.codepoetics.protonpack.StreamUtils.zip;
import static java.util.stream.IntStream.range;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CycleTest {
  
  @Test
  public void
  fizzBuzz() {
    List<String> fizzBuzzes = zip(
        zip(
            cycle("", "", "Fizz"),
            cycle("", "", "", "", "Buzz"),
            String::concat),
        range(1, 101).boxed(),
        (fb, i) -> fb.isEmpty() ? i.toString() : fb)
        .collect(Collectors.toList());

    Function<Integer, String> fizzBuzzAt = i -> fizzBuzzes.get(i - 1);
    assertThat(fizzBuzzAt.apply(1), equalTo("1"));
    assertThat(fizzBuzzAt.apply(3), equalTo("Fizz"));
    assertThat(fizzBuzzAt.apply(5), equalTo("Buzz"));
    assertThat(fizzBuzzAt.apply(9), equalTo("Fizz"));
    assertThat(fizzBuzzAt.apply(10), equalTo("Buzz"));
    assertThat(fizzBuzzAt.apply(11), equalTo("11"));
    assertThat(fizzBuzzAt.apply(15), equalTo("FizzBuzz"));
  }
}
