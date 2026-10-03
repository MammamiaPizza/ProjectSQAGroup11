package org.apache.commons.cli;

 import junit.framework.TestCase;

 /**
  * Tests for {@link Util#stripLeadingAndTrailingQuotes(String)}.
  *
  * Bug CLI-185: when the input string has embedded quotes, the method
  * incorrectly strips the trailing quote because it uses a stale length
  * variable after potentially removing the leading quote.
  */
 public class UtilTest extends TestCase {

     // --- normal cases: outer quotes only ---

     public void testStripBothQuotesNoEmbedded() {
         assertEquals("foo", Util.stripLeadingAndTrailingQuotes("\"foo\""));
     }

     public void testStripBothQuotesEmptyContent() {
         assertEquals("", Util.stripLeadingAndTrailingQuotes("\"\""));
     }

     public void testStripBothQuotesSingleCharContent() {
         assertEquals("a", Util.stripLeadingAndTrailingQuotes("\"a\""));
     }

     // --- leading-quote-only cases ---

     public void testStripLeadingQuoteOnly() {
         assertEquals("foo\"", Util.stripLeadingAndTrailingQuotes("\"foo\""));
     }

     public void testStripLeadingQuoteOnlyEmbeddedQuote() {
         assertEquals("foo\"bar\"", Util.stripLeadingAndTrailingQuotes("\"foo\"bar\""));
     }

     // --- trailing-quote-only cases ---

     public void testStripTrailingQuoteOnly() {
         assertEquals("\"foo", Util.stripLeadingAndTrailingQuotes("\"foo\""));
     }

     public void testStripTrailingQuoteOnlyEmbeddedQuote() {
         assertEquals("\"foo\"bar", Util.stripLeadingAndTrailingQuotes("\"foo\"bar\""));
     }

     // --- both quotes with embedded quotes (CLI-185 regression) ---

     public void testBothQuotesWithEmbeddedQuotes() {
         assertEquals("\"foo \"bar\"", Util.stripLeadingAndTrailingQuotes("\"\"foo \"bar\"\""));
     }

     // --- no quotes ---

     public void testNoQuotes() {
         assertEquals("foo", Util.stripLeadingAndTrailingQuotes("foo"));
     }

     public void testNoQuotesWithEmbedded() {
         assertEquals("foo \"bar\"", Util.stripLeadingAndTrailingQuotes("foo \"bar\""));
     }

     // --- edge cases ---

     public void testSingleQuoteCharOnly() {
         assertEquals("", Util.stripLeadingAndTrailingQuotes("\""));
     }

     public void testNullInput() {
         assertNull(Util.stripLeadingAndTrailingQuotes(null));
     }
 }
