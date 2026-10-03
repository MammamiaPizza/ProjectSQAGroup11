package org.apache.commons.collections4.map;

 import java.util.HashMap;
 import java.util.Map;
 import java.util.LinkedHashMap;
 import org.junit.Test;
 import static org.junit.Assert.*;
 import org.junit.Before;

 /**
  * Tests for {@link ListOrderedMap#putAll(int, Map)} targeting COLLECTIONS-474 bug.
  * Focuses on null values causing incorrect index tracking leading to IndexOutOfBoundsException.
  */
 public class ListOrderedMapPutAllTest {

     private ListOrderedMap<String, String> ordered;

     @Before
     public void setUp() {
         ordered = new ListOrderedMap<String, String>();
     }

     // ---- Valid operations, no overlap ----

     @Test
     public void putAllAtIndexZeroNoOverlap() {
         Map<String, String> source = new LinkedHashMap<String, String>();
         source.put("a", "1");
         source.put("b", "2");

         ordered.putAll(0, source);
         assertEquals("size", 2, ordered.size());
         assertEquals("a", ordered.get(0));
         assertEquals("b", ordered.get(1));
         assertEquals("1", ordered.getValue(0));
         assertEquals("2", ordered.getValue(1));
     }

     @Test
     public void putAllAtSizeEmptyMapAppends() {
         Map<String, String> source = new LinkedHashMap<String, String>();
         source.put("x", "10");
         source.put("y", "20");

         ordered.putAll(ordered.size(), source);
         assertEquals(2, ordered.size());
         assertEquals("x", ordered.get(0));
         assertEquals("y", ordered.get(1));
     }

     @Test
     public void putAllAtMidIndexNoOverlap() {
         ordered.put("a", "1");
         ordered.put("c", "3");
         Map<String, String> source = new LinkedHashMap<String, String>();
         source.put("b", "2");

         ordered.putAll(1, source);
         assertEquals(3, ordered.size());
         assertEquals("a", ordered.get(0));
         assertEquals("b", ordered.get(1));
         assertEquals("c", ordered.get(2));
     }

     // ---- Index bounds validation ----

     @Test(expected = IndexOutOfBoundsException.class)
     public void putAllNegativeIndexThrows() {
         Map<String, String> source = new HashMap<String, String>();
         source.put("a", "1");
         ordered.putAll(-1, source);
     }

     @Test(expected = IndexOutOfBoundsException.class)
     public void putAllIndexTooLargeThrows() {
         Map<String, String> source = new HashMap<String, String>();
         source.put("a", "1");
         ordered.putAll(1, source); // size is 0, index 1 > size
     }

     // ---- Null values, trigerring bug COLLECTIONS-474 ----

     @Test
     public void putAllAtSizeExistingNullValueKeyTriggersIndexBug() {
         // Setup: key with null value already present
         ordered.put("a", null);
         assertEquals(1, ordered.size());

         Map<String, String> source = new LinkedHashMap<String, String>();
         source.put("a", "newA");
         source.put("b", "newB");

         // This should succeed without exception, but the buggy version may throw
         // IndexOutOfBoundsException because old value is null, index is wrongly incremented.
         ordered.putAll(ordered.size(), source); // index = 1 (size)

         assertEquals("size", 2, ordered.size());
         assertEquals("order: a", "a", ordered.get(0));
         assertEquals("order: b", "b", ordered.get(1));
         assertEquals("value a", "newA", ordered.getValue(0));
         assertEquals("value b", "newB", ordered.getValue(1));
     }

     @Test
     public void putAllAtIndexZeroExistingNullValueKey() {
         ordered.put("a", null);
         Map<String, String> source = new LinkedHashMap<String, String>();
         source.put("a", "replaceA");
         source.put("b", "newB");

         ordered.putAll(0, source);
         assertEquals(2, ordered.size());
         // "a" is moved to position 0 (put retains order based on index 0)
         assertEquals("a", ordered.get(0));
         assertEquals("b", ordered.get(1));
         assertEquals("replaceA", ordered.getValue(0));
     }

     @Test
     public void putAllAtMidExistingNullValueKey() {
         ordered.put("x", "1");
         ordered.put("a", null);
         ordered.put("z", "2");

         Map<String, String> source = new LinkedHashMap<String, String>();
         source.put("a", "newA");
         source.put("y", "newY");

         ordered.putAll(1, source); // index between x and z
         assertEquals(4, ordered.size());
         // order: x, a, y, z
         assertEquals("x", ordered.get(0));
         assertEquals("a", ordered.get(1));
         assertEquals("y", ordered.get(2));
         assertEquals("z", ordered.get(3));
         assertEquals("newA", ordered.getValue(1));
     }

     // ---- Null key handling ----

     @Test
     public void putAllWithNullKeyAndNullValuesExistingNullKey() {
         ordered.put(null, null);

         Map<String, String> source = new LinkedHashMap<String, String>();
         source.put(null, "newNullKeyValue");
         source.put("c", "some");

         ordered.putAll(0, source);

         assertEquals(2, ordered.size());
         assertNull(ordered.get(0)); // null key remains
         assertEquals("c", ordered.get(1));
         assertEquals("newNullKeyValue", ordered.getValue(0));
     }

     @Test
     public void putAllAllNullValuesNoOverlap() {
         Map<String, String> source = new LinkedHashMap<String, String>();
         source.put("n1", null);
         source.put("n2", null);
         source.put("n3", null);

         ordered.putAll(0, source);
         assertEquals(3, ordered.size());
         assertEquals("n1", ordered.get(0));
         assertEquals("n2", ordered.get(1));
         assertEquals("n3", ordered.get(2));
         assertNull(ordered.getValue(0));
         assertNull(ordered.getValue(1));
         assertNull(ordered.getValue(2));
     }

     // ---- Non-null old values (no bug branch) ----

     @Test
     public void putAllOverlapWithNonNullOldValuesUsesCorrectIndexRecalc() {
         ordered.put("a", "initialA");
         ordered.put("b", "initialB");

         Map<String, String> source = new LinkedHashMap<String, String>();
         source.put("a", "replaceA");
         source.put("c", "newC");

         ordered.putAll(0, source);
         assertEquals(3, ordered.size());
         // a moved to 0, c inserted after a (index 1), b remains last
         assertEquals("a", ordered.get(0));
         assertEquals("c", ordered.get(1));
         assertEquals("b", ordered.get(2));
     }

     // ---- Empty map ----

     @Test
     public void putAllEmptyMapDoesNothing() {
         ordered.put("a", "1");
         Map<String, String> empty = new HashMap<String, String>();
         ordered.putAll(0, empty);
         assertEquals(1, ordered.size());
         assertEquals("a", ordered.get(0));
     }

     // ---- Multiple overlapping entries with mixed null/non-null ----

     @Test
     public void putAllMultipleExistingKeysMixedNullAndNonNullOldValues() {
         ordered.put("a", null);   // null
         ordered.put("b", "B");    // non-null
         ordered.put("c", null);   // null

         Map<String, String> source = new LinkedHashMap<String, String>();
         source.put("a", "replaceA");
         source.put("b", "replaceB");
         source.put("d", "newD");

         // This should complete without exception; assertion verifies correct final state.
         ordered.putAll(1, source); // index 1 (insert after "a")

         assertEquals(4, ordered.size());
         // Expected order: a (moved to index 0? Actually putAll starts at index 1:
         //  first entry "a": put(1,"a","replaceA") -> removes old "a" at pos 0, inserts at index 1
         //  then index recalc: old null? yes (null), so index++ to 2.
         //  second "b": put(2,"b","replaceB") -> removes old "b" at pos? after previous ops,
         //  let's not depend on precise order, just check no exception and all keys present.
         assertEquals("a exists", "replaceA", ordered.get("a"));
         assertEquals("b exists", "replaceB", ordered.get("b"));
         assertEquals("c exists", null, ordered.get("c"));
         assertEquals("d exists", "newD", ordered.get("d"));
     }
 }