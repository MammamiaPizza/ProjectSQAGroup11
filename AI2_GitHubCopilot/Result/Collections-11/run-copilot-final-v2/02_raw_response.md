package org.apache.commons.collections.keyvalue;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

public class MultiKeySerializationTest {

 private static MultiKey roundTrip(MultiKey original) throws Exception {
     ByteArrayOutputStream bytes = new ByteArrayOutputStream();
     ObjectOutputStream out = new ObjectOutputStream(bytes);
     out.writeObject(original);
     out.close();

     ByteArrayInputStream input = new ByteArrayInputStream(bytes.toByteArray());
     ObjectInputStream in = new ObjectInputStream(input);
     MultiKey restored = (MultiKey) in.readObject();
     in.close();
     return restored;
 }

 @Test
 public void testTwoKeySurvivesSerializationPreservingSizeAndKeys() throws Exception {
     MultiKey original = new MultiKey("a", "b");
     MultiKey restored = roundTrip(original);

     assertEquals(2, restored.size());
     assertEquals("a", restored.getKey(0));
     assertEquals("b", restored.getKey(1));
     assertEquals(original, restored);
 }

 @Test
 public void testFiveKeySurvivesSerialization() throws Exception {
     MultiKey original = new MultiKey(
             Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3),
             Integer.valueOf(4), Integer.valueOf(5));
     MultiKey restored = roundTrip(original);

     assertEquals(5, restored.size());
     for (int i = 0; i < 5; i++) {
         assertEquals(Integer.valueOf(i + 1), restored.getKey(i));
     }
     assertEquals(original, restored);
 }

 @Test
 public void testArrayConstructorWithCloneSurvivesSerialization() throws Exception {
     Object[] keys = new Object[] { "x", "y", "z" };
     MultiKey original = new MultiKey(keys);
     MultiKey restored = roundTrip(original);

     assertEquals(3, restored.size());
     for (int i = 0; i < keys.length; i++) {
         assertEquals(keys[i], restored.getKey(i));
     }
     assertEquals(original, restored);
 }

 @Test
 public void testArrayConstructorWithoutCloneSurvivesSerialization() throws Exception {
     Object[] keys = new Object[] { "p", "q" };
     MultiKey original = new MultiKey(keys, false);
     MultiKey restored = roundTrip(original);

     assertEquals(2, restored.size());
     assertEquals("p", restored.getKey(0));
     assertEquals("q", restored.getKey(1));
     assertEquals(original, restored);
 }

 @Test
 public void testTwoElementArrayBoundarySurvivesSerialization() throws Exception {
     MultiKey original = new MultiKey(new Object[] { "a", "b" });
     MultiKey restored = roundTrip(original);

     assertEquals(2, restored.size());
     assertEquals("a", restored.getKey(0));
     assertNotNull(restored.getKey(1));
     assertEquals(original, restored);
 }

 @Test
 public void testNullKeyValuesSurviveSerialization() throws Exception {
     MultiKey original = new MultiKey(null, null);
     MultiKey restored = roundTrip(original);

     assertEquals(2, restored.size());
     assertNull(restored.getKey(0));
     assertNull(restored.getKey(1));
     assertEquals(original, restored);
     assertEquals(original.hashCode(), restored.hashCode());
 }

 @Test
 public void testArrayWithNullElementSurvivesSerialization() throws Exception {
     Object[] keys = new Object[] { null, "middle", null };
     MultiKey original = new MultiKey(keys);
     MultiKey restored = roundTrip(original);

     assertEquals(3, restored.size());
     assertNull(restored.getKey(0));
     assertEquals("middle", restored.getKey(1));
     assertNull(restored.getKey(2));
     assertEquals(original, restored);
     assertEquals(original.hashCode(), restored.hashCode());
 }

 @Test
 public void testHashCodeRecalculatedAfterSerialization() throws Exception {
     MultiKey original = new MultiKey("a", "b", "c");
     MultiKey restored = roundTrip(original);

     assertEquals(original.hashCode(), restored.hashCode());
 }

 @Test
 public void testDeserializedGetKeysMatchesOriginal() throws Exception {
     MultiKey original = new MultiKey("k1", "k2", "k3");
     MultiKey restored = roundTrip(original);

     assertEquals(3, restored.size());
     assertArrayEquals(original.getKeys(), restored.getKeys());
 }

 @Test
 public void testToStringAfterSerialization() throws Exception {
     MultiKey original = new MultiKey("a", "b");
     MultiKey restored = roundTrip(original);

     String text = restored.toString();
     assertNotNull(text);
     assertEquals("MultiKey[a, b]", text);
 }

}