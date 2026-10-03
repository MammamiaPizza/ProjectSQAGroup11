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