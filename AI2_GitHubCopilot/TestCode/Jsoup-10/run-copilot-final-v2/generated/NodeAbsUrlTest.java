package org.jsoup.nodes;

 import static org.junit.Assert.*;
 import org.junit.Before;
 import org.junit.Test;
 import org.jsoup.parser.Tag;
 import org.jsoup.nodes.Attributes;

 /**
  * Tests for {@link Node#absUrl(String)} focusing on relative URL resolution,
  * particularly the bug where a relative URL containing a file name followed
  * by a query string loses the file portion (issue #49).
  */
 public class NodeAbsUrlTest {

     private Element element;

     @Before
     public void setUp() {
         // base URI is a directory (trailing slash)
         element = new Element(Tag.valueOf("a"),
                 "http://jsoup.org/path/",
                 new Attributes());
     }

     // ---- Core bug-triggering scenarios ----

     @Test
     public void absUrl_relativeFileWithQuery() {
         // The exact scenario from the bug report:
         // base: http://jsoup.org/path/
         // rel:  file?foo
         // expected: http://jsoup.org/path/file?foo
         element.attr("href", "file?foo");
         assertEquals("http://jsoup.org/path/file?foo",
                      element.absUrl("href"));
     }

     @Test
     public void absUrl_relativeFileWithMultiParamQuery() {
         element.attr("href", "page?x=1&y=2");
         assertEquals("http://jsoup.org/path/page?x=1&y=2",
                      element.absUrl("href"));
     }

     @Test
     public void absUrl_relativeFileWithFragment() {
         // file + fragment should not be affected by the query-only bug
         element.attr("href", "file#bar");
         assertEquals("http://jsoup.org/path/file#bar",
                      element.absUrl("href"));
     }

     @Test
     public void absUrl_relativeFileWithQueryAndFragment() {
         element.attr("href", "file?foo#bar");
         assertEquals("http://jsoup.org/path/file?foo#bar",
                      element.absUrl("href"));
     }

     @Test
     public void absUrl_relativeQueryOnly_presolvesCorrectly() {
         // query only (no file) on a directory base should work
         element.attr("href", "?foo");
         assertEquals("http://jsoup.org/path/?foo",
                      element.absUrl("href"));
     }

     @Test
     public void absUrl_relativeFragmentOnly() {
         element.attr("href", "#section");
         assertEquals("http://jsoup.org/path/#section",
                      element.absUrl("href"));
     }

     // ---- Base URI variations ----

     @Test
     public void absUrl_baseUriWithoutTrailingSlash() {
         // base treated as file, so relative "file" replaces last path segment
         Element el = new Element(Tag.valueOf("a"),
                 "http://jsoup.org/path",
                 new Attributes());
         el.attr("href", "other");
         assertEquals("http://jsoup.org/other",
                      el.absUrl("href"));
     }

     @Test
     public void absUrl_baseUriWithQuery_relativeResolves() {
         Element el = new Element(Tag.valueOf("a"),
                 "http://jsoup.org/path/?old=1",
                 new Attributes());
         el.attr("href", "new?x=2");
         assertEquals("http://jsoup.org/path/new?x=2",
                      el.absUrl("href"));
     }

     // ---- Attribute value is already absolute ----

     @Test
     public void absUrl_absoluteAttribute() {
         element.attr("href", "http://other.com/abs");
         assertEquals("http://other.com/abs",
                      element.absUrl("href"));
     }

     // ---- Missing or malformed key ----

     @Test
     public void absUrl_missingAttribute_returnsEmptyString() {
         // "href" not set
         assertEquals("", element.absUrl("href"));
     }

     @Test(expected = IllegalArgumentException.class)
     public void absUrl_emptyKey_throwsIllegalArgumentException() {
         element.absUrl("");
     }

     @Test(expected = IllegalArgumentException.class)
     public void absUrl_nullKey_throwsIllegalArgumentException() {
         element.absUrl(null);
     }
 }
