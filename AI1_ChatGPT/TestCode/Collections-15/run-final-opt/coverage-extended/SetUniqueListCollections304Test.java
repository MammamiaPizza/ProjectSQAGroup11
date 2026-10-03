package org.apache.commons.collections.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SetUniqueListCollections304Test {

    @Test
    public void testIndexedAddAllRejectsExistingAndRepeatedElements() {
        SetUniqueList list = SetUniqueList.decorate(
                new ArrayList(Arrays.asList(new String[] { "one", "two" })));

        boolean changed = list.addAll(1,
                Arrays.asList(new String[] { "two", "three", "three" }));

        assertTrue(changed);
        assertEquals(3, list.size());
        assertEquals(Arrays.asList(new String[] { "one", "three", "two" }), list);
        assertEquals(new HashSet(Arrays.asList(new String[] { "one", "two", "three" })),
                list.asSet());
    }

    @Test
    public void testIndexedAddAllPreservesOrderOfAcceptedElements() {
        SetUniqueList list = SetUniqueList.decorate(
                new ArrayList(Arrays.asList(new String[] { "a", "d" })));

        boolean changed = list.addAll(1,
                Arrays.asList(new String[] { "b", "c" }));

        assertTrue(changed);
        assertEquals(Arrays.asList(new String[] { "a", "b", "c", "d" }), list);
    }

    @Test
    public void testIndexedAddAllOfOnlyExistingElementsDoesNotChangeList() {
        SetUniqueList list = SetUniqueList.decorate(
                new ArrayList(Arrays.asList(new String[] { "a", "b" })));

        boolean changed = list.addAll(list.size(),
                Arrays.asList(new String[] { "a", "b", "a" }));

        assertFalse(changed);
        assertEquals(Arrays.asList(new String[] { "a", "b" }), list);
        assertEquals(2, list.asSet().size());
    }

    @Test
    public void testSetWithExistingValueRemovesPreviousOccurrenceAndSynchronizesSet() {
        SetUniqueList list = SetUniqueList.decorate(
                new ArrayList(Arrays.asList(new String[] { "a", "b", "c" })));

        Object previous = list.set(0, "c");

        assertEquals("a", previous);
        assertEquals(Arrays.asList(new String[] { "c", "b" }), list);
        assertEquals(new HashSet(Arrays.asList(new String[] { "b", "c" })), list.asSet());
        assertFalse(list.contains("a"));
    }

    @Test
    public void testRemoveOperationsAndClearSynchronizeSet() {
        SetUniqueList list = SetUniqueList.decorate(
                new ArrayList(Arrays.asList(new String[] { "a", "b", "c" })));

        assertEquals("b", list.remove(1));
        assertTrue(list.remove("a"));
        assertFalse(list.remove("missing"));
        assertEquals(Arrays.asList(new String[] { "c" }), list);
        assertEquals(new HashSet(Arrays.asList(new String[] { "c" })), list.asSet());

        list.clear();

        assertTrue(list.isEmpty());
        assertTrue(list.asSet().isEmpty());
    }

    @Test
    public void testDecorateRemovesDuplicatesKeepingFirstOccurrence() {
        ArrayList backing = new ArrayList(
                Arrays.asList(new String[] { "a", "b", "a", "c", "b" }));

        SetUniqueList list = SetUniqueList.decorate(backing);

        assertEquals(Arrays.asList(new String[] { "a", "b", "c" }), list);
        assertEquals(Arrays.asList(new String[] { "a", "b", "c" }), backing);
        assertEquals(new HashSet(Arrays.asList(new String[] { "a", "b", "c" })), list.asSet());
    }

    @Test
    public void testDecorateRejectsNullList() {
        try {
            SetUniqueList.decorate(null);
            fail("Expected IllegalArgumentException for a null list");
        } catch (IllegalArgumentException expected) {
            assertNotNull(expected);
        }
    }
}
