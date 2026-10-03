package org.jsoup.parser;

 import static org.junit.Assert.*;

 import org.junit.Test;

 /**
  * Unit tests for {@link Tag}, focusing on whitespace preservation and tag registration,
  * validating the bug that textarea does not preserve whitespace.
  */
 public class TagTest {

     @Test
     public void testWhitespacePreservingTags() {
         // Tags that should preserve whitespace
         assertTrue("pre must preserve whitespace", Tag.valueOf("pre").preserveWhitespace());
         assertTrue("textarea must preserve whitespace",
                 Tag.valueOf("textarea").preserveWhitespace());
         assertTrue("plaintext must preserve whitespace",
                 Tag.valueOf("plaintext").preserveWhitespace());
         assertTrue("title must preserve whitespace", Tag.valueOf("title").preserveWhitespace());
     }

     @Test
     public void testDivDoesNotPreserveWhitespace() {
         assertFalse("div must not preserve whitespace", Tag.valueOf("div").preserveWhitespace());
     }

     @Test
     public void testUnknownTagDoesNotPreserveWhitespace() {
         assertFalse("unknown tag must not preserve whitespace",
                 Tag.valueOf("foo").preserveWhitespace());
     }

     @Test(expected = IllegalArgumentException.class)
     public void testValueOfNullThrowsException() {
         Tag.valueOf(null);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testValueOfEmptyThrowsException() {
         Tag.valueOf("");
     }

     @Test(expected = IllegalArgumentException.class)
     public void testValueOfBlankThrowsException() {
         Tag.valueOf("   ");
     }

     @Test
     public void testIsKnownTag() {
         assertTrue("div must be known", Tag.isKnownTag("div"));
         assertFalse("foo must not be known", Tag.isKnownTag("foo"));
         assertTrue("tag object itself must be known", Tag.valueOf("div").isKnownTag());
         assertFalse("unknown tag object must not be known", Tag.valueOf("baz").isKnownTag());
     }

     @Test
     public void testValueOfCaseInsensitive() {
         assertSame("valueOf must return identical instance for known tag ignoring case",
                 Tag.valueOf("textarea"), Tag.valueOf("TEXTAREA"));
     }

     @Test
     public void testBlockAndInlineProperties() {
         Tag div = Tag.valueOf("div");
         assertTrue("div must be block", div.isBlock());
         assertFalse("div must not be inline", div.isInline());

         Tag span = Tag.valueOf("span");
         assertFalse("span must not be block", span.isBlock());
         assertTrue("span must be inline", span.isInline());
     }

     @Test
     public void testEmptyTagProperties() {
         Tag br = Tag.valueOf("br");
         assertTrue("br must be empty", br.isEmpty());
         assertTrue("br must be self closing", br.isSelfClosing());
         assertFalse("br must not preserve whitespace", br.preserveWhitespace());
         assertFalse("br must not be block", br.isBlock());
         assertTrue("br must be inline", br.isInline());
     }

     @Test
     public void testPreTagProperties() {
         Tag pre = Tag.valueOf("pre");
         assertTrue("pre must preserve whitespace", pre.preserveWhitespace());
         assertTrue("pre must be block", pre.isBlock());
         assertFalse("pre must be formatted as block", pre.formatAsBlock());
     }

     @Test
     public void testSelfClosingBehavior() {
         Tag meta = Tag.valueOf("meta");
         assertTrue("meta must be self closing", meta.isSelfClosing());

         Tag foo = Tag.valueOf("custom");
         assertFalse("unknown generic tag must not be self closing by default",
                 foo.isSelfClosing());
         foo.setSelfClosing();
         assertTrue("after setSelfClosing()", foo.isSelfClosing());
     }
 }
