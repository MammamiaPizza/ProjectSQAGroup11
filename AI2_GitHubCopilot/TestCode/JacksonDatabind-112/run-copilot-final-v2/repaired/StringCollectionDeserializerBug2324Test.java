package com.fasterxml.jackson.databind.deser.creators;

 import static org.junit.Assert.*;

 import java.io.IOException;
 import java.util.*;

 import org.junit.Test;

 import com.fasterxml.jackson.annotation.JsonCreator;
 import com.fasterxml.jackson.core.JsonParser;
 import com.fasterxml.jackson.core.JsonProcessingException;
 import com.fasterxml.jackson.core.type.TypeReference;
 import com.fasterxml.jackson.databind.*;
 import com.fasterxml.jackson.databind.exc.MismatchedInputException;

 public class StringCollectionDeserializerBug2324Test {

     private final ObjectMapper MAPPER = new ObjectMapper();

     /**
      * Test delegate: a Collection<String> with NO default constructor and only a delegating
      * @JsonCreator that receives the entire list.
      */
     public static class ImmutableBag extends AbstractCollection<String> {
         private final List<String> _items;

         @JsonCreator(mode = com.fasterxml.jackson.annotation.JsonCreator.Mode.DELEGATING)
         public ImmutableBag(List<String> items) {
             _items = items;
         }

         @Override
         public Iterator<String> iterator() {
             return _items.iterator();
         }
         @Override
         public int size() {
             return _items.size();
         }
         @Override
         public boolean add(String s) {
             throw new UnsupportedOperationException();
         }

         // equality semantic for assertion
         @Override
         public boolean equals(Object o) {
             if (o instanceof ImmutableBag) {
                 return _items.equals(((ImmutableBag) o)._items);
             }
             return false;
         }
         @Override
         public int hashCode() {
             return _items.hashCode();
         }
         @Override
         public String toString() {
             return _items.toString();
         }
     }

     /**
      * A Collection<String> without any @JsonCreator and without default constructor.
      * Expected to fail during deserialization.
      */
     public static class NoCreatorNoDefaultCtorCollection extends AbstractCollection<String> {
         private final List<String> _items;

         public NoCreatorNoDefaultCtorCollection(List<String> items) { // not annotated
             _items = items;
         }

         @Override
         public Iterator<String> iterator() { return _items.iterator(); }
         @Override
         public int size() { return _items.size(); }
         @Override
         public boolean add(String s) {
             throw new UnsupportedOperationException();
         }
     }

     // ----------------------------------------------------------------
     // Positive tests for ImmutableBag (delegating creator)
     // ----------------------------------------------------------------

     @Test
     public void testDeserializeImmutableBagFromArray() throws Exception {
         String json = "[\"a\",\"b\",\"c\"]";
         ImmutableBag bag = MAPPER.readValue(json, ImmutableBag.class);
         assertNotNull(bag);
         assertEquals(3, bag.size());
         assertTrue(bag.contains("a"));
         assertTrue(bag.contains("b"));
         assertTrue(bag.contains("c"));
     }

     @Test
     public void testDeserializeEmptyArrayToImmutableBag() throws Exception {
         ImmutableBag bag = MAPPER.readValue("[]", ImmutableBag.class);
         assertNotNull(bag);
         assertTrue(bag.isEmpty());
     }

     @Test
     public void testDeserializeSingleElementToImmutableBag() throws Exception {
         ImmutableBag bag = MAPPER.readValue("[\"only\"]", ImmutableBag.class);
         assertEquals(1, bag.size());
         assertEquals("only", bag.iterator().next());
     }

     @Test
     public void testDeserializeLargeArrayToImmutableBag() throws Exception {
         int count = 100;
         String[] arr = new String[count];
         for (int i = 0; i < count; i++) {
             arr[i] = "item" + i;
         }
         String json = MAPPER.writer().writeValueAsString(arr);
         ImmutableBag bag = MAPPER.readValue(json, ImmutableBag.class);
         assertEquals(count, bag.size());
         // spot-check a few elements
         assertTrue(bag.contains("item0"));
         assertTrue(bag.contains("item50"));
         assertTrue(bag.contains("item99"));
     }

     @Test
     public void testDeserializeArrayWithNulValuesToImmutableBag() throws Exception {
         String json = "[\"a\",null,\"c\"]";
         ImmutableBag bag = MAPPER.readValue(json, ImmutableBag.class);
         assertEquals(3, bag.size());
         Iterator<String> it = bag.iterator();
         assertEquals("a", it.next());
         assertNull(it.next());
         assertEquals("c", it.next());
     }

     // ----------------------------------------------------------------
     // Regression: default-constructor collections must still work
     // ----------------------------------------------------------------

     @Test
     public void testArrayListDefaultConstructorStillWorks() throws Exception {
         List<String> list = MAPPER.readValue("[\"x\",\"y\"]", ArrayList.class);
         assertEquals(2, list.size());
         assertEquals("x", list.get(0));
         assertEquals("y", list.get(1));
     }

     @Test
     public void testHashSetDefaultConstructorStillWorks() throws Exception {
         Set<String> set = MAPPER.readValue("[\"foo\",\"bar\"]", HashSet.class);
         assertEquals(2, set.size());
         assertTrue(set.contains("foo"));
         assertTrue(set.contains("bar"));
     }

     @Test
     public void testLinkedListDefaultConstructorStillWorks() throws Exception {
         List<String> list = MAPPER.readValue("[\"p\",\"q\",\"r\"]", LinkedList.class);
         assertEquals(3, list.size());
         assertEquals("p", list.get(0));
     }

     // ----------------------------------------------------------------
     // Negative tests: erroneous inputs / missing creator
     // ----------------------------------------------------------------

     @Test(expected = MismatchedInputException.class)
     public void testNoDefaultConstructorAndNoDelegatingCreatorFails() throws Exception {
         // This class has no @JsonCreator and no default constructor → must fail
         MAPPER.readValue("[\"a\"]", NoCreatorNoDefaultCtorCollection.class);
     }

     // Verify that an empty array with a proper delegating creator does NOT throw
     @Test
     public void testEmptyArrayImmutableBagDoesNotThrow() throws Exception {
         try {
             ImmutableBag bag = MAPPER.readValue("[]", ImmutableBag.class);
             assertTrue(bag.isEmpty());
         } catch (Exception e) {
             fail("Should not have thrown: " + e.getMessage());
         }
     }

     // ----------------------------------------------------------------
     // Edge: single value (non-array) with ACCEPT_SINGLE_VALUE_AS_ARRAY enabled
     // ----------------------------------------------------------------

     @Test
     public void testSingleValueAsArrayForImmutableBag() throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
         ImmutableBag bag = mapper.readValue("\"single\"", ImmutableBag.class);
         assertEquals(1, bag.size());
         assertEquals("single", bag.iterator().next());
     }
 }
