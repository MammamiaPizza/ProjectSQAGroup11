package org.jfree.data.time.junit;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriodValues;
import org.junit.Test;

public class TimePeriodValuesMiddleIndexTest {

    private SimpleTimePeriod period(long start, long end) {
        return new SimpleTimePeriod(new Date(start), new Date(end));
    }

    @Test
    public void testMaxMiddleIndexRemainsAtEarlierMaximumWhenLaterPeriodIsOnlyIntermediate() {
        TimePeriodValues values = new TimePeriodValues("S");
        values.add(period(0L, 10L), 1.0);
        values.add(period(100L, 110L), 2.0);
        values.add(period(20L, 30L), 3.0);
        values.add(period(40L, 50L), 4.0);

        assertEquals(1, values.getMaxMiddleIndex());
        assertEquals(0, values.getMinMiddleIndex());
    }

    @Test
    public void testMaxMiddleIndexCanRemainAtFirstInsertedPeriod() {
        TimePeriodValues values = new TimePeriodValues("S");
        values.add(period(100L, 110L), 1.0);
        values.add(period(0L, 10L), 2.0);
        values.add(period(20L, 30L), 3.0);

        assertEquals(0, values.getMaxMiddleIndex());
        assertEquals(1, values.getMinMiddleIndex());
    }

    @Test
    public void testUpdateValueDoesNotChangeMiddleBounds() {
        TimePeriodValues values = new TimePeriodValues("S");
        values.add(period(0L, 10L), 1.0);
        values.add(period(20L, 30L), 2.0);
        values.add(period(100L, 110L), 3.0);

        values.update(1, 99.0);

        assertEquals(0, values.getMinMiddleIndex());
        assertEquals(2, values.getMaxMiddleIndex());
        assertEquals(99.0, values.getValue(1).doubleValue(), 0.0);
    }

    @Test
    public void testDeleteCurrentMaximumRecalculatesMiddleBounds() {
        TimePeriodValues values = new TimePeriodValues("S");
        values.add(period(0L, 10L), 1.0);
        values.add(period(100L, 110L), 2.0);
        values.add(period(90L, 100L), 3.0);
        values.add(period(80L, 90L), 4.0);

        values.delete(1, 1);

        assertEquals(3, values.getItemCount());
        assertEquals(0, values.getMinMiddleIndex());
        assertEquals(1, values.getMaxMiddleIndex());
    }

    @Test
    public void testEmptyAndSingleItemBounds() {
        TimePeriodValues values = new TimePeriodValues("S");

        assertEquals(-1, values.getMinStartIndex());
        assertEquals(-1, values.getMaxStartIndex());
        assertEquals(-1, values.getMinMiddleIndex());
        assertEquals(-1, values.getMaxMiddleIndex());
        assertEquals(-1, values.getMinEndIndex());
        assertEquals(-1, values.getMaxEndIndex());

        values.add(period(10L, 20L), 1.0);

        assertEquals(0, values.getMinStartIndex());
        assertEquals(0, values.getMaxStartIndex());
        assertEquals(0, values.getMinMiddleIndex());
        assertEquals(0, values.getMaxMiddleIndex());
        assertEquals(0, values.getMinEndIndex());
        assertEquals(0, values.getMaxEndIndex());
    }

    @Test
    public void testCreateCopyMaintainsBoundsForCopiedItems() throws CloneNotSupportedException {
        TimePeriodValues values = new TimePeriodValues("S");
        values.add(period(10L, 20L), 1.0);
        values.add(period(10L, 20L), 2.0);

        TimePeriodValues copy = values.createCopy(0, 1);

        assertEquals(2, copy.getItemCount());
        assertEquals(0, copy.getMinMiddleIndex());
        assertEquals(0, copy.getMaxMiddleIndex());
        assertEquals(2.0, copy.getValue(1).doubleValue(), 0.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNullItemIsRejected() {
        TimePeriodValues values = new TimePeriodValues("S");
        values.add(null);
    }

@org.junit.Test
public void testAllTimeBoundsWithNonChronologicalInsertion() {
    TimePeriodValues values = new TimePeriodValues("S");
    values.add(new org.jfree.data.time.SimpleTimePeriod(
            new java.util.Date(100L), new java.util.Date(200L)),
            java.lang.Integer.valueOf(1));
    values.add(new org.jfree.data.time.SimpleTimePeriod(
            new java.util.Date(0L), new java.util.Date(400L)),
            java.lang.Integer.valueOf(2));
    values.add(new org.jfree.data.time.SimpleTimePeriod(
            new java.util.Date(50L), new java.util.Date(60L)),
            java.lang.Integer.valueOf(3));

    org.junit.Assert.assertEquals(1, values.getMinStartIndex());
    org.junit.Assert.assertEquals(0, values.getMaxStartIndex());
    org.junit.Assert.assertEquals(2, values.getMinMiddleIndex());
    org.junit.Assert.assertEquals(1, values.getMaxMiddleIndex());
    org.junit.Assert.assertEquals(2, values.getMinEndIndex());
    org.junit.Assert.assertEquals(1, values.getMaxEndIndex());
}

@org.junit.Test
public void testEqualsDistinguishesDescriptionsCountsAndItems() {
    org.jfree.data.time.SimpleTimePeriod period
            = new org.jfree.data.time.SimpleTimePeriod(
                    new java.util.Date(10L), new java.util.Date(20L));
    TimePeriodValues first = new TimePeriodValues("S");
    TimePeriodValues second = new TimePeriodValues("S");
    first.add(period, java.lang.Integer.valueOf(1));
    second.add(period, java.lang.Integer.valueOf(1));

    org.junit.Assert.assertTrue(first.equals(first));
    org.junit.Assert.assertFalse(first.equals("S"));
    org.junit.Assert.assertTrue(first.equals(second));

    second.setDomainDescription("Other Domain");
    org.junit.Assert.assertFalse(first.equals(second));
    second.setDomainDescription(first.getDomainDescription());

    second.setRangeDescription("Other Range");
    org.junit.Assert.assertFalse(first.equals(second));
    second.setRangeDescription(first.getRangeDescription());

    second.add(period, java.lang.Integer.valueOf(1));
    org.junit.Assert.assertFalse(first.equals(second));
    first.add(period, java.lang.Integer.valueOf(1));
    org.junit.Assert.assertTrue(first.equals(second));

    second.update(1, java.lang.Integer.valueOf(2));
    org.junit.Assert.assertFalse(first.equals(second));
}

@org.junit.Test
public void testCloneCreatesIndependentEquivalentSeries()
        throws CloneNotSupportedException {
    TimePeriodValues values = new TimePeriodValues("S");
    values.add(new org.jfree.data.time.SimpleTimePeriod(
            new java.util.Date(10L), new java.util.Date(20L)),
            java.lang.Integer.valueOf(3));

    TimePeriodValues clone = (TimePeriodValues) values.clone();

    org.junit.Assert.assertNotSame(values, clone);
    org.junit.Assert.assertEquals(values, clone);
    clone.update(0, java.lang.Integer.valueOf(9));

    org.junit.Assert.assertEquals(3, values.getValue(0).intValue());
    org.junit.Assert.assertEquals(9, clone.getValue(0).intValue());
    org.junit.Assert.assertFalse(values.equals(clone));
}
}
