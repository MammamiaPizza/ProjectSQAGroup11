package org.apache.commons.math.util;

import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.exception.OutOfRangeException;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class MultidimensionalCounterGeneratedTest {

    @Test
    public void testIteratorAndConversionsAreConsistentForAllThreeDimensionalSlots() {
        MultidimensionalCounter counter = new MultidimensionalCounter(2, 3, 4);
        MultidimensionalCounter.Iterator iterator = counter.iterator();

        for (int index = 0; index < counter.getSize(); index++) {
            int[] expected = new int[] {
                index / 12,
                (index / 4) % 3,
                index % 4
            };

            assertTrue(iterator.hasNext());
            assertEquals(index, iterator.next().intValue());
            assertEquals(index, iterator.getCount());
            assertArrayEquals(expected, iterator.getCounts());
            assertEquals(expected[0], iterator.getCount(0));
            assertEquals(expected[1], iterator.getCount(1));
            assertEquals(expected[2], iterator.getCount(2));

            assertArrayEquals(expected, counter.getCounts(index));
            assertEquals(index, counter.getCount(expected));
        }

        assertFalse(iterator.hasNext());
    }

    @Test
    public void testFirstAndLastSlotsAndTotalSize() {
        MultidimensionalCounter counter = new MultidimensionalCounter(2, 3, 4);

        assertEquals(24, counter.getSize());
        assertArrayEquals(new int[] {0, 0, 0}, counter.getCounts(0));
        assertArrayEquals(new int[] {1, 2, 3}, counter.getCounts(counter.getSize() - 1));
        assertEquals(0, counter.getCount(0, 0, 0));
        assertEquals(23, counter.getCount(1, 2, 3));
    }

    @Test
    public void testIteratorRollsOverInnerDimensions() {
        MultidimensionalCounter counter = new MultidimensionalCounter(2, 3, 4);
        MultidimensionalCounter.Iterator iterator = counter.iterator();

        for (int i = 0; i < 4; i++) {
            iterator.next();
        }
        assertArrayEquals(new int[] {0, 0, 3}, iterator.getCounts());

        iterator.next();
        assertArrayEquals(new int[] {0, 1, 0}, iterator.getCounts());

        for (int i = 0; i < 8; i++) {
            iterator.next();
        }
        assertArrayEquals(new int[] {1, 0, 0}, iterator.getCounts());
    }

    @Test(expected = OutOfRangeException.class)
    public void testGetCountsRejectsNegativeIndex() {
        new MultidimensionalCounter(2, 3).getCounts(-1);
    }

    @Test(expected = OutOfRangeException.class)
    public void testGetCountsRejectsIndexEqualToSize() {
        MultidimensionalCounter counter = new MultidimensionalCounter(2, 3);
        counter.getCounts(counter.getSize());
    }

    @Test(expected = DimensionMismatchException.class)
    public void testGetCountRejectsWrongNumberOfDimensions() {
        new MultidimensionalCounter(2, 3, 4).getCount(1, 2);
    }

    @Test(expected = OutOfRangeException.class)
    public void testGetCountRejectsCoordinateOutsideDimensionRange() {
        new MultidimensionalCounter(2, 3, 4).getCount(2, 0, 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testIteratorGetCountRejectsInvalidDimension() {
        MultidimensionalCounter.Iterator iterator =
                new MultidimensionalCounter(2, 3).iterator();
        iterator.getCount(2);
    }

    @Test
    public void testIteratorHasNextIsFalseAfterLastElement() {
        MultidimensionalCounter counter = new MultidimensionalCounter(1, 2);
        MultidimensionalCounter.Iterator iterator = counter.iterator();

        assertTrue(iterator.hasNext());
        assertEquals(0, iterator.next().intValue());
        assertArrayEquals(new int[] {0, 0}, iterator.getCounts());

        assertTrue(iterator.hasNext());
        assertEquals(1, iterator.next().intValue());
        assertArrayEquals(new int[] {0, 1}, iterator.getCounts());

        assertFalse(iterator.hasNext());
    }

    @Test
    public void testConstructorRejectsZeroAndNegativeDimensionSizes() {
        try {
            new MultidimensionalCounter(2, 0);
            fail("A zero dimension size must be rejected");
        } catch (NotStrictlyPositiveException expected) {
            // expected
        }

        try {
            new MultidimensionalCounter(-1, 2);
            fail("A negative dimension size must be rejected");
        } catch (NotStrictlyPositiveException expected) {
            // expected
        }
    }
}
