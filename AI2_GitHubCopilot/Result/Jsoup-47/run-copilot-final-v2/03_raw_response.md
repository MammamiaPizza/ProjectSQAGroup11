package org.jsoup.nodes;

 import org.junit.Test;
 import static org.junit.Assert.*;

 public class EntitiesTest {

     private Document.OutputSettings newOutputSettings(Entities.EscapeMode mode) {
         Document.OutputSettings out = new Document.OutputSettings();
         out.escapeMode(mode);
         return out;
     }

     private String escape(String string, Entities.EscapeMode mode) {
         return Entities.escape(string, newOutputSettings(mode));
     }

     // --- XML attribute: '>' MUST be escaped (this fails in the buggy version) ---

     @Test
     public void escapesGtInXmlAttribute() {
         String result = escape(">", Entities.EscapeMode.xhtml);
         assertEquals("&gt;", result);
     }

     @Test
     public void escapesGtInXmlAttributeString() {
         String result = escape("a>b", Entities.EscapeMode.xhtml);
         assertEquals("a&gt;b", result);
     }

     @Test
     public void escapesMultipleGtInXmlAttribute() {
         String result = escape(">>", Entities.EscapeMode.xhtml);
         assertEquals("&gt;&gt;", result);
     }

     @Test
     public void escapesGtInXmlAttributeLong() {
         String input = "a>b>c>d";
         String result = escape(input, Entities.EscapeMode.xhtml);
         assertEquals("a&gt;b&gt;c&gt;d", result);
     }

     // --- HTML attribute: '>' must NOT be escaped ---

     @Test
     public void doesNotEscapeGtInHtmlAttribute() {
         String result = escape(">", Entities.EscapeMode.base);
         assertEquals(">", result);
     }

     @Test
     public void doesNotEscapeGtInHtmlAttributeString() {
         String result = escape("a>b", Entities.EscapeMode.base);
         assertEquals("a>b", result);
     }

     // --- XHTML mode (text content): '<' and '>' both escaped ---

     @Test
     public void escapesLtAndGtInXhtmlText() {
         String result = escape("<p>One</p>", Entities.EscapeMode.xhtml);
         assertEquals("&lt;p&gt;One&lt;/p&gt;", result);
     }

     // --- Interleaved special chars in XML attribute ---

     @Test
     public void fullTagInXmlAttribute() {
         String result = escape("<p>One</p>", Entities.EscapeMode.xhtml);
         assertEquals("&lt;p&gt;One&lt;/p&gt;", result);
     }

     @Test
     public void ampersandAndGtInXmlAttribute() {
         String result = escape("a&b>c", Entities.EscapeMode.xhtml);
         assertEquals("a&amp;b&gt;c", result);
     }

     // --- Other bounding cases ---

     @Test
     public void emptyString() {
         String result = escape("", Entities.EscapeMode.xhtml);
         assertEquals("", result);
     }

     @Test
     public void onlyGtString() {
         String result = escape(">", Entities.EscapeMode.xhtml);
         assertEquals("&gt;", result);
     }

     @Test
     public void cdataLikeSequence() {
         String result = escape("]]>", Entities.EscapeMode.xhtml);
         assertEquals("]]&gt;", result);
     }

     // --- Map integrity: xhtmlByVal must contain mapping for '>' ---

     @Test
     public void xhtmlMapContainsGt() {
         assertTrue("xhtml map must contain entry for '>'",
                 Entities.EscapeMode.xhtml.getMap().containsKey('>'));
         assertEquals("gt", Entities.EscapeMode.xhtml.getMap().get('>'));
     }

     // --- ensure '&' is always escaped ---

     @Test
     public void ampersandAlwaysEscaped() {
         String result = escape("&", Entities.EscapeMode.base);
         assertEquals("&amp;", result);
         result = escape("&", Entities.EscapeMode.xhtml);
         assertEquals("&amp;", result);
     }

 }