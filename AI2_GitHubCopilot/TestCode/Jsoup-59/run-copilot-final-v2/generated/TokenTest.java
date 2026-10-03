package org.jsoup.parser;

import static org.junit.Assert.*;
import org.junit.Test;

/**

 - Regression tests for the Token class focusing on control-character handling
 - in tag names and attribute names (Jsoup bug #793 / Defects4J Bug 59).
 - Buggy version throws IllegalArgumentException when control characters result
 - in an empty name after trimming; the fix should skip such attributes/tags.
  */
 public class TokenTest {
  /**
  - Attribute name consisting only of null character (U+0000) should be skipped,
  - not throw an exception.
    */
   @Test
   public void testAttributeNameWithOnlyNullCharSkipped() {
   Token.StartTag tag = new Token.StartTag();
   tag.name("div");
   // Only a null character – trims to empty in buggy version -> exception
   tag.appendAttributeName("\0");
   tag.newAttribute(); // would throw in buggy
   assertNotNull(tag.getAttributes());
   assertEquals("attribute with null name should be skipped", 0,
       tag.getAttributes().size());
  }
  /**
  - Attribute name consisting only of U+001F should be skipped, not throw.
    */
   @Test
   public void testAttributeNameWithOnlyUs001FSkipped() {
   Token.StartTag tag = new Token.StartTag();
   tag.name("div");
   tag.appendAttributeName("\u001F");
   tag.newAttribute();
   assertEquals("attribute with U+001F name should be skipped", 0,
       tag.getAttributes().size());
  }
  /**
  - Multiple control characters that all trims to empty should be skipped.
    */
   @Test
   public void testAttributeNameWithMultipleControlCharsSkipped() {
   Token.StartTag tag = new Token.StartTag();
   tag.name("div");
   tag.appendAttributeName("\0\u001F\u000B");
   tag.newAttribute();
   assertEquals(0, tag.getAttributes().size());
  }
  /**
  - Simulates a control character appearing directly after the tag name
  - (triggers attribute parsing in the tokeniser). The resulting
  - attribute name becomes empty after trimming and must be skipped.
    */
   @Test
   public void testControlCharAfterTagNameIgnored() {
   Token.StartTag tag = new Token.StartTag();
   tag.name("div");
   // control char after tag name – starts an (invalid) attribute name
   tag.appendAttributeName('\0');
   tag.newAttribute();
   assertEquals(0, tag.getAttributes().size());
  }
  /**
  - A normal attribute name is still added correctly.
    */
   @Test
   public void testNormalAttributeNameAdded() {
   Token.StartTag tag = new Token.StartTag();
   tag.name("div");
   tag.appendAttributeName("class");
   tag.newAttribute();
   assertEquals(1, tag.getAttributes().size());
   assertTrue("normal attribute should be present",
       tag.getAttributes().hasKey("class"));
  }
  /**
  - Attribute name that trims to empty (only whitespace) should be skipped
  - instead of throwing.
    */
   @Test
   public void testAttributeNameWhitespaceOnlySkipped() {
   Token.StartTag tag = new Token.StartTag();
   tag.name("div");
   tag.appendAttributeName("   ");
   tag.newAttribute();
   assertEquals(0, tag.getAttributes().size());
  }
  /**
  - Tag name must not be empty – the validation throws when empty.
    */
   @Test(expected = IllegalArgumentException.class)
   public void testTagNameThrowsWhenEmpty() {
   Token.StartTag tag = new Token.StartTag();
   tag.name(""); // sets tagName to ""
   tag.name();  // throws Validate.isFalse
   }
  /**
  - Append normal character to tag name works.
    */
   @Test
   public void testAppendTagNameNormal() {
   Token.StartTag tag = new Token.StartTag();
   tag.name("di");
   tag.appendTagName('v');
   assertEquals("div", tag.name());
  }
  /**
  - Control characters appended to a tag name should not cause
  - an exception when obtaining the name (non-empty name).
    */
   @Test
   public void testTagNameWithControlCharsNoException() {
   Token.StartTag tag = new Token.StartTag();
   tag.name("a");
   tag.appendTagName("\0"); // "a\0" – still non-empty
   assertNotNull(tag.name());
   assertEquals("a\0", tag.name()); // Token does not strip; validation ok
  }
  /**
  - Reset on a tag clears the pending attribute information.
    */
   @Test
   public void testResetTagClearsPendingAttribute() {
   Token.StartTag tag = new Token.StartTag();
   tag.name("div");
   tag.appendAttributeName("class");
   tag.reset();
   tag.newAttribute(); // pendingAttributeName is null after reset
   assertEquals(0, tag.getAttributes().size());
  }
  /**
  - finaliseTag flushes any pending attribute via newAttribute.
    */
   @Test
   public void testFinaliseTagProcessesPendingAttribute() {
   Token.StartTag tag = new Token.StartTag();
   tag.name("div");
   tag.appendAttributeName("id");
   tag.finaliseTag();
   assertEquals(1, tag.getAttributes().size());
   assertTrue(tag.getAttributes().hasKey("id"));
  }
  /**
  - Token.reset(StringBuilder) with null must not throw.
    */
   @Test
   public void testResetNullStringBuilderNoException() {
   Token.reset((StringBuilder) null);
   // passes silently
   }

}
