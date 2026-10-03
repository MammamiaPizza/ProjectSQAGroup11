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
}
