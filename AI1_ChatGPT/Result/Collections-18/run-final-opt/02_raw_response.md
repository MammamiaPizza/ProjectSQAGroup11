package org.apache.commons.collections.set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ListOrderedSetRetainAllTest {

    @Test
    public void testRetainAllKeepsOriginalInsertionOrderForSubset() {
        ListOrderedSet<String> set = new ListOrderedSet<String>();
        set.addAll(Arrays.asList("one", "two", "three", "four"));

        boolean changed = set.retainAll(Arrays.asList("four", "one", "three"));

        assertTrue(changed);
        assertEquals(Arrays.asList("one", "three", "four"), set.asList());
        assertEquals(3, set.size());
        assertTrue(set.contains("one"));
        assertFalse(set.contains("two"));
        assertTrue(set.contains("three"));
        assertTrue(set.contains("four"));
    }

    @Test
    public void testRetainAllWithDuplicatesInArgumentDoesNotCreateDuplicates() {
        ListOrderedSet<String> set = new ListOrderedSet<String>();
        set.addAll(Arrays.asList("alpha", "beta", "gamma"));

        boolean changed = set.retainAll(Arrays.asList("gamma", "alpha", "gamma", "alpha"));

        assertTrue(changed);
        assertEquals(Arrays.asList("alpha", "gamma"), set.asList());
        assertEquals(2, set.size());
        assertEquals(0, set.indexOf("alpha"));
        assertEquals(1, set.indexOf("gamma"));
    }

    @Test
    public void testRetainAllWithNoOverlapClearsBothSetAndOrderList() {
        ListOrderedSet<String> set = new ListOrderedSet<String>();
        set.addAll(Arrays.asList("one", "two"));

        boolean changed = set.retainAll(Arrays.asList("three", "four"));

        assertTrue(changed);
        assertTrue(set.isEmpty());
        assertEquals(0, set.asList().size());
        assertFalse(set.contains("one"));
        assertFalse(set.contains("two"));

        assertTrue(set.add("replacement"));
        assertEquals(Arrays.asList("replacement"), set.asList());
        assertTrue(set.contains("replacement"));
    }

    @Test
    public void testRetainAllContainingEveryElementLeavesSetUnchanged() {
        ListOrderedSet<String> set = new ListOrderedSet<String>();
        set.addAll(Arrays.asList("first", "second", "third"));

        boolean changed = set.retainAll(Arrays.asList("third", "first", "second", "extra"));

        assertFalse(changed);
        assertEquals(Arrays.asList("first", "second", "third"), set.asList());
        assertEquals(3, set.size());
    }

    @Test
    public void testRetainAllOnEmptySetReportsNoChange() {
        ListOrderedSet<String> set = new ListOrderedSet<String>();

        boolean changed = set.retainAll(Arrays.asList("anything"));

        assertFalse(changed);
        assertTrue(set.isEmpty());
        assertEquals(0, set.asList().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testListOrderedSetFactoryRejectsNonEmptyBackingCollections() {
        Set<String> backingSet = new HashSet<String>();
        List<String> backingList = new ArrayList<String>();
        backingSet.add("value");

        ListOrderedSet.listOrderedSet(backingSet, backingList);
    }
}