package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;
import static org.junit.Assert.*;

public class HtmlTreeBuilderStackOverflowTest {

 private String generateNestedSpans(int depth) {
     StringBuilder sb = new StringBuilder(depth * 11 + 4);
     for (int i = 0; i < depth; i++) {
         sb.append("<span>");
     }
     sb.append("text");
     for (int i = 0; i < depth; i++) {
         sb.append("</span>");
     }
     return sb.toString();
 }

 // Returns the innermost span element after navigating from root span
 private Element innermostSpan(Element outerSpan, int depth) {
     Element cur = outerSpan;
     for (int i = 0; i < depth - 1; i++) {
         assertNotNull(cur);
         assertEquals(1, cur.childrenSize());
         cur = cur.child(0);
     }
     return cur;
 }

 @Test
 public void testShallowSpans() {
     String html = generateNestedSpans(3);
     Document doc = Jsoup.parse(html);
     Element outer = doc.body().child(0);

     Element innermost = innermostSpan(outer, 3);
     assertEquals("span", innermost.tagName());
     assertEquals("text", innermost.ownText());
 }

 @Test
 public void testSingleSpan() {
     Document doc = Jsoup.parse("<span>text</span>");
     Element span = doc.body().child(0);
     assertEquals("span", span.tagName());
     assertEquals("text", span.ownText());
 }

 @Test
 public void testSpansAtMaxScopeDepth_100() {
     String html = generateNestedSpans(100);
     Document doc = Jsoup.parse(html);
     Element outer = doc.body().child(0);
     assertNotNull(outer);
     // Verify nesting depth without crashing
     Element cur = outer;
     for (int i = 0; i < 100; i++) {
         assertEquals("span", cur.tagName());
         if (i < 99) {
             assertEquals(1, cur.childrenSize());
             cur = cur.child(0);
         }
     }
     assertEquals("text", cur.ownText());
 }

 @Test
 public void testSpansAtMaxScopeDepth_101() {
     String html = generateNestedSpans(101);
     Document doc = Jsoup.parse(html);
     Element outer = doc.body().child(0);
     assertNotNull(outer);
     // Should parse without StackOverflowError; structure may be truncated but not required
     // At minimum, no exception.
 }

 @Test
 public void testSpansBeyondMaxScopeDepth_500() {
     String html = generateNestedSpans(500);
     // Parsing must complete without StackOverflowError.
     Document doc = Jsoup.parse(html);
     assertNotNull(doc.body());
 }

 @Test
 public void testEmptySpan() {
     Document doc = Jsoup.parse("<span></span>");
     Element span = doc.body().child(0);
     assertEquals("span", span.tagName());
     assertEquals(0, span.childrenSize());
     assertEquals("", span.ownText());
 }

 @Test
 public void testSelfClosingSpan() {
     // Span is known, not void – self-closing should not crash
     Document doc = Jsoup.parse("<span/>");
     assertNotNull(doc.body());
     assertFalse(doc.body().children().isEmpty());
 }

 @Test
 public void testMixedInlineElementsDeep() {
     StringBuilder sb = new StringBuilder();
     for (int i = 0; i < 80; i++) {
         sb.append("<span>");
         if (i % 2 == 0) sb.append("<b>");
     }
     sb.append("hello");
     for (int i = 0; i < 80; i++) {
         if (i % 2 == 0) sb.append("</b>");
         sb.append("</span>");
     }
     Document doc = Jsoup.parse(sb.toString());
     assertNotNull(doc.body().child(0)); // no exception
 }

 @Test
 public void testUnclosedSpansReconstruction() {
     // Render tree builder must reconstruct active formatting elements without recursion blow‑up
     StringBuilder sb = new StringBuilder();
     for (int i = 0; i < 100; i++) {
         sb.append("<span>");
     }
     // No closing tags – parser tries to reconstruct
     Document doc = Jsoup.parse(sb.toString());
     assertNotNull(doc.body()); // no StackOverflowError
 }

 @Test
 public void testSpanInListDeep() {
     StringBuilder sb = new StringBuilder("<ul><li>");
     for (int i = 0; i < 120; i++) {
         sb.append("<span>");
     }
     sb.append("deep");
     for (int i = 0; i < 120; i++) {
         sb.append("</span>");
     }
     sb.append("</li></ul>");
     Document doc = Jsoup.parse(sb.toString());
     // Should not crash; list context may trigger additional scope checks
     assertNotNull(doc.select("span").first());
 }

}
