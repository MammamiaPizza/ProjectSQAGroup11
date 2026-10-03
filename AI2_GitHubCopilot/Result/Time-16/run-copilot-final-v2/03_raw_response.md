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
         tokyo = DateTimeZone.forID("Asia/Tokyo");
         london = DateTimeZone.forID("Europe/London");

         monthOnlyFmt = DateTimeFormat.forPattern("MM").withDefaultYear(2004);
         monthDayFmt  = DateTimeFormat.forPattern("MM/dd").withDefaultYear(2004);
     }

     @Test
     public void testParseInto_monthOnly_baseStartYear() {
         MutableDateTime mdt = new MutableDateTime(2000, 1, 1, 12, 20, 30, 0, tokyo);
         monthOnlyFmt.parseInto(mdt, "05", 0);
         DateTime expected = new DateTime(2000, 5, 1, 12, 20, 30, 0, tokyo);
         assertEquals(expected, mdt.toDateTime());
     }

     @Test
     public void testParseInto_monthOnly_parseStartYear() {
         MutableDateTime mdt = new MutableDateTime(2000, 1, 1, 12, 20, 30, 0, tokyo);
         monthOnlyFmt.parseInto(mdt, "05", 0);
         DateTime expected = new DateTime(2000, 1, 1, 12, 20, 30, 0, tokyo);
         assertEquals(expected, mdt.toDateTime());
     }

     @Test
     public void testParseInto_monthOnly_baseEndYear() {
         MutableDateTime mdt = new MutableDateTime(2000, 5, 31, 12, 20, 30, 0, tokyo);
         monthOnlyFmt.parseInto(mdt, "05", 0);
         DateTime expected = new DateTime(2000, 5, 31, 12, 20, 30, 0, tokyo);
         assertEquals(expected, mdt.toDateTime());
     }

     @Test
     public void testParseInto_monthOnly() {
         MutableDateTime mdt = new MutableDateTime(2000, 5, 9, 12, 20, 30, 0, london);
         monthOnlyFmt.parseInto(mdt, "05", 0);
         DateTime expected = new DateTime(2000, 5, 9, 12, 20, 30, 0, london);
         assertEquals(expected, mdt.toDateTime());
     }

     @Test
     public void testParseInto_monthOnly_parseEndYear() {
         MutableDateTime mdt = new MutableDateTime(2000, 12, 31, 12, 20, 30, 0, tokyo);
         monthOnlyFmt.parseInto(mdt, "12", 0);
         DateTime expected = new DateTime(2000, 12, 31, 12, 20, 30, 0, tokyo);
         assertEquals(expected, mdt.toDateTime());
     }

     @Test
     public void testParseInto_monthDay_feb29() {
         MutableDateTime mdt = new MutableDateTime(2000, 2, 29, 12, 20, 30, 0, DateTimeZone.UTC);
         monthDayFmt.parseInto(mdt, "02/29", 0);
         DateTime expected = new DateTime(2000, 2, 29, 12, 20, 30, 0, DateTimeZone.UTC);
         assertEquals(expected, mdt.toDateTime());
     }

     @Test
     public void testParseInto_monthDay_withDefaultYear_feb29() {
         MutableDateTime mdt = new MutableDateTime(2012, 2, 29, 12, 20, 30, 0, DateTimeZone.UTC);
         monthDayFmt.parseInto(mdt, "02/29", 0);
         DateTime expected = new DateTime(2012, 2, 29, 12, 20, 30, 0, DateTimeZone.UTC);
         assertEquals(expected, mdt.toDateTime());
     }

     @Test
     public void testParseLocalDate_monthOnly() {
         LocalDate date = monthOnlyFmt.parseLocalDate("05");
         assertEquals(new LocalDate(2004, 5, 1), date);
     }

     @Test
     public void testParseLocalDateTime_monthOnly() {
         LocalDateTime ldt = monthOnlyFmt.parseLocalDateTime("05");
         assertEquals(new LocalDateTime(2004, 5, 1, 0, 0, 0, 0), ldt);
     }

     @Test
     public void testParseLocalDate_monthDay_feb29() {
         LocalDate date = monthDayFmt.parseLocalDate("02/29");
         assertEquals(new LocalDate(2004, 2, 29), date);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testParseFeb29_inNonLeapDefaultYear() {
         DateTimeFormatter fmt = DateTimeFormat.forPattern("MM/dd").withDefaultYear(2003);
         fmt.parseDateTime("02/29");
     }

     @Test
     public void testParseWith2020DefaultYear() {
         DateTimeFormatter fmt = DateTimeFormat.forPattern("MM").withDefaultYear(2020);
         MutableDateTime mdt = new MutableDateTime(2010, 6, 15, 0, 0, 0, 0, DateTimeZone.UTC);
         fmt.parseInto(mdt, "07", 0);
         DateTime expected = new DateTime(2010, 7, 15, 0, 0, 0, 0, DateTimeZone.UTC);
         assertEquals(expected, mdt.toDateTime());
     }
 }