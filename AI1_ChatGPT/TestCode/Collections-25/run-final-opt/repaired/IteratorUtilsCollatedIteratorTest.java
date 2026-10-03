package org.apache.commons.collections4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

public class IteratorUtilsCollatedIteratorTest {

    @Test
    public void testCollatedIteratorWithoutComparatorUsesNaturalOrder() {
        final Iterator<Integer> merged = IteratorUtils.collatedIterator(
                null,
                Arrays.asList(1, 4, 7).iterator(),
                Arrays.asList(2, 3, 8).iterator());

        assertEquals(Arrays.asList(1, 2, 3, 4, 7, 8), drain(merged));
    }

    @Test
    public void testCollatedIteratorWithoutComparatorHandlesEmptyInput() {
        final Iterator<Integer> merged = IteratorUtils.collatedIterator(
                null,
                Arrays.<Integer>asList().iterator(),
                Arrays.asList(2, 5, 9).iterator());

        assertEquals(Arrays.asList(2, 5, 9), drain(merged));
    }

    @Test
    public void testCollatedIteratorWithoutComparatorHandlesAllEmptyInputs() {
        final Iterator<Integer> merged = IteratorUtils.collatedIterator(
                null,
                Arrays.<Integer>asList().iterator(),
                Arrays.<Integer>asList().iterator());

        assertFalse(merged.hasNext());
    }

    @Test
    public void testCollatedIteratorWithComparatorUsesProvidedOrdering() {
        final Comparator<Integer> descending = new Comparator<Integer>() {
            @Override
            public int compare(final Integer left, final Integer right) {
                return right.compareTo(left);
            }
        };

        final Iterator<Integer> merged = IteratorUtils.collatedIterator(
                descending,
                Arrays.asList(9, 5, 1).iterator(),
                Arrays.asList(8, 6, 2).iterator());

        assertEquals(Arrays.asList(9, 8, 6, 5, 2, 1), drain(merged));
    }

    private static <E> List<E> drain(final Iterator<E> iterator) {
        final List<E> values = new ArrayList<E>();
        while (iterator.hasNext()) {
            values.add(iterator.next());
        }
        return values;
    }
}
