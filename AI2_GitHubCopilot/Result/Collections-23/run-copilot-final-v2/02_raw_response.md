package org.apache.commons.collections4.trie;

 import static org.junit.Assert.*;

 import java.util.AbstractMap;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.Map;
 import java.util.Set;
 import java.util.SortedMap;

 import org.junit.Test;

 /**
  * Tests for {@link UnmodifiableTrie}, targeting COLLECTIONS-495.
  */
 public class UnmodifiableTrieTest {

     @Test
     public void testFactoryOnMutableTrie() {
         final Trie<String, String> original = new PatriciaTrie<String>();
         original.put("a", "1");
         final Trie<String, String> unmod = UnmodifiableTrie.unmodifiableTrie(original);
         assertNotNull(unmod);
         assertTrue(unmod instanceof UnmodifiableTrie);
         assertNotSame(original, unmod);
     }

     @Test
     public void testFactoryOnAlreadyUnmodifiable() {
         final Trie<String, String> original = new PatriciaTrie<String>();
         original.put("a", "1");
         final Trie<String, String> firstWrap = UnmodifiableTrie.unmodifiableTrie(original);
         // Bug COLLECTIONS-495: decorating an already unmodifiable trie must return the same
reference
         final Trie<String, String> secondWrap = UnmodifiableTrie.unmodifiableTrie(firstWrap);
         assertSame("UnmodifiableTrie should not be double-wrapped", firstWrap, secondWrap);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testConstructorNull() {
         new UnmodifiableTrie<String, String>(null);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testFactoryNull() {
         UnmodifiableTrie.unmodifiableTrie(null);
     }

     @Test
     public void testReadDelegation() {
         final PatriciaTrie<String, String> trie = new PatriciaTrie<String>();
         trie.put("apple", "fruit");
         trie.put("car", "vehicle");
         final Trie<String, String> unmod = UnmodifiableTrie.unmodifiableTrie(trie);

         assertEquals(2, unmod.size());
         assertEquals("fruit", unmod.get("apple"));
         assertTrue(unmod.containsKey("car"));
         assertTrue(unmod.containsValue("vehicle"));
         assertFalse(unmod.isEmpty());
         assertEquals("apple", unmod.firstKey());
         assertEquals("car", unmod.lastKey());
         assertEquals("car", unmod.nextKey("apple"));
         assertEquals("apple", unmod.previousKey("car"));
         assertNull(unmod.get("nonexistent"));
         assertFalse(unmod.containsKey("nonexistent"));
     }

     @Test
     public void testMutatorsThrowUnsupportedOperationException() {
         final Trie<String, String> unmod = UnmodifiableTrie.unmodifiableTrie(new
PatriciaTrie<String>());
         try {
             unmod.put("key", "value");
             fail("Expected UnsupportedOperationException on put");
         } catch (final UnsupportedOperationException e) {
             // expected
         }
         try {
             unmod.putAll(new HashMap<String, String>());
             fail("Expected UnsupportedOperationException on putAll");
         } catch (final UnsupportedOperationException e) {
             // expected
         }
         try {
             unmod.remove("key");
             fail("Expected UnsupportedOperationException on remove");
         } catch (final UnsupportedOperationException e) {
             // expected
         }
         try {
             unmod.clear();
             fail("Expected UnsupportedOperationException on clear");
         } catch (final UnsupportedOperationException e) {
             // expected
         }
     }

     @Test(expected = UnsupportedOperationException.class)
     public void testKeySetIsUnmodifiable() {
         final PatriciaTrie<String, String> trie = new PatriciaTrie<String>();
         trie.put("a", "1");
         final Trie<String, String> unmod = UnmodifiableTrie.unmodifiableTrie(trie);
         unmod.keySet().add("b");
     }

     @Test(expected = UnsupportedOperationException.class)
     public void testValuesIsUnmodifiable() {
         final PatriciaTrie<String, String> trie = new PatriciaTrie<String>();
         trie.put("a", "1");
         final Trie<String, String> unmod = UnmodifiableTrie.unmodifiableTrie(trie);
         unmod.values().add("2");
     }

     @Test(expected = UnsupportedOperationException.class)
     public void testEntrySetIsUnmodifiable() {
         final PatriciaTrie<String, String> trie = new PatriciaTrie<String>();
         trie.put("a", "1");
         final Trie<String, String> unmod = UnmodifiableTrie.unmodifiableTrie(trie);
         final Set<Map.Entry<String, String>> entries = unmod.entrySet();
         entries.add(new AbstractMap.SimpleEntry<String, String>("b", "2"));
     }

     @Test
     public void testEqualsAndHashCodeDelegate() {
         final PatriciaTrie<String, String> trie1 = new PatriciaTrie<String>();
         trie1.put("a", "1");
         final PatriciaTrie<String, String> trie2 = new PatriciaTrie<String>();
         trie2.put("a", "1");
         final Trie<String, String> unmod1 = UnmodifiableTrie.unmodifiableTrie(trie1);
         final Trie<String, String> unmod2 = UnmodifiableTrie.unmodifiableTrie(trie2);
         assertTrue(unmod1.equals(unmod2));
         assertEquals(unmod1.hashCode(), unmod2.hashCode());
     }

     @Test
     public void testToStringDelegation() {
         final PatriciaTrie<String, String> trie = new PatriciaTrie<String>();
         trie.put("key", "value");
         final Trie<String, String> unmod = UnmodifiableTrie.unmodifiableTrie(trie);
         assertEquals(trie.toString(), unmod.toString());
     }

     @Test
     public void testSortedMapViewsAreUnmodifiable() {
         final PatriciaTrie<String, String> trie = new PatriciaTrie<String>();
         trie.put("a", "1");
         trie.put("b", "2");
         trie.put("c", "3");
         final Trie<String, String> unmod = UnmodifiableTrie.unmodifiableTrie(trie);

         final SortedMap<String, String> head = unmod.headMap("c");
         assertNotNull(head);
         try {
             head.put("d", "4");
             fail("headMap should be unmodifiable");
         } catch (final UnsupportedOperationException e) {
             // expected
         }

         final SortedMap<String, String> tail = unmod.tailMap("a");
         try {
             tail.put("e", "5");
             fail("tailMap should be unmodifiable");
         } catch (final UnsupportedOperationException e) {
             // expected
         }

         final SortedMap<String, String> sub = unmod.subMap("a", "c");
         try {
             sub.put("f", "6");
             fail("subMap should be unmodifiable");
         } catch (final UnsupportedOperationException e) {
             // expected
         }

         final SortedMap<String, String> prefix = unmod.prefixMap("a");
         try {
             prefix.put("g", "7");
             fail("prefixMap should be unmodifiable");
         } catch (final UnsupportedOperationException e) {
             // expected
         }
     }
 }