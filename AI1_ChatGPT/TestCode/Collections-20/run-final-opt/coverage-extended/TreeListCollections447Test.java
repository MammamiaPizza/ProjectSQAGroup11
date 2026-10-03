package org.apache.commons.collections.list;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.junit.Test;

public class TreeListCollections447Test {

    @Test
    public void iteratorPreviousAfterRemovingMiddleElementReturnsPredecessor() {
        TreeList<String> list = new TreeList<String>();
        list.add("A");
        list.add("B");
        list.add("C");

        ListIterator<String> iterator = list.listIterator(1);
        assertEquals("B", iterator.next());
        iterator.remove();

        assertEquals("A", iterator.previous());
        iterator.set("X");

        assertEquals(2, list.size());
        assertArrayEquals(new Object[] { "X", "C" }, list.toArray());
        assertEquals("X", list.get(0));
        assertEquals("C", list.get(1));
    }

    @Test
    public void iteratorEditsAroundMiddleMaintainIndexedOrder() {
        TreeList<Integer> list = new TreeList<Integer>();
        for (int i = 0; i < 9; i++) {
            list.add(Integer.valueOf(i));
        }

        ListIterator<Integer> iterator = list.listIterator(4);

        assertEquals(Integer.valueOf(3), iterator.previous());
        iterator.remove();
        assertEquals(3, iterator.nextIndex());

        assertEquals(Integer.valueOf(4), iterator.next());
        iterator.set(Integer.valueOf(40));
        iterator.add(Integer.valueOf(41));
        assertEquals(5, iterator.nextIndex());

        assertEquals(Integer.valueOf(41), iterator.previous());
        iterator.remove();

        assertEquals(Integer.valueOf(5), iterator.next());
        iterator.remove();

        assertEquals(Integer.valueOf(40), iterator.previous());
        iterator.set(Integer.valueOf(400));

        assertArrayEquals(new Object[] {
            Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(2),
            Integer.valueOf(400), Integer.valueOf(6), Integer.valueOf(7),
            Integer.valueOf(8)
        }, list.toArray());
        assertEquals(Integer.valueOf(400), list.get(3));
        assertEquals(7, list.size());
    }

    @Test
    public void iteratorCanRemoveEveryElementAfterNext() {
        TreeList<Integer> list = new TreeList<Integer>();
        for (int i = 0; i < 16; i++) {
            list.add(Integer.valueOf(i));
        }

        ListIterator<Integer> iterator = list.listIterator();
        for (int i = 0; i < 16; i++) {
            assertTrue(iterator.hasNext());
            assertEquals(Integer.valueOf(i), iterator.next());
            iterator.remove();
            assertEquals(0, iterator.nextIndex());
            assertEquals(-1, iterator.previousIndex());
        }

        assertFalse(iterator.hasNext());
        assertFalse(iterator.hasPrevious());
        assertEquals(0, list.size());
        assertArrayEquals(new Object[0], list.toArray());
    }

    @Test
    public void iteratorAtEndSupportsBackwardTraversalAndInsertion() {
        TreeList<String> list = new TreeList<String>();
        list.add("A");
        list.add("C");

        ListIterator<String> iterator = list.listIterator(list.size());
        assertFalse(iterator.hasNext());
        assertTrue(iterator.hasPrevious());
        assertEquals(2, iterator.nextIndex());
        assertEquals(1, iterator.previousIndex());

        assertEquals("C", iterator.previous());
        iterator.add("B");

        assertEquals(2, iterator.nextIndex());
        assertEquals("C", iterator.next());
        assertFalse(iterator.hasNext());
        assertArrayEquals(new Object[] { "A", "B", "C" }, list.toArray());
    }

    @Test
    public void iteratorSetRemoveAndAddEnforceTheirLegalStates() {
        TreeList<String> list = new TreeList<String>();
        list.add("A");
        ListIterator<String> iterator = list.listIterator();

        try {
            iterator.remove();
        } catch (IllegalStateException expected) {
            // expected
        }

        try {
            iterator.set("X");
        } catch (IllegalStateException expected) {
            // expected
        }

        assertEquals("A", iterator.next());
        iterator.set("X");
        assertEquals("X", list.get(0));

        iterator.remove();
        assertEquals(0, list.size());

        try {
            iterator.set("Y");
        } catch (IllegalStateException expected) {
            // expected
        }

        iterator.add("Z");
        assertEquals(1, iterator.nextIndex());
        assertEquals("Z", list.get(0));

        try {
            iterator.remove();
        } catch (IllegalStateException expected) {
            // expected
        }
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void listIteratorRejectsNegativeStartIndex() {
        new TreeList<String>().listIterator(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void listIteratorRejectsStartIndexPastEnd() {
        TreeList<String> list = new TreeList<String>();
        list.add("A");
        list.listIterator(2);
    }

    @Test(expected = NoSuchElementException.class)
    public void previousAtStartThrowsNoSuchElementException() {
        TreeList<String> list = new TreeList<String>();
        list.add("A");
        list.listIterator().previous();
    }

@Test
public void collectionConstructorPreservesOrderAndIteratorStartsAtFirstElement() {
    final TreeList<String> list = new TreeList<String>(java.util.Arrays.asList("A", "B", "C"));

    assertEquals(3, list.size());
    assertArrayEquals(new Object[] { "A", "B", "C" }, list.toArray());

    final java.util.Iterator<String> iterator = list.iterator();
    assertEquals("A", iterator.next());
}

@Test
public void setReturnsPreviousValueAndReplacesIndexedElement() {
    final TreeList<String> list = new TreeList<String>();
    list.add("A");
    list.add("B");
    list.add("C");

    assertEquals("B", list.set(1, "X"));
    assertArrayEquals(new Object[] { "A", "X", "C" }, list.toArray());
}

@Test
public void clearEmptiesListAndAllowsReuse() {
    final TreeList<String> list = new TreeList<String>();
    list.add("A");
    list.add("B");

    list.clear();

    assertEquals(0, list.size());
    assertEquals(-1, list.indexOf("A"));
    assertEquals(Boolean.FALSE, Boolean.valueOf(list.contains("A")));

    list.add("C");
    assertEquals("C", list.get(0));
}
}
