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