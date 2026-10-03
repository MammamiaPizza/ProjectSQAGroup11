package org.apache.commons.collections4.trie;

 import static org.junit.Assert.*;
 import java.util.*;
 import org.junit.Test;

 /**
  * Regression tests for COLLECTIONS-586: NullPointerException thrown when calling
  * clear() on a prefix map view of AbstractPatriciaTrie.
  */
 public class PatriciaTriePrefixMapClearRegressionTest {

     /**
      * Helper to populate a PatriciaTrie with alternating key/value pairs.
      */
     private PatriciaTrie<String> trieWithEntries(Object... entries) {
         PatriciaTrie<String> trie = new PatriciaTrie<String>();
         for (int i = 0; i < entries.length; i += 2) {
             trie.put((String) entries[i], (String) entries[i + 1]);
         }
         return trie;
     }

     /* ---- 1. Clear on empty trie must not throw ---- */
     @Test
     public void testEmptyTriePrefixMapClear() {
         PatriciaTrie<String> trie = new PatriciaTrie<String>();
         SortedMap<String, String> prefixMap = trie.prefixMap("a");
         prefixMap.clear(); // regression: no NPE
         assertTrue(prefixMap.isEmpty());
         assertEquals(0, trie.size());
     }

     /* ---- 2. Clear when no matching keys ---- */
     @Test
     public void testPrefixMapClearNoMatchingKeys() {
         PatriciaTrie<String> trie = trieWithEntries("foo", "1", "bar", "2");
         SortedMap<String, String> prefixMap = trie.prefixMap("baz");
         assertEquals(0, prefixMap.size());
         prefixMap.clear();
         assertTrue(prefixMap.isEmpty());
         assertEquals(2, trie.size());
         assertEquals("1", trie.get("foo"));
         assertEquals("2", trie.get("bar"));
     }

     /* ---- 3. Single exact matching key ---- */
     @Test
     public void testPrefixMapClearSingleExactMatch() {
         PatriciaTrie<String> trie = trieWithEntries("apple", "red");
         SortedMap<String, String> prefixMap = trie.prefixMap("apple");
         assertEquals(1, prefixMap.size());
         prefixMap.clear();
         assertTrue(prefixMap.isEmpty());
         assertEquals(0, trie.size());
     }

     /* ---- 4. Remove only prefix-matching entries, keep others ---- */
     @Test
     public void testPrefixMapClearRemovesOnlyMatchingPrefixEntries() {
         PatriciaTrie<String> trie =
                 trieWithEntries("cat", "feline", "car", "vehicle", "dog", "canine", "catalog",
"book");
         SortedMap<String, String> prefixMap = trie.prefixMap("ca");
         assertEquals(3, prefixMap.size()); // cat, car, catalog
         prefixMap.clear();
         assertTrue(prefixMap.isEmpty());
         assertEquals(1, trie.size());
         assertFalse(trie.containsKey("cat"));
         assertFalse(trie.containsKey("car"));
         assertFalse(trie.containsKey("catalog"));
         assertTrue(trie.containsKey("dog"));
         assertEquals("canine", trie.get("dog"));
     }

     /* ---- 5. Clear whole map via empty-string prefix ---- */
     @Test
     public void testPrefixMapClearEntireMapWithEmptyPrefix() {
         PatriciaTrie<String> trie = trieWithEntries("x", "1", "y", "2", "z", "3");
         SortedMap<String, String> prefixMap = trie.prefixMap("");
         prefixMap.clear();
         assertTrue(prefixMap.isEmpty());
         assertTrue(trie.isEmpty());
     }

     /* ---- 6. Iterator after clear is empty ---- */
     @Test
     public void testPrefixMapClearIteratorEmptyAfterClear() {
         PatriciaTrie<String> trie = trieWithEntries("alpha", "a", "alphabet", "ab");
         SortedMap<String, String> prefixMap = trie.prefixMap("al");
         assertEquals(2, prefixMap.size());
         prefixMap.clear();
         assertTrue(prefixMap.isEmpty());
         Iterator<Map.Entry<String, String>> it = prefixMap.entrySet().iterator();
         assertFalse(it.hasNext());
         try {
             it.next();
             fail("Expected NoSuchElementException");
         } catch (NoSuchElementException expected) {
             // expected
         }
     }

     /* ---- 7. containsKey behaviour after clear ---- */
     @Test
     public void testPrefixMapClearContainsKeyAfter() {
         PatriciaTrie<String> trie = trieWithEntries("hello", "world", "help", "me");
         SortedMap<String, String> prefixMap = trie.prefixMap("hel");
         prefixMap.clear();
         assertFalse(prefixMap.containsKey("hello"));
         assertFalse(prefixMap.containsKey("help"));
         assertTrue(trie.isEmpty());
         // add a non-prefix key and re-check
         trie.put("other", "val");
         prefixMap = trie.prefixMap("hel");
         assertTrue(prefixMap.isEmpty());
         assertFalse(prefixMap.containsKey("hello"));
         assertTrue(trie.containsKey("other"));
     }

     /* ---- 8. Clear twice (idempotent) does not throw ---- */
     @Test
     public void testPrefixMapClearTwiceNoThrow() {
         PatriciaTrie<String> trie = trieWithEntries("test", "case");
         SortedMap<String, String> prefixMap = trie.prefixMap("t");
         prefixMap.clear();
         prefixMap.clear(); // second clear must not throw
         assertTrue(prefixMap.isEmpty());
         assertTrue(trie.isEmpty());
     }

     /* ---- 9. After clear, new entries can be put with same prefix ---- */
     @Test
     public void testPrefixMapClearThenPutNewEntries() {
         PatriciaTrie<String> trie = new PatriciaTrie<String>();
         trie.put("abc", "first");
         SortedMap<String, String> prefixMap = trie.prefixMap("ab");
         prefixMap.clear();
         assertTrue(prefixMap.isEmpty());
         trie.put("abc", "second");
         assertEquals(1, trie.size());
         prefixMap = trie.prefixMap("ab");
         assertEquals(1, prefixMap.size());
         assertEquals("second", prefixMap.get("abc"));
     }

     /* ---- 10. Branching prefix keys (shared bits) with partial clear ---- */
     @Test
     public void testPrefixMapClearBoundarySharedBranch() {
         PatriciaTrie<String> trie = new PatriciaTrie<String>();
         trie.put("abc", "1");
         trie.put("abd", "2");
         trie.put("a", "3");
         SortedMap<String, String> prefixMap = trie.prefixMap("ab");
         assertEquals(2, prefixMap.size());
         prefixMap.clear();
         assertTrue(prefixMap.isEmpty());
         assertEquals(1, trie.size());
         assertEquals("3", trie.get("a"));
         assertFalse(trie.containsKey("abc"));
         assertFalse(trie.containsKey("abd"));
     }

     /* ---- 11. Clear view after structural modifications does not NPE ---- */
     @Test
     public void testPrefixMapClearAfterRemovals() {
         PatriciaTrie<String> trie = new PatriciaTrie<String>();
         trie.put("k1", "v1");
         trie.put("k2", "v2");
         SortedMap<String, String> prefixMap = trie.prefixMap("k");
         trie.remove("k1");
         prefixMap.clear(); // NPE regression
         assertTrue(prefixMap.isEmpty());
         assertFalse(trie.containsKey("k1"));
         assertFalse(trie.containsKey("k2"));
     }

     /* ---- 12. Empty-string key (root entry) edge case ---- */
     @Test
     public void testPrefixMapClearWithEmptyStringKey() {
         PatriciaTrie<String> trie = new PatriciaTrie<String>();
         trie.put("", "empty");
         trie.put("a", "a-val");
         SortedMap<String, String> prefixMap = trie.prefixMap("");
         assertEquals(2, prefixMap.size());
         prefixMap.clear();
         assertTrue(trie.isEmpty());
     }
 }