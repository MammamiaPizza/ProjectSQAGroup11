package org.apache.commons.collections;

import static org.hamcrest.Matchers.;
import static org.junit.Assert.;
import org.junit.Before;
import org.junit.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class TestExtendedProperties {
    private ExtendedProperties ep;

 @Before
 public void setUp() {
     ep = new ExtendedProperties();
 }

 @Test
 public void testKeySetEmpty() {
     Set keySet = ep.keySet();
     assertTrue(keySet.isEmpty());
 }

 @Test
 public void testKeySetBasicOrder() {
     ep.addProperty("b", "valb");
     ep.addProperty("a", "vala");
     ep.addProperty("c", "valc");
     Iterator it = ep.keySet().iterator();
     assertEquals("b", it.next());
     assertEquals("a", it.next());
     assertEquals("c", it.next());
     assertFalse(it.hasNext());
 }

 @Test
 public void testKeySetAfterDuplicateAddProperty() {
     ep.addProperty("a", "1");
     ep.addProperty("b", "2");
     ep.addProperty("a", "3"); // duplicate key
     ep.addProperty("c", "4");
     Set keySet = ep.keySet();
     assertEquals(3, ep.size());
     assertEquals(3, keySet.size());
     Iterator it = keySet.iterator();
     assertEquals("a", it.next());
     assertEquals("b", it.next());
     assertEquals("c", it.next());
     assertFalse(it.hasNext());
 }

 @Test
 public void testKeySetAfterSetProperty() {
     ep.addProperty("x", "1");
     ep.setProperty("y", "2");
     ep.setProperty("x", "3"); // overwrite
     ep.addProperty("z", "4");
     Iterator it = ep.keySet().iterator();
     assertEquals("x", it.next());
     assertEquals("y", it.next());
     assertEquals("z", it.next());
     assertFalse(it.hasNext());
 }

 @Test
 public void testKeySetAfterClearProperty() {
     ep.addProperty("a", "1");
     ep.addProperty("b", "2");
     ep.addProperty("c", "3");
     ep.clearProperty("b");
     Set keySet = ep.keySet();
     assertEquals(2, ep.size());
     assertEquals(2, keySet.size());
     Iterator it = keySet.iterator();
     assertEquals("a", it.next());
     assertEquals("c", it.next());
     assertFalse(it.hasNext());
 }

 @Test
 public void testKeySetIteratorHasNextCorrectness() {
     ep.addProperty("k1", "v1");
     ep.addProperty("k2", "v2");
     Iterator it = ep.keySet().iterator();
     assertTrue(it.hasNext());
     it.next();
     assertTrue(it.hasNext());
     it.next();
     assertFalse(it.hasNext());
     try {
         it.next();
         fail("Expected NoSuchElementException");
     } catch (NoSuchElementException e) {
         // expected
     }
 }

 @Test
 public void testKeySetAfterCombine() {
     ExtendedProperties other = new ExtendedProperties();
     other.addProperty("x", "1");
     other.addProperty("y", "2");
     ep.addProperty("a", "1");
     ep.combine(other);
     Set keySet = ep.keySet();
     assertTrue(keySet.contains("a"));
     assertTrue(keySet.contains("x"));
     assertTrue(keySet.contains("y"));
     assertEquals(3, keySet.size());
 }

 @Test
 public void testKeySetAfterSubset() {
     ep.addProperty("prefix.a", "1");
     ep.addProperty("prefix.b", "2");
     ep.addProperty("other.c", "3");
     ExtendedProperties sub = ep.subset("prefix");
     assertNotNull(sub);
     Set keySet = sub.keySet();
     assertEquals(2, keySet.size());
     assertTrue(keySet.contains("a"));
     assertTrue(keySet.contains("b"));
 }

 @Test
 public void testGetKeysMatchesKeySet() {
     ep.addProperty("one", "1");
     ep.addProperty("two", "2");
     Iterator keys = ep.getKeys();
     Iterator keySetIter = ep.keySet().iterator();
     while (keys.hasNext()) {
         assertTrue(keySetIter.hasNext());
         assertEquals(keys.next(), keySetIter.next());
     }
     assertFalse(keySetIter.hasNext());
 }

 @Test
 public void testKeySetAfterLoad() throws IOException {
     String props = "key1=value1\nkey2=value2\n";
     InputStream is = new ByteArrayInputStream(props.getBytes("8859_1"));
     ep.load(is);
     Set keySet = ep.keySet();
     assertEquals(2, keySet.size());
     assertTrue(keySet.contains("key1"));
     assertTrue(keySet.contains("key2"));
 }

 @Test
 public void testKeySetAfterLoadDuplicateKeys() throws IOException {
     String props = "dup=first\ndup=second\n";
     InputStream is = new ByteArrayInputStream(props.getBytes("8859_1"));
     ep.load(is);
     Set keySet = ep.keySet();
     assertEquals(1, keySet.size());
     assertTrue(keySet.contains("dup"));
 }

 @Test
 public void testKeySetAddThenRemoveThenAddAgain() {
     ep.addProperty("a", "1");
     ep.addProperty("b", "2");
     ep.clearProperty("b");
     ep.addProperty("b", "new");
     Iterator it = ep.keySet().iterator();
     assertEquals("a", it.next());
     assertEquals("b", it.next());
     assertFalse(it.hasNext());
 }

}
