package org.jsoup.nodes;

 import org.junit.Test;
 import static org.junit.Assert.*;

 public class AttributeTest {

     @Test
     public void testSetKeyOnOrphan() {
         Attribute attr = new Attribute("original", "value", null);
         attr.setKey("newKey");
         assertEquals("newKey", attr.getKey());
         assertNull(attr.parent);
     }

     @Test
     public void testSetValueOnOrphanNoExceptionAndReturnsOldValue() {
         Attribute attr = new Attribute("key", "oldVal", null);
         String oldVal = attr.setValue("newVal");
         assertEquals("oldVal", oldVal);
         assertEquals("newVal", attr.getValue());
         assertNull(attr.parent);
     }

     @Test
     public void testSetKeyThenSetValueOnOrphan() {
         Attribute attr = new Attribute("k", "v", null);
         attr.setKey("k2");
         attr.setValue("v2");
         assertEquals("k2", attr.getKey());
         assertEquals("v2", attr.getValue());
     }

     @Test
     public void testSetKeyOnNonOrphanUpdatesParent() {
         Attributes attrs = new Attributes();
         Attribute attr = new Attribute("oldKey", "val", attrs);
         attrs.put(attr); // sets parent and inserts into internal arrays
         attr.setKey("newKey");
         assertEquals("newKey", attr.getKey());
         assertNull(attrs.get("oldKey"));
         assertEquals("val", attrs.get("newKey"));
     }

     @Test
     public void testSetValueOnNonOrphanUpdatesParent() {
         Attributes attrs = new Attributes();
         Attribute attr = new Attribute("key", "oldVal");
         attrs.put(attr);
         attr.setValue("newVal");
         assertEquals("newVal", attr.getValue());
         assertEquals("newVal", attrs.get("key"));
     }

     @Test
     public void testSetValueReturnsOldValueNonOrphan() {
         Attributes attrs = new Attributes();
         Attribute attr = new Attribute("key", "oldVal");
         attrs.put(attr);
         String oldVal = attr.setValue("newVal");
         assertEquals("oldVal", oldVal);
     }

     @Test
     public void testHtmlOnOrphanDoesNotThrow() {
         Attribute attr = new Attribute("href", "index.html", null);
         String html = attr.html();
         assertNotNull(html);
         assertTrue(html.contains("href"));
     }

     @Test
     public void testIsBooleanAttributeOnOrphan() {
         Attribute boolAttr = new Attribute("checked", "", null);
         Attribute nonBool = new Attribute("href", "value", null);
         assertTrue(boolAttr.isBooleanAttribute());
         assertFalse(nonBool.isBooleanAttribute());
     }

     @Test
     public void testCloneOrphan() {
         Attribute original = new Attribute("key", "val", null);
         Attribute clone = original.clone();
         assertNotSame(original, clone);
         assertEquals(original, clone);
         assertEquals(original.getKey(), clone.getKey());
         assertEquals(original.getValue(), clone.getValue());
         assertNull(clone.parent);
     }

     @Test
     public void testEqualsOrphan() {
         Attribute a1 = new Attribute("key", "val");
         Attribute a2 = new Attribute("key", "val");
         Attribute a3 = new Attribute("other", "val");
         assertEquals(a1, a2);
         assertNotEquals(a1, a3);
         assertNotEquals(a1, null);
         assertNotEquals(a1, "notAnAttribute");
     }

     @Test(expected = IllegalArgumentException.class)
     public void testSetKeyNullThrows() {
         new Attribute("valid", "val", null).setKey(null);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testSetKeyEmptyThrows() {
         new Attribute("valid", "val", null).setKey("   ");
     }
 }
