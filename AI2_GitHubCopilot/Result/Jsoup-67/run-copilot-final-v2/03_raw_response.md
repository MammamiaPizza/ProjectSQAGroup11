package org.jsoup.parser;

  import org.jsoup.Jsoup;
  import org.jsoup.nodes.Document;
  import org.jsoup.nodes.Element;
  import org.jsoup.nodes.Node;
  import org.jsoup.nodes.TextNode;
  import org.junit.Test;

  import static org.junit.Assert.assertEquals;
  import static org.junit.Assert.assertNotNull;
  import static org.junit.Assert.assertTrue;
  import static org.junit.Assert.fail;

  /**
   * Tests targeted at the deep‑nesting stack overflow bug in HtmlTreeBuilder (Defects4J bug 67).
   * The bug causes a StackOverflowError when parsing deeply nested elements.
   */
  public class HtmlTreeBuilderDeepStackTest {

      // Helper to build a string of <div id='n'> ... </div> repeated `depth` times.
      private String buildDeepDivs(int depth) {
          StringBuilder sb = new StringBuilder();
          for (int i = 0; i < depth; i++) {
              sb.append("<div id='").append(i).append("'>");
          }
          for (int i = 0; i < depth; i++) {
              sb.append("</div>");
          }
          return sb.toString();
      }

      // Count how many nested <div>s sit under <body> (body → div → div → ...).
      private int countNestedDivDepth(Document doc) {
          Element body = doc.body();
          assertNotNull("Document must have a <body>", body);
          Node current = body.childNode(0);
          int count = 0;
          while (current instanceof Element && ((Element) current).tagName().equals("div")) {
              count++;
              if (current.childNodeSize() == 0) break;
              current = current.childNode(0);
          }
          return count;
      }

      @Test
      public void nest500DivsShouldNotOverflow() {
          int depth = 500;
          Document doc = Jsoup.parse(buildDeepDivs(depth));
          assertEquals(depth, countNestedDivDepth(doc));
      }

      @Test
      public void nest2000DivsCausesNoStackOverflow() {
          // This depth is typical for triggering the recursive StackOverflow in the buggy version.
          int depth = 2000;
          try {
              Document doc = Jsoup.parse(buildDeepDivs(depth));
              assertEquals(depth, countNestedDivDepth(doc));
          } catch (StackOverflowError e) {
              fail("StackOverflowError occurred while parsing " + depth + " nested <div>s");
          }
      }

      @Test
      public void extremeDepth10000Divs() {
          int depth = 10000;
          try {
              Document doc = Jsoup.parse(buildDeepDivs(depth));
              assertEquals(depth, countNestedDivDepth(doc));
          } catch (StackOverflowError e) {
              fail("StackOverflowError on extreme depth " + depth);
          }
      }

      @Test
      public void deepNestingWithTextNodes() {
          int depth = 800;
          StringBuilder sb = new StringBuilder();
          for (int i = 0; i < depth; i++) {
              sb.append("<div>");
          }
          sb.append("leaf text");
          for (int i = 0; i < depth; i++) {
              sb.append("</div>");
          }
          Document doc = Jsoup.parse(sb.toString());
          Element body = doc.body();
          Node current = body.childNode(0);
          int count = 0;
          while (current instanceof Element && ((Element) current).tagName().equals("div")) {
              count++;
              current = current.childNode(0);
          }
          assertEquals(depth, count);
          assertTrue(current instanceof TextNode);
          assertEquals("leaf text", ((TextNode) current).getWholeText().trim());
      }

      @Test
      public void deeplyNestedUnclosedTagsDoesNotCrash() {
          // Many opening <div> without corresponding closing – triggers error recovery.
          int depth = 2000;
          StringBuilder sb = new StringBuilder();
          for (int i = 0; i < depth; i++) {
              sb.append("<div id='d").append(i).append("'>");
          }
          try {
              Document doc = Jsoup.parse(sb.toString());
              assertNotNull(doc);
              // Error recovery should produce some DOM without exception.
          } catch (StackOverflowError e) {
              fail("StackOverflowError when parsing unclosed deeply nested tags");
          }
      }

      @Test
      public void deepNestingWithFormattingElements() {
          // <b> tags trigger the "active formatting elements" list which can grow large.
          int depth = 500;
          StringBuilder sb = new StringBuilder();
          for (int i = 0; i < depth; i++) {
              sb.append("<b><span>");
          }
          sb.append("text");
          for (int i = 0; i < depth; i++) {
              sb.append("</span></b>");
          }
          Document doc = Jsoup.parse(sb.toString());
          Element body = doc.body();
          Node current = body.childNode(0);
          int count = 0;
          while (current instanceof Element) {
              if (((Element) current).tagName().equals("b") || ((Element)
 current).tagName().equals("span"))
                  count++;
              else break;
              if (current.childNodeSize() == 0) break;
              current = current.childNode(0);
          }
          // Each level contributes a <b> and a <span>; the innermost text counts as leaf.
          assertEquals(depth * 2, count);
      }

      @Test
      public void deepSiblingStructureDoesNotOverflow() {
          // Many sibling elements (wide, not deep) – should not stress stack.
          int count = 2000;
          StringBuilder sb = new StringBuilder("<div>");
          for (int i = 0; i < count; i++) {
              sb.append("<p>item").append(i).append("</p>");
          }
          sb.append("</div>");
          Document doc = Jsoup.parse(sb.toString());
          Element div = doc.body().child(0);
          assertEquals("div", div.tagName());
          assertEquals(count, div.children().size());
      }

      @Test
      public void deepTableNesting() {
          // Tables have special parsing rules and may exercise different code paths.
          int depth = 200;
          StringBuilder sb = new StringBuilder();
          for (int i = 0; i < depth; i++) {
              sb.append("<table><tr><td>");
          }
          for (int i = 0; i < depth; i++) {
              sb.append("</td></tr></table>");
          }
          Document doc = Jsoup.parse(sb.toString());
          // The DOM will be a chain of table > tbody > tr > td > table ...; count td elements.
          assertEquals(depth, doc.getElementsByTag("td").size());
      }

      @Test
      public void deepListNesting() {
          int depth = 200;
          StringBuilder sb = new StringBuilder();
          for (int i = 0; i < depth; i++) {
              sb.append("<ul><li>");
          }
          sb.append("leaf");
          for (int i = 0; i < depth; i++) {
              sb.append("</li></ul>");
          }
          Document doc = Jsoup.parse(sb.toString());
          assertEquals(depth, doc.getElementsByTag("ul").size());
          assertEquals(depth, doc.getElementsByTag("li").size());
      }

      @Test
      public void deepMixedElements() {
          // Mixed tags (div, span, p) nested to trigger various states.
          int depth = 500;
          String[] tags = {"div", "span", "p"};
          StringBuilder sb = new StringBuilder();
          for (int i = 0; i < depth; i++) {
              String tag = tags[i % tags.length];
              sb.append("<").append(tag).append(">");
          }
          for (int i = 0; i < depth; i++) {
              String tag = tags[(depth - 1 - i) % tags.length];
              sb.append("</").append(tag).append(">");
          }
          Document doc = Jsoup.parse(sb.toString());
          // At a minimum, parsing should succeed without StackOverflowError.
          assertNotNull(doc.body());
      }

      @Test
      public void deepNestingWithComments() {
          int depth = 1000;
          StringBuilder sb = new StringBuilder();
          for (int i = 0; i < depth; i++) {
              sb.append("<!-- cmt ").append(i).append(" --><div>");
          }
          for (int i = 0; i < depth; i++) {
              sb.append("</div>");
          }
          Document doc = Jsoup.parse(sb.toString());
          assertEquals(depth, doc.getElementsByTag("div").size());
      }
  }