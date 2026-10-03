package org.jsoup.nodes;

 import static org.junit.Assert.*;

 import java.nio.charset.Charset;

 import org.junit.Test;

 public class EntitiesTest {

     private Document.OutputSettings shiftJisSettings() {
         Document doc = new Document("");
         doc.outputSettings().charset(Charset.forName("Shift_JIS"));
         return doc.outputSettings();
     }

     private Document.OutputSettings asciiSettings() {
         Document doc = new Document("");
         doc.outputSettings().charset(Charset.forName("US-ASCII"));
         return doc.outputSettings();
     }

     private Document.OutputSettings utf8Settings() {
         Document doc = new Document("");
         doc.outputSettings().charset(Charset.forName("UTF-8"));
         return doc.outputSettings();
     }

     // Escape under Shift_JIS must not introduce '?' for characters that should be encodable
     @Test
     public void testShiftJisEscapeNoQuestionMarks() {
         Document.OutputSettings settings = shiftJisSettings();
         // characters typical for Shift_JIS and known problematic in the bug
         String input = "ABCabc123\u00A5\uFF5E\u2019\u201C\u2122";
         String escaped = Entities.escape(input, settings);
         assertFalse("Escaped string should not contain '?'", escaped.contains("?"));
     }

     // Escape followed by unescape must return original string under Shift_JIS
     @Test
     public void testShiftJisRoundtrip() {
         Document.OutputSettings settings = shiftJisSettings();
         String original = "Test \u00A5\uFF5E\u2122\u00B0";
         String escaped = Entities.escape(original, settings);
         assertFalse("Escaped string should not contain '?'", escaped.contains("?"));
         String unescaped = Entities.unescape(escaped);
         assertEquals("Roundtrip escape->unescape should preserve original", original, unescaped);
     }

     // Boundary characters specific to Shift_JIS (bug focus)
     @Test
     public void testShiftJisBoundaryCharacters() {
         Document.OutputSettings settings = shiftJisSettings();
         String input = "\u00A5\uFF5E\uFF0D\uFF02\uFF08\uFF09"; // ¥, ～, －, ", （, ）
         String escaped = Entities.escape(input, settings);
         assertFalse("Shift_JIS boundary chars must not produce '?'", escaped.contains("?"));
         // The printable ASCII equivalent should be unchanged if they are encodable
         assertTrue(escaped.contains("\u00A5"));
     }

     // ASCII-only string must be unchanged under any charset
     @Test
     public void testAsciiStringUnchanged() {
         Document.OutputSettings settings = shiftJisSettings();
         String input = "Hello, world! <html> & stuff";
         String escaped = Entities.escape(input, settings);
         // ASCII characters must not become '?'
         assertFalse(escaped.contains("?"));
         // But the escaping of HTML-sensitive characters depends on escape mode; we keep defaults.
         // Don't check exact escape because that's mode-dependent.
     }

     // Under US-ASCII, non-ASCII chars must be numeric entities and no '?''
     @Test
     public void testAsciiEscapeNonAscii() {
         Document.OutputSettings settings = asciiSettings();        String input = "Test \u00E9"; //
e-acute
         String escaped = Entities.escape(input, settings);
         assertFalse(escaped.contains("?"));
         assertTrue("Non-ASCII under ASCII should become entity", escaped.contains("&#x"));
     }

     // UTF-8 can encode everything, so output should not contain entities (except mandatory
HTMLescapes)
     @Test
     public void testUtf8PresrvesCharacters() {
         Document.OutputSettings settings = utf8Settings();
         String input = "UTF supports \u00E4\u00F6\u00FC\u00A5\uFF5E\uD83D\uDE00"; // äöü¥～😀
         String escaped = Entities.escape(input, settings);
assertFalse(escaped.contains("?"));        // UTF-8 fasth preserves all characters directly (except
HTM mandatory)        assertTrue(escaped.contains("\u00E4"));
         assertTrue(escaped.contains("\uD83D\uDE00"));
     }

     // Empty string must return empty string
     @Test
     public void testEmptyString() {
         Document.OutputSettings settings = shiftJisSettings();
         assertEquals("", Entities.escape("", settings));
     }

     // Mixed named entities and raw text: ensure no '?' and entities are preserved
     @Test
     public void testMixedEntitiesAndRaw() {
         Document.OutputSettings settings = shiftJisSettings();
         // Input contains ampersand which must be escaped; raw text should pass
         String input = "Price < 5 & > 3";
         String escaped = Entities.escape(input, settings);
         assertFalse(escaped.contains("?"));
         assertTrue(escaped.contains("&amp;"));
         // Rest of the ascii text should be present
         assertTrue(escaped.contains("Price"));
     }

     // Escape with base mode: known named entities must be used when not encodable
     @Test
     public void testEscapeBaseMode() {
         Document.OutputSettings settings = new Document("").outputSettings();
         settings.escapeMode(Entities.EscapeMode.base);
         settings.charset(Charset.forName("US-ASCII")); // force entities for non-ASCII
         String input = "Copyright \u00A9"; // copyright sign
         String escaped = Entities.escape(input, settings);
         assertFalse(escaped.contains("?"));
         assertTrue("Copyright sign should become &copy;", escaped.contains("&copy;"));
     }

     // Escape with xhtml mode: only amp, lt, gt, quot
     @Test
     public void testEscapeXhtmlMode() {
         Document.OutputSettings settings = new Document("").outputSettings();
         settings.escapeMode(Entities.EscapeMode.xhtml);
         settings.charset(Charset.forName("US-ASCII"));
         String input = "& < > \" \u00A0 test";
         String escaped = Entities.escape(input, settings);
         assertFalse(escaped.contains("?"));
         // &lt; &gt; &quot; &amp; must be present
         assertTrue(escaped.contains("&amp;"));
         assertTrue(escaped.contains("&lt;"));
         assertTrue(escaped.contains("&gt;"));
         assertTrue(escaped.contains("&quot;"));
         // non-breaking space should not become &nbsp; (xhtml does not include nbsp)
         assertFalse(escaped.contains("&nbsp;"));
         // The non-ASCII nbsp is encoded as numeric entity
         assertTrue(escaped.contains("&#xa0;") || escaped.contains("&#xA0;"));
     }

     // Non-BMP supplementary characters: must be numeric entity under Shift_JIS
     @Test
     public void testNonBmpSupplementaryChars() {
         Document.OutputSettings settings = shiftJisSettings();
         String input = "Smile: \uD83D\uDE00"; // 😀
         String escaped = Entities.escape(input, settings);
         assertFalse(escaped.contains("?"));
         // Under Shift_JIS, supplementary chars cannot be encoded; expect numeric entity
         assertTrue(escaped.contains("&#x1f600;") || escaped.contains("&#x1F600;"));
     }

     // Verify that canEncode(Escape) does not leak '?' for many characters
     @Test
     public void testBulkCharacterScanNoQuestionMarks() {
         Document.OutputSettings settings = shiftJisSettings();
         StringBuilder sb = new StringBuilder();
         // Cover a broad range: ASCII + Latin-1 Supplement + CJK compat + some symbols
         for (int cp = 32; cp < 0xFFFF; cp++) {
             if (Character.isDefined(cp) && !Character.isISOControl(cp)) {
                 sb.appendCodePoint(cp);
             }
         }
         String input = sb.toString();
         String escaped = Entities.escape(input, settings);
         assertFalse("Bulk escape under Shift_JIS must not produce '?'", escaped.contains("?"));
     }
 }