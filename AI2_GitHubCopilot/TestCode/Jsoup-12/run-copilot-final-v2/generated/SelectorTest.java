package org.jsoup.select;

 import static org.junit.Assert.*;

 import org.jsoup.Jsoup;
 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.jsoup.select.Elements;
 import org.junit.Before;
 import org.junit.Test;

 public class SelectorTest {

     private Document doc;

     @Before
     public void setUp() {
         String html = "<html><body>"
                 + "<div id='1' attr='x|y'>A</div>"
                 + "<div id='2' attr='x'>B</div>"
                 + "<div id='3' attr='alpha'>C</div>"
                 + "<div id='4' attr='alpha|beta'>D</div>"
                 + "<div id='5' attr='beta'>E</div>"
                 + "<div id='6' attr='start-mid|end'>F</div>"
                 + "<div id='7' attr='x|'>G</div>"
                 + "<div id='8' attr='|y'>H</div>"
                 + "<div id='9' attr='orphan'>I</div>"
                 + "<span id='10' attr='x|y'>J</span>"
                 + "</body></html>";
         doc = Jsoup.parse(html);
     }

     @Test
     public void testByAttributeExactMatchWithPipe() {
         // [attr='x|y'] should match element with literal "x|y"
         Elements els = doc.select("[attr='x|y']");
         assertEquals(2, els.size()); // div#1 and span#10
         assertEquals("1", els.first().id());
     }

     @Test
     public void testByAttributeNotEqualWithPipe() {
         // valid != selector where value contains |
         Elements els = doc.select("[attr!='x|y']");
         assertTrue(els.size() > 0);
         for (Element el : els) {
             assertFalse("x|y".equals(el.attr("attr")));
         }
     }

     @Test
     public void testByAttributeStartingWithPipeInValue() {
         // ^= with | inside the value
         Elements els = doc.select("[attr^='alpha|']");
         assertEquals(1, els.size());
         assertEquals("4", els.first().id());
     }

     @Test
     public void testByAttributeEndingWithPipeInValue() {
         // $= with | inside the value
         Elements els = doc.select("[attr$='|beta']");
         assertEquals(1, els.size());
         assertEquals("4", els.first().id());
     }

     @Test
     public void testByAttributeContainingPipe() {
         // *= with pipe in substring
         Elements els = doc.select("[attr*='|']");
         assertEquals(4, els.size()); // ids: 1, 4, 7, 8
     }

     @Test
     public void testByAttributeRegexMatchWithPipe() {
         // ~= interpret value as regex: "x|y" matches "x" or "y"
         Elements els = doc.select("[attr~=x|y]");
         assertEquals(3, els.size()); // ids 1, 2, 10 (x|y, x, x|y)
         assertTrue(els.stream().anyMatch(e -> "2".equals(e.id())));
     }

     @Test
     public void testByAttributeRegexPipeAtStart() {
         // regex starting with |
         Elements els = doc.select("[attr~=|y]");
         assertEquals(3, els.size()); // ids 1 (x|y), 8 (|y), 10 (x|y)
         assertTrue(els.stream().anyMatch(e -> "8".equals(e.id())));
     }

     @Test
     public void testByAttributeRegexPipeAtEnd() {
         // regex ending with |
         Elements els = doc.select("[attr~=x|]");
         assertEquals(2, els.size()); // ids 1 (x|y), 7 (x|)
         assertTrue(els.stream().anyMatch(e -> "7".equals(e.id())));
     }

     @Test
     public void testByAttributeRegexWithConsecutivePipes() {
         // regex contains ||
         Elements els = doc.select("[attr~=x||b]");
         // none of the test data match, but parsing must succeed
         assertEquals(0, els.size());
     }

     @Test
     public void testByAttributeRegexCombined() {
         // the original trigger test: attribute regex combined with tag and descendant
         Elements els = doc.select("div[attr~=x|y]");
         assertEquals(2, els.size()); // only divs with id 1 and 2
     }

     @Test
     public void testByAttributeRegexWithDescendantCombinator() {
         // ensure descendant combinator does not break pipe parsing
         Elements els = doc.select("body [attr~=x|y]");
         assertEquals(3, els.size()); // both div and span inside body
     }

     @Test
     public void testByAttributeRegexAndAnotherAttribute() {
         // chaining attribute selectors: [attr~=x|y][id] should find elements with both an id and
matching attr
         Elements els = doc.select("[attr~=x|y][id]");
         assertEquals(3, els.size()); // ids 1,2,10
     }
 }
