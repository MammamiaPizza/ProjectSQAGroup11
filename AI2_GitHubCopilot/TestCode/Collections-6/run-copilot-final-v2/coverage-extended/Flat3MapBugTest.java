package org.apache.commons.collections.map;

 import java.util.HashMap;
 import java.util.Map;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for Flat3Map focused on the COLLECTIONS-261 bug:
  * get() returns wrong value after remove() shifts internal array entries,
  * particularly when the removed key is at position 1 (key1/value1) and
  * a key from position 3 (key3/value3) is shifted into position 1.
  */
 public class Flat3MapBugTest {

     /**
      * Direct reproduction of the COLLECTIONS-261 bug.
      * Put 3 entries, remove the first one, then get the remaining ones.
      * The bug causes get() to return the wrong value because remove()
      * shifts key3/value3 into key1/value1 but get()'s switch fall-through
      * for size=2 matches key2 before key1, returning the wrong value.
      */
     @Test
     public void testGetAfterRemoveReproducesBug261() {
         Flat3Map map = new Flat3Map();
         map.put("A", "1");
         map.put("B", "2");
         map.put("C", "3");

         assertEquals("1", map.get("A"));
         assertEquals("2", map.get("B"));
         assertEquals("3", map.get("C"));
         assertEquals(3, map.size());

         // Remove "A" which is at position key1/value1
         Object removed = map.remove("A");
         assertEquals("1", removed);
         assertEquals(2, map.size());

         // After remove, "C" should have shifted to position 1 (key3->key1, value3->value1)
         // Bug: get() for size==2 checks key2 first, returning "2" instead of "3"
         Object valueC = map.get("C");
         assertEquals("Bug: get after remove returned wrong value for shifted entry",
                 "3", valueC);

         Object valueB = map.get("B");
         assertEquals("2", valueB);
     }

     /**
      * Test get after removing the first of two entries.
      * When size goes from 2 to 1, positions are consolidated.
      */
     @Test
     public void testGetAfterRemoveFromTwoEntries() {
         Flat3Map map = new Flat3Map();
         map.put("X", "10");
         map.put("Y", "20");

         assertEquals("10", map.get("X"));
         assertEquals("20", map.get("Y"));
         assertEquals(2, map.size());

         // Remove first entry
         map.remove("X");
         assertEquals(1, map.size());

         // Y should now be at position 1
         assertEquals("20", map.get("Y"));
         assertNull(map.get("X"));
     }

     /**
      * Test get after removing the second of three entries.
      * Removing from the middle triggers a different shift path.
      */
     @Test
     public void testGetAfterRemoveMiddleOfThree() {
         Flat3Map map = new Flat3Map();
         map.put("A", "a");
         map.put("B", "b");
         map.put("C", "c");

         // Remove the middle entry (key2/value2)
         map.remove("B");
         assertEquals(2, map.size());

         assertEquals("a", map.get("A"));
         assertEquals("c", map.get("C"));
         assertNull(map.get("B"));
     }

     /**
      * Test get after removing the last of three entries (no shift needed).
      */
     @Test
     public void testGetAfterRemoveLastOfThree() {
         Flat3Map map = new Flat3Map();
         map.put("A", "a");
         map.put("B", "b");
         map.put("C", "c");

         map.remove("C");
         assertEquals(2, map.size());

         assertEquals("a", map.get("A"));
         assertEquals("b", map.get("B"));
         assertNull(map.get("C"));
     }

     /**
      * Test get after removing an entry that causes delegate map conversion.
      * Adding a 4th entry triggers convertToMap; the existing entries
      * should be retrievable from the delegate.
      */
     @Test
     public void testGetAfterDelegateConversion() {
         Flat3Map map = new Flat3Map();
         map.put("A", "1");
         map.put("B", "2");
         map.put("C", "3");
         map.put("D", "4"); // triggers delegate conversion

         assertEquals(4, map.size());
         assertEquals("1", map.get("A"));
         assertEquals("2", map.get("B"));
         assertEquals("3", map.get("C"));
         assertEquals("4", map.get("D"));
     }

     /**
      * Test get after remove then add (re-fill the flat storage).
      */
     @Test
     public void testGetAfterRemoveThenAdd() {
         Flat3Map map = new Flat3Map();
         map.put("A", "1");
         map.put("B", "2");
         map.put("C", "3");

         // Remove first, triggering shift
         map.remove("A");
         assertEquals(2, map.size());

         // Add a new entry (fills slot 3)
         map.put("D", "4");
         assertEquals(3, map.size());

         // Verify all entries accessible
         assertEquals("2", map.get("B"));
         assertEquals("3", map.get("C"));
         assertEquals("4", map.get("D"));
         assertNull(map.get("A"));
     }

     /**
      * Test get with null key.
      */
     @Test
     public void testGetWithNullKey() {
         Flat3Map map = new Flat3Map();
         map.put(null, "nullValue");
         map.put("A", "a");

         assertEquals("nullValue", map.get(null));
         assertEquals("a", map.get("A"));

         // Remove non-null entry, shifting null entry
         map.remove("A");
         assertEquals("nullValue", map.get(null));
         assertEquals(1, map.size());
     }

     /**
      * Test containsKey after remove reproduces the same bug pattern.
      */
     @Test
     public void testContainsKeyAfterRemove() {
         Flat3Map map = new Flat3Map();
         map.put("A", "1");
         map.put("B", "2");
         map.put("C", "3");

         map.remove("A");

         assertTrue("containsKey should find shifted key C", map.containsKey("C"));
         assertTrue("containsKey should find remaining key B", map.containsKey("B"));
         assertFalse("containsKey should not find removed key A", map.containsKey("A"));
     }

     /**
      * Test containsKey with null key before and after remove.
      */
     @Test
     public void testContainsKeyNullAfterRemove() {
         Flat3Map map = new Flat3Map();
         map.put(null, "n");
         map.put("A", "a");
         map.put("B", "b");

         assertTrue(map.containsKey(null));
         assertEquals(3, map.size());

         map.remove("A");
         assertEquals(2, map.size());
         assertTrue(map.containsKey(null));
         assertTrue(map.containsKey("B"));
     }

     /**
      * Test size consistency through put, remove, and delegate conversion.
      */
     @Test
     public void testSizeConsistency() {
         Flat3Map map = new Flat3Map();

         assertEquals(0, map.size());
         assertTrue(map.isEmpty());

         map.put("A", "1");
         assertEquals(1, map.size());
         assertFalse(map.isEmpty());

         map.put("B", "2");
         map.put("C", "3");
         assertEquals(3, map.size());

         // Remove and verify size decrements
         map.remove("A");
         assertEquals(2, map.size());

         map.remove("B");
         assertEquals(1, map.size());

         map.remove("C");
         assertEquals(0, map.size());
         assertTrue(map.isEmpty());
     }

     /**
      * Test put overwrite behavior does not change size.
      */
     @Test
     public void testPutOverwriteExistingKey() {
         Flat3Map map = new Flat3Map();
         map.put("key", "old");
         assertEquals(1, map.size());

         Object oldValue = map.put("key", "new");
         assertEquals("old", oldValue);
         assertEquals("new", map.get("key"));
         assertEquals(1, map.size());
     }

     /**
      * Test clear restores flat mode from delegate.
      */
     @Test
     public void testClearAfterDelegateConversion() {
         Flat3Map map = new Flat3Map();
         map.put("A", "1");
         map.put("B", "2");
         map.put("C", "3");
         map.put("D", "4"); // delegate conversion
         assertEquals(4, map.size());

         map.clear();
         assertEquals(0, map.size());
         assertTrue(map.isEmpty());
         assertNull(map.get("A"));

         // After clear, should be back in flat mode
         map.put("X", "x");
         assertEquals(1, map.size());
         assertEquals("x", map.get("X"));
     }

@Test
public void testConstructorWithMap() {
    java.util.Map<String, String> source = new java.util.HashMap<String, String>();
    source.put("A", "1");
    source.put("B", "2");
    Flat3Map map = new Flat3Map(source);
    assertEquals(2, map.size());
    assertEquals("1", map.get("A"));
    assertEquals("2", map.get("B"));
    // verify independence from source
    source.put("C", "3");
    assertEquals(2, map.size());
    assertNull(map.get("C"));
}

@Test
public void testClone() {
    // clone flat map (no delegate)
    Flat3Map original = new Flat3Map();
    original.put("A", "1");
    original.put("B", "2");
    Flat3Map cloned = (Flat3Map) original.clone();
    assertEquals(original.size(), cloned.size());
    assertEquals(original.get("A"), cloned.get("A"));
    assertEquals(original.get("B"), cloned.get("B"));
    cloned.put("C", "3");
    assertEquals(2, original.size()); // original unchanged

 // clone map with delegate (size >= 4 triggers convertToMap)
 original.put("C", "3");
 original.put("D", "4");
 cloned = (Flat3Map) original.clone();
 assertEquals(4, cloned.size());
 assertEquals(original.get("A"), cloned.get("A"));
 assertEquals(original.get("D"), cloned.get("D"));
 cloned.put("E", "5");
 assertEquals(4, original.size()); // original unchanged

}

@Test
public void testClearFlatMap() {
    Flat3Map map = new Flat3Map();
    map.put("A", "1");
    map.put("B", "2");
    assertEquals(2, map.size());
    map.clear();
    assertEquals(0, map.size());
    assertFalse(map.containsKey("A"));
    assertNull(map.get("A"));
    assertNull(map.get("B"));
}

@Test
public void testContainsKeyAndValueWithDelegateAndFlatNullValues() {
    // flat map: exercise null-value and non-null-value branches of containsValue
    Flat3Map flatMap = new Flat3Map();
    flatMap.put("X", null);
    flatMap.put("Y", "value");
    flatMap.put("Z", null);
    assertTrue(flatMap.containsValue(null));
    assertTrue(flatMap.containsValue("value"));
    assertFalse(flatMap.containsValue("missing"));

 // delegate map: exercise containsKey/containsValue delegation (lines 189,224)
 Flat3Map delegateMap = new Flat3Map();
 delegateMap.put("A", "1");
 delegateMap.put("B", "2");
 delegateMap.put("C", "3");
 delegateMap.put("D", "4"); // triggers convertToMap
 assertTrue(delegateMap.containsKey("A"));
 assertTrue(delegateMap.containsValue("3"));
 assertFalse(delegateMap.containsKey("Z"));
 assertFalse(delegateMap.containsValue("99"));
 // null value via delegate
 delegateMap.put("E", null);
 assertTrue(delegateMap.containsValue(null));

}
}
