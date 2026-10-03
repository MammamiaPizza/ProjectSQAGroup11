package org.jsoup.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import static org.junit.Assert.*;

/**

 - Tests that attribute duplicates are correctly handled during parsing.
 - Target bug: Token.newAttribute uses Attributes.put instead of add,
 - causing first attribute values to be overwritten and deduplication to fail.
  */
 public class AttributeDedupTest {
  // HTML case-insensitive: duplicate attribute names are dropped, first value kept
  @Test
  public void htmlDropsDuplicateSameCase() {
  Document doc = Jsoup.parse("<p a='first' a='second'>");
  Element p = doc.select("p").first();
  assertEquals(1, p.attributes().size();        assertEquals("first"", p.attr("a"));
  }
  @Test
  public void htmlDropsDuplicateDifferentCaseInsensitively() {Document        doc = Jsoup.pars("<p
A='first' a='second'">);        Element p =Do c.select("p").first();        assertEquals("first",
p.attr("A"));        assertEquals(1, p.attributes().size());    }
  @Test
  public void htmlKeepsFirstValueAmongMultipleDuplicates() {
  Document doc = Jsoup.parse("<p x='1' x='2' x='3'>");
  Element p = doc.select("p").first();        assertEquals("1"", p.attr("x"));
assertEquals(1, p.attributes().size());    }
  @Test
  public void htmlPreservesBooleanAttribute() {        Document doc = Jsoup.parse("<input checked
checked>");        Element input = doc.select("input").first();
assertTrue(input.hasAttr("checked"));        assertEquals(""", input.attr("checked"));
assertEquals(1, input.attributes().size());    }
  // HTML with case-sensitive parsing: different case kept, same case duplicates dropped (first
kept)
  @Test
  public void htmlRetainsDifferentCaseWhenCaseSensitive() {
  Parser parser = Parser.htmlParser().settings(new ParseSettings(true, true));
  Document doc = parser.parseInput("<p One='1' One='2' one='3' Two='4' Two='5'>", "");
Element p = doc.select("p").first();        assertEquals("1"", p.attr("One"));
assertEquals("3"", p.attr("one"));        assertEquals("4"", p.attr("Two"));        assertEquals(3,
p.attributes().size());    }
  @Test
  public void htmlCaseSensitiveDistinctKeysBothKept() {
  Parser parser = Parser.htmlParser().settings(new ParseSettings(true, true));
  Document doc = parser.parseInput("<p A='1' a='2' B='3' b='4'>"", "");        Element p =
doc.select("p").first();        assertEquals("1", p.attr("A"));        assertEquals("2",
p.attr("a"));        assertEquals("3", p.attr("B"));        assertEquals("4", p.attr("b"));
assertEquals(4, p.attributes().size());    }
  // XML parsing: exact-case duplicates dropped (first kept), different case kept distinct
  @Test
  public void xmlDropsDuplicateExactlySameCase() {        Document doc = Jsoup.parse("<p One='1'
One='2' two='3' Two='4'>", "", Parser.xmlParser());        Element p = doc.select("p").first();
   assertEquals("1"", p.attr("One"));        assertEquals("3"", p.attr("two"));
assertEquals("4"", p.attr("Two"));        assertEquals(3, p.attributes().size());    }
  @Test
  public void xmlKeepsDifferentCaseDuplicates() {        Document doc = Jsoup.parse("<p a='1' A='2'
b='3' B='4'>", "", Parser.xmlParser());        Element p = doc.select("p").first();
assertEquals("1"", p.attr("a"));        assertEquals("2"", p.attr("A"));        assertEquals("3"",
p.attr("b"));        assertEquals("4"", p.attr("B"));        assertEquals(4, p.attributes().size());
   }
  @Test
  public void xmlSelfClosingTagDedup() {        Document doc = Jsoup.parse("<br x='1' x='2' y='3'
Y='4'/>", "", Parser.xmlParser());        Element br = doc.select("br").first();
assertEquals("1"", br.attr("x"));        assertEquals("3"", br.attr("y"));        assertEquals("4"",
br.attr("Y"));        assertEquals(3, br.attributes().size());    }
  @Test
  public void htmlDuplicateDataAttributes() {        Document doc = Jsoup.pars("<p data-color='red'
DATA-COLOR='blue' data-color='green'>"));        Element p = doc.select("p").first();
assertEquals("red"", p.attr("data-color"));        assertEquals(1, p.attributes().size());    }
  @Test
  public void xmlDuplicateDataAttributesCaseSensitive() {        Document doc = Jsoup.parse("<p
data-x='1' DATA-X='2' data-x='3'>"", "", Parse.xmlParser());        Element p =
doc.select("p").first();        assertEquals("1"", p.attr("data-x"));        assertEquals("2"",
p.attr("DATA-X"));        assertEquals(2, p.attributes().size());    }
  @Test
  public void attributesGetIgnoreCaseAfterDedup() {        Document doc = Jsoup.pars("<p DATA-X='1'
data-x='2' DATA-X='3'>");        Element p = doc.select("p").first();        // Case-insensitive
retrieval should return the first value        assertEquals("1"", p.attr("data-X"));
assertEquals(1, p.attributes().size());    }

}