package org.jsoup.nodes;

 import static org.junit.Assert.*;

 import java.nio.charset.Charset;
 import java.nio.charset.CharsetEncoder;

 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Entities.EscapeMode;
 import org.junit.Test;

 public class EntitiesBug6Test {

     @Test
     public void testEscapeBaseModeNamedEntities() {
         CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
         String escaped = Entities.escape("& < > \"", encoder, EscapeMode.base);
         assertEquals("&amp; &lt; &gt; &quot;", escaped);
     }

     @Test
     public void testEscapeExtendedMode() {
         CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
         String escaped = Entities.escape("'", encoder, EscapeMode.extended);
         assertEquals("&apos;", escaped);
     }

     @Test
     public void testEscapeNumericForNonEncodable() {
         CharsetEncoder asciiEncoder = Charset.forName("US-ASCII").newEncoder();
         String escaped = Entities.escape("é", asciiEncoder, EscapeMode.base);
         assertEquals("&#233;", escaped);
     }

     @Test
     public void testEscapeViaOutputSettings() {
         Document doc = new Document("");
         Document.OutputSettings out = doc.outputSettings();
         String escaped = Entities.escape("& < > \"", out);
         assertEquals("&amp; &lt; &gt; &quot;", escaped);
     }

     @Test
     public void testUnescapeNamedEntities() {
         assertEquals("&", Entities.unescape("&amp;"));
         assertEquals("<", Entities.unescape("&lt;"));
         assertEquals(">", Entities.unescape("&gt;"));
         assertEquals("\"", Entities.unescape("&quot;"));
         assertEquals("'", Entities.unescape("&apos;"));
         // optional semicolon
         assertEquals("&", Entities.unescape("&amp"));
     }

     @Test
     public void testUnescapeNumericDecimal() {
         assertEquals("\"", Entities.unescape("&#34;"));
         assertEquals("&", Entities.unescape("&#38;"));
         assertEquals("<", Entities.unescape("&#60;"));
         assertEquals(">", Entities.unescape("&#62;"));
         // without semicolon
         assertEquals("\"", Entities.unescape("&#34"));
         assertEquals("&", Entities.unescape("&#38"));
     }

     @Test
     public void testUnescapeNumericHex() {
         assertEquals("\"", Entities.unescape("&#x22;"));
         assertEquals("&", Entities.unescape("&#x26;"));
         assertEquals("<", Entities.unescape("&#x3c;"));
         assertEquals(">", Entities.unescape("&#x3e;"));
         // without semicolon, case insensitive X
         assertEquals("\"", Entities.unescape("&#x22"));
         assertEquals("\"", Entities.unescape("&#X22;"));
     }

     @Test
     public void testUnescapeMixedText() {
         String input = "Hello &amp; goodbye &lt; world &gt;";
         String expected = "Hello & goodbye < world >";
         assertEquals(expected, Entities.unescape(input));
     }

     @Test
     public void testUnescapeEmptyAndNoEntities() {
         assertEquals("", Entities.unescape(""));
         assertEquals("plain text", Entities.unescape("plain text"));
     }

     @Test
     public void testUnescapeMalformedEntities() {
         // bare ampersand at end
         assertEquals("text &", Entities.unescape("text &"));
         // lone ampersand
         assertEquals("&", Entities.unescape("&"));
         // empty entity name after &
         assertEquals("&;", Entities.unescape("&;"));
         // unknown named entity
         assertEquals("&unknown;", Entities.unescape("&unknown;"));
         // truncated numeric reference
         assertEquals("&#", Entities.unescape("&#"));
         assertEquals("&#x", Entities.unescape("&#x"));
         assertEquals("&#X", Entities.unescape("&#X"));
     }

     @Test
     public void testUnescapeSpecialCharsDollarBackslash() {
         // Numeric entity for '$' (U+0024)
         assertEquals("$", Entities.unescape("&#36;"));
         assertEquals("$", Entities.unescape("&#x24;"));
         // Numeric entity for '\' (U+005C) – bug triggers here
         assertEquals("\\", Entities.unescape("&#92;"));
         assertEquals("\\", Entities.unescape("&#x5c;"));
     }

     @Test
     public void testEscapeUnescapeRoundtrip() {
         String original = "& < > \"";
         CharsetEncoder encoder = Charset.forName("UTF-8").newEncoder();
         String escaped = Entities.escape(original, encoder, EscapeMode.base);
         String unescaped = Entities.unescape(escaped);
         assertEquals(original, unescaped);
     }
 }
