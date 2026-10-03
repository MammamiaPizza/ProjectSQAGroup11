package com.fasterxml.jackson.databind.type;

 import static org.junit.Assert.*;
 import org.junit.Test;
 import com.fasterxml.jackson.databind.JavaType;
 import java.util.*;

 @SuppressWarnings("deprecation")
 public class MapCollectionLikeTypeNarrowingTest {

     /**
      * Key class that mirrors the CompoundKey from bug #1384.
      * The bug caused key-type information to be lost after narrowing,
      * leading to JsonMappingException during key deserialization.
      */
     public static class CompoundKey {
         public final int id;
         public CompoundKey(int id) { this.id = id; }
         @Override
         public boolean equals(Object o) {
             if (!(o instanceof CompoundKey)) return false;
             return ((CompoundKey) o).id == this.id;
         }
         @Override
         public int hashCode() { return id; }
     }

     private static JavaType type(Class<?> clz) {
         return SimpleType.constructUnsafe(clz);
     }

     // ---- Core bug-regression: Map narrowing must preserve key type ----

     @Test
     public void testNarrowMapPreservesKeyType() {
         MapLikeType mapType = MapLikeType.construct(
                 HashMap.class, type(CompoundKey.class), type(String.class));

         JavaType narrowed = mapType.forcedNarrowBy(LinkedHashMap.class);

         assertTrue("narrowed map should still be MapLikeType", narrowed.isMapLikeType());
         assertNotNull("key type must not be null after narrowing", narrowed.getKeyType());
         assertEquals("key type raw class must be CompoundKey",
                 CompoundKey.class, narrowed.getKeyType().getRawClass());
     }

     @Test
     public void testNarrowMapPreservesValueType() {
         MapLikeType mapType = MapLikeType.construct(
                 HashMap.class, type(CompoundKey.class), type(String.class));

         JavaType narrowed = mapType.forcedNarrowBy(LinkedHashMap.class);

         assertNotNull("content type must not be null after narrowing", narrowed.getContentType());
         assertEquals("value type raw class must be String",
                 String.class, narrowed.getContentType().getRawClass());
     }

     @Test
     public void testNarrowMapIsMapLikeType() {
         MapLikeType mapType = MapLikeType.construct(
                 HashMap.class, type(Integer.class), type(Double.class));

         JavaType narrowed = mapType.forcedNarrowBy(TreeMap.class);

         assertTrue("isMapLikeType must remain true after narrowing", narrowed.isMapLikeType());
     }

     // ---- Collection narrowing ----

     @Test
     public void testNarrowCollectionPreservesElementType() {
         CollectionLikeType collType = CollectionLikeType.construct(
                 ArrayList.class, type(String.class));

         JavaType narrowed = collType.forcedNarrowBy(LinkedList.class);

         assertNotNull("content type must not be null after narrowing", narrowed.getContentType());
         assertEquals("element type raw class must be String",
                 String.class, narrowed.getContentType().getRawClass());
     }

     @Test
     public void testNarrowCollectionIsCollectionLikeType() {
         CollectionLikeType collType = CollectionLikeType.construct(
                 HashSet.class, type(Long.class));

         JavaType narrowed = collType.forcedNarrowBy(TreeSet.class);

         assertTrue("isCollectionLikeType must remain true after narrowing",
                 narrowed.isCollectionLikeType());
     }

     // ---- Handler preservation ----

     @Test
     public void testForcedNarrowByPreservesValueHandler() {
         Object handler = new Object();
         JavaType baseType = MapLikeType.construct(
                 HashMap.class, type(String.class), type(String.class));
         MapLikeType mapType = (MapLikeType) baseType.withValueHandler(handler);

         JavaType narrowed = mapType.forcedNarrowBy(LinkedHashMap.class);

         assertSame("value handler must survive forcedNarrowBy", handler,
narrowed.getValueHandler());
     }

     @Test
     public void testForcedNarrowByPreservesTypeHandler() {
         Object handler = new Object();
         JavaType baseType = MapLikeType.construct(
                 HashMap.class, type(String.class), type(String.class));
         MapLikeType mapType = (MapLikeType) baseType.withTypeHandler(handler);

         JavaType narrowed = mapType.forcedNarrowBy(LinkedHashMap.class);

         assertSame("type handler must survive forcedNarrowBy", handler, narrowed.getTypeHandler());
     }

     @Test
     public void testNarrowMapPreservesKeyTypeHandler() {
         Object handler = new Object();
         JavaType baseType = MapLikeType.construct(
                 HashMap.class, type(CompoundKey.class), type(String.class));
         MapLikeType mapType = (MapLikeType) baseType.withKeyTypeHandler(handler);

         JavaType narrowed = mapType.forcedNarrowBy(LinkedHashMap.class);

         assertEquals("key class must still be CompoundKey",
                 CompoundKey.class, narrowed.getKeyType().getRawClass());
         assertSame("key type handler must survive narrowing",
                 handler, narrowed.getKeyType().getTypeHandler());
     }

     // ---- Boundary and edge cases ----

     @Test
     public void testForcedNarrowBySameClassReturnsSame() {
         MapLikeType mapType = MapLikeType.construct(
                 HashMap.class, type(String.class), type(Integer.class));

         JavaType result = mapType.forcedNarrowBy(HashMap.class);

         assertSame("narrowing to same class should return identical instance", mapType, result);
     }

     @Test
     public void testNarrowMapWithObjectKeyBoundary() {
         MapLikeType mapType = MapLikeType.construct(
                 HashMap.class, type(Object.class), type(String.class));

         JavaType narrowed = mapType.forcedNarrowBy(LinkedHashMap.class);

         assertNotNull("key type must not be null for Object key", narrowed.getKeyType());
         assertTrue("key should still be MapLikeType", narrowed.isMapLikeType());
         assertEquals("key type must be Object",
                 Object.class, narrowed.getKeyType().getRawClass());
         assertEquals("value type must be String",
                 String.class, narrowed.getContentType().getRawClass());
     }

     @Test
     public void testNarrowMapPreservesStaticTyping() {
         MapLikeType mapType = MapLikeType.construct(
                 HashMap.class, type(CompoundKey.class), type(String.class));
         MapLikeType staticMapType = mapType.withStaticTyping();

         JavaType narrowed = staticMapType.forcedNarrowBy(LinkedHashMap.class);

         assertEquals("key type must survive static-typing narrowing",
                 CompoundKey.class, narrowed.getKeyType().getRawClass());
         assertEquals("value type must survive static-typing narrowing",
                 String.class, narrowed.getContentType().getRawClass());
     }

     // ---- upgradeFrom path ----

     @Test
     public void testUpgradeFromMapPreservesTypes() {
         JavaType base = SimpleType.constructUnsafe(Hashtable.class);
         MapLikeType mapType = MapLikeType.upgradeFrom(
                 base, type(CompoundKey.class), type(String.class));

         assertEquals("key type raw class must be CompoundKey after upgradeFrom",
                 CompoundKey.class, mapType.getKeyType().getRawClass());
         assertEquals("value type raw class must be String after upgradeFrom",
                 String.class, mapType.getContentType().getRawClass());
     }
 }