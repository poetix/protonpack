package com.codepoetics.protonpack;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;

public class SeqTest {

    @Test
    public void emptySeqsAreEqual() {
        assertThat(Seq.empty(), equalTo(Seq.empty()));
    }

    @Test
    public void seqsWithSameContentsAreEqual() {
        assertThat(Seq.of(1, 2, 3), equalTo(Seq.of(1, 2, 3)));
    }

    @Test
    public void seqsWithDifferentContentsAreNotEqual() {
        assertThat(Seq.of(1, 2, 4), not(equalTo(Seq.of(1, 2, 3))));
    }

    @Test
    public void seqsHaveAStringRepresentation() {
        assertThat(Seq.of("Hello", "World").toString(), equalTo("(Hello,World)"));
    }

    @Test
    public void seqsAreAppendable() {
        assertThat(Seq.of(1, 2, 3).append(Seq.of(4, 5, 6)), equalTo(Seq.of(1, 2, 3, 4, 5, 6)));
    }

    @Test
    public void valuesCanBeConsedOntoAStream() {
        assertThat(Seq.of("Blankets").cons("In").cons("Pigs"), equalTo(Seq.of("Pigs", "In", "Blankets")));
    }
}
