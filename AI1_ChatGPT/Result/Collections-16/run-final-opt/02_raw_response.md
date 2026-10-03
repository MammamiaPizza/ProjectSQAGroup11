package org.apache.commons.collections.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SetUniqueListCollections307Test {

    private SetUniqueList newList(Object... values) {
        SetUniqueList list = SetUniqueList.decorate(new ArrayList());
        list.addAll(Arrays.asList(values));
        return list;
    }

    @Test
    public void indexedAddAllInsertsNewElementsInCollectionOrder() {
        SetUniqueList list = newList("a", "d");

        assertTrue(list.addAll(1, Arrays.asList("b", "c")));

        assertEquals(Arrays.asList("a", "b", "c", "d"), list);
        assertEquals(4, list.size());
        assertEquals(new HashSet(Arrays.asList("a", "b", "c", "d")), list.asSet());
    }

    @Test
    public void indexedAddAllDoesNotAdvanceInsertionPointForRejectedDuplicates() {
        SetUniqueList list = newList("a", "b", "c");

        assertTrue(list.addAll(1, Arrays.asList("a", "d", "d", "e", "b")));

        assertEquals(Arrays.asList("a", "d", "e", "b", "c"), list);
        assertEquals(new HashSet(Arrays.asList("a", "b", "c", "d", "e")), list.asSet());
    }

    @Test
    public void indexedAddAllSupportsBeginningAndEndBoundaries() {
        SetUniqueList list = newList("b", "c");

        assertTrue(list.addAll(0, Arrays.asList("a")));
        assertTrue(list.addAll(list.size(), Arrays.asList("d")));

        assertEquals(Arrays.asList("a", "b", "c", "d"), list);
        assertEquals(new HashSet(Arrays.asList("a", "b", "c", "d")), list.asSet());
    }

    @Test
    public void indexedAddAllWithOnlyExistingOrEmptyElementsDoesNotChangeList() {
        SetUniqueList list = newList("a", "b");

        assertFalse(list.addAll(1, Arrays.asList("a", "b", "a")));
        assertFalse(list.addAll(1, Arrays.asList()));

        assertEquals(Arrays.asList("a", "b"), list);
        assertEquals(new HashSet(Arrays.asList("a", "b")), list.asSet());
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void indexedAddAllValidatesIndexEvenWhenNothingCanBeInserted() {
        SetUniqueList list = newList("a", "b");

        list.addAll(3, Arrays.asList("a", "b"));
    }

    @Test
    public void decorateRemovesExistingDuplicatesKeepingFirstOccurrence() {
        List backing = new ArrayList(Arrays.asList("a", "b", "a", "c", "b"));

        SetUniqueList list = SetUniqueList.decorate(backing);

        assertEquals(Arrays.asList("a", "b", "c"), list);
        assertEquals(Arrays.asList("a", "b", "c"), backing);
        assertEquals(new HashSet(Arrays.asList("a", "b", "c")), list.asSet());
    }

    @Test
    public void setToAnExistingElementRemovesThePreviousOccurrence() {
        SetUniqueList list = newList("a", "b", "c");

        assertEquals("c", list.set(2, "a"));

        assertEquals(Arrays.asList("b", "a"), list);
        assertEquals(new HashSet(Arrays.asList("a", "b")), list.asSet());
        assertTrue(list.contains("a"));
        assertFalse(list.contains("c"));
    }

    @Test
    public void iteratorRemovalKeepsSetViewInSync() {
        SetUniqueList list = newList("a", "b", "c");
        Iterator iterator = list.iterator();

        assertEquals("a", iterator.next());
        iterator.remove();

        assertEquals(Arrays.asList("b", "c"), list);
        assertFalse(list.contains("a"));
        assertEquals(new HashSet(Arrays.asList("b", "c")), list.asSet());
    }

    @Test
    public void listIteratorAddsOnlyUniqueValuesAndUpdatesSet() {
        SetUniqueList list = newList("a", "c");
        ListIterator iterator = list.listIterator(1);

        iterator.add("b");
        iterator.add("a");

        assertEquals(Arrays.asList("a", "b", "c"), list);
        assertEquals(new HashSet(Arrays.asList("a", "b", "c")), list.asSet());
    }

    @Test
    public void subListRemovalUpdatesParentAndSetView() {
        SetUniqueList list = newList("a", "b", "c", "d");
        List subList = list.subList(1, 3);

        assertEquals("b", subList.remove(0));

        assertEquals(Arrays.asList("a", "c", "d"), list);
        assertFalse(list.contains("b"));
        assertEquals(new HashSet(Arrays.asList("a", "c", "d")), list.asSet());
    }
}