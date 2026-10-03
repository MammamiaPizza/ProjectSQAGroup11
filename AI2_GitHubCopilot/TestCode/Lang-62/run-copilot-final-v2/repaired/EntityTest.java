package org.apache.commons.lang;

 import java.io.IOException;
 import java.io.StringWriter;
 import org.junit.Test;
 import static org.junit.Assert.*;

 public class EntityTest {

     @Test
     public void testUnescapeNamedEntity() {
         Entities entities = Entities.HTML40;
         assertEquals("<", entities.unescape("&lt;"));
     }

     @Test
     public void testUnescapeDecimalNormal() {
         Entities entities = Entities.HTML40;
         assertEquals("<", entities.unescape("&#60;"));
     }

     @Test
     public void testUnescapeHexNormal() {
         Entities entities = Entities.HTML40;
         assertEquals("<", entities.unescape("&#x3C;"));
     }

     @Test
     public void testUnescapeSupplementaryChar() {
         Entities entities = Entities.HTML40;
         assertEquals("\u2620", entities.unescape("&#9760;"));
     }

     @Test
     public void testUnescapeMaxCodePoint() {
         Entities entities = Entities.HTML40;
         // U+10FFFF = 1114111 decimal; implementation truncates to char
         assertEquals("\uFFFF", entities.unescape("&#1114111;"));
     }

     @Test
     public void testUnescapeOverflowDecimal() {
         Entities entities = Entities.HTML40;
         assertEquals("&#12345678;", entities.unescape("&#12345678;"));
     }

     @Test
     public void testUnescapeOverflowHex() {
         Entities entities = Entities.HTML40;
         assertEquals("&#xFFFFFFFF;", entities.unescape("&#xFFFFFFFF;"));
     }

     @Test
     public void testUnescapeNegative() {
         Entities entities = Entities.HTML40;
         assertEquals("&#-1;", entities.unescape("&#-1;"));
     }

     @Test
     public void testUnescapeEmptyNumeric() {
         Entities entities = Entities.HTML40;
         assertEquals("&#;", entities.unescape("&#;"));
     }

     @Test
     public void testUnescapeUnknownEntity() {
         Entities entities = Entities.HTML40;
         assertEquals("&unknown;", entities.unescape("&unknown;"));
     }

     @Test
     public void testUnescapeMixed() {
         Entities entities = Entities.HTML40;
         assertEquals("Hello < World <", entities.unescape("Hello &lt; World &#60;"));
     }

     @Test
     public void testWriterUnescapeOverflow() throws IOException {
         Entities entities = Entities.HTML40;
         StringWriter writer = new StringWriter();
         entities.unescape(writer, "&#12345678;");
         writer.flush();
         assertEquals("&#12345678;", writer.toString());
     }
 }
