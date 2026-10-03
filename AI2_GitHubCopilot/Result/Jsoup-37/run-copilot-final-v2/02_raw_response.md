package org.jsoup.nodes;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ElementTest {

 @Test
 public void testPrettyPrintFalseProducesCompactHtml() {
     Document doc = Jsoup.parse("<html><head></head><body></body></html>");
     doc.outputSettings().prettyPrint(false);
     Element body = doc.body();

     Element div = new Element(Tag.valueOf("div"), "");
     Element p = new Element(Tag.valueOf("p"), "");
     p.appendText("Hello");
     div.appendChild(p);
     body.appendChild(div);

     String html = div.html();
     assertEquals("<div><p>Hello</p></div>", html);
 }

 @Test
 public void testPrettyPrintFalseNestedBlocks() {
     Document doc = Jsoup.parse("<html><head></head><body></body></html>");
     doc.outputSettings().prettyPrint(false);
     Element body = doc.body();

     Element outer = new Element(Tag.valueOf("div"), "");
     Element inner = new Element(Tag.valueOf("div"), "");
     Element p = new Element(Tag.valueOf("p"), "");
     p.appendText("Nested");
     inner.appendChild(p);
     outer.appendChild(inner);
     body.appendChild(outer);

     assertEquals("<div><div><p>Nested</p></div></div>", outer.html());
 }

 @Test
 public void testPrettyPrintFalseInlineElements() {
     Document doc = Jsoup.parse("<html><head></head><body></body></html>");
     doc.outputSettings().prettyPrint(false);
     Element body = doc.body();

     Element p = new Element(Tag.valueOf("p"), "");
     Element span = new Element(Tag.valueOf("span"), "");
     span.appendText("inline");
     p.appendChild(span);
     body.appendChild(p);

     assertEquals("<p><span>inline</span></p>", p.html());
 }

 @Test
 public void testPrettyPrintFalseTextWithNewlines() {
     Document doc = Jsoup.parse("<html><head></head><body></body></html>");
     doc.outputSettings().prettyPrint(false);
     Element body = doc.body();

     Element p = new Element(Tag.valueOf("p"), "");
     p.appendText("Line1\nLine2");
     body.appendChild(p);

     assertEquals("<p>Line1\nLine2</p>", p.html());
 }

 @Test
 public void testPrettyPrintFalseAfterChangeSettings() {
     Document doc = Jsoup.parse("<html><head></head><body></body></html>");
     doc.outputSettings().prettyPrint(false);
     Element body = doc.body();

     Element div = new Element(Tag.valueOf("div"), "");
     Element p = new Element(Tag.valueOf("p"), "");
     p.appendText("Hello");
     div.appendChild(p);
     body.appendChild(div);

     String compactHtml = div.html();
     assertEquals("<div><p>Hello</p></div>", compactHtml);

     doc.outputSettings().prettyPrint(true);
     String indentedHtml = div.html();

     assertTrue("Indented output should contain newline", indentedHtml.contains("\n"));
     assertFalse("Indented output should not equal compact", indentedHtml.equals(compactHtml));
 }

 @Test
 public void testToStringMatchesHtmlWhenPrettyPrintFalse() {
     Document doc = Jsoup.parse("<html><head></head><body></body></html>");
     doc.outputSettings().prettyPrint(false);
     Element body = doc.body();

     Element div = new Element(Tag.valueOf("div"), "");
     Element p = new Element(Tag.valueOf("p"), "");
     p.appendText("Test");
     div.appendChild(p);
     body.appendChild(div);

     assertEquals(div.html(), div.toString());
 }

 @Test
 public void testPrettyPrintTrueProducesIndentedOutput() {
     Document doc = Jsoup.parse("<html><head></head><body></body></html>");
     doc.outputSettings().prettyPrint(true);
     Element body = doc.body();

     Element div = new Element(Tag.valueOf("div"), "");
     Element p = new Element(Tag.valueOf("p"), "");
     p.appendText("Hello");
     div.appendChild(p);
     body.appendChild(div);

     String html = div.html();
     assertTrue("Indented HTML should contain newline", html.contains("\n"));
     assertFalse("Indented HTML should not equal compact version",
html.equals("<div><p>Hello</p></div>"));
 }

 @Test
 public void testPrettyPrintFalseEmptyElement() {
     Document doc = Jsoup.parse("<html><head></head><body></body></html>");
     doc.outputSettings().prettyPrint(false);
     Element body = doc.body();

     Element div = new Element(Tag.valueOf("div"), "");
     body.appendChild(div);

     assertEquals("<div></div>", div.html());
 }

 @Test
 public void testPrettyPrintFalseSingleChild() {
     Document doc = Jsoup.parse("<html><head></head><body></body></html>");
     doc.outputSettings().prettyPrint(false);
     Element body = doc.body();

     Element parent = new Element(Tag.valueOf("div"), "");
     Element child = new Element(Tag.valueOf("p"), "");
     parent.appendChild(child);
     body.appendChild(parent);

     assertEquals("<div><p></p></div>", parent.html());
 }

 @Test
 public void testPrettyPrintFalseWithBlankText() {
     Document doc = Jsoup.parse("<html><head></head><body></body></html>");
     doc.outputSettings().prettyPrint(false);
     Element body = doc.body();

     Element p = new Element(Tag.valueOf("p"), "");
     p.appendText("   ");
     body.appendChild(p);

     assertEquals("<p>   </p>", p.html());
 }

 @Test
 public void testPrettyPrintFalseMultipleChildren() {
     Document doc = Jsoup.parse("<html><head></head><body></body></html>");
     doc.outputSettings().prettyPrint(false);
     Element body = doc.body();

     Element div = new Element(Tag.valueOf("div"), "");
     Element first = new Element(Tag.valueOf("p"), "");
     first.appendText("First");
     Element second = new Element(Tag.valueOf("p"), "");
     second.appendText("Second");
     div.appendChild(first);
     div.appendChild(second);
     body.appendChild(div);

     assertEquals("<div><p>First</p><p>Second</p></div>", div.html());
 }

 @Test
 public void testPrettyPrintFalseNewlineOnlyText() {
     Document doc = Jsoup.parse("<html><head></head><body></body></html>");
     doc.outputSettings().prettyPrint(false);
     Element body = doc.body();

     Element p = new Element(Tag.valueOf("p"), "");
     p.appendText("\n");
     body.appendChild(p);

     assertEquals("<p>\n</p>", p.html());
 }

}