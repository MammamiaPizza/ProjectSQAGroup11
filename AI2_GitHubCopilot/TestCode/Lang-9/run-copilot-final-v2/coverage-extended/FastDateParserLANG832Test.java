package org.apache.commons.lang3.time;

 import static org.junit.Assert.*;
 import static org.junit.Assert.assertEquals;

 import java.text.ParseException;
 import java.text.ParsePosition;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.Locale;
 import java.util.TimeZone;

 import org.junit.Test;

 /**
  * Tests for FastDateParser bug LANG-832: quoted literals in patterns are not
  * correctly enforced, causing parse to succeed when it should fail.
  *
  * The correct behaviour as implied by the bug report is that a literal
  * character introduced via quoting ('x') must be present in the input;
  * otherwise parsing should return null (via ParsePosition overload) or throw
  * ParseException.
  */
 public class FastDateParserLANG832Test {

     private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
     private static final Locale EN = Locale.ENGLISH;

     @Test
     public void testQuotedLiteralZMissingReturnsNull() {
         FastDateParser parser = new FastDateParser("yyyy-MM-dd'z'", UTC, EN);
         ParsePosition pos = new ParsePosition(0);
         Date result = parser.parse("2023-01-01", pos);
         assertNull("Missing literal 'z' must cause null result", result);
         assertEquals("Parse position must not advance", 0, pos.getIndex());
     }

     @Test
     public void testQuotedLiteralZMissingThrowsParseException() {
         FastDateParser parser = new FastDateParser("yyyy-MM-dd'z'", UTC, EN);
         try {
             parser.parse("2023-01-01");
             fail("ParseException expected when literal 'z' is missing");
         } catch (ParseException e) {
             // expected
         }
     }

     @Test
     public void testQuotedLiteralZPresentParsesCorrectly() {
         FastDateParser parser = new FastDateParser("yyyy-MM-dd'z'", UTC, EN);
         Date result = parser.parse("2023-01-01z", new ParsePosition(0));
         assertNotNull(result);
         Calendar cal = Calendar.getInstance(UTC, EN);
         cal.clear();
         cal.set(2023, Calendar.JANUARY, 1);
         assertEquals(cal.getTime(), result);
     }

     @Test
     public void testQuotedLiteralCapitalZMissingReturnsNull() {
         FastDateParser parser = new FastDateParser("yyyy-MM-dd'Z'", UTC, EN);
         ParsePosition pos = new ParsePosition(0);
         Date result = parser.parse("2023-01-01", pos);
         assertNull("Missing literal 'Z' must cause null result", result);
     }

     @Test
     public void testQuotedLiteralGMissingReturnsNull() {
         FastDateParser parser = new FastDateParser("'G'yyyy-MM-dd", UTC, EN);
         ParsePosition pos = new ParsePosition(0);
         Date result = parser.parse("2023-01-01", pos);
         assertNull("Missing literal 'G' must cause null result", result);
     }

     @Test
     public void testQuotedLiteralDPresentParsesCorrectly() {
         // Pattern "d'd'" means day number followed by literal 'd'
         FastDateParser parser = new FastDateParser("d'd'", UTC, EN);
         Date result = parser.parse("1d", new ParsePosition(0));
         assertNotNull(result);
         Calendar cal = Calendar.getInstance(UTC, EN);
         cal.clear();
         cal.set(Calendar.DAY_OF_MONTH, 1);
         assertEquals(cal.getTime(), result);
     }

     @Test
     public void testQuotedLiteralDMissingReturnsNull() {
         FastDateParser parser = new FastDateParser("d'd'", UTC, EN);
         // "1x" lacks literal 'd', parse must fail
         ParsePosition pos = new ParsePosition(0);
         Date result = parser.parse("1x", pos);
         assertNull("Missing literal 'd' must cause null result", result);
     }

     @Test
     public void testQuotedLiteralWordMissingReturnsNull() {
         FastDateParser parser = new FastDateParser("yyyy-MM'foo'dd", UTC, EN);
         ParsePosition pos = new ParsePosition(0);
         // missing 'foo'
         Date result = parser.parse("2023-0101", pos);
         assertNull("Missing quoted word 'foo' must cause null", result);
     }

     @Test
     public void testQuotedLiteralWordPresentParsesCorrectly() {
         FastDateParser parser = new FastDateParser("yyyy-MM'foo'dd", UTC, EN);
         Date result = parser.parse("2023-01foo01", new ParsePosition(0));
         assertNotNull(result);
         Calendar cal = Calendar.getInstance(UTC, EN);
         cal.clear();
         cal.set(2023, Calendar.JANUARY, 1);
         assertEquals(cal.getTime(), result);
     }

     @Test
     public void testEmptySourceReturnsNull() {
         FastDateParser parser = new FastDateParser("yyyy-MM-dd", UTC, EN);
         ParsePosition pos = new ParsePosition(0);
         Date result = parser.parse("", pos);
         assertNull("Empty source must return null", result);
     }

     @Test(expected = NullPointerException.class)
     public void testSourceNullThrowsNullPointerException() {
         FastDateParser parser = new FastDateParser("yyyy-MM-dd", UTC, EN);
         parser.parse(null, new ParsePosition(0));
     }

     @Test
     public void testNormalDatePatternParsesCorrectly() {
         FastDateParser parser = new FastDateParser("yyyy-MM-dd", UTC, EN);
         Date result = parser.parse("2023-12-25", new ParsePosition(0));
         assertNotNull(result);
         Calendar cal = Calendar.getInstance(UTC, EN);
         cal.clear();
         cal.set(2023, Calendar.DECEMBER, 25);
         assertEquals(cal.getTime(), result);
     }
 }
