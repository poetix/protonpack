package com.codepoetics.protonpack;

import org.junit.Test;

import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

/**
 * Created by hadrien on 17/09/16.
 */
public class JoinSpliteratorTest {

    @Test
    public void test_join() throws Exception {

        Stream<Integer> a = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Stream<Integer> b = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9);

        Stream<Integer> joined = StreamUtils.join(a, b, (l, r) -> l * r);
        assertThat(joined.collect(toList()), contains(
                1, 4, 9, 16, 25, 36, 49, 64, 81
        ));
    }

    @Test
    public void test_skip_left() throws Exception {

        Stream<Integer> a = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9);
        Stream<Integer> b = Stream.of(5, 6, 7, 8, 9, 10, 11, 12);

        Stream<Integer> joined = StreamUtils.join(a, b, (l, r) -> l * r);
        assertThat(joined.collect(toList()), contains(
                25, 36, 49, 64, 81
        ));
    }

    @Test
    public void test_skip_right() throws Exception {

        Stream<Integer> a = Stream.of(5, 6, 7, 8, 9, 10, 11, 12);
        Stream<Integer> b = Stream.of(1, 2, 3, 4, 5, 6, 7, 8, 9);

        Stream<Integer> joined = StreamUtils.join(a, b, (l, r) -> l * r);
        assertThat(joined.collect(toList()), contains(
                25, 36, 49, 64, 81
        ));
    }
}