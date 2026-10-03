package org.apache.commons.collections.map;

 import java.util.ArrayList;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.HashMap;
 import java.util.HashSet;
 import java.util.Map;

 import static org.junit.Assert.*;
 import org.junit.Before;
 import org.junit.Test;

 /**
  * JUnit tests for MultiValueMap targeting the COLLECTIONS-228 bug.
  * Focuses on the return semantics of {@link MultiValueMap#put(Object, Object)}
  * and {@link MultiValueMap#putAll(Object, Collection)}.
  */
 public class TestMultiValueMapBug4 {

     private MultiValueMap map;

     @Before
     public void setUp() {
         map = MultiValueMap.decorate(new HashMap());
     }

     // put(Object,Object) must return the value that was added, not null, for a new key
     @Test
     public void testPutNewKeyReturnsValue() {
         Object returned = map.put("key1", "a");
         assertEquals("a", returned);
         assertEquals(1, map.size("key1"));
         assertTrue(map.containsValue("key1", "a"));
     }

     // put with an existing key returns the added value when the collection changes
     @Test
     public void testPutExistingKeyReturnsValue() {
         map.put("key1", "a");
         Object returned = map.put("key1", "b");
         assertEquals("b", returned);
         assertEquals(2, map.size("key1"));
     }

     // put returns null when the value is already present and the collection does not change
     // (using a Set to ensure uniqueness)
     @Test
     public void testPutDuplicateValueWithSetReturnsNull() {
         MultiValueMap setMap = MultiValueMap.decorate(new HashMap(), HashSet.class);
         setMap.put("key1", "a");
         Object returned = setMap.put("key1", "a");
         assertNull(returned);
         assertEquals(1, setMap.size("key1"));
     }

     // putAll(Object key, Collection values) with a new key must return true
     @Test
     public void testPutAllNewKeyReturnsTrue() {
         Collection<String> values = Arrays.asList("a", "b");
         boolean result = map.putAll("key1", values);
         assertTrue(result);
         assertEquals(2, map.size("key1"));
     }

     // putAll with an existing key returns true when new values are added
     @Test
     public void testPutAllExistingKeyReturnsTrue() {
         map.putAll("key1", Arrays.asList("a", "b"));
         boolean result = map.putAll("key1", Arrays.asList("c", "d"));
         assertTrue(result);
         assertEquals(4, map.size("key1"));
     }

     // putAll returns false when all supplied values are already present (Set-based)
     @Test
     public void testPutAllExistingKeyNoChangeReturnsFalse() {
         MultiValueMap setMap = MultiValueMap.decorate(new HashMap(), HashSet.class);
         setMap.put("key1", "a");
         setMap.put("key1", "b");
         boolean result = setMap.putAll("key1", Arrays.asList("a", "b"));
         assertFalse(result);
         assertEquals(2, setMap.size("key1"));
     }

     // putAll with an empty collection returns false and does not create a mapping
     @Test
     public void testPutAllEmptyCollectionReturnsFalse() {
         boolean result = map.putAll("key1", new ArrayList<Object>());
         assertFalse(result);
         assertNull(map.getCollection("key1"));
     }

     // putAll with null collection returns false
     @Test
     public void testPutAllNullCollectionReturnsFalse() {
         boolean result = map.putAll("key1", null);
         assertFalse(result);
         assertNull(map.getCollection("key1"));
     }

     // putAll(Map) correctly handles a regular Map source
     @Test
     public void testPutAllMap() {
         Map<Object, Object> toAdd = new HashMap<>();
         toAdd.put("a", 1);
         toAdd.put("b", 2);
         map.putAll(toAdd);
         assertEquals(1, map.size("a"));
         assertEquals(1, map.size("b"));
         assertTrue(map.containsValue("a", 1));
         assertTrue(map.containsValue("b", 2));
     }

     // putAll(Map) correctly handles a MultiMap source (collection values)
     @Test
     public void testPutAllMapMultiMapSource() {
         MultiValueMap source = MultiValueMap.decorate(new HashMap());
         source.putAll("a", Arrays.asList(1, 2));
         source.putAll("b", Arrays.asList(3));
         map.putAll((Map) source);
         assertEquals(2, map.size("a"));
         assertEquals(1, map.size("b"));
         assertTrue(map.containsValue("a", 1));
         assertTrue(map.containsValue("a", 2));
     }

     // put with a null value – the return is null because the value is null
     @Test
     public void testPutNullValue() {
         Object returned = map.put("key1", null);
         assertNull(returned);
         assertEquals(1, map.size("key1"));
         assertTrue(map.containsValue("key1", null));
     }

     // boundary: a large collection of values inserted via putAll
     @Test
     public void testLargeNumberOfValues() {
         Collection<String> values = new ArrayList<>();
         for (int i = 0; i < 1000; i++) {
             values.add("value" + i);
         }
         map.putAll("key", values);
         assertEquals(1000, map.totalSize());
     }
 }