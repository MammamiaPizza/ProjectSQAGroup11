package org.apache.commons.lang;

 import static org.junit.Assert.*;
 import org.junit.Test;

 public class EntitiesTest {

     private final Entities entities = Entities.HTML40;

     @Test
     public void testEscapeSupplementaryU1D476() {
         String input = new String(Character.toChars(119650));
         String escaped = entities.escape(input);
         assertEquals("&#55348;&#57186;", escaped);
     }

     @Test
     public void testEscapeSupplementaryU10000() {
         String input = new String(Character.toChars(0x10000));
         String escaped = entities.escape(input);
         assertEquals("&#65536;", escaped);
     }

     @Test
     public void testEscapeMaxCodePoint() {
         String input = new String(Character.toChars(0x10FFFF));
         String escaped = entities.escape(input);
         assertEquals("&#1114111;", escaped);
     }

     @Test
     public void testEscapeBoundaryBMP() {
         String input = new String(Character.toChars(0xFFFF));
         String escaped = entities.escape(input);
         assertEquals("&#65535;", escaped);
     }

     @Test
     public void testEscapeMixedBMPAndSupplementary() {
         String input = "A" + "\u00E9" + new String(Character.toChars(119650)) + "B";
         String escaped = entities.escape(input);
         assertTrue(escaped.contains("&#55348;"));
         assertTrue(escaped.contains("&#57186;"));
         assertTrue(escaped.startsWith("A"));
         assertTrue(escaped.endsWith("B"));
     }

     @Test(expected = NullPointerException.class)
     public void testEscapeNull() {
         entities.escape(null);
     }

     @Test
     public void testEscapeEmpty() {
         assertEquals("", entities.escape(""));
     }

     @Test
     public void testEntityNameForSupplementary() {
         assertNull(entities.entityName(0x1D476));
     }

     @Test
     public void testEntityNameForBMP() {
         assertEquals("lt", entities.entityName('<'));
     }

     @Test
     public void testUnescapeSupplementaryNumericEntity() {
         String entityStr = "&#119650;";
         String unescaped = entities.unescape(entityStr);
         assertEquals(entityStr, unescaped);
     }

     @Test
     public void testRoundTripSupplementary() {
         String input = new String(Character.toChars(0x1D476));
         String escaped = entities.escape(input);
         String unescaped = entities.unescape(escaped);
         assertNotEquals("Round-trip should fail for unescape with value > 0xFFFF",
                 input, unescaped);
         assertEquals(escaped, unescaped);
     }

     @Test
     public void testEscapeMultipleSupplementary() {
         String input = new String(Character.toChars(119650))
                 + new String(Character.toChars(0x10000))
                 + new String(Character.toChars(0x10FFFF));
         String escaped = entities.escape(input);
         assertTrue(escaped.contains("&#55348;"));
         assertTrue(escaped.contains("&#57186;"));
         assertTrue(escaped.contains("&#65536;"));
         assertTrue(escaped.contains("&#1114111;"));
     }
 }
