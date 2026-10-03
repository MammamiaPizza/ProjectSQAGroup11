package org.apache.commons.collections4.keyvalue;

  import static org.junit.Assert.assertEquals;
  import static org.junit.Assert.assertFalse;
  import static org.junit.Assert.assertNotEquals;
  import static org.junit.Assert.assertTrue;
  import static org.junit.Assert.fail;

  import java.io.ByteArrayInputStream;
  import java.io.ByteArrayOutputStream;
  import java.io.ObjectInputStream;
  import java.io.ObjectOutputStream;
  import java.io.Serializable;

  import org.junit.Test;

  /**
   * Tests for {@link MultiKey}, with particular focus on serialization
   * round-trip integrity and the recalculation of the cached hash code after
   * deserialization, including for derived classes.
   */
  public class MultiKeyTest {

      /** A derived class used to expose the deserialization behavior of subclasses. */
      public static class DerivedMultiKey<K> extends MultiKey<K> {
          private static final long serialVersionUID = 1L;

          public DerivedMultiKey(final K key1, final K key2, final K key3) {
              super(key1, key2, key3);
          }
      }

      private static Object roundTrip(final Object obj) throws Exception {
          final ByteArrayOutputStream baos = new ByteArrayOutputStream();
          final ObjectOutputStream oos = new ObjectOutputStream(baos);
          oos.writeObject(obj);
          oos.close();

          final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
          final ObjectInputStream ois = new ObjectInputStream(bais);
          final Object result = ois.readObject();
          ois.close();
          return result;
      }

      //-----------------------------------------------------------------------
      // Serialization round-trip integrity

      @Test
      public void testSerializationRoundTripPreservesKeysAndSize() throws Exception {
          final MultiKey<String> original = new MultiKey<String>("a", "b");
          final MultiKey<String> deserialized = (MultiKey<String>) roundTrip(original);

          assertEquals(original.size(), deserialized.size());
          assertEquals("a", deserialized.getKey(0));
          assertEquals("b", deserialized.getKey(1));
          assertEquals(original, deserialized);
      }

      @Test
      public void testSerializationRoundTripPreservesHashCode() throws Exception {
          final MultiKey<String> original = new MultiKey<String>("a", "b");
          final MultiKey<String> deserialized = (MultiKey<String>) roundTrip(original);

          assertEquals(original.hashCode(), deserialized.hashCode());
          assertEquals(new MultiKey<String>("a", "b").hashCode(), deserialized.hashCode());
      }

      @Test
      public void testFiveKeySerializationRoundTrip() throws Exception {
          final MultiKey<String> original =
                  new MultiKey<String>("a", "b", "c", "d", "e");
          final MultiKey<String> deserialized = (MultiKey<String>) roundTrip(original);

          assertEquals(5, deserialized.size());
          assertEquals(original, deserialized);
          assertEquals(original.hashCode(), deserialized.hashCode());
      }

      @Test
      public void testEmptyArraySerializationRoundTrip() throws Exception {
          final MultiKey<String> original = new MultiKey<String>(new String[0]);
          final MultiKey<String> deserialized = (MultiKey<String>) roundTrip(original);

          assertEquals(0, deserialized.size());
          assertEquals(original, deserialized);
          assertEquals(original.hashCode(), deserialized.hashCode());
      }

      //-----------------------------------------------------------------------
      // Fault-related behavior: derived class hash code after deserialization

      @Test
      public void testEqualsAndHashCodeAfterSerializationOfDerivedClass() throws Exception {
          final MultiKey<String> original = new DerivedMultiKey<String>("a", "b", "c");
          final MultiKey<String> deserialized = (MultiKey<String>) roundTrip(original);

          assertTrue(original.equals(deserialized));
          assertTrue(deserialized.equals(original));
          assertEquals("state (size) must be preserved after deserialization",
                  3, deserialized.size());
          assertEquals("cached hash code must be recalculated after deserialization",
                  original.hashCode(), deserialized.hashCode());
          assertEquals(new MultiKey<String>("a", "b", "c").hashCode(),
                  deserialized.hashCode());
      }

      @Test
      public void testDerivedClassHashCodeMatchesPlainMultiKey() throws Exception {
          final MultiKey<String> derived = new DerivedMultiKey<String>("a", "b", "c");
          final MultiKey<String> plain = new MultiKey<String>("a", "b", "c");

          assertEquals(derived, plain);
          assertEquals(plain.hashCode(), derived.hashCode());
      }

      //-----------------------------------------------------------------------
      // Normal and boundary behavior

      @Test
      public void testSingleKeyBoundary() {
          final MultiKey<String> key = new MultiKey<String>(new String[] { "solo" });

          assertEquals(1, key.size());
          assertEquals("solo", key.getKey(0));
          assertEquals("MultiKey[solo]", key.toString());
      }

      @Test
      public void testGetKeysReturnsDefensiveCopy() {
          final MultiKey<String> key = new MultiKey<String>("x", "y");
          final String[] keys = key.getKeys();
          assertEquals(2, keys.length);
          assertEquals("x", keys[0]);
          assertEquals("y", keys[1]);
          assertEquals("x", key.getKey(0));
          assertEquals("y", key.getKey(1));
      }

      @Test
      public void testEqualsWithDifferentKeyCount() {
          final MultiKey<String> two = new MultiKey<String>("a", "b");
          final MultiKey<String> three = new MultiKey<String>("a", "b", "c");

          assertFalse(two.equals(three));
          assertFalse(three.equals(two));
          assertNotEquals(two.hashCode(), three.hashCode());
      }

      @Test
      public void testEqualsWithSameKeyCountButDifferentKeys() {
          final MultiKey<String> ab = new MultiKey<String>("a", "b");
          final MultiKey<String> ac = new MultiKey<String>("a", "c");

          assertFalse(ab.equals(ac));
          assertNotEquals(ab.hashCode(), ac.hashCode());
      }

      @Test
      public void testEqualsWithNonMultiKeyAndNull() {
          final MultiKey<String> key = new MultiKey<String>("a", "b");

          assertFalse(key.equals("not-a-multikey"));
          assertFalse(key.equals(null));
      }

      //-----------------------------------------------------------------------
      // Invalid input

      @Test
      public void testNullKeyArrayConstructorThrows() {
          try {
              new MultiKey<String>((String[]) null);
              fail("Expected IllegalArgumentException for null key array");
          } catch (final IllegalArgumentException expected) {
              // expected
          }
      }
  }
