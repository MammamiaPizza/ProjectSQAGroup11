package org.apache.commons.collections4.trie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

import org.junit.Test;

public class AbstractPatriciaTriePrefixMapClearTest {

    @Test
    public void clearPrefixMapRemovesOnlyMatchingEntries() {
        final PatriciaTrie<String> trie = new PatriciaTrie<String>();
        trie.put("Anna", "anna");
        trie.put("Anabel", "anabel");
        trie.put("Anita", "anita");
        trie.put("Aydy", "andy");
        trie.put("Bob", "bob");

        final SortedMap<String, String> prefix = trie.prefixMap("An");
        assertEquals(3, prefix.size());

        prefix.clear();

        assertTrue(prefix.isEmpty());
        assertEquals(2, trie.size());
        assertFalse(trie.containsKey("Anna"));
        assertFalse(trie.containsKey("Anabel"));
        assertFalse(trie.containsKey("Anita"));
        assertEquals("andy", trie.get("Aydy"));
        assertEquals("bob", trie.get("Bob"));
    }

    @Test
    public void clearPrefixMapLeavesEntriesWithSimilarButNonMatchingPrefix() {
        final PatriciaTrie<Integer> trie = new PatriciaTrie<Integer>();
        trie.put("app", Integer.valueOf(1));
        trie.put("apple", Integer.valueOf(2));
        trie.put("application", Integer.valueOf(3));
        trie.put("apt", Integer.valueOf(4));
        trie.put("banana", Integer.valueOf(5));

        final SortedMap<String, Integer> prefix = trie.prefixMap("app");
        prefix.clear();

        assertEquals(2, trie.size());
        assertFalse(trie.containsKey("app"));
        assertFalse(trie.containsKey("apple"));
        assertFalse(trie.containsKey("application"));
        assertEquals(Integer.valueOf(4), trie.get("apt"));
        assertEquals(Integer.valueOf(5), trie.get("banana"));
        assertTrue(prefix.entrySet().isEmpty());
    }

    @Test
    public void clearSingleEntryPrefixMapKeepsOtherEntriesAccessible() {
        final PatriciaTrie<String> trie = new PatriciaTrie<String>();
        trie.put("car", "car");
        trie.put("cat", "cat");
        trie.put("dog", "dog");

        final SortedMap<String, String> prefix = trie.prefixMap("car");
        assertEquals(1, prefix.size());
        assertEquals("car", prefix.get("car"));

        prefix.clear();

        assertEquals(2, trie.size());
        assertNull(trie.get("car"));
        assertFalse(trie.containsKey("car"));
        assertEquals("cat", trie.get("cat"));
        assertEquals("dog", trie.get("dog"));
        assertTrue(prefix.isEmpty());
    }

    @Test
    public void clearEmptyPrefixMapDoesNotChangeParentTrie() {
        final PatriciaTrie<String> trie = new PatriciaTrie<String>();
        trie.put("alpha", "a");
        trie.put("beta", "b");

        final SortedMap<String, String> prefix = trie.prefixMap("z");
        assertTrue(prefix.isEmpty());

        prefix.clear();

        assertEquals(2, trie.size());
        assertEquals("a", trie.get("alpha"));
        assertEquals("b", trie.get("beta"));
        assertFalse(trie.containsKey("z"));
        assertTrue(prefix.entrySet().isEmpty());
    }

    @Test
    public void prefixEntrySetCanBeIteratedAfterClear() {
        final PatriciaTrie<String> trie = new PatriciaTrie<String>();
        trie.put("north", "n");
        trie.put("northern", "nn");
        trie.put("south", "s");

        final SortedMap<String, String> prefix = trie.prefixMap("north");
        prefix.clear();

        final Iterator<Map.Entry<String, String>> iterator = prefix.entrySet().iterator();
        assertFalse(iterator.hasNext());
        assertEquals(1, trie.size());
        assertEquals("s", trie.get("south"));
    }

@org.junit.Test
public void clearEmptyStringPrefixMapRemovesAllEntriesIncludingEmptyKey() {
    final org.apache.commons.collections4.trie.PatriciaTrie<String> trie =
            new org.apache.commons.collections4.trie.PatriciaTrie<String>();
    trie.put("", "empty");
    trie.put("apple", "fruit");
    trie.put("banana", "fruit");

    trie.prefixMap("").clear();

    org.junit.Assert.assertTrue(trie.isEmpty());
    org.junit.Assert.assertFalse(trie.containsKey(""));
    org.junit.Assert.assertFalse(trie.containsKey("apple"));
    org.junit.Assert.assertFalse(trie.containsKey("banana"));
}

@org.junit.Test
public void clearPrefixMapRemovesEntriesWhoseValuesAreNull() {
    final org.apache.commons.collections4.trie.PatriciaTrie<String> trie =
            new org.apache.commons.collections4.trie.PatriciaTrie<String>();
    trie.put("car", null);
    trie.put("cart", "vehicle");
    trie.put("dog", "animal");

    trie.prefixMap("car").clear();

    org.junit.Assert.assertFalse(trie.containsKey("car"));
    org.junit.Assert.assertFalse(trie.containsKey("cart"));
    org.junit.Assert.assertTrue(trie.containsKey("dog"));
    org.junit.Assert.assertEquals("animal", trie.get("dog"));
}

@org.junit.Test
public void prefixMapCanBeUsedToAddMatchingEntryAfterClear() {
    final org.apache.commons.collections4.trie.PatriciaTrie<String> trie =
            new org.apache.commons.collections4.trie.PatriciaTrie<String>();
    trie.put("apple", "fruit");
    trie.put("application", "software");
    trie.put("banana", "fruit");

    final java.util.SortedMap<String, String> prefix = trie.prefixMap("app");
    prefix.clear();
    prefix.put("appetite", "hunger");

    org.junit.Assert.assertEquals(2, trie.size());
    org.junit.Assert.assertEquals("hunger", trie.get("appetite"));
    org.junit.Assert.assertEquals("fruit", trie.get("banana"));
    org.junit.Assert.assertFalse(trie.containsKey("apple"));
    org.junit.Assert.assertFalse(trie.containsKey("application"));
}
}
