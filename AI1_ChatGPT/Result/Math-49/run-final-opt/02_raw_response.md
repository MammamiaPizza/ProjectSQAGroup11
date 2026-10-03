package org.apache.commons.math.linear;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OpenMapRealVectorConcurrentModificationTest {

    @Test
    public void sparseIteratorContinuesAfterCurrentEntriesAreSetToZero() {
        OpenMapRealVector vector = new OpenMapRealVector(
                new double[] { 0.0, 1.0, 0.0, -2.0, 0.0, 3.0, 0.0 });

        Set<Integer> expectedIndices = new HashSet<Integer>();
        expectedIndices.add(Integer.valueOf(1));
        expectedIndices.add(Integer.valueOf(3));
        expectedIndices.add(Integer.valueOf(5));

        Iterator<RealVector.Entry> iterator = vector.sparseIterator();
        int visited = 0;
        while (iterator.hasNext()) {
            RealVector.Entry entry = iterator.next();
            assertTrue(expectedIndices.remove(Integer.valueOf(entry.getIndex())));
            entry.setValue(0.0);
            visited++;
        }

        assertEquals(3, visited);
        assertTrue(expectedIndices.isEmpty());
        for (int i = 0; i < vector.getDimension(); i++) {
            assertEquals(0.0, vector.getEntry(i), 0.0);
        }
        assertFalse(iterator.hasNext());
    }

    @Test
    public void sparseIteratorContinuesAfterCurrentEntriesAreUpdatedToNonDefaultValues() {
        OpenMapRealVector vector = new OpenMapRealVector(
                new double[] { 0.0, 1.5, 0.0, -2.0, 0.0, 4.0 });

        Set<Integer> expectedIndices = new HashSet<Integer>();
        expectedIndices.add(Integer.valueOf(1));
        expectedIndices.add(Integer.valueOf(3));
        expectedIndices.add(Integer.valueOf(5));

        Iterator<RealVector.Entry> iterator = vector.sparseIterator();
        int visited = 0;
        while (iterator.hasNext()) {
            RealVector.Entry entry = iterator.next();
            assertTrue(expectedIndices.remove(Integer.valueOf(entry.getIndex())));
            entry.setValue(entry.getValue() * 2.0);
            visited++;
        }

        assertEquals(3, visited);
        assertTrue(expectedIndices.isEmpty());
        assertEquals(3.0, vector.getEntry(1), 0.0);
        assertEquals(-4.0, vector.getEntry(3), 0.0);
        assertEquals(8.0, vector.getEntry(5), 0.0);
    }

    @Test(expected = NoSuchElementException.class)
    public void sparseIteratorNextOnEmptyVectorThrowsNoSuchElementException() {
        OpenMapRealVector vector = new OpenMapRealVector(4);
        Iterator<RealVector.Entry> iterator = vector.sparseIterator();

        assertFalse(iterator.hasNext());
        iterator.next();
    }
}