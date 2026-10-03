package org.apache.commons.math.stat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FrequencyPctRegressionTest {

    @Test
    public void getPctObjectReturnsIndividualFrequencyRatherThanCumulativeFrequency() {
        Frequency frequency = new Frequency();
        frequency.addValue("one");
        frequency.addValue("two");
        frequency.addValue("three");
        frequency.addValue("three");

        Object value = "three";

        assertEquals(4L, frequency.getSumFreq());
        assertEquals(2L, frequency.getCount(value));
        assertEquals(0.5d, frequency.getPct(value), 0.0d);
    }

    @Test
    public void getPctObjectAgreesWithComparableOverload() {
        Frequency frequency = new Frequency();
        frequency.addValue("alpha");
        frequency.addValue("beta");
        frequency.addValue("beta");
        frequency.addValue("gamma");

        Object objectValue = "beta";
        Comparable<?> comparableValue = "beta";

        assertEquals(frequency.getPct(comparableValue), frequency.getPct(objectValue), 0.0d);
        assertEquals(0.5d, frequency.getPct(objectValue), 0.0d);
    }

    @Test
    public void getPctObjectForAbsentValueIsZero() {
        Frequency frequency = new Frequency();
        frequency.addValue("alpha");
        frequency.addValue("beta");
        frequency.addValue("beta");

        Object absentValue = "zeta";

        assertEquals(0L, frequency.getCount(absentValue));
        assertEquals(0.0d, frequency.getPct(absentValue), 0.0d);
    }

    @Test
    public void getPctReturnsNaNForEmptyFrequency() {
        Frequency frequency = new Frequency();

        Object objectValue = "value";
        Comparable<?> comparableValue = "value";

        assertTrue(Double.isNaN(frequency.getPct(objectValue)));
        assertTrue(Double.isNaN(frequency.getPct(comparableValue)));
    }
}