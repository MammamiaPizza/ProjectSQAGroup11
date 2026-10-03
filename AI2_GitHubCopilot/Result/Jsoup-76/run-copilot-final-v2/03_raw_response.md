package org.jsoup.parser;

 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.jsoup.nodes.Node;
 import org.jsoup.nodes.TextNode;
 import org.junit.Test;

 import static org.junit.Assert.*;

 public class HtmlTreeBuilderStateTest {

     @Test
     public void preLeadingNewlineNotConsumed() {
         Document doc = Jsoup.parse("<pre>\nhello</pre>");
         Element pre = doc.select("pre").first();
         assertNotNull(pre);
         assertFalse("pre should have child nodes", pre.childNodes().isEmpty());
         Node first = pre.childNodes().get(0);
         assertTrue("first child should be TextNode", first instanceof TextNode);
         String text = ((TextNode) first).getWholeText();
         assertTrue("leading newline should not be consumed, got: '" + text + "'",
                 text.startsWith("\n"));
         assertTrue("content after newline should be present", text.contains("hello"));
     }

     @Test
     public void preWithoutNewlineNormal() {
         Document doc = Jsoup.parse("<pre>hello world</pre>");
         Element pre = doc.select("pre").first();
         assertNotNull(pre);
         assertEquals("hello world", pre.text());
     }

     @Test
     public void emptyPre() {
         Document doc = Jsoup.parse("<pre></pre>");
         Element pre = doc.select("pre").first();
         assertNotNull(pre);
         assertTrue("empty pre should have no child nodes", pre.childNodes().isEmpty());
     }

     @Test
     public void textareaLeadingNewlineNotConsumed() {
         Document doc = Jsoup.parse("<textarea>\nsample text</textarea>");
         Element textarea = doc.select("textarea").first();
         assertNotNull(textarea);
         assertFalse(textarea.childNodes().isEmpty());
         Node first = textarea.childNodes().get(0);
         assertTrue(first instanceof TextNode);
         String text = ((TextNode) first).getWholeText();
         assertTrue("leading newline in textarea should not be consumed: '" + text + "'",
                 text.startsWith("\n"));
     }

     @Test
     public void preWithAttributesAndLeadingNewline() {
         Document doc = Jsoup.parse("<pre class=\"code\" id=\"block1\">\ncontent here</pre>");
         Element pre = doc.select("pre").first();
         assertNotNull(pre);
         assertEquals("code", pre.attr("class"));
         assertEquals("block1", pre.attr("id"));
         Node first = pre.childNodes().get(0);
         assertTrue(first instanceof TextNode);
         assertTrue("leading newline should be preserved with attributes",
                 ((TextNode) first).getWholeText().startsWith("\n"));
     }

     @Test
     public void preOnlyNewline() {
         Document doc = Jsoup.parse("<pre>\n</pre>");
         Element pre = doc.select("pre").first();
         assertNotNull(pre);
         assertEquals("only one child node expected", 1, pre.childNodes().size());
         Node first = pre.childNodes().get(0);
         assertTrue(first instanceof TextNode);
         assertEquals("single newline should be preserved", "\n",
                 ((TextNode) first).getWholeText());
     }

     @Test
     public void preMultipleLeadingNewlines() {
         Document doc = Jsoup.parse("<pre>\n\nhello</pre>");
         Element pre = doc.select("pre").first();
         assertNotNull(pre);
         Node first = pre.childNodes().get(0);
         assertTrue(first instanceof TextNode);
         String text = ((TextNode) first).getWholeText();
         assertTrue("multiple leading newlines should all be preserved: '" + text + "'",
                 text.startsWith("\n\n"));
         assertTrue(text.contains("hello"));
     }

     @Test
     public void codeTagLeadingNewlineNotConsumed() {
         Document doc = Jsoup.parse("<code>\nint x = 1;</code>");
         Element code = doc.select("code").first();
         assertNotNull(code);
         Node first = code.childNodes().get(0);
         assertTrue(first instanceof TextNode);
         assertTrue("leading newline in code should not be consumed",
                 ((TextNode) first).getWholeText().startsWith("\n"));
     }

     @Test
     public void preNestedTagsAfterNewline() {
         Document doc = Jsoup.parse("<pre>\n<b>bold</b> and <i>italic</i></pre>");
         Element pre = doc.select("pre").first();
         assertNotNull(pre);
         // first child is the leading newline TextNode
         Node first = pre.childNodes().get(0);
         assertTrue(first instanceof TextNode);
         assertTrue(((TextNode) first).getWholeText().startsWith("\n"));
         // verify nested elements exist
         assertNotNull(pre.select("b").first());
         assertNotNull(pre.select("i").first());
         // combined text should include nested content
         assertTrue(pre.text().contains("bold"));
         assertTrue(pre.text().contains("italic"));
     }

     @Test
     public void preCarriageReturnLineFeed() {
         Document doc = Jsoup.parse("<pre>\r\nhello</pre>");
         Element pre = doc.select("pre").first();
         assertNotNull(pre);
         Node first = pre.childNodes().get(0);
         assertTrue(first instanceof TextNode);
         String text = ((TextNode) first).getWholeText();
         assertTrue("CR+LF should both be preserved: '" + text + "'",
                 text.startsWith("\r\n"));
     }

     @Test
     public void textareaWithoutNewline() {
         Document doc = Jsoup.parse("<textarea>plain text</textarea>");
         Element textarea = doc.select("textarea").first();
         assertNotNull(textarea);
         assertFalse(textarea.childNodes().isEmpty());
         Node first = textarea.childNodes().get(0);
         assertTrue(first instanceof TextNode);
         assertEquals("plain text", ((TextNode) first).getWholeText());
     }

     @Test
     public void preInFullDocumentContext() {
         Document doc = Jsoup.parse("<!doctype
html><html><body><pre>\ncontent</pre></body></html>");
         Element pre = doc.select("pre").first();
         assertNotNull(pre);
         Node first = pre.childNodes().get(0);
         assertTrue(first instanceof TextNode);
         assertTrue("newline should be preserved in full document context",
                 ((TextNode) first).getWholeText().startsWith("\n"));
     }
 }