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

@org.junit.Test
public void testPrimitiveArrayIteratorHonorsBoundsAndCanReset() {
    final org.apache.commons.collections4.ResettableIterator<Integer> iterator =
            org.apache.commons.collections4.IteratorUtils.<Integer>arrayIterator(
                    (Object) new int[] { 1, 2, 3, 4 }, 1, 3);

    org.junit.Assert.assertEquals(Integer.valueOf(2), iterator.next());
    org.junit.Assert.assertEquals(Integer.valueOf(3), iterator.next());
    org.junit.Assert.assertFalse(iterator.hasNext());

    iterator.reset();
    org.junit.Assert.assertEquals(Integer.valueOf(2), iterator.next());
}

@org.junit.Test
public void testArrayListIteratorHonorsBoundsWhenTraversingBackwards() {
    final org.apache.commons.collections4.ResettableListIterator<Integer> iterator =
            org.apache.commons.collections4.IteratorUtils.arrayListIterator(
                    new Integer[] { 10, 20, 30, 40 }, 1, 3);

    org.junit.Assert.assertEquals(Integer.valueOf(20), iterator.next());
    org.junit.Assert.assertEquals(Integer.valueOf(30), iterator.next());
    org.junit.Assert.assertTrue(iterator.hasPrevious());
    org.junit.Assert.assertEquals(Integer.valueOf(30), iterator.previous());
    org.junit.Assert.assertEquals(Integer.valueOf(20), iterator.previous());
    org.junit.Assert.assertFalse(iterator.hasPrevious());
}

@org.junit.Test
public void testApplyExecutesClosureForEachElementInOrder() {
    final java.util.List<Integer> visited = new java.util.ArrayList<Integer>();

    org.apache.commons.collections4.IteratorUtils.apply(
            java.util.Arrays.asList(4, 1, 7).iterator(),
            new org.apache.commons.collections4.Closure<Integer>() {
                @Override
                public void execute(final Integer input) {
                    visited.add(input);
                }
            });

    org.junit.Assert.assertEquals(java.util.Arrays.asList(4, 1, 7), visited);
}

@org.junit.Test
public void testFindReturnsFirstMatchingElementAndNullWhenNoMatchExists() {
    final org.apache.commons.collections4.Predicate<Integer> even =
            new org.apache.commons.collections4.Predicate<Integer>() {
                @Override
                public boolean evaluate(final Integer input) {
                    return input.intValue() % 2 == 0;
                }
            };

    org.junit.Assert.assertEquals(Integer.valueOf(4),
            org.apache.commons.collections4.IteratorUtils.find(
                    java.util.Arrays.asList(1, 3, 4, 6).iterator(), even));
    org.junit.Assert.assertNull(
            org.apache.commons.collections4.IteratorUtils.find(
                    java.util.Arrays.asList(1, 3, 5).iterator(), even));
}
}
