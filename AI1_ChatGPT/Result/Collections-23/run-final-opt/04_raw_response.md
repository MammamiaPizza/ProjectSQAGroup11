@org.junit.Test
public void unmodifiableTriePreventsMutationThroughCollectionViews() {
    final org.apache.commons.collections4.Trie<String, Integer> trie =
            new org.apache.commons.collections4.trie.PatriciaTrie<Integer>();
    trie.put("alpha", Integer.valueOf(1));
    trie.put("beta", Integer.valueOf(2));
    trie.put("gamma", Integer.valueOf(3));

    final org.apache.commons.collections4.trie.UnmodifiableTrie<String, Integer> unmodifiable =
            org.apache.commons.collections4.trie.UnmodifiableTrie.unmodifiableTrie(trie);

    try {
        unmodifiable.keySet().remove("alpha");
        org.junit.Assert.fail("keySet must be unmodifiable");
    } catch (final UnsupportedOperationException expected) {
    }

    try {
        unmodifiable.values().remove(Integer.valueOf(2));
        org.junit.Assert.fail("values must be unmodifiable");
    } catch (final UnsupportedOperationException expected) {
    }

    try {
        unmodifiable.entrySet().iterator().next().setValue(Integer.valueOf(10));
        org.junit.Assert.fail("entrySet entries must be unmodifiable");
    } catch (final UnsupportedOperationException expected) {
    }

    org.junit.Assert.assertEquals(Integer.valueOf(1), trie.get("alpha"));
    org.junit.Assert.assertEquals(Integer.valueOf(2), trie.get("beta"));
    org.junit.Assert.assertEquals(Integer.valueOf(3), trie.get("gamma"));
}

@org.junit.Test
public void unmodifiableTrieRangeAndPrefixMapsAreReadOnly() {
    final org.apache.commons.collections4.Trie<String, Integer> trie =
            new org.apache.commons.collections4.trie.PatriciaTrie<Integer>();
    trie.put("app", Integer.valueOf(1));
    trie.put("apple", Integer.valueOf(2));
    trie.put("banana", Integer.valueOf(3));

    final org.apache.commons.collections4.trie.UnmodifiableTrie<String, Integer> unmodifiable =
            org.apache.commons.collections4.trie.UnmodifiableTrie.unmodifiableTrie(trie);

    org.junit.Assert.assertEquals(2, unmodifiable.headMap("banana").size());
    org.junit.Assert.assertEquals(2, unmodifiable.subMap("app", "banana").size());
    org.junit.Assert.assertEquals(1, unmodifiable.tailMap("banana").size());
    org.junit.Assert.assertEquals(2, unmodifiable.prefixMap("app").size());

    try {
        unmodifiable.prefixMap("app").clear();
        org.junit.Assert.fail("prefixMap must be unmodifiable");
    } catch (final UnsupportedOperationException expected) {
    }

    org.junit.Assert.assertEquals(3, trie.size());
    org.junit.Assert.assertEquals(Integer.valueOf(1), trie.get("app"));
    org.junit.Assert.assertEquals(Integer.valueOf(2), trie.get("apple"));
}

@org.junit.Test
public void unmodifiableTrieDelegatesOrderedOperationsAndProtectsMapIterator() {
    final org.apache.commons.collections4.Trie<String, Integer> trie =
            new org.apache.commons.collections4.trie.PatriciaTrie<Integer>();
    trie.put("alpha", Integer.valueOf(1));
    trie.put("beta", Integer.valueOf(2));
    trie.put("gamma", Integer.valueOf(3));

    final org.apache.commons.collections4.trie.UnmodifiableTrie<String, Integer> unmodifiable =
            org.apache.commons.collections4.trie.UnmodifiableTrie.unmodifiableTrie(trie);

    org.junit.Assert.assertEquals(trie.firstKey(), unmodifiable.firstKey());
    org.junit.Assert.assertEquals(trie.lastKey(), unmodifiable.lastKey());
    org.junit.Assert.assertEquals(trie.nextKey("alpha"), unmodifiable.nextKey("alpha"));
    org.junit.Assert.assertEquals(trie.previousKey("gamma"), unmodifiable.previousKey("gamma"));
    org.junit.Assert.assertSame(trie.comparator(), unmodifiable.comparator());
    org.junit.Assert.assertEquals(trie.hashCode(), unmodifiable.hashCode());
    org.junit.Assert.assertEquals(trie.toString(), unmodifiable.toString());
    org.junit.Assert.assertEquals(trie.equals(trie), unmodifiable.equals(trie));

    final org.apache.commons.collections4.OrderedMapIterator<String, Integer> iterator =
            unmodifiable.mapIterator();
    org.junit.Assert.assertEquals("alpha", iterator.next());
    org.junit.Assert.assertEquals(Integer.valueOf(1), iterator.getValue());

    try {
        iterator.remove();
        org.junit.Assert.fail("mapIterator must be unmodifiable");
    } catch (final UnsupportedOperationException expected) {
    }

    org.junit.Assert.assertEquals(Integer.valueOf(1), trie.get("alpha"));
}

@org.junit.Test
public void unmodifiableTrieRejectsClearAndRemoveWithoutChangingDelegate() {
    final org.apache.commons.collections4.Trie<String, Integer> trie =
            new org.apache.commons.collections4.trie.PatriciaTrie<Integer>();
    trie.put("alpha", Integer.valueOf(1));

    final org.apache.commons.collections4.trie.UnmodifiableTrie<String, Integer> unmodifiable =
            org.apache.commons.collections4.trie.UnmodifiableTrie.unmodifiableTrie(trie);

    try {
        unmodifiable.remove("alpha");
        org.junit.Assert.fail("remove must be unsupported");
    } catch (final UnsupportedOperationException expected) {
    }

    try {
        unmodifiable.clear();
        org.junit.Assert.fail("clear must be unsupported");
    } catch (final UnsupportedOperationException expected) {
    }

    org.junit.Assert.assertEquals(1, trie.size());
    org.junit.Assert.assertEquals(Integer.valueOf(1), trie.get("alpha"));
}