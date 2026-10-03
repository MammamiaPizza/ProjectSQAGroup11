package org.apache.commons.math.stat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

public class FrequencyNonComparableTest {

    @Test
    public void testAddNonComparableValueToEmptyFrequency() {
        Frequency frequency = new Frequency();
        Object value = new Object();

        frequency.addValue(value);

        assertEquals(1L, frequency.getSumFreq());
        assertEquals(1L, frequency.getCount(value));
    }

    @Test
    public void testAddingDistinctNonComparableValueIsRejectedWithoutChangingCounts() {
        Frequency frequency = new Frequency();
        Object firstValue = new Object();
        Object incompatibleValue = new Object();
        frequency.addValue(firstValue);

        try {
            frequency.addValue(incompatibleValue);
            fail("Expected IllegalArgumentException for a value not comparable to an existing key");
        } catch (IllegalArgumentException expected) {
            assertEquals(1L, frequency.getSumFreq());
            assertEquals(1L, frequency.getCount(firstValue));
            assertEquals(0L, frequency.getCount(incompatibleValue));
        }
    }

    @Test
    public void testAddingIncompatibleValueToNumericFrequencyLeavesExistingCountsUntouched() {
        Frequency frequency = new Frequency();
        frequency.addValue(2);
        frequency.addValue(2);
        frequency.addValue(5);

        try {
            frequency.addValue(new Object());
            fail("Expected IllegalArgumentException for a value not comparable to numeric keys");
        } catch (IllegalArgumentException expected) {
            assertEquals(3L, frequency.getSumFreq());
            assertEquals(2L, frequency.getCount(2));
            assertEquals(1L, frequency.getCount(5));
            assertEquals(2L, frequency.getCumFreq(2));
        }
    }

    @Test
    public void testComparableValuesRetainFrequencyAndCumulativeOrdering() {
        Frequency frequency = new Frequency();
        frequency.addValue(3);
        frequency.addValue(1);
        frequency.addValue(3);
        frequency.addValue(2);

        assertEquals(4L, frequency.getSumFreq());
        assertEquals(1L, frequency.getCount(1));
        assertEquals(1L, frequency.getCount(2));
        assertEquals(2L, frequency.getCount(3));
        assertEquals(2L, frequency.getCumFreq(2));
        assertEquals(4L, frequency.getCumFreq(3));
    }
}
