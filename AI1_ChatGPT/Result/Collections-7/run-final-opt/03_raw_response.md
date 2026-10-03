package org.apache.commons.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ExtendedPropertiesKeyIteratorTest {

    @Test
    public void getKeysReturnsAllAddedKeysInListedOrder() {
        ExtendedProperties properties = new ExtendedProperties();
        properties.addProperty("first", "1");
        properties.addProperty("second", "2");
        properties.addProperty("third", "3");

        assertIteratorContents(properties.getKeys(),
                new String[] { "first", "second", "third" });
    }

    @Test
    public void getKeysSkipsPropertyClearedBeforeIteration() {
        ExtendedProperties properties = new ExtendedProperties();
        properties.addProperty("a", "1");
        properties.addProperty("b", "2");
        properties.addProperty("c", "3");
        properties.clearProperty("b");

        assertIteratorContents(properties.getKeys(), new String[] { "a", "c" });
    }

    @Test
    public void setPropertyMovesReplacedKeyAfterUnchangedListedKeys() {
        ExtendedProperties properties = new ExtendedProperties();
        properties.addProperty("a", "1");
        properties.addProperty("b", "2");
        properties.addProperty("c", "3");

        properties.setProperty("b", "replacement");

        Iterator keys = properties.getKeys();
        assertTrue(keys.hasNext());
        assertEquals("a", keys.next());
        assertTrue(keys.hasNext());
        assertEquals("c", keys.next());
        assertTrue(keys.hasNext());
        assertEquals("b", keys.next());
        assertFalse(keys.hasNext());
    }

    @Test
    public void clearAndReaddDoesNotExposeStaleKeys() {
        ExtendedProperties properties = new ExtendedProperties();
        properties.addProperty("old.one", "1");
        properties.addProperty("old.two", "2");

        properties.clearProperty("old.one");
        properties.clearProperty("old.two");
        properties.addProperty("new", "3");

        assertIteratorContents(properties.getKeys(), new String[] { "new" });
    }

    @Test
    public void getKeysWithPrefixReturnsOnlyMatchingSuffixesInListedOrder() {
        ExtendedProperties properties = new ExtendedProperties();
        properties.addProperty("database.driver", "driver");
        properties.addProperty("other.value", "ignored");
        properties.addProperty("database.url", "url");

        assertIteratorContents(properties.getKeys("database"),
                new String[] { "database.driver", "database.url" });
    }

    private void assertIteratorContents(Iterator iterator, String[] expected) {
        for (int i = 0; i < expected.length; i++) {
            assertTrue(iterator.hasNext());
            assertTrue(iterator.hasNext());
            assertEquals(expected[i], iterator.next());
        }

        assertFalse(iterator.hasNext());

        try {
            iterator.next();
            fail("next() must throw when the iterator is exhausted");
        } catch (NoSuchElementException expectedException) {
            // expected
        }
    }
}