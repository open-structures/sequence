package com.openstructures.sequence;

import org.junit.Before;
import org.junit.Test;

import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;


public class InMemorySequenceTest {

    private InMemorySequence<Integer, Integer> sequence;

    private final Comparator<Integer> integerComparable = Integer::compareTo;

    @Before
    public void setUp() {
        sequence = new InMemorySequence<>(integerComparable);
    }

    @Test
    public void shouldInsertFirst() {
        // when
        SequenceLink<Integer> sequenceLink = sequence.insert(1);

        // then
        assertThat(sequenceLink).isNotNull();
        assertThat(sequenceLink.getValue()).isEqualTo(1);
        assertThat(sequenceLink.getLeft()).isNull();
        assertThat(sequenceLink.getRight()).isNull();
    }

    @Test
    public void shouldInsertAndGet() {
        // when
        sequence.insert(1);
        sequence.insert(10);
        sequence.insert(7);
        sequence.insert(5);
        sequence.insert(18);

        // then
        assertThat(sequence.get(10)).isNotNull();
        assertThat(sequence.get(10).getLeft()).isEqualTo(sequence.get(7));
        assertThat(sequence.get(10).getRight()).isEqualTo(sequence.get(18));

        assertThat(sequence.get(5)).isNotNull();
        assertThat(sequence.get(5).getLeft()).isEqualTo(sequence.get(1));
        assertThat(sequence.get(5).getRight()).isEqualTo(sequence.get(7));

        assertThat(sequence.get(1)).isNotNull();
        assertThat(sequence.get(1).getLeft()).isNull();
        assertThat(sequence.get(1).getRight()).isEqualTo(sequence.get(5));

        assertThat(sequence.get(18)).isNotNull();
        assertThat(sequence.get(18).getLeft()).isEqualTo(sequence.get(10));
        assertThat(sequence.get(18).getRight()).isNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfInsertingDuplicatedElement() {
        // when
        sequence.insert(1);
        sequence.insert(1);

        // then expect exception
    }

    @Test
    public void shouldDeleteSingleElement() {
        // given
        sequence.insert(1);

        // when
        sequence.delete(1);

        // then
        assertThat(sequence.isEmpty()).isTrue();
    }

    @Test
    public void shouldDeleteLeftmostAndRightmostElements() {
        // given
        sequence.insert(1);
        sequence.insert(3);
        sequence.insert(5);
        sequence.insert(7);
        sequence.insert(11);

        // when
        sequence.delete(1);
        sequence.delete(11);

        // then
        assertThat(sequence.get(3).getLeft()).isNull();
        assertThat(sequence.get(3).getRight()).isNotNull();
        assertThat(sequence.get(3).getRight().getValue()).isEqualTo(5);
        assertThat(sequence.get(7).getLeft().getValue()).isEqualTo(5);
        assertThat(sequence.get(7).getRight()).isNull();
    }


    @Test
    public void shouldDeleteElementFromTheMiddle() {
        // given
        sequence.insert(1);
        sequence.insert(3);
        sequence.insert(5);

        // when
        sequence.delete(3);

        // then
        assertThat(sequence.get(1).getLeft()).isNull();
        assertThat(sequence.get(1).getRight()).isNotNull();
        assertThat(sequence.get(1).getRight().getValue()).isEqualTo(5);
        assertThat(sequence.get(5).getLeft().getValue()).isEqualTo(1);
        assertThat(sequence.get(5).getRight()).isNull();
    }

    @Test
    public void shouldInsertAfterDelete() {
        // given
        sequence.insert(1);
        sequence.insert(3);
        sequence.insert(7);

        // when
        sequence.delete(3);
        sequence.insert(5);

        // then
        SequenceLink<Integer> link = sequence.get(5);
        assertThat(link).isNotNull();
        assertThat(link.getLeft().getValue()).isEqualTo(1);
        assertThat(link.getRight().getValue()).isEqualTo(7);
    }

    @Test
    public void shouldJoinWithEmptySequence() {
        // given
        InMemorySequence<Integer, Integer> sequence1 = new InMemorySequence<>(integerComparable);
        InMemorySequence<Integer, Integer> sequence2 = new InMemorySequence<>(integerComparable);
        sequence2.insert(1);

        // when
        InMemorySequence<Integer, Integer> result = InMemorySequence.join(sequence1, sequence2);

        // then
        assertThat(result).isNotNull();
        assertThat(result.get(1).getValue()).isEqualTo(1);
        assertThat(result.get(1).getLeft()).isNull();
        assertThat(result.get(1).getRight()).isNull();
    }

    @Test
    public void shouldJoinWithEmptySequence2() {
        // given
        InMemorySequence<Integer, Integer> sequence1 = new InMemorySequence<>(integerComparable);
        sequence1.insert(1);
        InMemorySequence<Integer, Integer> sequence2 = new InMemorySequence<>(integerComparable);

        // when
        InMemorySequence<Integer, Integer> result = InMemorySequence.join(sequence1, sequence2);

        // then
        assertThat(result).isNotNull();
        assertThat(result.get(1).getValue()).isEqualTo(1);
        assertThat(result.get(1).getLeft()).isNull();
        assertThat(result.get(1).getRight()).isNull();
    }

    @Test
    public void shouldJoinTwoSequences() {
        // given
        InMemorySequence<Integer, Integer> sequence1 = new InMemorySequence<>(integerComparable);
        sequence1.insert(1);
        sequence1.insert(2);
        InMemorySequence<Integer, Integer> sequence2 = new InMemorySequence<>(integerComparable);
        sequence2.insert(3);
        sequence2.insert(4);
        sequence2.insert(5);
        sequence2.insert(6);
        sequence2.insert(7);

        // when
        InMemorySequence<Integer, Integer> result = InMemorySequence.join(sequence1, sequence2);

        // then
        assertThat(result).isNotNull();
        assertThat(result.get(1).getValue()).isEqualTo(1);
        assertThat(result.get(1).getLeft()).isNull();
        assertThat(result.get(1).getRight().getValue()).isEqualTo(2);

        assertThat(result.get(2).getValue()).isEqualTo(2);
        assertThat(result.get(2).getLeft().getValue()).isEqualTo(1);
        assertThat(result.get(2).getRight().getValue()).isEqualTo(3);

        assertThat(result.get(3).getValue()).isEqualTo(3);
        assertThat(result.get(3).getLeft().getValue()).isEqualTo(2);
        assertThat(result.get(3).getRight().getValue()).isEqualTo(4);

        assertThat(result.get(4).getValue()).isEqualTo(4);
        assertThat(result.get(4).getLeft().getValue()).isEqualTo(3);
        assertThat(result.get(4).getRight().getValue()).isEqualTo(5);

        assertThat(result.get(5).getValue()).isEqualTo(5);
        assertThat(result.get(5).getLeft().getValue()).isEqualTo(4);
        assertThat(result.get(5).getRight().getValue()).isEqualTo(6);

        assertThat(result.get(6).getValue()).isEqualTo(6);
        assertThat(result.get(6).getLeft().getValue()).isEqualTo(5);
        assertThat(result.get(6).getRight().getValue()).isEqualTo(7);

        assertThat(result.get(7).getValue()).isEqualTo(7);
        assertThat(result.get(7).getLeft().getValue()).isEqualTo(6);
        assertThat(result.get(7).getRight()).isNull();
    }

    @Test
    public void shouldJoinMultipleSequencesInIncreasingOrder() {
        // when
        InMemorySequence<Integer, Integer> sequence = InMemorySequence.join(newSequenceWith(1),
                newSequenceWith(2));
        sequence = InMemorySequence.join(sequence, newSequenceWith(3));
        sequence = InMemorySequence.join(sequence, newSequenceWith(4));
        sequence = InMemorySequence.join(sequence, newSequenceWith(5));
        sequence = InMemorySequence.join(sequence, newSequenceWith(6));
        sequence = InMemorySequence.join(sequence, newSequenceWith(7));
        sequence = InMemorySequence.join(sequence, newSequenceWith(8));
        sequence = InMemorySequence.join(sequence, newSequenceWith(9));
        sequence = InMemorySequence.join(sequence, newSequenceWith(10));

        // then
        assertSequence(sequence, 1, null, 2);
        assertSequence(sequence, 2, 1, 3);
        assertSequence(sequence, 3, 2, 4);
        assertSequence(sequence, 4, 3, 5);
        assertSequence(sequence, 5, 4, 6);
        assertSequence(sequence, 6, 5, 7);
        assertSequence(sequence, 7, 6, 8);
        assertSequence(sequence, 8, 7, 9);
        assertSequence(sequence, 9, 8, 10);
        assertSequence(sequence, 10, 9, null);
    }


    private InMemorySequence<Integer, Integer> newSequenceWith(int number) {
        InMemorySequence<Integer, Integer> sequence = new InMemorySequence<>(integerComparable);
        sequence.insert(number);
        return sequence;
    }

    @Test
    public void shouldJoinMultipleSequencesInDecreasingOrder() {
        // when
        InMemorySequence<Integer, Integer> sequence = InMemorySequence.join(newSequenceWith(9),
                newSequenceWith(10));
        sequence = InMemorySequence.join(newSequenceWith(8), sequence);
        sequence = InMemorySequence.join(newSequenceWith(7), sequence);
        sequence = InMemorySequence.join(newSequenceWith(6), sequence);
        sequence = InMemorySequence.join(newSequenceWith(5), sequence);
        sequence = InMemorySequence.join(newSequenceWith(4), sequence);
        sequence = InMemorySequence.join(newSequenceWith(3), sequence);
        sequence = InMemorySequence.join(newSequenceWith(2), sequence);
        sequence = InMemorySequence.join(newSequenceWith(1), sequence);

        // then
        assertSequence(sequence, 1, null, 2);
        assertSequence(sequence, 2, 1, 3);
        assertSequence(sequence, 3, 2, 4);
        assertSequence(sequence, 4, 3, 5);
        assertSequence(sequence, 5, 4, 6);
        assertSequence(sequence, 6, 5, 7);
        assertSequence(sequence, 7, 6, 8);
        assertSequence(sequence, 8, 7, 9);
        assertSequence(sequence, 9, 8, 10);
        assertSequence(sequence, 10, 9, null);
    }

    /**
     * Bugfix. Deleting elements from the result of join didn't work
     */
    @Test
    public void shouldJoinAndDelete() {
        // given
        InMemorySequence<Integer, Integer> sequence1 = new InMemorySequence<>(integerComparable);
        sequence1.insert(1);
        sequence1.insert(2);
        InMemorySequence<Integer, Integer> sequence2 = new InMemorySequence<>(integerComparable);
        sequence2.insert(7);

        // when
        InMemorySequence<Integer, Integer> joined = InMemorySequence.join(sequence1, sequence2);
        joined.delete(2);

        // then
        assertThat(joined).isNotNull();
        assertThat(joined.get(1).getValue()).isEqualTo(1);
        assertThat(joined.get(1).getLeft()).isNull();
        assertThat(joined.get(1).getRight().getValue()).isEqualTo(7);

        assertThat(joined.get(7).getValue()).isEqualTo(7);
        assertThat(joined.get(7).getLeft().getValue()).isEqualTo(1);
        assertThat(joined.get(7).getRight()).isNull();
    }

    @Test
    public void shouldReturnElementThatIsGreaterThan() {
        // given
        sequence.insert(1);
        sequence.insert(3);
        sequence.insert(5);

        // when and then
        assertThat(sequence.greaterThan(0)).isEqualTo(1);
        assertThat(sequence.greaterThan(1)).isEqualTo(3);
        assertThat(sequence.greaterThan(2)).isEqualTo(3);
        assertThat(sequence.greaterThan(3)).isEqualTo(5);
        assertThat(sequence.greaterThan(4)).isEqualTo(5);
        assertThat(sequence.greaterThan(5)).isNull();
    }

    @Test
    public void shouldReturnLessThan() {
        // given
        sequence.insert(1);
        sequence.insert(3);
        sequence.insert(5);

        // when and then
        assertThat(sequence.lessThan(1)).isNull();
        assertThat(sequence.lessThan(2)).isEqualTo(1);
        assertThat(sequence.lessThan(3)).isEqualTo(1);
        assertThat(sequence.lessThan(4)).isEqualTo(3);
        assertThat(sequence.lessThan(5)).isEqualTo(3);
        assertThat(sequence.lessThan(6)).isEqualTo(5);
    }

    @Test
    public void shouldBeEqualTo() {
        // given
        sequence.insert(1);
        sequence.insert(3);
        sequence.insert(5);

        // when and then
        assertThat(sequence.equalTo(1)).isEqualTo(1);
        assertThat(sequence.equalTo(2)).isNull();
        assertThat(sequence.equalTo(3)).isEqualTo(3);
        assertThat(sequence.equalTo(4)).isNull();
        assertThat(sequence.equalTo(5)).isEqualTo(5);
    }

    @Test
    public void shouldInsertInTheMiddle() {
        // when
        sequence.insert(3);
        sequence.insert(5);
        sequence.insert(7);
        sequence.insert(6);

        // then
        assertSequence(sequence, 6, 5, 7);
    }

    private static void assertSequence(Sequence<Integer, Integer> sequence, Integer value,
                                       Integer left, Integer right) {
        assertThat(sequence).isNotNull();
        assertThat(sequence.get(value)).isNotNull();
        assertThat(sequence.get(value).getValue()).isEqualTo(value);
        SequenceLink<Integer> integerSequenceLink = sequence.get(value);
        if (left == null) {
            assertThat(integerSequenceLink.getLeft()).isNull();
        } else {
            assertThat(integerSequenceLink.getLeft()).isNotNull();
            assertThat(integerSequenceLink.getLeft().getValue()).isEqualTo(left);
        }
        if (right == null) {
            assertThat(integerSequenceLink.getRight()).isNull();
        } else {
            assertThat(integerSequenceLink.getRight()).isNotNull();
            assertThat(integerSequenceLink.getRight().getValue()).isEqualTo(right);
        }
    }
}
