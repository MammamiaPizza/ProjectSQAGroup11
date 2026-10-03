package org.apache.commons.math.stat;

import java.util.Comparator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class FrequencyMATH259Test {

    @Test
    public void testNonComparableValuesWithComparatorAreAddedAndCounted() {
        Frequency frequency = new Frequency(new Comparator() {
            public int compare(Object left, Object right) {
                return ((Token) left).id - ((Token) right).id;
            }
        });

        Token higher = new Token(2);
        Token lower = new Token(1);

        frequency.addValue(higher);
        frequency.addValue(higher);
        frequency.addValue(lower);

        assertEquals(3L, frequency.getSumFreq());
        assertEquals(2L, frequency.getCount(higher));
        assertEquals(1L, frequency.getCount(lower));
        assertEquals(1L, frequency.getCumFreq(lower));
        assertEquals(3L, frequency.getCumFreq(higher));
        assertSame(lower, frequency.valuesIterator().next());
    }

    @Test
    public void testNaturalOrderNumericValuesRemainNormalizedAndCounted() {
        Frequency frequency = new Frequency();

        frequency.addValue(4);
        frequency.addValue(4L);
        frequency.addValue(Integer.valueOf(7));

        assertEquals(3L, frequency.getSumFreq());
        assertEquals(2L, frequency.getCount(4));
        assertEquals(2L, frequency.getCount(4L));
        assertEquals(1L, frequency.getCount(7L));
        assertEquals(2L, frequency.getCumFreq(4));
        assertEquals(3L, frequency.getCumFreq(7));
    }

    @Test
    public void testEmptyFrequencyHasNoCountsAndUndefinedPercentages() {
        Frequency frequency = new Frequency();

        assertEquals(0L, frequency.getSumFreq());
        assertEquals(0L, frequency.getCount(1));
        assertEquals(0L, frequency.getCumFreq(1));
        assertTrue(Double.isNaN(frequency.getPct(1)));
        assertTrue(Double.isNaN(frequency.getCumPct(1)));
    }

    private static final class Token implements Comparable<Token> {
        private final int id;

        private Token(int id) {
            this.id = id;
        }

        public int compareTo(Token other) {
            return id - other.id;
        }
    }
}