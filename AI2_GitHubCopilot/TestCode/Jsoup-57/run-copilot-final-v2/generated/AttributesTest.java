package org.jsoup.nodes;

 import static org.junit.Assert.*;

 import java.util.ConcurrentModificationException;
 import java.util.Iterator;
 import java.util.Map;

 import org.junit.Test;

 public class AttributesTest {

     @Test
     public void testRemove() {
         Attributes attrs = new Attributes();
         attrs.put("key", "value");
         attrs.remove("key");
         assertEquals(0, attrs.size());
         assertFalse(attrs.hasKey("key"));
         assertEquals("", attrs.get("key"));
     }

     @Test
     public void testRemoveIgnoreCase() {
         Attributes attrs = new Attributes();
         attrs.put("Key", "value");
         attrs.removeIgnoreCase("key");
         assertEquals(0, attrs.size());
         assertFalse(attrs.hasKey("Key"));
     }

     @Test
     public void testRemoveIgnoreCaseCaseInsensitivity() {
         Attributes attrs = new Attributes();
         attrs.put("Key", "value");
         attrs.put("other", "otherValue");
         attrs.removeIgnoreCase("kEy");
         assertEquals(1, attrs.size());
         assertFalse(attrs.hasKey("Key"));
         assertTrue(attrs.hasKey("other"));
     }

     @Test
     public void testRemoveNonExistentKey() {
         Attributes attrs = new Attributes();
         attrs.put("key", "value");
         attrs.remove("nonexistent");
         assertEquals(1, attrs.size());
     }

     @Test
     public void testRemoveSameKeyMultipleTimes() {
         Attributes attrs = new Attributes();
         attrs.put("key", "value");
         attrs.remove("key");
         attrs.remove("key"); // should not throw
         assertEquals(0, attrs.size());
     }

     @Test
     public void testChainedRemoveIgnoreCase() {
         Attributes attrs = new Attributes();
         attrs.put("a", "1");
         attrs.put("b", "2");
         attrs.put("c", "3");
         attrs.removeIgnoreCase("A");
         attrs.removeIgnoreCase("B");
         attrs.removeIgnoreCase("C");
         assertEquals(0, attrs.size());
     }

     @Test
     public void testIteratorRemove() {
         Attributes attrs = new Attributes();
         attrs.put("a", "1");
         attrs.put("b", "2");
         attrs.put("c", "3");

         Iterator<Attribute> it = attrs.iterator();
         while (it.hasNext()) {
             Attribute attr = it.next();
             if ("b".equals(attr.getKey())) {
                 it.remove();
             }
         }
         assertEquals(2, attrs.size());
         assertFalse(attrs.hasKey("b"));
     }

     @Test
     public void testDatasetIteratorRemove() {
         Attributes attrs = new Attributes();
         attrs.put("data-foo", "bar");
         attrs.put("data-baz", "qux");

         Map<String, String> dataset = attrs.dataset();
         Iterator<Map.Entry<String, String>> it = dataset.entrySet().iterator();
         while (it.hasNext()) {
             Map.Entry<String, String> entry = it.next();
             if ("foo".equals(entry.getKey())) {
                 it.remove();
             }
         }
         assertEquals(1, dataset.size());
         assertEquals("", attrs.get("data-foo"));
         assertEquals("qux", attrs.get("data-baz"));
     }

     @Test
     public void testAddAllThenRemove() {
         Attributes first = new Attributes();
         first.put("a", "1");
         first.put("b", "2");

         Attributes second = new Attributes();
         second.put("c", "3");
         second.put("d", "4");

         first.addAll(second);
         assertEquals(4, first.size());

         first.remove("a");
         first.remove("c");
         assertEquals(2, first.size());
         assertTrue(first.hasKey("b"));
         assertTrue(first.hasKey("d"));
     }

     @Test
     public void testSizeConsistency() {
         Attributes attrs = new Attributes();
         assertEquals(0, attrs.size());
         attrs.put("a", "1");
         attrs.put("b", "2");
         assertEquals(2, attrs.size());
         attrs.remove("a");
         assertEquals(1, attrs.size());
         attrs.removeIgnoreCase("B");
         assertEquals(0, attrs.size());
     }

     @Test
     public void testRemoveThenHasKey() {
         Attributes attrs = new Attributes();
         attrs.put("key", "val");
         assertTrue(attrs.hasKey("key"));
         attrs.remove("key");
         assertFalse(attrs.hasKey("key"));
     }

     @Test
     public void testGetAfterRemove() {
         Attributes attrs = new Attributes();
         attrs.put("key", "val");
         assertEquals("val", attrs.get("key"));
         attrs.remove("key");
         assertEquals("", attrs.get("key"));
     }
 }
