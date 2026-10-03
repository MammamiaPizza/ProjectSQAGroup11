package org.jsoup.parser;

 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.jsoup.nodes.Node;
 import org.jsoup.nodes.TextNode;
 import org.jsoup.select.Elements;
 import org.junit.Test;
 import static org.junit.Assert.*;

 public class HtmlTreeBuilderTest {

     @Test
     public void testReinsertionModeForThCelss() {
         Document doc = Jsoup.parse("<table><tr><th><b>text</th></tr></table>");
         Elements boldInTh = doc.select("th b");
         assertEquals("Formatting element <b> should be adopted once inside <th>",
                 1, boldInTh.size());
         Element b = boldInTh.first();
         assertNotNull(b);
         assertEquals("text", b.text());
     }

     @Test
     public void testTdWithFormattingElement() {
         Document doc = Jsoup.parse("<table><tr><td><i>content</td></tr></table>");
         Elements italicInTd = doc.select("td i");
         assertEquals("Formatting element <i> should be adopted once inside <td>",
                 1, italicInTd.size());
         assertEquals("content", doc.select("td i").first().text());
     }

     @Test
     public void testThWithNestedFormattingElements() {
         Document doc = Jsoup.parse("<table><tr><th><b><i>nested</i></b></th></tr></table>");
         assertEquals("Both formatting elements should be preserved inside <th>",
                 1, doc.select("th b").size());
         assertEquals("Both formatting elements should be preserved inside <th>",
                 1, doc.select("th b i").size());
         assertEquals("nested", doc.select("th b i").text());
     }

     @Test
     public void testTdWithMultipleFormattingElements() {
         Document doc = Jsoup.parse("<table><tr><td><b>bold</b><i>italic</i></td></tr></table>");
         assertEquals("Two formatting elements should be inside <td>",
                 2, doc.select("td > b, td > i").size());
         assertEquals("bold", doc.select("td > b").text());
         assertEquals("italic", doc.select("td > i").text());
     }

     @Test
     public void testThClosingWithoutExplicitFormattingClose() {
         Document doc = Jsoup.parse("<table><tr><th><b>text<i>more</th></tr></table>");
         Elements bInTh = doc.select("th b");
         Elements iInTh = doc.select("th i");
         assertEquals("Formatting element <b> should appear once inside <th>",
                 1, bInTh.size());
         assertEquals("Formatting element <i> should appear once inside <th>",
                 1, iInTh.size());
         assertTrue("Nesting should be preserved: <b> contains <i>",
                 bInTh.first().select("i").size() == 1 ||
iInTh.first().parent().tagName().equals("b"));
     }

     @Test
     public void testConsecutiveCellsWithFormatting() {
         Document doc =
Jsoup.parse("<table><tr><td><b>first</b></td><td><b>second</b></td></tr></table>");
         Elements bElements = doc.select("td b");
         assertEquals("Two <b> elements should exist, one per <td>",
                 2, bElements.size());
         assertEquals("first", doc.select("td:eq(0) b").text());
         assertEquals("second", doc.select("td:eq(1) b").text());
     }

     @Test
     public void testTableInsideTdWithFormatting() {
         Document doc =
Jsoup.parse("<table><tr><td><b><table><tr><td>inner</td></tr></table></b></td></tr></table>");
         Element outerTd = doc.select("td").first();
         assertNotNull(outerTd);
         assertTrue("Foster-parented table should be before <td> or inside document structure",
                 doc.select("table table").size() >= 1);
     }

     @Test
     public void testEmptyTd() {
         Document doc = Jsoup.parse("<table><tr><td></td></tr></table>");
         Element td = doc.select("td").first();
         assertNotNull(td);
         assertEquals("Empty <td> should have no child nodes",
                 0, td.childNodes().size());
     }

     @Test
     public void testThWithTextAndFormatting() {
         Document doc = Jsoup.parse("<table><tr><th>plain<b>bold</b></th></tr></table>");
         Element th = doc.select("th").first();
         assertEquals("Text before <b> plus <b> element = 2 children",
                 2, th.childNodes().size());
         assertTrue("First child should be text 'plain'",
                 th.childNodes().get(0) instanceof TextNode);
         assertEquals("plain", ((TextNode) th.childNodes().get(0)).getWholeText());
         assertEquals("bold", doc.select("th b").text());
     }

     @Test
     public void testMultipleFormattingWithTableCellReopen() {
         Document doc = Jsoup.parse("<table><tr><th><b><u>text</u></b></th></tr></table>");
         assertEquals("Single <b> under <th>", 1, doc.select("th b").size());
         assertEquals("Single <u> under <th> <b>", 1, doc.select("th b u").size());
         assertEquals("text", doc.select("th u").text());
     }

     @Test
     public void testDeeplyNestedFormattingInTableCell() {
         Document doc =
Jsoup.parse("<table><tr><td><b><i><u><s>deep</s></u></i></b></td></tr></table>");
         assertEquals("All four formatting elements should be preserved inside <td>",
                 4, doc.select("td b, td i, td u, td s").size());
         assertEquals("deep", doc.select("td s").text());
     }

     @Test
     public void testTdEndTagAfterFormattingWithoutBody() {
         Document doc = Jsoup.parse("<table><tr><td><b></td></tr></table>");
         Elements bInTd = doc.select("td b");
         assertEquals("Empty formatting element <b> should appear once in <td>",
                 1, bInTd.size());
         Element td = doc.select("td").first();
         assertNotNull(td);
         assertTrue("Child count should be 1 (just <b>)",
                 td.childNodes().size() == 1);
     }

     @Test
     public void testFormattingElementSelfClosingInTableCell() {
         Document doc = Jsoup.parse("<table><tr><th><br/><b>text</b></th></tr></table>");
         assertEquals("Self-closing <br/> should be inside <th>",
                 1, doc.select("th br").size());
         assertEquals("text", doc.select("th b").text());
     }
 }
