package org.jsoup.nodes;

 import static org.junit.Assert.*;

 import org.junit.Test;

 /**
  * Tests for {@link Attribute} focusing on key validation (must not be empty or contain only
whitespace)
  * and parent state update on key change.
  */
 public class AttributeTest {

     // ---------- setKey() validation ----------

     @Test(expected = IllegalArgumentException.class)
     public void setKeyEmptyThrowsIllegalArgumentException() {
         Attribute attr = new Attribute("key", "value");
         attr.setKey("");
     }

     @Test(expected = IllegalArgumentException.class)
     public void setKeyNullThrowsIllegalArgumentException() {
         Attribute attr = new Attribute("key", "value");
         attr.setKey(null);
     }

     @Test(expected = IllegalArgumentException.class)
     public void setKeyWhitespaceOnlyThrowsIllegalArgumentException() {
         Attribute attr = new Attribute("key", "value");
         attr.setKey("   ");
     }

     @Test
     public void setKeyValidSucceedsAndTrims() {
         Attribute attr = new Attribute("key", "value");
         attr.setKey("  newKey ");
         assertEquals("newKey", attr.getKey());
     }

     // ---------- Constructor validation ----------

     @Test(expected = IllegalArgumentException.class)
     public void constructorEmptyKeyThrowsIllegalArgumentException() {
         new Attribute("", "value");
     }

     @Test(expected = IllegalArgumentException.class)
     public void constructorWhitespaceOnlyKeyThrowsIllegalArgumentException() {
         new Attribute("  ", "value");
     }

     @Test(expected = IllegalArgumentException.class)
     public void constructorNullKeyThrowsIllegalArgumentException() {
         new Attribute(null, "value");
     }

     @Test
     public void constructorValidKeySucceedsAndTrims() {
         Attribute attr = new Attribute("  key ", "value");
         assertEquals("key", attr.getKey());
     }

     // ---------- Parent state on key change ----------

     @Test
     public void setKeyUpdatesParentKeys() {
         Attributes parent = new Attributes();
         parent.put("original", "val");
         Attribute attr = parent.iterator().next();
         assertEquals("original", attr.getKey());

         attr.setKey("updated");
         assertTrue(parent.hasKey("updated"));
         assertFalse(parent.hasKey("original"));
         assertEquals("val", parent.get("updated"));
     }

     @Test
     public void setKeyExceptionPreservesParentState() {
         Attributes parent = new Attributes();
         parent.put("original", "val");
         Attribute attr = parent.iterator().next();

         try {
             attr.setKey("");
             fail("Expected IllegalArgumentException");
         } catch (IllegalArgumentException e) {
             // expected
         }

         // Parent should be unchanged
         assertTrue(parent.hasKey("original"));
         assertEquals("val", parent.get("original"));
         assertEquals("original", attr.getKey());
     }

     @Test
     public void setKeyExceptionPreservesParentStateOnNull() {
         Attributes parent = new Attributes();
         parent.put("original", "val");
         Attribute attr = parent.iterator().next();

         try {
             attr.setKey(null);
             fail("Expected IllegalArgumentException");
         } catch (IllegalArgumentException e) {
             // expected
         }

         assertTrue(parent.hasKey("original"));
         assertEquals("val", parent.get("original"));
         assertEquals("original", attr.getKey());
     }

     // ---------- Edge case: parent with duplicate-like scenario ----------

     @Test
     public void setKeyDoesNotAffectOtherAttributesInParent() {
         Attributes parent = new Attributes();
         parent.put("a", "1");
         parent.put("b", "2");
         Attribute attrA = parent.asList().get(0);
         attrA.setKey("x");
         assertEquals("1", parent.get("x"));
         assertEquals("2", parent.get("b"));
         assertEquals(2, parent.size());
     }
 }