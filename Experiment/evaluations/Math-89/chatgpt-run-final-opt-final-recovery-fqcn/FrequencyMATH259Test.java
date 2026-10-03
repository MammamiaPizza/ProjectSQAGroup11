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

@org.junit.Test
public void testAddValueObjectRejectsNonComparableValuesWithIllegalArgumentException() {
    org.apache.commons.math.stat.Frequency frequency =
            new org.apache.commons.math.stat.Frequency();

    try {
        frequency.addValue((Object) new Object());
        org.junit.Assert.fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException expected) {
        // expected
    }

    org.junit.Assert.assertEquals(0L, frequency.getSumFreq());
}

@org.junit.Test
public void testClearRemovesPreviouslyAddedValues() {
    org.apache.commons.math.stat.Frequency frequency =
            new org.apache.commons.math.stat.Frequency();
    frequency.addValue(3);
    frequency.addValue(3);
    frequency.addValue(5);

    frequency.clear();

    org.junit.Assert.assertEquals(0L, frequency.getSumFreq());
    org.junit.Assert.assertEquals(0L, frequency.getCount(3));
    org.junit.Assert.assertFalse(frequency.valuesIterator().hasNext());

    frequency.addValue(7);
    org.junit.Assert.assertEquals(1L, frequency.getSumFreq());
    org.junit.Assert.assertEquals(1L, frequency.getCount(7));
}

@org.junit.Test
public void testCumulativeFrequencyBelowRangeAndForIncomparableValueIsZero() {
    org.apache.commons.math.stat.Frequency frequency =
            new org.apache.commons.math.stat.Frequency();
    frequency.addValue(2);
    frequency.addValue(5);

    org.junit.Assert.assertEquals(0L, frequency.getCumFreq(1));
    org.junit.Assert.assertEquals(0.0d, frequency.getCumPct(1), 0.0d);
    org.junit.Assert.assertEquals(0L, frequency.getCumFreq("not comparable"));
    org.junit.Assert.assertEquals(0.0d, frequency.getCumPct("not comparable"), 0.0d);
}

@org.junit.Test
public void testCharacterValuesAreCountedAndAccumulated() {
    org.apache.commons.math.stat.Frequency frequency =
            new org.apache.commons.math.stat.Frequency();
    frequency.addValue('b');
    frequency.addValue('a');
    frequency.addValue('a');

    org.junit.Assert.assertEquals(2L, frequency.getCount('a'));
    org.junit.Assert.assertEquals(1L, frequency.getCount('b'));
    org.junit.Assert.assertEquals(2L, frequency.getCumFreq('a'));
    org.junit.Assert.assertEquals(3L, frequency.getCumFreq('b'));
    org.junit.Assert.assertEquals(2.0d / 3.0d, frequency.getPct('a'), 0.0d);
}
}
