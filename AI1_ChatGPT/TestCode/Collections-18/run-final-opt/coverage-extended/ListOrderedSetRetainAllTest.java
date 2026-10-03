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

@Test
public void testAddAllAddsOnlyNewElementsAndReportsWhetherChanged() {
    final ListOrderedSet<String> set = new ListOrderedSet<String>();

    assertTrue(set.addAll(java.util.Arrays.asList("alpha", "beta", "alpha")));
    assertEquals(java.util.Arrays.asList("alpha", "beta"), set.asList());

    assertFalse(set.addAll(java.util.Arrays.asList("beta", "alpha")));
    assertEquals(2, set.size());
    assertEquals(java.util.Arrays.asList("alpha", "beta"), set.asList());
}

@Test
public void testAddAllAtIndexInsertsOnlyNewElementsInEncounterOrder() {
    final ListOrderedSet<String> set = new ListOrderedSet<String>();
    set.add("one");
    set.add("four");

    assertTrue(set.addAll(1, java.util.Arrays.asList("two", "one", "three", "two")));
    assertEquals(java.util.Arrays.asList("one", "two", "three", "four"), set.asList());

    assertFalse(set.addAll(2, java.util.Arrays.asList("one", "three")));
    assertEquals(java.util.Arrays.asList("one", "two", "three", "four"), set.asList());
}

@Test
public void testOrderedIteratorPreviousAndRemoveKeepOrderAndSetInSync() {
    final ListOrderedSet<String> set = new ListOrderedSet<String>();
    set.addAll(java.util.Arrays.asList("alpha", "beta", "gamma"));

    final org.apache.commons.collections.OrderedIterator<String> iterator = set.iterator();
    assertFalse(iterator.hasPrevious());
    assertEquals("alpha", iterator.next());
    assertTrue(iterator.hasPrevious());
    assertEquals("beta", iterator.next());
    assertEquals("beta", iterator.previous());

    iterator.remove();

    assertEquals(java.util.Arrays.asList("alpha", "gamma"), set.asList());
    assertFalse(set.contains("beta"));
    assertEquals(2, set.size());
}

@Test
public void testListOrderedSetFactoryWithSetCopiesInitialOrderAndUsesBackingSet() {
    final java.util.Set<String> backingSet = new java.util.LinkedHashSet<String>();
    backingSet.add("first");
    backingSet.add("second");

    final ListOrderedSet<String> set = ListOrderedSet.listOrderedSet(backingSet);

    assertEquals(java.util.Arrays.asList("first", "second"), set.asList());
    assertTrue(set.add("third"));
    assertTrue(backingSet.contains("third"));
    assertEquals(java.util.Arrays.asList("first", "second", "third"), set.asList());
}
}
