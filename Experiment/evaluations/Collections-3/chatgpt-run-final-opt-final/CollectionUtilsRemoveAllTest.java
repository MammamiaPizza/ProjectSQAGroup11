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

@org.junit.Test
public void testAddAllOverloadsAndAddIgnoreNull() {
    java.util.Collection values = new java.util.ArrayList();

    org.apache.commons.collections.CollectionUtils.addAll(
            values, new Object[] { "array-one", "array-two" });
    org.apache.commons.collections.CollectionUtils.addAll(
            values, java.util.Arrays.asList(new Object[] { "iterator-one", "iterator-two" }).iterator());

    java.util.Vector enumerationValues = new java.util.Vector();
    enumerationValues.add("enumeration-one");
    enumerationValues.add("enumeration-two");
    org.apache.commons.collections.CollectionUtils.addAll(values, enumerationValues.elements());

    org.junit.Assert.assertFalse(
            org.apache.commons.collections.CollectionUtils.addIgnoreNull(values, null));
    org.junit.Assert.assertTrue(
            org.apache.commons.collections.CollectionUtils.addIgnoreNull(values, "non-null"));

    org.junit.Assert.assertEquals(
            java.util.Arrays.asList(new Object[] {
                    "array-one", "array-two", "iterator-one", "iterator-two",
                    "enumeration-one", "enumeration-two", "non-null"
            }),
            values);
}

@org.junit.Test
public void testCardinalityHandlesSetAndNullElements() {
    java.util.Set set = new java.util.HashSet();
    set.add("present");

    org.junit.Assert.assertEquals(
            1, org.apache.commons.collections.CollectionUtils.cardinality("present", set));
    org.junit.Assert.assertEquals(
            0, org.apache.commons.collections.CollectionUtils.cardinality("missing", set));

    java.util.Collection values = new java.util.ArrayList();
    values.add(null);
    values.add("value");
    values.add(null);

    org.junit.Assert.assertEquals(
            2, org.apache.commons.collections.CollectionUtils.cardinality(null, values));
    org.junit.Assert.assertEquals(
            1, org.apache.commons.collections.CollectionUtils.cardinality("value", values));
}

@org.junit.Test
public void testCollectPopulatesProvidedCollectionAndLeavesItUnchangedForNullInput() {
    org.apache.commons.collections.Transformer suffixTransformer =
            new org.apache.commons.collections.Transformer() {
                public Object transform(Object input) {
                    return input + "-transformed";
                }
            };

    java.util.Collection output = new java.util.ArrayList();
    output.add("existing");
    java.util.Collection returned = org.apache.commons.collections.CollectionUtils.collect(
            java.util.Arrays.asList(new Object[] { "first", "second" }),
            suffixTransformer,
            output);

    org.junit.Assert.assertSame(output, returned);
    org.junit.Assert.assertEquals(
            java.util.Arrays.asList(new Object[] {
                    "existing", "first-transformed", "second-transformed"
            }),
            output);

    java.util.Collection unchanged = new java.util.ArrayList();
    unchanged.add("unchanged");
    org.junit.Assert.assertSame(
            unchanged,
            org.apache.commons.collections.CollectionUtils.collect(
                    (java.util.Collection) null, suffixTransformer, unchanged));
    org.junit.Assert.assertEquals(
            java.util.Arrays.asList(new Object[] { "unchanged" }), unchanged);
}
}
