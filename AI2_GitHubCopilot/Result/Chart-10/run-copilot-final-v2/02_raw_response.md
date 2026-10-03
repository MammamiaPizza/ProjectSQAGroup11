package org.jfree.chart.imagemap.junit;

 import static org.junit.Assert.*;

 import org.junit.Before;
 import org.junit.Test;
 import org.jfree.chart.imagemap.StandardToolTipTagFragmentGenerator;

 /**
  * Tests for {@link StandardToolTipTagFragmentGenerator}.
  * Verifies that double-quote characters are HTML-escaped to &amp;quot; in the
  * generated title attribute. Other HTML special characters are not escaped
  * because the specification only requires double-quote escaping.
  */
 public class StandardToolTipTagFragmentGeneratorTests {

     private StandardToolTipTagFragmentGenerator generator;

     @Before
     public void setUp() {
         generator = new StandardToolTipTagFragmentGenerator();
     }

     @Test
     public void testGenerateURLFragment() {
         // The trigger test from the bug report
         String input = "Series [\"A\"], 100.0";
         String expected = " title=\"Series [&quot;A&quot;], 100.0\" alt=\"\"";
         assertEquals(expected, generator.generateToolTipFragment(input));
     }

     @Test
     public void testNormalText() {
         String input = "Hello World";
         String expected = " title=\"Hello World\" alt=\"\"";
         assertEquals(expected, generator.generateToolTipFragment(input));
     }

     @Test
     public void testSingleDoubleQuote() {
         String input = "He said \"Hello\"";
         String expected = " title=\"He said &quot;Hello&quot;\" alt=\"\"";
         assertEquals(expected, generator.generateToolTipFragment(input));
     }

     @Test
     public void testMultipleDoubleQuotes() {
         String input = "\"a\" \"b\"";
         String expected = " title=\"&quot;a&quot; &quot;b&quot;\" alt=\"\"";
         assertEquals(expected, generator.generateToolTipFragment(input));
     }

     @Test
     public void testSingleQuote() {
         // Single quotes should not be escaped
         String input = "It's a test";
         String expected = " title=\"It's a test\" alt=\"\"";
         assertEquals(expected, generator.generateToolTipFragment(input));
     }

     @Test
     public void testOtherHTMLCharsNotEscaped() {
         // Ampersand, less-than and greater-than are NOT escaped
         // by the specification; only double-quote escaping is required.
         String input = "a & b < c > d";
         String expected = " title=\"a & b < c > d\" alt=\"\"";
         assertEquals(expected, generator.generateToolTipFragment(input));
     }

     @Test
     public void testEmptyString() {
         String input = "";
         String expected = " title=\"\" alt=\"\"";
         assertEquals(expected, generator.generateToolTipFragment(input));
     }

     @Test
     public void testNullToolTipText() {
         // Current implementation concatenates null as the string "null"
         String expected = " title=\"null\" alt=\"\"";
         assertEquals(expected, generator.generateToolTipFragment(null));
     }

     @Test
     public void testLongToolTipText() {
         StringBuilder sb = new StringBuilder();
         for (int i = 0; i < 1000; i++) {
             sb.append("a\"");
         }
         String input = sb.toString();
         String escapedQuotes = input.replace("\"", "&quot;");
         String expected = " title=\"" + escapedQuotes + "\" alt=\"\"";
         assertEquals(expected, generator.generateToolTipFragment(input));
     }

     @Test
     public void testNewlineAndTabCharacters() {
         // Whitespace characters are passed through without escaping.
         String input = "Line1\nLine2\tTab";
         String expected = " title=\"Line1\nLine2\tTab\" alt=\"\"";
         assertEquals(expected, generator.generateToolTipFragment(input));
     }

     @Test
     public void testExistingQuoteEntity() {
         // When the input already contains &quot;, it must not be modified.
         String input = "Already &quot;escaped&quot; text";
         String expected = " title=\"Already &quot;escaped&quot; text\" alt=\"\"";
         assertEquals(expected, generator.generateToolTipFragment(input));
     }

     @Test
     public void testMixedQuotesAndAmpersand() {
         // Both double-quote escaping and unchanged ampersand.
         String input = "\"a\" & \"b\"";
         String expected = " title=\"&quot;a&quot; & &quot;b&quot;\" alt=\"\"";
         assertEquals(expected, generator.generateToolTipFragment(input));
     }
 }