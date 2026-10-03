package org.jsoup.select;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**

 - Tests for the comma-separation bug in Selector and QueryParser (issue #179).
 - The bug causes incorrect parssing when commas appear inside attribute selectors
 - or when combintor groups are combined with commas.
  */
 public class CommBugTest {
  /**
  - Union of simple tag selectors via comma: both tags should be found.
    */
   @Test
   public void testSimpleCommaUnion() {
   Document doc = Jsoup.parse("<div>one</div><p>two</p>");
   Elements els = doc.select("div, p");
   assertEquals("comma union of div and p should select both", 2, els.size());
   }
  /**
  - Attribute selector containing a comma in the value: must be treated as a single selector,
  - not split. Fixes PatternSyntaxException and wrong splitting.
    */
   @Test
   public void testCommaInsideAttributeSelector() {
   Document doc = Jsoup.parse("<p title='a,b'>text</p>");
   Elements els = doc.select("[title='a,b']");
   assertEquals("attribute with comma value should match one element", 1, els.size());
   }
  /**
  - Attribute selector with ~= operator containing a comma in the regex value.
  - The original bug caused PatternSyntaxException when splitting the comma.
    */
   @Test
   public void testCommaInRegexAttribute() {
   Document doc = Jsoup.parse("<p title='a,b'>text</p>");
   // The ~= operator compiles the value as a regex. Ensure no exception.
   Elements els = null;
   try {
   els = doc.select("[title~=a,b]");
   } catch (Exception e) {
   fail("Should not throw on comma inside ~= value: " + e.getMessage());
   }
   assertNotNull(els);
   // The regex "a,b" should match the value "a,b"
   assertEquals(1, els.size());
   }
  /**
  - Attribute selector with
   *= (contains) containing comma.
    /
   @Test
   public void testCommaInContainsAttribute() {
   Document doc = Jsoup.parse("<p title='foo,bar'>text</p><p title='barfoo'>other</p>");
   Elements els = doc.select("[title=foo,bar]");
   assertEquals("contains attribute with comma should match only the first element", 1, els.size());
   }
  /**
  - Attribute selector with ^= (starts-with) containing comma.
    */
   @Test
   public void testCommaInStartsWithAttribute() {
   Document doc = Jsoup.parse("<p title='a,b'>text</p><p title='a,c'>other</p>");
   Elements els = doc.select("[title^='a,']");
   assertEquals("starts-with attribute with comma should match both elements", 2, els.size());
   }
  /**
  - Mixing combinators with commas: ensures correct grouping and element count.
  - The bug caused extra elements to be matched (expected 2, got 3).
    */
   @Test
   public void testMixCombinatorGroup() {
   Document doc = Jsoup.parse("<div><p>P</p><div>D</div></div><p>P2</p><div>D2</div>");
   // "div p + div" selects div immediately after p inside div (D)
   // "p + div" selects div immediately after p at any level (D2)
   Elements els = doc.select("div p + div, p + div");
   assertEquals("mix of combintor groups should select 2 elements", 2, els.size());
   }
  /**
  - Comma with multiple combinators and depth.
    */
   @Test
   public void testComplexCommaWithCombinators() {
   Document doc = Jsoup.parse("<div><span>1</span></div><p>2</p>");
   Elements els = doc.select("div > span, p + div");
   assertEquals("div > span selects span, p + div selects none", 1, els.size());
   }
  /**
  - Multiple commas with simple selectors.
    */
   @Test
   public void testMultipleCommas() {
   Document doc = Jsoup.parse("<div/><p/><span/><a/>");
   Elements els = doc.select("div, p, span, a");
   assertEquals("four comma-separated selectors select 4 elements", 4, els.size());
   }
  /**
  - Empty subquery (trailing comma) should be ignored / handled gracefully.
  - The parser should either throw or return only the valid parts.
  - In the buggy version, trailing comma may cause unexpected errors.
    */
   @Test
   public void testTrailingComma() {
   Document doc = Jsoup.parse("<div>test</div>");
   // Accept that trailing comma is tolerated or throws; here we verify that if tolerated it works.
   Elements els = doc.select("div,");
   // If the parser treats the empty second part as invalid and throws, this test will fail
(expected).
   // We expect at least the div to be matched if the parser is lenient.
   assertTrue("trailing comma should at least select the first part", els.size() >= 1);
   }
  /**
  - Leading comma: should either ignore empty first part or throw.
    */
   @Test(expected = Selector.SelectorParseException.class)
   public void testLeadingCommaThrows() {
   Document doc = Jsoup.parse("<div>test</div>");
   doc.select(", div");
   }
  /**
  - Unclosed bracket with comma inside a later selector should not break the first parts.
  - The original bug might cascade the error.
    */
   @Test
   public void testCommaWithUnclosedBracket() {
   Document doc = Jsoup.parse("<div>test</div><p>other</p>");
   try {
   Elements els = doc.select("div, [attr");
   fail("should have thrown parsing exception due to unclosed bracket");
   } catch (Selector.SelectorParseException expected) {
   // expected
   } catch (Exception e) {
   fail("Expected SelectorParseException but got " + e);
   }
   }

}
