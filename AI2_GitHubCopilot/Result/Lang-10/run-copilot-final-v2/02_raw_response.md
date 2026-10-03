package org.apache.commons.lang3.time;

 import java.text.ParsePosition;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.Locale;
 import java.util.TimeZone;
 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for the LANG-831 bug: FastDateParser must enforce literal characters
  * in the pattern and return null when they are missing in the input.
  */
 public class FastDateParserLiteralBugTest {

     // ---- Bug-specific case ----

     @Test
     public void testMissingLiteralCommaReturnsNull() {
         FastDateParser parser = new FastDateParser("M E,3 Tue", TimeZone.getDefault(), Locale.US);
         ParsePosition pos = new ParsePosition(0);
         Date result = parser.parse("3 Tue", pos);
         assertNull("Parsing without required comma should return null", result);
     }

     @Test
     public void testValidPatternWithLiteralComma() {
         FastDateParser parser = new FastDateParser("M E,3 Tue", TimeZone.getDefault(), Locale.US);
         ParsePosition pos = new ParsePosition(0);
         Date result = parser.parse("3 Tue,3 Tue", pos);
         assertNotNull("Parsing with literal comma should succeed", result);
         assertEquals("Parser should consume entire input", 10, pos.getIndex());
         Calendar cal = Calendar.getInstance();
         cal.setTime(result);
         assertEquals(Calendar.MARCH, cal.get(Calendar.MONTH));
         assertEquals(Calendar.TUESDAY, cal.get(Calendar.DAY_OF_WEEK));
     }

     // ---- Quoted literal tests ----

     @Test
     public void testMissingQuotedLiteralFails() {
         FastDateParser parser = new FastDateParser("yyyy'abc'", TimeZone.getDefault(), Locale.US);
         ParsePosition pos = new ParsePosition(0);
         assertNull("Should fail when literal is incomplete", parser.parse("1970ab", pos));
         pos = new ParsePosition(0);
         assertNull("Should fail when literal is completely absent", parser.parse("1970", pos));
     }

     @Test
     public void testValidQuotedLiteral() {
         FastDateParser parser = new FastDateParser("yyyy'abc'", TimeZone.getDefault(), Locale.US);
         ParsePosition pos = new ParsePosition(0);
         Date result = parser.parse("1970abc", pos);
         assertNotNull("Should parse when quoted literal is present", result);
         Calendar cal = Calendar.getInstance();
         cal.clear();
         cal.set(Calendar.YEAR, 1970);
         assertEquals("Year should be set to 1970", cal.getTime(), result);
     }

     @Test
     public void testLiteralInMiddleOfPattern() {
         FastDateParser parser = new FastDateParser("yyyy'-'MM'-'dd", TimeZone.getDefault(),
Locale.US);
         ParsePosition pos = new ParsePosition(0);
         assertNull("Should fail when literal dash is replaced by slash",
                    parser.parse("1970/03/02", pos));
         pos = new ParsePosition(0);
         assertNotNull("Should succeed with literal dash",
                       parser.parse("1970-03-02", pos));
     }

     // ---- Edge cases ----

     @Test
     public void testPatternWithOnlyLiteral() {
         FastDateParser parser = new FastDateParser("'hello'", TimeZone.getDefault(), Locale.US);
         ParsePosition pos = new ParsePosition(0);
         Date result = parser.parse("hello", pos);
         assertNotNull("Should parse exact literal", result);
         assertEquals("Should consume all characters", 5, pos.getIndex());
         Calendar cal = Calendar.getInstance();
         cal.clear();
         assertEquals("Should produce epoch date", cal.getTime(), result);

         pos = new ParsePosition(0);
         assertNull("Should fail for partial literal", parser.parse("hell", pos));
     }

     @Test
     public void testLiteralAfterField() {
         FastDateParser parser = new FastDateParser("yyyy'AD'", TimeZone.getDefault(), Locale.US);
         ParsePosition pos = new ParsePosition(0);
         assertNull("Should fail when literal suffix doesn't match",
                    parser.parse("1970BC", pos));
     }

     @Test
     public void testTextStrategyWithMissingLiteral() {
         FastDateParser parser = new FastDateParser("E', hello'", TimeZone.getDefault(), Locale.US);
         ParsePosition pos = new ParsePosition(0);
         assertNull("Should fail without comma literal", parser.parse("Tue hello", pos));
         pos = new ParsePosition(0);
         assertNotNull("Should succeed with comma literal", parser.parse("Tue, hello", pos));
     }

     @Test
     public void testMultipleStrategiesWithLiteralBetween() {
         FastDateParser parser = new FastDateParser("M','E", TimeZone.getDefault(), Locale.US);
         ParsePosition pos = new ParsePosition(0);
         assertNull("Should fail without comma", parser.parse("3Tue", pos));
         pos = new ParsePosition(0);
         assertNotNull("Should succeed with comma", parser.parse("3,Tue", pos));
     }

     @Test
     public void testLiteralNumberFollowedByText() {
         FastDateParser parser = new FastDateParser("'3'E", TimeZone.getDefault(), Locale.US);
         ParsePosition pos = new ParsePosition(0);
         assertNull("Should fail when literal number is missing", parser.parse("Tue", pos));
         pos = new ParsePosition(0);
         assertNotNull("Should succeed with literal number", parser.parse("3Tue", pos));
     }

     @Test
     public void testLiteralWithEscapedQuote() {
         FastDateParser parser = new FastDateParser("yyyy''MM", TimeZone.getDefault(), Locale.US);
         ParsePosition pos = new ParsePosition(0);
         assertNotNull("Should parse with literal single quote", parser.parse("1970'03", pos));
         pos = new ParsePosition(0);
         assertNull("Should fail without literal quote", parser.parse("197003", pos));
     }

     @Test
     public void testNullForInvalidInputPosition() {
         FastDateParser parser = new FastDateParser("yyyy", TimeZone.getDefault(), Locale.US);
         ParsePosition pos = new ParsePosition(0);
         Date result = parser.parse("abcd", pos);
         assertNull("Should return null for non-numeric input", result);
         assertEquals("Position should not advance", 0, pos.getIndex());
     }
 }