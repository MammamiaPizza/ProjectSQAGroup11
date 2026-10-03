package org.apache.commons.collections;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import junit.framework.TestCase;

public class CollectionUtilsRemoveAllTest extends TestCase {

    public void testRemoveAllExcludesEveryMatchingDuplicate() {
        Collection source = new ArrayList(Arrays.asList(
                "remove", "keep", "remove", "remove"));
        Collection remove = new ArrayList(Arrays.asList("remove"));

        Collection result = CollectionUtils.removeAll(source, remove);

        assertEquals(1, result.size());
        assertTrue(result.contains("keep"));
        assertFalse(result.contains("remove"));
    }

    public void testRemoveAllPreservesNonRemovedValuesAndTheirMultiplicity() {
        Collection source = new ArrayList(Arrays.asList(
                "keep", "remove", "keep", "other", "remove"));
        Collection remove = new ArrayList(Arrays.asList("remove"));

        Collection result = CollectionUtils.removeAll(source, remove);

        assertEquals(3, result.size());
        assertEquals(2, CollectionUtils.cardinality("keep", result));
        assertEquals(1, CollectionUtils.cardinality("other", result));
        assertEquals(0, CollectionUtils.cardinality("remove", result));
    }

    public void testRemoveAllWithNoMatchingValuesReturnsAllSourceValues() {
        Collection source = new ArrayList(Arrays.asList("one", "two", "one"));
        Collection remove = new ArrayList(Arrays.asList("three", "four"));

        Collection result = CollectionUtils.removeAll(source, remove);

        assertEquals(3, result.size());
        assertEquals(2, CollectionUtils.cardinality("one", result));
        assertEquals(1, CollectionUtils.cardinality("two", result));
    }

    public void testRemoveAllFromEmptyCollectionIsEmpty() {
        Collection result = CollectionUtils.removeAll(
                Collections.EMPTY_LIST, Arrays.asList("remove"));

        assertTrue(result.isEmpty());
    }

    public void testRemoveAllWithEmptyRemoveCollectionKeepsSourceValues() {
        Collection source = new ArrayList(Arrays.asList("one", "two", "one"));

        Collection result = CollectionUtils.removeAll(source, Collections.EMPTY_LIST);

        assertEquals(3, result.size());
        assertEquals(2, CollectionUtils.cardinality("one", result));
        assertEquals(1, CollectionUtils.cardinality("two", result));
    }
}