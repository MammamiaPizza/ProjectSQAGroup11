package org.mockito.internal.util.reflection;

 import static org.junit.Assert.assertEquals;

 import java.lang.reflect.Field;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;

 import org.junit.Before;
 import org.junit.Test;

 public class GenericMasterTest {

     private GenericMaster master;

     // Fields used for reflection-based tests
     List<String> listString;
     List<Set<String>> listSetString;
     String stringField;
     @SuppressWarnings("rawtypes")
     List rawList;
     List<? extends Number> listWildcardExtends;
     Map<String, Integer> mapStringInteger;
     List<?> listUnboundedWildcard;
     List<List<List<String>>> deepNested;
     int[] intArray;

     static class GenericHolder<T> {
         T value;
     }

     @Before
     public void setUp() {
         master = new GenericMaster();
     }

     private Class<?> getGenericType(String fieldName) throws Exception {
         Field field = this.getClass().getDeclaredField(fieldName);
         return master.getGenericType(field);
     }

     @Test
     public void shouldReturnListClassForParameterizedList() throws Exception {
         assertEquals(List.class, getGenericType("listString"));
     }

     @Test
     public void shouldDealWithNestedGenerics() throws Exception {
         assertEquals(List.class, getGenericType("listSetString"));
     }

     @Test
     public void shouldReturnObjectClassForNonGenericField() throws Exception {
         assertEquals(Object.class, getGenericType("stringField"));
     }

     @Test
     public void shouldReturnObjectClassForRawTypeField() throws Exception {
         assertEquals(Object.class, getGenericType("rawList"));
     }

     @Test
     public void shouldReturnListClassForWildcardExtendsField() throws Exception {
         assertEquals(List.class, getGenericType("listWildcardExtends"));
     }

     @Test
     public void shouldReturnMapClassForMapParameterized() throws Exception {
         assertEquals(Map.class, getGenericType("mapStringInteger"));
     }

     @Test
     public void shouldReturnListClassForUnboundedWildcard() throws Exception {
         assertEquals(List.class, getGenericType("listUnboundedWildcard"));
     }

     @Test
     public void shouldReturnListClassForDeepNestedGenerics() throws Exception {
         assertEquals(List.class, getGenericType("deepNested"));
     }

     @Test
     public void shouldReturnObjectClassForArrayField() throws Exception {
         assertEquals(Object.class, getGenericType("intArray"));
     }

     @Test
     public void shouldReturnObjectClassForTypeVariableField() throws Exception {
         Field field = GenericHolder.class.getDeclaredField("value");
         assertEquals(Object.class, master.getGenericType(field));
     }
 }
