package org.jsoup.parser;

 import org.jsoup.nodes.Document;
 import org.jsoup.nodes.Element;
 import org.junit.Test;
 import static org.junit.Assert.*;

 public class ParserTest {

     @Test
     public void parsesQuiteRoughAttributes() {
         // main trigger test: rough attributes that previously caused
StringIndexOutOfBoundsException
         String html = "<a href=foo bar=qux>";
         Document doc = Parser.parse(html, "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         assertNotNull(doc);
         Element body = doc.body();
         assertEquals("expected one child element", 1, body.children().size());
         Element a = body.child(0);
         assertEquals("a", a.tagName());
         assertTrue(a.hasAttr("href"));
         assertEquals("foo", a.attr("href"));
         assertTrue(a.hasAttr("bar"));
         assertEquals("qux", a.attr("bar"));
     }

     @Test
     public void testNormalAttributeQuoted() {
         Document doc = Parser.parse("<a href=\"]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;\">link</a>", ]8;;
"]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         Element a = doc.body().child(0);
         assertEquals("a", a.tagName());
         assertEquals("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;", a.attr("href"));]8;;
         assertEquals("link", a.text());
     }

     @Test
     public void testAttributeSingleQuote() {
         Document doc = Parser.parse("<a href=']8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;'>link</a>", "]8;;]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         Element a = doc.body().child(0);
         assertEquals("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;", a.attr("href"));]8;;
     }

     @Test
     public void testAttributeEmptyValue() {
         Document doc = Parser.parse("<a href=\"\">link</a>", "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         Element a = doc.body().child(0);
         assertTrue(a.hasAttr("href"));
         assertEquals("", a.attr("href"));
     }

     @Test
     public void testAttributeNoValue() {
         Document doc = Parser.parse("<a href>link</a>", "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         Element a = doc.body().child(0);
         assertTrue(a.hasAttr("href"));
         assertEquals("", a.attr("href"));
     }

     @Test
     public void testAttributeUnquotedValue() {
         Document doc = Parser.parse("<a href=]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;>link</a>", "]8;;]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         Element a = doc.body().child(0);
         assertEquals("]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;", a.attr("href"));]8;;
     }

     @Test
     public void testRoughAttributesGreaterThanInQuotedValue() {
         // quoted value containing '>' must be preserved, not interpreted as tag end
         Document doc = Parser.parse("<a value=\"a > b\">x</a>", "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         Element a = doc.body().child(0);
         assertEquals("a > b", a.attr("value"));
     }

     @Test
     public void testRoughAttributesUnclosedQuoteAtEnd() {
         // unclosed single-quoted value at end of input should not throw
         Document doc = Parser.parse("<a href='unclosed", "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         assertNotNull(doc);
         Element a = doc.body().child(0);
         assertTrue(a.hasAttr("href"));
         assertNotNull(a.attr("href")); // value may be partial, just check no exception
     }

     @Test
     public void testRoughAttributesUnclosedDoubleQuoteAtEnd() {
         // unclosed double-quoted value at end of input should not throw
         Document doc = Parser.parse("<a href=\"unclosed", "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         assertNotNull(doc);
         Element a = doc.body().child(0);
         assertTrue(a.hasAttr("href"));
         assertNotNull(a.attr("href"));
     }

     @Test
     public void testTrailingEqualsAtEnd() {
         // attribute with trailing '=' and no value before end of input
         Document doc = Parser.parse("<a href=>link</a>", "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         assertNotNull(doc);
         Element a = doc.body().child(0);
         assertTrue(a.hasAttr("href"));
         assertEquals("", a.attr("href"));
     }

     @Test
     public void testRepeatedRoughAttributes() {
         // several attributes with mixed quoting and spacing
         String html = "<img src=foo bar=\"b az\" qux=zzz>";
         Document doc = Parser.parse(html, "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         assertNotNull(doc);
         Element img = doc.body().child(0);
         assertEquals("img", img.tagName());
         assertEquals("foo", img.attr("src"));
         assertEquals("b az", img.attr("bar"));
         assertEquals("zzz", img.attr("qux"));
     }

     @Test
     public void testRoughAttributesInBodyFragment() {
         // rough attributes with body fragment parsing
         Document doc = Parser.parseBodyFragment("<a href=foo bar=qux>", "]8;id=md-3j18as;http://example.comhttp://example.com]8;;]8;;");]8;;
         assertNotNull(doc);
         Element body = doc.body();
         assertEquals(1, body.children().size());
         Element a = body.child(0);
         assertEquals("a", a.tagName());
         assertTrue(a.hasAttr("href"));
         assertEquals("foo", a.attr("href"));
         assertTrue(a.hasAttr("bar"));
         assertEquals("qux", a.attr("bar"));
     }
 }