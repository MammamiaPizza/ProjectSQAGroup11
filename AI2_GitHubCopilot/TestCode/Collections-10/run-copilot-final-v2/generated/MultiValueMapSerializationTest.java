package org.apache.commons.collections.map;

 import static org.junit.Assert.*;

 import java.io.*;
 import java.util.*;

 import org.apache.commons.collections.Factory;
 import org.junit.Test;

 public class MultiValueMapSerializationTest {

     @SuppressWarnings("unchecked")
     private <T> T roundTrip(T obj) throws IOException, ClassNotFoundException {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(bos);
         oos.writeObject(obj);
         oos.close();

         ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
         ObjectInputStream ois = new ObjectInputStream(bis);
         T result = (T) ois.readObject();
         ois.close();
         return result;
     }

     @Test
     public void testEmptyMapRoundtrip() throws Exception {
         MultiValueMap map = new MultiValueMap();
         MultiValueMap deserialized = roundTrip(map);
         assertNotNull(deserialized);
         assertTrue(map.equals(deserialized));
         assertEquals(0, deserialized.totalSize());
         assertTrue(deserialized.isEmpty());
     }

     @Test
     public void testSingleKeyMultipleValues() throws Exception {
         MultiValueMap map = new MultiValueMap();
         map.put("K", "V1");
         map.put("K", "V2");
         map.put("K", "V3");
         MultiValueMap deserialized = roundTrip(map);
         assertNotNull(deserialized);
         assertTrue(map.equals(deserialized));
         assertEquals(3, deserialized.totalSize());
         Collection col = deserialized.getCollection("K");
         assertTrue(col instanceof ArrayList);
         assertTrue(col.contains("V1"));
         assertTrue(col.contains("V2"));
         assertTrue(col.contains("V3"));
     }

     @Test
     public void testDecorateWithHashSetClass() throws Exception {
         Map base = new HashMap();
         MultiValueMap map = MultiValueMap.decorate(base, HashSet.class);
         map.put("a", 1);
         map.put("a", 2);
         map.put("b", 3);
         MultiValueMap deserialized = roundTrip(map);
         assertNotNull(deserialized);
         assertTrue(map.equals(deserialized));
         assertEquals(3, deserialized.totalSize());
         Collection colA = deserialized.getCollection("a");
         assertTrue(colA instanceof HashSet);
         // ensure factory survived serialization
         deserialized.put("c", 4);
         Collection colC = deserialized.getCollection("c");
         assertTrue(colC instanceof HashSet);
         assertTrue(colC.contains(4));
     }

     @Test
     public void testDecorateWithCustomFactory() throws Exception {
         Factory linkedListFactory = new Factory() {
             public Object create() {
                 return new LinkedList();
             }
         };
         MultiValueMap map = MultiValueMap.decorate(new HashMap(), linkedListFactory);
         map.put("x", "X1");
         map.put("x", "X2");
         MultiValueMap deserialized = roundTrip(map);
         assertNotNull(deserialized);
         assertTrue(map.equals(deserialized));
         Collection colX = deserialized.getCollection("x");
         assertTrue(colX instanceof LinkedList);
         deserialized.put("y", "Y1");
         Collection colY = deserialized.getCollection("y");
         assertTrue(colY instanceof LinkedList);
     }

     @Test
     public void testPutAllFromMultiMapSerialization() throws Exception {
         MultiValueMap source = new MultiValueMap();
         source.put("k1", "a");
         source.put("k1", "b");
         source.put("k2", "c");
         MultiValueMap target = new MultiValueMap();
         target.putAll(source);
         MultiValueMap deserialized = roundTrip(target);
         assertNotNull(deserialized);
         assertTrue(target.equals(deserialized));
         assertEquals(3, deserialized.totalSize());
         assertTrue(deserialized.containsValue("k1", "a"));
         assertTrue(deserialized.containsValue("k1", "b"));
         assertTrue(deserialized.containsValue("k2", "c"));
     }

     @Test
     public void testTotalSizeBoundaryAfterDeserialization() throws Exception {
         MultiValueMap m0 = roundTrip(new MultiValueMap());
         MultiValueMap m1 = new MultiValueMap();
         m1.put("x", 1);
         m1 = roundTrip(m1);
         MultiValueMap mM = new MultiValueMap();
         mM.put("a", 1);
         mM.put("a", 2);
         mM.put("b", 3);
         mM.put("b", 4);
         mM.put("c", 5);
         mM.put("c", 6);
         mM.put("c", 7);
         mM = roundTrip(mM);
         assertEquals(0, m0.totalSize());
         assertEquals(1, m1.totalSize());
         assertEquals(7, mM.totalSize());
     }

     @Test
     public void testRemoveMappingSerialization() throws Exception {
         MultiValueMap map = new MultiValueMap();
         map.put("key", 1);
         map.put("key", 2);
         map.put("key", 3);
         Object removed = map.removeMapping("key", 2);
         assertEquals(Integer.valueOf(2), removed);
         MultiValueMap deserialized = roundTrip(map);
         assertNotNull(deserialized);
         assertTrue(map.equals(deserialized));
         assertFalse(deserialized.containsValue("key", 2));
         assertEquals(2, deserialized.totalSize());
     }

     @Test
     public void testPutAfterDeserializationUsesFactory() throws Exception {
         MultiValueMap map = MultiValueMap.decorate(new HashMap(), HashSet.class);
         map.put("pre", "value");
         MultiValueMap deserialized = roundTrip(map);
         deserialized.put("post", "newValue");
         Collection col = deserialized.getCollection("post");
         assertNotNull(col);
         assertTrue(col instanceof HashSet);
         assertTrue(col.contains("newValue"));
     }

     @Test
     public void testMultipleKeysSerialization() throws Exception {
         MultiValueMap map = new MultiValueMap();
         map.put("A", "a1");
         map.put("A", "a2");
         map.put("B", "b1");
         map.put("B", "b2");
         map.put("B", "b3");
         map.put("C", "c1");
         MultiValueMap deserialized = roundTrip(map);
         assertNotNull(deserialized);
         assertTrue(map.equals(deserialized));
         assertEquals(6, deserialized.totalSize());
         assertEquals(2, deserialized.getCollection("A").size());
         assertEquals(3, deserialized.getCollection("B").size());
         assertEquals(1, deserialized.getCollection("C").size());
     }

     @Test
     public void testSerializationIdempotent() throws Exception {
         MultiValueMap map = new MultiValueMap();
         map.put("k1", "v1");
         map.put("k2", "v2");
         map.put("k1", "v3");
         map = roundTrip(map);
         assertEquals(3, map.totalSize());
         map.put("k2", "v4");
         assertEquals(4, map.totalSize());
         map = roundTrip(map);
         assertEquals(4, map.totalSize());
         assertTrue(map.containsValue("k1", "v1"));
         assertTrue(map.containsValue("k1", "v3"));
         assertTrue(map.containsValue("k2", "v2"));
         assertTrue(map.containsValue("k2", "v4"));
     }

     @Test
     public void testClearAfterDeserialization() throws Exception {
         MultiValueMap map = new MultiValueMap();
         map.put("temp", "data");
         map = roundTrip(map);
         map.clear();
         assertTrue(map.isEmpty());
         assertEquals(0, map.totalSize());
         map.put("new", "value");
         assertEquals(1, map.totalSize());
         assertTrue(map.containsValue("new", "value"));
     }

     @Test
     public void testPutAllCollectionSerialization() throws Exception {
         MultiValueMap map = new MultiValueMap();
         List<String> values = Arrays.asList("x", "y", "z");
         map.putAll("key", values);
         assertEquals(3, map.totalSize());
         MultiValueMap deserialized = roundTrip(map);
         assertNotNull(deserialized);
         assertTrue(map.equals(deserialized));
         Collection col = deserialized.getCollection("key");
         assertEquals(3, col.size());
         assertTrue(col.contains("x"));
         assertTrue(col.contains("y"));
         assertTrue(col.contains("z"));
     }
 }
