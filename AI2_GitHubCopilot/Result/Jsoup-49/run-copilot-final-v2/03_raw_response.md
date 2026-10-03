package org.jsoup.nodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class NodeTest {

 @Test
 public void testAppendExistingChildMovesToEnd() {
     Document doc = Jsoup.parse("<body><div id='d1'>a</div><div id='d2'>b</div><div
id='d3'>c</div></body>");
     Element body = doc.body();
     Element d1 = doc.getElementById("d1");
     body.appendChild(d1); // move to end
     assertEquals("<body>\n <div id=\"d2\">\n  b\n </div>\n <div id=\"d3\">\n  c\n </div>\n <div
id=\"d1\">\n  a\n </div>\n</body>",
             body.outerHtml());
 }

 @Test
 public void testAppendMultipleExistingChildrenPreservesOrderNoLoss() {
     Document doc = Jsoup.parse("<body><div id='d1'></div><div id='d2'></div><div
id='d3'>Check</div><div id='d4'></div></body>");
     Element body = doc.body();
     Element d1 = doc.getElementById("d1");
     Element d2 = doc.getElementById("d2");
     Element d4 = doc.getElementById("d4");

     body.appendChild(d4);
     body.appendChild(d1);
     body.appendChild(d2);

     assertEquals("<body>\n <div id=\"d3\">\n  Check\n </div>\n <div id=\"d4\">\n </div>\n <div
id=\"d1\">\n </div>\n <div id=\"d2\">\n </div>\n</body>",
             body.outerHtml());
     // all children still present
     assertEquals(4, body.childNodeSize());
 }

 @Test
 public void testInsertChildMovesToIndex() {
     Document doc = Jsoup.parse("<body><div id='d1'>a</div><div id='d2'>b</div><div
id='d3'>c</div></body>");
     Element body = doc.body();
     Element d3 = doc.getElementById("d3");
     Element d1 = doc.getElementById("d1");

     // insert d3 at position where d1 currently sits (move d3 before d1)
     body.insertChildren(d1.siblingIndex(), Collections.singletonList(d3));

     assertEquals("<body>\n <div id=\"d3\">\n  c\n </div>\n <div id=\"d1\">\n  a\n </div>\n <div
id=\"d2\">\n  b\n </div>\n</body>",
             body.outerHtml());
 }

 @Test
 public void testUnwrapPreservesTextOrder() {
     Document doc = Jsoup.parse("<div>Hello <span>world</span>!</div>");
     Element span = doc.select("span").first();
     Node firstChild = span.unwrap(); // unwrap <span>, text "world" goes into parent div

     // firstChild should be the text node "world"
     assertNotNull(firstChild);
     assertEquals("world", firstChild.outerHtml().trim()); // TextNode outerHtml is its text
     // parent div now contains the correct sequence
     assertEquals("<div>\n Hello world !\n</div>", doc.select("div").first().outerHtml());
 }

 @Test
 public void testRemoveChild_parentNullAfterRemove() {
     Document doc = Jsoup.parse("<div><p>text</p></div>");
     Element p = doc.select("p").first();
     p.remove();
     assertNull(p.parent());
 }

 @Test
 public void testRemoveChild_remainingSiblingsReindexed() {
     Document doc = Jsoup.parse("<div><p id='p1'>first</p><p id='p2'>second</p><p
id='p3'>third</p></div>");
     Element div = doc.select("div").first();
     Element p2 = doc.getElementById("p2");
     p2.remove();

     // after removal, p3 should now be at index 1 (previously index 2)
     Element p3 = doc.getElementById("p3");
     assertEquals(1, p3.siblingIndex());
 }

 @Test
 public void testAfterMovesExistingSibling() {
     Document doc = Jsoup.parse("<div><p id='a'>A</p><p id='b'>B</p><p id='c'>C</p></div>");
     Element div = doc.select("div").first();
     Element a = doc.getElementById("a");
     Element c = doc.getElementById("c");
     a.after(c); // moves c after a
     // Expected order: a, c, b
     assertEquals(0, a.siblingIndex());
     assertEquals(1, c.siblingIndex());
     assertEquals(2, doc.getElementById("b").siblingIndex());
     assertNull(c.nextSibling().nextSibling()); // only 3 children
 }

 @Test
 public void testBeforeMovesExistingSibling() {
     Document doc = Jsoup.parse("<div><p id='a'>A</p><p id='b'>B</p><p id='c'>C</p></div>");
     Element div = doc.select("div").first();
     Element b = doc.getElementById("b");
     Element c = doc.getElementById("c");
     b.before(c); // moves c before b
     // Expected order: a, c, b
     assertEquals(1, c.siblingIndex());
     assertEquals(2, b.siblingIndex());
 }

 @Test
 public void testWrap_withoutParent_doesNotFail() {
     Document doc = Jsoup.parse("<p>text</p>");
     Element p = doc.select("p").first();
     p.remove(); // orphan
     Node wrapped = p.wrap("<span></span>");
     assertNull(wrapped); // no parent, wrap should return null (as per code)
     assertNull(p.parent());
 }

 @Test
 public void testWrap_remainderChildrenAppendedToWrapElement() {
     Document doc = Jsoup.parse("<div><p id='p1'>content</p></div>");
     Element p1 = doc.getElementById("p1");

     // wrap with multiple elements; the first becomes the wrapper, remainder appended as children
     p1.wrap("<span><b></b></span><i>extra</i>");
     Element span = p1.parent(); // span is now parent of p1
     assertNotNull(span);
     assertEquals("span", span.nodeName());
     // The <i> extra should be appended as a child of span (after p1)
     // So span's children: b? Actually wrap structure: <span><b></b></span>, so b is child of span,
and p1 is added inside deepest (= b). Then <i>extra</i> is appended to span.
     // After wrap: span contains b, and b contains p1; span also contains i.
     Node b = span.childNode(0);
     assertEquals("b", b.nodeName());
     assertTrue(b.childNodes().contains(p1));
     assertEquals("i", span.childNode(1).nodeName());
 }

 @Test
 public void testReplaceChildViaReplaceWith_inSameParent() {
     Document doc = Jsoup.parse("<div><p id='a'>A</p><p id='b'>B</p></div>");
     Element a = doc.getElementById("a");
     Element b = doc.getElementById("b");
     a.replaceWith(b); // replaces a with b; b should be moved from its position to a's position
     // After: single p with id='b' (since b moved)
     List<Element> children = doc.select("div").first().children();
     assertEquals(1, children.size());
     assertEquals("b", children.get(0).id());
     assertNull(a.parent()); // a removed
 }

 @Test
 public void testWrapAndUnwrap_preservesDeepStructure() {
     Document doc = Jsoup.parse("<div>Hello <p id='inner'>world</p></div>");
     Element inner = doc.getElementById("inner");
     inner.wrap("<span></span>");
     Element span = inner.parent();
     assertEquals("span", span.nodeName());
     // Unwrap the span
     span.unwrap();
     assertEquals("world", doc.select("p").first().text());
     // p should now be direct child of div again
     assertEquals("div", inner.parent().nodeName());
 }

}