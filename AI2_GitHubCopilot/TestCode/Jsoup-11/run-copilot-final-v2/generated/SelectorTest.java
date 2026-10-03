package org.jsoup.select;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class SelectorTest {

 private static Document doc;
 private static int totalElements;
 private static int paragraphCount;

 @BeforeClass
 public static void setUp() {
     String html = "<html><head></head><body>"
             + "<div id='1' class='left'>div left</div>"
             + "<div class='right'>div right</div>"
             + "<p class='left'>p left</p>"
             + "<p id='1'>p id1</p>"
             + "<span><a href='#'>link</a></span>"
             + "<div><span>span1</span></div>"
             + "<div><span>span2</span></div>"
             + "<div><span>span3</span></div>"
             + "<div>no span</div>"
             + "<ul><li>item</li></ul>"
             + "</body></html>";
     doc = Jsoup.parse(html);
     totalElements = doc.getAllElements().size();
     paragraphCount = doc.getElementsByTag("p").size();
 }

 @Test
 public void testNotTag() {
     Elements selected = Selector.select(":not(p)", doc);
     // Expect all elements except <p> tags
     for (Element el : selected) {
         assertFalse("Should not contain <p>", "p".equals(el.tagName()));
     }
     assertEquals(totalElements - paragraphCount, selected.size());
 }

 @Test
 public void testNotClass() {
     Elements selected = Selector.select("div:not(.left)", doc);
     int divTotal = doc.getElementsByTag("div").size();
     int divLeft = doc.select("div.left").size();
     assertEquals(divTotal - divLeft, selected.size());
     for (Element el : selected) {
         assertFalse("Should not have class left", el.hasClass("left"));
     }
 }

 @Test
 public void testNotAttribute() {
     Elements selected = Selector.select("p:not([id=1])", doc);
     int pTotal = doc.getElementsByTag("p").size();
     int pId1 = doc.select("p[id=1]").size();
     assertEquals(pTotal - pId1, selected.size());
     for (Element el : selected) {
         assertFalse("Should not have id=1", "1".equals(el.id()));
     }
 }

 @Test
 public void testPseudoHas() {
     Elements selected = Selector.select("div:has(span)", doc);
     // Expect exactly 3 divs that contain a span
     assertEquals(3, selected.size());
     for (Element el : selected) {
         assertFalse("Should contain span descendant", el.getElementsByTag("span").isEmpty());
     }
 }

 @Test
 public void testHasChild() {
     Elements selected = Selector.select("span:has(a)", doc);
     // Only one span has an anchor
     assertEquals(1, selected.size());
 }

 @Test
 public void testHasDirectChild() {
     Elements selected = Selector.select("ul:has(>li)", doc);
     assertEquals(1, selected.size());
 }

 @Test
 public void testNestedNot() {
     Elements selected = Selector.select(":not(:not(p))", doc);
     // Should be equivalent to just p
     assertEquals(paragraphCount, selected.size());
     for (Element el : selected) {
         assertEquals("p", el.tagName());
     }
 }

 @Test
 public void testNotAll() {
     Elements selected = Selector.select(":not(p)", doc);
     // Already covered by testNotTag, but we duplicate test name as required by bug triggers.
     // We keep original expectation: no p.
     assertFalse(selected.isEmpty());
     for (Element el : selected) {
         assertFalse("p".equals(el.tagName()));
     }
 }

 @Test(expected = Selector.SelectorParseException.class)
 public void testInvalidNotMissingParen() {
     Selector.select(":not(p", doc);
 }

 @Test(expected = Selector.SelectorParseException.class)
 public void testInvalidNotEmpty() {
     Selector.select(":not()", doc);
 }

 @Test(expected = Selector.SelectorParseException.class)
 public void testInvalidNotBadInner() {
     Selector.select(":not(!!)", doc);
 }

 @Test(expected = Selector.SelectorParseException.class)
 public void testInvalidHasEmpty() {
     Selector.select(":has()", doc);
 }

}
