package org.apache.commons.collections.list;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SetUniqueListAddAllTest {

    @Test
    public void testAddAllAtStartInsertsFirstNewElementAtRequestedIndex() {
        List list = SetUniqueList.decorate(new ArrayList(Arrays.asList(new Integer(1))));

        assertTrue(list.addAll(0, Arrays.asList(new Integer(1), new Integer(2), new Integer(3))));

        assertEquals(Arrays.asList(new Integer(2), new Integer(3), new Integer(1)), list);
        assertEquals(new Integer(2), list.get(0));
    }

    @Test
    public void testAddAllInMiddleSkipsDuplicatesWithoutMovingInsertionPoint() {
        List list = SetUniqueList.decorate(new ArrayList(Arrays.asList(
                "a", "b", "c")));

        assertTrue(list.addAll(1, Arrays.asList("b", "x", "x", "c", "y", "a")));

        assertEquals(Arrays.asList("a", "x", "y", "b", "c"), list);
    }

    @Test
    public void testAddAllAtEndFiltersDuplicatesAndPreservesNewElementOrder() {
        List list = SetUniqueList.decorate(new ArrayList(Arrays.asList(
                "one", "two")));

        assertTrue(list.addAll(list.size(), Arrays.asList(
                "two", "three", "three", "four", "one")));

        assertEquals(Arrays.asList("one", "two", "three", "four"), list);
    }

    @Test
    public void testAddAllReturnsFalseForOnlyExistingOrEmptyCollection() {
        List list = SetUniqueList.decorate(new ArrayList(Arrays.asList(
                "one", "two")));

        assertFalse(list.addAll(1, Arrays.asList("two", "one", "two")));
        assertEquals(Arrays.asList("one", "two"), list);

        assertFalse(list.addAll(Collections.EMPTY_LIST));
        assertEquals(Arrays.asList("one", "two"), list);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testAddAllAtInvalidIndexWithNewElementThrowsException() {
        List list = SetUniqueList.decorate(new ArrayList(Arrays.asList("one")));

        list.addAll(2, Arrays.asList("two"));
    }
}
