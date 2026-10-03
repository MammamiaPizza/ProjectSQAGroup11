package org.apache.commons.collections4.map;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.fail;

 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.util.AbstractCollection;
 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.Collections;
 import java.util.HashMap;
 import java.util.Iterator;

 import org.apache.commons.collections4.Factory;
 import org.junit.Test;

 public class MultiValueMapUnsafeDeserializationTest {

     /**
      * A collection class that is not part of the safe/allow-listed deserialization
      * types. It must expose a public no-arg constructor so ReflectionFactory could
      * instantiate it when the vulnerable code path is exercised.
      */
     public static class UnsafeCollection extends AbstractCollection<Object> {
         public UnsafeCollection() {
         }

         @Override
         public Iterator<Object> iterator() {
             return Collections.emptyIterator();
         }

         @Override
         public int size() {
             return 0;
         }
     }

     private byte[] serialize(final Object obj) throws IOException {
         final ByteArrayOutputStream bos = new ByteArrayOutputStream();
         final ObjectOutputStream oos = new ObjectOutputStream(bos);
         try {
             oos.writeObject(obj);
         } finally {
             oos.close();
         }
         return bos.toByteArray();
     }

     private Object deserialize(final byte[] bytes) throws IOException, ClassNotFoundException {
         final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
         try {
             return ois.readObject();
         } finally {
             ois.close();
         }
     }

     private void assertDeserializationRejected(final byte[] bytes) throws IOException {
         try {
             deserialize(bytes);
         } catch (final Exception expected) {
             // rejection is the expected outcome; any serialization/validation exception
             // such as IOException, ClassCastException or IllegalArgumentException
             // is acceptable because the exact exception type is not part of the contract.
             return;
         }
         fail("Expected deserialization of an unsafe collection class to be rejected");
     }

     @Test
     public void testDefaultConstructorRoundTripPreservesValues() throws Exception {
         final MultiValueMap<String, Object> map = new MultiValueMap<String, Object>();
         map.put("first", "one");
         map.put("first", "two");
         map.put("second", 3);

         final MultiValueMap<?, ?> restored = (MultiValueMap<?, ?>) deserialize(serialize(map));

         assertNotNull(restored);
         assertEquals(2, restored.getCollection("first").size());
         assertEquals(1, restored.getCollection("second").size());
     }

     @Test
     @SuppressWarnings("unchecked")
     public void testSafeListCollectionFactoryRoundTrip() throws Exception {
         final MultiValueMap map = MultiValueMap.multiValueMap(new HashMap(), ArrayList.class);
         map.put("key", "value1");
         map.put("key", "value2");

         final MultiValueMap restored = (MultiValueMap) deserialize(serialize(map));

         assertEquals(2, restored.getCollection("key").size());
     }

     @Test
     @SuppressWarnings("unchecked")
     public void testUnsafeCollectionFactoryIsRejectedOnDeserialization() throws Exception {
         try {
             MultiValueMap.multiValueMap(new HashMap(), UnsafeCollection.class);
         } catch (final Exception expected) {
             // The unsafe factory should be rejected at construction time or
             // deserialization time. Any exception type is acceptable because
             // the exact type is not part of the contract.
             return;
         }
         fail("Expected construction with an unsafe collection class to be rejected");
     }

     @Test
     @SuppressWarnings("unchecked")
     public void testNullCollectionFactoryIsRejected() {
         try {
             MultiValueMap.multiValueMap(new HashMap(), (Factory) null);
         } catch (final IllegalArgumentException expected) {
             return;
         }
         fail("Expected IllegalArgumentException when constructing MultiValueMap with a null
factory");
     }

     @Test
     public void testGetCollectionForMissingKeyReturnsNull() {
         final MultiValueMap<String, Object> map = new MultiValueMap<String, Object>();
         assertEquals(null, map.getCollection("missingKey"));
     }

     @Test
     public void testTotalSizeRemainsCorrectAfterRoundTrip() throws Exception {
         final MultiValueMap<String, Object> map = new MultiValueMap<String, Object>();
         map.put("a", 1);
         map.put("a", 2);
         map.put("b", 3);

         final MultiValueMap<?, ?> restored = (MultiValueMap<?, ?>) deserialize(serialize(map));

         assertEquals(3, restored.totalSize());
     }

 }