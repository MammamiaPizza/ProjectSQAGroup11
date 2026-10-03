package org.apache.commons.collections.list;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.junit.Test;

public class SetUniqueListCollections444Test {

    private static SetUniqueList<String> uniqueList(final String... values) {
        return SetUniqueList.setUniqueList(
                new ArrayList<String>(Arrays.asList(values)));
    }

    private static void assertSynchronized(final SetUniqueList<String> list,
            final String... expected) {
        assertEquals(Arrays.asList(expected), list);
        assertEquals(new HashSet<String>(Arrays.asList(expected)), list.asSet());
        assertEquals(list.size(), list.asSet().size());
    }

    @Test
    public void testFactoryRemovesDuplicatesAndBuildsMatchingSet() {
        final SetUniqueList<String> list = uniqueList("one", "two", "one",
                "three", "two");

        assertSynchronized(list, "one", "two", "three");
        assertTrue(list.containsAll(Arrays.asList("one", "two", "three")));
        assertFalse(list.contains("missing"));
    }

    @Test
    public void testSetToSameElementDoesNotRemoveIt() {
        final SetUniqueList<String> list = uniqueList("one", "two", "three");

        assertEquals("two", list.set(1, "two"));

        assertSynchronized(list, "one", "two", "three");
        assertEquals("two", list.get(1));
    }

    @Test
    public void testSetToNewElementReplacesSetMembership() {
        final SetUniqueList<String> list = uniqueList("one", "two", "three");

        assertEquals("two", list.set(1, "four"));

        assertSynchronized(list, "one", "four", "three");
        assertFalse(list.contains("two"));
        assertTrue(list.contains("four"));
    }

    @Test
    public void testAddAndAddAllIgnoreDuplicateElements() {
        final SetUniqueList<String> list = uniqueList("one");

        assertTrue(list.add("two"));
        assertFalse(list.add("one"));
        assertTrue(list.addAll(Arrays.asList("two", "three", "three", "four")));

        assertSynchronized(list, "one", "two", "three", "four");
    }

    @Test
    public void testIndexedAddAllPreservesOrderOfNewElementsOnly() {
        final SetUniqueList<String> list = uniqueList("one", "four");

        assertTrue(list.addAll(1, Arrays.asList("two", "one", "three", "two")));

        assertSynchronized(list, "one", "two", "three", "four");
    }

    @Test
    public void testRemoveAndRetainKeepSetInSync() {
        final SetUniqueList<String> list = uniqueList("one", "two", "three",
                "four");

        assertTrue(list.remove("two"));
        assertEquals("four", list.remove(2));
        assertFalse(list.remove("missing"));
        assertFalse(list.retainAll(Arrays.asList("one", "three")));

        assertSynchronized(list, "one", "three");
        assertFalse(list.retainAll(Arrays.asList("one", "three")));
    }

    @Test
    public void testIteratorRemovalKeepsSetInSync() {
        final SetUniqueList<String> list = uniqueList("one", "two", "three");
        final Iterator<String> iterator = list.iterator();

        assertEquals("one", iterator.next());
        iterator.remove();
        assertEquals("two", iterator.next());
        iterator.remove();

        assertSynchronized(list, "three");
    }

    @Test
    public void testListIteratorAddAndRemoveKeepSetInSync() {
        final SetUniqueList<String> list = uniqueList("one", "three");
        final ListIterator<String> iterator = list.listIterator(1);

        iterator.add("two");
        iterator.add("one");
        assertEquals("three", iterator.next());
        iterator.remove();

        assertSynchronized(list, "one", "two");
    }

    @Test
    public void testClearEmptiesBothListAndSet() {
        final SetUniqueList<String> list = uniqueList("one", "two");

        list.clear();

        assertSynchronized(list);
        assertTrue(list.isEmpty());
    }

    @Test
    public void testAsSetIsUnmodifiable() {
        final SetUniqueList<String> list = uniqueList("one");

        try {
            list.asSet().add("two");
            fail("asSet must be unmodifiable");
        } catch (final UnsupportedOperationException expected) {
            assertSynchronized(list, "one");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFactoryRejectsNullList() {
        SetUniqueList.setUniqueList(null);
    }
}