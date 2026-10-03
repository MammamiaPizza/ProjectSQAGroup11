package org.apache.commons.collections.keyvalue;

 import static org.junit.Assert.*;
 import org.junit.Test;
 import java.io.*;

 /**
  * Tests for {@link MultiKey} targeting serialization round-trip preservation
  * of equals, hashCode, size, and key integrity (COLLECTIONS-266).
  */
 public class TestMultiKey {

     // --- Helper: serialize then deserialize an object ---
     @SuppressWarnings("unchecked")
     private static <T> T roundTrip(T obj) throws Exception {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos);
         oos.writeObject(obj);
         oos.close();
         ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
         ObjectInputStream ois = new ObjectInputStream(bais);
         return (T) ois.readObject();
     }

     // ----------------------------------------------------------------
     // Normal cases: constructors, size, getKey, getKeys
     // ----------------------------------------------------------------

     @Test
     public void testTwoKeyConstructorAndSize() {
         MultiKey mk = new MultiKey("a", "b");
         assertEquals(2, mk.size());
         assertEquals("a", mk.getKey(0));
         assertEquals("b", mk.getKey(1));
     }

     @Test
     public void testThreeKeyConstructorAndGetKeys() {
         MultiKey mk = new MultiKey(1, 2, 3);
         assertArrayEquals(new Object[]{1, 2, 3}, mk.getKeys());
         assertEquals(3, mk.size());
     }

     @Test
     public void testArrayConstructorClones() {
         Object[] input = {"x", "y", "z"};
         MultiKey mk = new MultiKey(input);
         // getKeys returns a clone
         Object[] keys = mk.getKeys();
         assertArrayEquals(input, keys);
         assertNotSame(input, keys);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testNullArrayThrows() {
         new MultiKey(null);
     }

     @Test
     public void testEmptyArrayConstructor() {
         MultiKey mk = new MultiKey(new Object[0]);
         assertEquals(0, mk.size());
         assertEquals(0, mk.getKeys().length);
     }

     // ----------------------------------------------------------------
     // equals and hashCode contract
     // ----------------------------------------------------------------

     @Test
     public void testEqualsSymmetryAndHashCodeConsistency() {
         MultiKey mk1 = new MultiKey("key", 42);
         MultiKey mk2 = new MultiKey("key", 42);
         MultiKey mk3 = new MultiKey("key", 99);

         assertTrue(mk1.equals(mk2));
         assertTrue(mk2.equals(mk1));
         assertEquals(mk1.hashCode(), mk2.hashCode());

         assertFalse(mk1.equals(mk3));
         assertFalse(mk3.equals(mk1));
     }

     @Test
     public void testEqualsWithSameReference() {
         MultiKey mk = new MultiKey("a", "b", "c");
         assertTrue(mk.equals(mk));
     }

     @Test
     public void testEqualsWithNullAndOtherType() {
         MultiKey mk = new MultiKey("a");
         assertFalse(mk.equals(null));
         assertFalse(mk.equals("not a MultiKey"));
     }

     // ----------------------------------------------------------------
     // Null key handling
     // ----------------------------------------------------------------

     @Test
     public void testNullKeyInConstructor() {
         MultiKey mk = new MultiKey(null, "b", null);
         assertEquals(3, mk.size());
         assertNull(mk.getKey(0));
         assertEquals("b", mk.getKey(1));
         assertNull(mk.getKey(2));
         // hash code should not NPE
         assertNotNull(mk.toString());
         int hc = mk.hashCode();
         // two MultiKeys with same null pattern are equal
         MultiKey mk2 = new MultiKey(null, "b", null);
         assertEquals(mk, mk2);
         assertEquals(hc, mk2.hashCode());
     }

     @Test(expected = IndexOutOfBoundsException.class)
     public void testGetKeyOutOfBounds() {
         new MultiKey("a", "b").getKey(2);
     }

     // ----------------------------------------------------------------
     // Serialization round-trip (COLLECTIONS-266)
     // ----------------------------------------------------------------

     @Test
     public void testSerializationPreservesEqualsAndHashCode() throws Exception {
         MultiKey original = new MultiKey("x", "y", "z");
         MultiKey deserialized = roundTrip(original);

         assertEquals(original, deserialized);
         assertEquals(deserialized, original);
         assertEquals(original.hashCode(), deserialized.hashCode());
         assertEquals(original.size(), deserialized.size());
     }

     @Test
     public void testSerializationWithNullKeys() throws Exception {
         MultiKey original = new MultiKey(null, "b", null);
         MultiKey deserialized = roundTrip(original);

         // Bug COLLECTIONS-266 would cause size/hash to be corrupted (null)
         assertNotNull("Deserialized MultiKey should not be null", deserialized);
         assertEquals("Size should be preserved after deserialization",
                      original.size(), deserialized.size());
         assertEquals("Keys should be equal after deserialization",
                      original, deserialized);
         assertEquals("Hash code should be preserved after deserialization",
                      original.hashCode(), deserialized.hashCode());
     }
 }
