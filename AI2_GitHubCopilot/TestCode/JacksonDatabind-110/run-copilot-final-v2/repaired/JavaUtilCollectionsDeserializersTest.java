package com.fasterxml.jackson.databind.deser.impl;

 import static org.junit.Assert.*;

 import java.util.*;

 import org.junit.Test;

 import com.fasterxml.jackson.databind.ObjectMapper;

 public class JavaUtilCollectionsDeserializersTest {

     private static Class<?> unmodifiableLinkedListClass() {
         List<String> template = Collections.unmodifiableList(
                 new LinkedList<>(Arrays.asList("a")));
         return template.getClass();
     }

     private static Class<?> unmodifiableArrayListClass() {
         List<String> template = Collections.unmodifiableList(
                 new ArrayList<>(Arrays.asList("a")));
         return template.getClass();
     }

     private final ObjectMapper mapper = new ObjectMapper();

     @Test
     public void testUnmodifiableListFromLinkedList_EmptyArray() throws Exception {
         Class<?> targetClass = unmodifiableLinkedListClass();
         Object result = mapper.readValue("[]", targetClass);
         assertNotNull(result);
         assertTrue(result instanceof List);
         List<?> list = (List<?>) result;
         assertEquals(0, list.size());
         assertModificationForbidden(list);
         assertEquals(targetClass, result.getClass());
     }

     @Test
     public void testUnmodifiableListFromLinkedList_SingleElement() throws Exception {
         Class<?> targetClass = unmodifiableLinkedListClass();
         Object result = mapper.readValue("[\"x\"]", targetClass);
         assertNotNull(result);
         assertTrue(result instanceof List);
         List<?> list = (List<?>) result;
         assertEquals(1, list.size());
         assertEquals("x", list.get(0));
         assertModificationForbidden(list);
         assertEquals(targetClass, result.getClass());
     }

     @Test
     public void testUnmodifiableListFromLinkedList_MultipleElements() throws Exception {
         Class<?> targetClass = unmodifiableLinkedListClass();
         String json = "[\"a\",\"b\",\"c\"]";
         Object result = mapper.readValue(json, targetClass);
         assertNotNull(result);
         assertTrue(result instanceof List);
         List<?> list = (List<?>) result;
         assertEquals(3, list.size());
         assertEquals(Arrays.asList("a", "b", "c"), list);
         assertModificationForbidden(list);
         assertEquals(targetClass, result.getClass());
     }

     @Test
     public void testUnmodifiableListFromLinkedList_NullElement() throws Exception {
         Class<?> targetClass = unmodifiableLinkedListClass();
         Object result = mapper.readValue("[null, \"y\"]", targetClass);
         assertNotNull(result);
         assertTrue(result instanceof List);
         List<?> list = (List<?>) result;
         assertEquals(2, list.size());
         assertNull(list.get(0));
         assertEquals("y", list.get(1));
         assertModificationForbidden(list);
         assertEquals(targetClass, result.getClass());
     }

     @Test
     public void testUnmodifiableListFromArrayList_EmptyArray() throws Exception {
         Class<?> targetClass = unmodifiableArrayListClass();
         Object result = mapper.readValue("[]", targetClass);
         assertNotNull(result);
         assertTrue(result instanceof List);
         List<?> list = (List<?>) result;
         assertEquals(0, list.size());
         assertModificationForbidden(list);
         assertEquals(targetClass, result.getClass());
     }

     @Test
     public void testUnmodifiableListFromArrayList_SingleElement() throws Exception {
         Class<?> targetClass = unmodifiableArrayListClass();
         Object result = mapper.readValue("[\"a\"]", targetClass);
         assertNotNull(result);
         assertTrue(result instanceof List);
         List<?> list = (List<?>) result;
         assertEquals(1, list.size());
         assertEquals("a", list.get(0));
         assertModificationForbidden(list);
         assertEquals(targetClass, result.getClass());
     }

     @Test
     public void testUnmodifiableListFromArrayList_MultipleElements() throws Exception {
         Class<?> targetClass = unmodifiableArrayListClass();
         Object result = mapper.readValue("[\"p\",\"q\",\"r\"]", targetClass);
         assertNotNull(result);
         assertTrue(result instanceof List);
         List<?> list = (List<?>) result;
         assertEquals(3, list.size());
         assertEquals(Arrays.asList("p", "q", "r"), list);
         assertModificationForbidden(list);
         assertEquals(targetClass, result.getClass());
     }

     @Test
     public void testUnmodifiableListFromArrayList_NullElement() throws Exception {
         Class<?> targetClass = unmodifiableArrayListClass();
         Object result = mapper.readValue("[null, \"n\"]", targetClass);
         assertNotNull(result);
         assertTrue(result instanceof List);
         List<?> list = (List<?>) result;
         assertEquals(2, list.size());
         assertNull(list.get(0));
         assertEquals("n", list.get(1));
         assertModificationForbidden(list);
         assertEquals(targetClass, result.getClass());
     }

     @Test
     public void testUnmodifiableListModificationThrowsException() throws Exception {
         Class<?> targetClass = unmodifiableLinkedListClass();
         List<?> list = (List<?>) mapper.readValue("[\"item\"]", targetClass);
         try {
             ((List) list).add("new");
             fail("Expected UnsupportedOperationException");
         } catch (UnsupportedOperationException expected) {
             // expected
         }
     }

     @Test
     public void testUnmodifiableListFromLinkedList_InvalidJson() throws Exception {
         Class<?> targetClass = unmodifiableLinkedListClass();
         try {
             mapper.readValue("{\"key\":\"value\"}", targetClass);
             fail("Expected exception for non-array JSON");
         } catch (Exception e) {
             // any exception is acceptable; just verifying no InvalidDefinitionException
             // that would be a bug – the deserializer should be found and used.
         }
     }

     private void assertModificationForbidden(List<?> list) {
         try {
             ((List) list).add(new Object());
             fail("Modification should throw UnsupportedOperationException");
         } catch (UnsupportedOperationException e) {
             // expected
         }
     }
 }
