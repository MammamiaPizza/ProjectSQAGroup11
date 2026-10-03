package org.joda.time.format;

 import static org.junit.Assert.*;

 import org.joda.time.*;
 import org.joda.time.format.DateTimeFormatter;
 import org.joda.time.format.DateTimeFormat;
 import org.junit.Before;
 import org.junit.Test;

 public class TestDateTimeFormatterBug148 {

     private DateTimeFormatter monthOnlyFmt;
     private DateTimeFormatter monthDayFmt;
     private DateTimeZone tokyo;
     private DateTimeZone london;

     @Before
     public void setUp() {
         tokyo = DateTimeZone.forID("Asia/Tokyo");   // +09:00
         london = DateTimeZone.forID("Europe/London"); // +01:00 on 2004-05-09

         // Formats that parse only month or month+day; year must come from defaultYear
         monthOnlyFmt = DateTimeFormat.forPattern("MM").withDefaultYear(2004);
         monthDayFmt  = DateTimeFormat.forPattern("MM/dd").withDefaultYear(2004);
     }

     // ------------------------------------------------------------------------
     // Trigger-style tests: parseInto with MutableDateTime
     // ------------------------------------------------------------------------

     @Test
     public void testParseInto_monthOnly_baseStartYear() {
         MutableDateTime mdt = new MutableDateTime(2000, 1, 1, 12, 20, 30, 0, tokyo);
         monthOnlyFmt.parseInto(mdt, "05", 0);
         DateTime expected = new DateTime(2004, 5, 1, 12, 20, 30, 0, tokyo);
         assertEquals(expected, mdt.toDateTime());
     }

     @Test
     public void testParseInto_monthOnly_parseStartYear() {
         MutableDateTime mdt = new MutableDateTime(2000, 1, 1, 12, 20, 30, 0, tokyo);
         // parse starting at position 0 -> read "05", stop after 2 chars
         monthOnlyFmt.parseInto(mdt, "05", 0);
         DateTime expected = new DateTime(2004, 1, 1, 12, 20, 30, 0, tokyo);
         assertEquals(expected, mdt.toDateTime());
     }

     @Test
     public void testParseInto_monthOnly_baseEndYear() {
         MutableDateTime mdt = new MutableDateTime(2000, 5, 31, 12, 20, 30, 0, tokyo);
         monthOnlyFmt.parseInto(mdt, "05", 0);
         DateTime expected = new DateTime(2004, 5, 31, 12, 20, 30, 0, tokyo);
         assertEquals(expected, mdt.toDateTime());
     }

     @Test
     public void testParseInto_monthOnly() {
         MutableDateTime mdt = new MutableDateTime(2000, 5, 9, 12, 20, 30, 0, london);
         monthOnlyFmt.parseInto(mdt, "05", 0);
         DateTime expected = new DateTime(2004, 5, 9, 12, 20, 30, 0, london);
         assertEquals(expected, mdt.toDateTime());
     }

     @Test
     public void testParseInto_monthOnly_parseEndYear() {
         MutableDateTime mdt = new MutableDateTime(2000, 12, 31, 12, 20, 30, 0, tokyo);
         monthOnlyFmt.parseInto(mdt, "12", 0);
         DateTime expected = new DateTime(2004, 12, 31, 12, 20, 30, 0, tokyo);
         assertEquals(expected, mdt.toDateTime());
     }

     // ------------------------------------------------------------------------
     // Leap-day related: February 29 parsed with a leap default year
     // ------------------------------------------------------------------------

     @Test
     public void testParseInto_monthDay_feb29() {
         MutableDateTime mdt = new MutableDateTime(2000, 2, 29, 12, 20, 30, 0, DateTimeZone.UTC);
         monthDayFmt.parseInto(mdt, "02/29", 0);
         DateTime expected = new DateTime(2004, 2, 29, 12, 20, 30, 0, DateTimeZone.UTC);
         assertEquals(expected, mdt.toDateTime());
     }

     @Test
     public void testParseInto_monthDay_withDefaultYear_feb29() {
         MutableDateTime mdt = new MutableDateTime(2012, 2, 29, 12, 20, 30, 0, DateTimeZone.UTC);
         // base has 2012-02-29; parsing "02/29" with default 2004 must override year to 2004-02-29
         monthDayFmt.parseInto(mdt, "02/29", 0);
         DateTime expected = new DateTime(2004, 2, 29, 12, 20, 30, 0, DateTimeZone.UTC);
         assertEquals(expected, mdt.toDateTime());
     }

     // ------------------------------------------------------------------------
     // parseLocalDate / parseLocalDateTime wrappers (all should honour defaultYear)
     // ------------------------------------------------------------------------

     @Test
     public void testParseLocalDate_monthOnly() {
         LocalDate date = monthOnlyFmt.parseLocalDate("05");
         assertEquals(new LocalDate(2004, 5, 1), date);
     }

     @Test
     public void testParseLocalDateTime_monthOnly() {
         LocalDateTime ldt = monthOnlyFmt.parseLocalDateTime("05");
         // month-only parsed at midnight on first day of that month
         assertEquals(new LocalDateTime(2004, 5, 1, 0, 0, 0, 0), ldt);
     }

     @Test
     public void testParseLocalDate_monthDay_feb29() {
         LocalDate date = monthDayFmt.parseLocalDate("02/29");
         assertEquals(new LocalDate(2004, 2, 29), date);
     }

     // ------------------------------------------------------------------------
     // Error / boundary: non-leap default year with Feb 29 must throw
     // ------------------------------------------------------------------------

     @Test(expected = IllegalArgumentException.class)
     public void testParseFeb29_inNonLeapDefaultYear() {
         DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd").withDefaultYear(2003);
         fmt.parseDateTime("02/29"); // Feb 29 in non-leap year -> invalid
     }

     // ------------------------------------------------------------------------
     // Boundary: default is different from 2000 (e.g. 2020)
     // ------------------------------------------------------------------------

     @Test
     public void testParseWith2020DefaultYear() {
         DateTimeFormatter fmt = DateTimeFormat.forPattern("MM").withDefaultYear(2020);
         MutableDateTime mdt = new MutableDateTime(2010, 6, 15, 0, 0, 0, 0, DateTimeZone.UTC);
         fmt.parseInto(mdt, "07", 0);
         DateTime expected = new DateTime(2020, 7, 15, 0, 0, 0, 0, DateTimeZone.UTC);
         assertEquals(expected, mdt.toDateTime());
     }
 }
