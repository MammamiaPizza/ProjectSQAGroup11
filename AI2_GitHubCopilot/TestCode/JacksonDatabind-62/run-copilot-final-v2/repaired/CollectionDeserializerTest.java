package com.fasterxml.jackson.databind.deser.std;

 import static org.junit.Assert.*;
 import org.junit.Test;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.DeserializationFeature;
 import com.fasterxml.jackson.databind.JsonMappingException;
 import com.fasterxml.jackson.core.type.TypeReference;
 import java.util.*;

 /**
  * Tests for {@link CollectionDeserializer} focusing on deserialization of
  * unmodifiable collections (which lack default constructors) as reported in
  * <a href="https://github.com/FasterXML/jackson-databind/issues/1392">#1392</a>.
  * These tests should NOT throw {@code IllegalStateException: No default constructor}.
  */
 public class CollectionDeserializerTest {
     private final ObjectMapper mapper = new ObjectMapper();

     // --- Normal mutable collections (regression) ---
     @Test
     public void testMutableArrayList() throws Exception {
         List<Integer> result = mapper.readValue("[1,2,3]",
                 new TypeReference<ArrayList<Integer>>() {});
         assertNotNull(result);
         assertEquals(3, result.size());
         assertEquals((Integer) 1, result.get(0));
     }

     @Test
     public void testMutableHashSet() throws Exception {
         Set<String> result = mapper.readValue("[\"a\",\"b\"]",
                 new TypeReference<HashSet<String>>() {});
         assertNotNull(result);
         assertEquals(2, result.size());
         assertTrue(result instanceof HashSet);
     }

     @Test
     public void testCollectionInterface() throws Exception {
         Collection<Integer> result = mapper.readValue("[10, 20]",
                 new TypeReference<Collection<Integer>>() {});
         assertNotNull(result);
         assertEquals(2, result.size());
     }

     // --- Unmodifiable collections (the bug) ---
     @Test
     public void testUnmodifiableSetMultipleElements() throws Exception {
         Class<?> cls = Collections.unmodifiableSet(new HashSet<String>()).getClass();
         Object result = mapper.readValue("[\"x\",\"y\",\"z\"]", cls);
         assertNotNull(result);
         assertTrue(result instanceof Set);
         assertEquals(3, ((Set<?>) result).size());
     }

     @Test
     public void testUnmodifiableSetEmpty() throws Exception {
         Class<?> cls = Collections.unmodifiableSet(new HashSet<String>()).getClass();
         Object result = mapper.readValue("[]", cls);
         assertNotNull(result);
         assertTrue(result instanceof Set);
         assertTrue(((Set<?>) result).isEmpty());
     }

     @Test
     public void testUnmodifiableSetSingleElement() throws Exception {
         Class<?> cls = Collections.unmodifiableSet(new HashSet<String>()).getClass();
         Object result = mapper.readValue("[\"only\"]", cls);
         assertNotNull(result);
         Set<?> set = (Set<?>) result;
         assertEquals(1, set.size());
         assertTrue(set.contains("only"));
     }

     @Test
     public void testUnmodifiableList() throws Exception {
         Class<?> cls = Collections.unmodifiableList(new ArrayList<String>()).getClass();
         Object result = mapper.readValue("[\"a\",\"b\",\"c\"]", cls);
         assertNotNull(result);
         assertTrue(result instanceof List);
         assertEquals(3, ((List<?>) result).size());
     }

     @Test
     public void testUnmodifiableSortedSet() throws Exception {
         Class<?> cls = Collections.unmodifiableSortedSet(new TreeSet<String>()).getClass();
         Object result = mapper.readValue("[\"c\",\"a\",\"b\"]", cls);
         assertNotNull(result);
         assertTrue(result instanceof SortedSet);
         assertEquals(3, ((Set<?>) result).size());
     }

     // --- Boundary / error cases ---
     @Test
     public void testNullElementsInArray() throws Exception {
         List<String> result = mapper.readValue("[\"hello\", null, \"world\"]",
                 new TypeReference<ArrayList<String>>() {});
         assertNotNull(result);
         assertEquals(3, result.size());
         assertEquals("hello", result.get(0));
         assertNull(result.get(1));
     }

     @Test(expected = JsonMappingException.class)
     public void testNonArrayTokenWithoutAcceptSingle() throws Exception {
         ObjectMapper strictMapper = new ObjectMapper();
         strictMapper.disable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
         strictMapper.readValue("42", new TypeReference<ArrayList<Integer>>() {});
     }

     @Test
     public void testNonArrayTokenWithAcceptSingle() throws Exception {
         ObjectMapper lenientMapper = new ObjectMapper();
         lenientMapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
         List<Integer> result = lenientMapper.readValue("77",
                 new TypeReference<ArrayList<Integer>>() {});
         assertNotNull(result);
         assertEquals(1, result.size());
         assertEquals((Integer) 77, result.get(0));
     }
 }
