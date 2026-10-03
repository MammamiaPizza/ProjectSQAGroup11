package org.apache.commons.collections4.trie;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.apache.commons.collections4.Trie;
import org.junit.Test;

public class UnmodifiableTrieFactoryRegressionTest {

    @Test
    public void unmodifiableTrieReturnsSameInstanceWhenAlreadyUnmodifiable() {
        final Trie<String, Integer> mutable = new PatriciaTrie<Integer>();
        final Trie<String, Integer> unmodifiable =
                UnmodifiableTrie.unmodifiableTrie(mutable);

        final Trie<String, Integer> decoratedAgain =
                UnmodifiableTrie.unmodifiableTrie(unmodifiable);

        assertSame(unmodifiable, decoratedAgain);
    }

    @Test
    public void repeatedDecorationPreservesIdentityForTrieWithEntries() {
        final Trie<String, Integer> mutable = new PatriciaTrie<Integer>();
        mutable.put("alpha", Integer.valueOf(1));
        mutable.put("beta", Integer.valueOf(2));

        final Trie<String, Integer> first =
                UnmodifiableTrie.unmodifiableTrie(mutable);
        final Trie<String, Integer> second =
                UnmodifiableTrie.unmodifiableTrie(first);
        final Trie<String, Integer> third =
                UnmodifiableTrie.unmodifiableTrie(second);

        assertSame(first, second);
        assertSame(first, third);
        assertEquals(Integer.valueOf(1), third.get("alpha"));
        assertEquals(Integer.valueOf(2), third.get("beta"));
    }

    @Test
    public void decoratedTrieDelegatesReadOperationsAndReflectsWrappedTrie() {
        final Trie<String, Integer> mutable = new PatriciaTrie<Integer>();
        mutable.put("cat", Integer.valueOf(3));

        final Trie<String, Integer> unmodifiable =
                UnmodifiableTrie.unmodifiableTrie(mutable);

        assertEquals(1, unmodifiable.size());
        assertFalse(unmodifiable.isEmpty());
        assertTrue(unmodifiable.containsKey("cat"));
        assertTrue(unmodifiable.containsValue(Integer.valueOf(3)));
        assertEquals(Integer.valueOf(3), unmodifiable.get("cat"));

        mutable.put("car", Integer.valueOf(4));

        assertEquals(2, unmodifiable.size());
        assertEquals(Integer.valueOf(4), unmodifiable.get("car"));
    }

    @Test
    public void decoratedTrieRejectsDirectMutation() {
        final Trie<String, Integer> mutable = new PatriciaTrie<Integer>();
        mutable.put("key", Integer.valueOf(1));
        final Trie<String, Integer> unmodifiable =
                UnmodifiableTrie.unmodifiableTrie(mutable);

        try {
            unmodifiable.put("other", Integer.valueOf(2));
            fail("An unmodifiable trie must reject put");
        } catch (final UnsupportedOperationException expected) {
            // expected
        }

        try {
            unmodifiable.remove("key");
            fail("An unmodifiable trie must reject remove");
        } catch (final UnsupportedOperationException expected) {
            // expected
        }

        assertEquals(Integer.valueOf(1), unmodifiable.get("key"));
        assertEquals(1, unmodifiable.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void unmodifiableTrieRejectsNullTrie() {
        UnmodifiableTrie.<String, Integer>unmodifiableTrie(null);
    }
}
