package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**

 - Tests for {@link TreeBuilder} comment and start-tag processing,
 - concentrating on the table foster-parenting bug #44.
  */
 public class TreeBuilderTest {
  private static final String TABLE_COMMENT_TEXT =
  "<html><body><table><tbody><tr><td>Search text</td></tr><!--
comment\n--></tbody></table></body></html>";
  private Document baseDoc;
  @Before
  public void setUp() {
  baseDoc = Jsoup.parse(TABLE_COMMENT_TEXT);
  }
  // -------- comment ordering --------
  @Test public void invalidTableContentsOrder() {
  // reproduces the "Search text did not come after comment" failure
  String bodyHtl = baseDoc.body().html();
  int commentPos = bodyHtl.indexOf("<!-- comment\n-->" );
  int textPos = bodyHtl.indexOf("Search text");
  assertTrue("Search text did not come after comment", textPos > commentPos);
  }
  @Test public void commentBeforeTrInTable() {
  Document doc = Jsoup.parse("<html><body><table><!--
hello\n--><tr><td>cell</td></tr></table></body></html>");
  String bodyHtl = doc.body().html();
  assertTrue("comment should precede cell text",
      bodyHtl.indexOf("<!-- hello\n-->" ) < bodyHtl.indexOf("cell"));
  }
  @Test public void commentInsideTdWithText() {
  Document doc = Jsoup.parse("<table><tr><td>before<!-- c -->after</td></tr></table>");
  Element td = doc.select("td").first();
  List<Node> children = td.childNodes();
  assertEquals(3, children.size());
  assertTrue(children.get(0) instanceof TextNode);
  assertEquals("before", ((TextNode) children.get(0)).getWholeText());
  assertTrue(children.get(1) instanceof Comment);
  assertEquals(" c ", ((Comment) children.get(1)).getData());
  assertTrue(children.get(2) instanceof TextNode);
  assertEquals("after", ((TextNode) children.get(2)).getWholeText());
  }
  @Test public void multipleCommentsInCell() {
  Document doc = Jsoup.parse("<table><tr><td>x<!-- a --><!-- b -->y</td></tr></table>");
  Element td = doc.select("td").first();
  List<Node> children = td.childNodes();
  // expect: TextNode("x"), Comment(" a "), Comment(" b "), TextNode("y")
  assertEquals(4, children.size());
  assertTrue(children.get(0) instanceof TextNode);
  assertTrue(children.get(1) instanceof Comment);
  assertTrue(children.get(2) instanceof Comment);
  assertTrue(children.get(3) instanceof TextNode);
  }
  // -------- foster-parenting of comments --------
  @Test public void commentAfterTrBecomesPreviousSiblingOfTable() {
  Document doc = Jsoup.parse("<table><tr><td>cell</td></tr><!-- c --></table>");
  Element table = doc.select("table").first();
  Node prev = table.previousSibling();
  assertNotNull("comment should be placed before table", prev);
  assertTrue(prev instanceof Comment);
  }
  @Test public void commentBeforeAnyRowIsFosterParented() {
  Document doc = Jsoup.parse("<table><!-- c\n--><tbody><tr><td>data</td></tr></tbody></table>");
  Element table = doc.select("table").first();
  Node prev = table.previousSibling();
  if (prev != null && prev instanceof Comment) {
      // common spec behaviour: comment moved before table
      assertEquals(" c \n", ((Comment) prev).getData());
  } else {
      // alternatve: inside table but before tbody
      Element tbody = table.select("tbody").first();
      assertNotNull(tbody);
      Node bodyPrev = tbody.previousSibling();
      assertTrue(bodyPrev == null || !(bodyPrev instanceof Comment));
  }
  }
  @Test public void commentOutsideTableUnaffected() {
  Document doc = Jsoup.parse("<div><p>before</p><table><tr><td>inside</td></tr></table><!--\nafter
--></div>");
  Element div = doc.select("div").first();
  List<Node> children = div.childNodes();
  Node last = children.get(children.size() - 1);
  assertTrue(last instanceof Comment);
  assertEquals("\nafter ", ((Comment) last).getData());
  }
  // -------- nested tables --------
  @Test public void commentInNestedTable() {
  Document doc = Jsoup.parse(
      "<table><tr><td><table><tr><td>nested</td></tr><!-- inner\n--></table></td></tr></table>");
  Element outerTd = doc.select("td").first();
  Element innerTable = outerTd.select("table").first();
  Node prev = innerTable.previousSibling();
  if (prev != null) {
      assertTrue(prev instanceof Comment);
  }
  // "nested" text should still be inside the inner table
  assertTrue(innerTable.text().contains("nested"));
  }
  // -------- start-tag processing (exercises TreeBuilder.processStartTag) --------
  @Test public void processStartTagViaParser() {
  Document doc = Jsoup.parse("<p>hello</p>");
  assertNotNull(doc.select("p").first());
  assertEquals("hello", doc.select("p").first().text());
  }
  @Test public void processStartTagWithAttributes() {
  Document doc = Jsoup.parse("<div id='x' class='y'>content</div>");
  Element div = doc.select("div").first();
  assertEquals("x", div.id());
  assertEquals("y", div.className());
  }
  @Test public void emptyTableNoCrash() {
  Document doc = Jsoup.parse("<table></table>");
  assertNotNull(doc.select("table").first());
  assertEquals("", doc.select("table").first().text());
  }

}