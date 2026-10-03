package org.joda.time.format;

 import java.util.HashMap;
 import java.util.Map;

 import org.joda.time.DateTime;
 import org.joda.time.DateTimeZone;
 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for the underscore-related bug in DateTimeFormatterBuilder parsing time zone IDs/names.
  */
 public class TestDateTimeFormatterBuilder {

     // Helper to create a formatter that expects "yyyy-MM-dd HH:mm " followed by a time zone ID
     private DateTimeFormatter newZoneIdFormatter() {
         return new DateTimeFormatterBuilder()
             .appendPattern("yyyy-MM-dd HH:mm ")
             .appendTimeZoneId()
             .toFormatter();
     }

     // Helper to create a formatter with a custom name map for "America/Dawson_Creek"
     private DateTimeFormatter newZoneNameFormatter() {
         Map<String, DateTimeZone> map = new HashMap<String, DateTimeZone>();
         DateTimeZone zone = DateTimeZone.forID("America/Dawson_Creek");
         map.put("America/Dawson_Creek", zone);
         return new DateTimeFormatterBuilder()
             .appendPattern("yyyy-MM-dd HH:mm ")
             .appendTimeZoneName(map)
             .toFormatter();
     }

     @Test
     public void testParseZoneIdDawsonCreek() {
         DateTimeFormatter f = newZoneIdFormatter();
         DateTime dt = f.parseDateTime("2007-03-04 12:30 America/Dawson_Creek");
         assertEquals(DateTimeZone.forID("America/Dawson_Creek"), dt.getZone());
     }

     @Test
     public void testParseZoneNameDawsonCreek() {
         DateTimeFormatter f = newZoneNameFormatter();
         DateTime dt = f.parseDateTime("2007-03-04 12:30 America/Dawson_Creek");
         assertEquals(DateTimeZone.forID("America/Dawson_Creek"), dt.getZone());
     }

     @Test
     public void testParseZoneIdPortAuPrince() {
         DateTimeFormatter f = new DateTimeFormatterBuilder()
             .appendPattern("yyyy-MM-dd HH:mm ")
             .appendTimeZoneId()
             .toFormatter();
         DateTime dt = f.parseDateTime("2007-03-04 12:30 America/Port-au-Prince");
         assertEquals(DateTimeZone.forID("America/Port-au-Prince"), dt.getZone());
     }

     @Test
     public void testParseZoneIdBuenosAires() {
         DateTimeFormatter f = new DateTimeFormatterBuilder()
             .appendPattern("yyyy-MM-dd HH:mm ")
             .appendTimeZoneId()
             .toFormatter();
         DateTime dt = f.parseDateTime("2007-03-04 12:30 America/Argentina/Buenos_Aires");
         assertEquals(DateTimeZone.forID("America/Argentina/Buenos_Aires"), dt.getZone());
     }

     @Test
     public void testParseZoneIdEtcGMTPlus10() {
         DateTimeFormatter f = new DateTimeFormatterBuilder()
             .appendPattern("yyyy-MM-dd HH:mm ")
             .appendTimeZoneId()
             .toFormatter();
         DateTime dt = f.parseDateTime("2007-03-04 12:30 Etc/GMT+10");
         assertEquals(DateTimeZone.forID("Etc/GMT+10"), dt.getZone());
     }

     @Test
     public void testRoundtripZoneId() {
         DateTimeFormatter f = newZoneIdFormatter();
         DateTime expected = new DateTime(2007, 3, 4, 12, 30, 0, 0,
             DateTimeZone.forID("America/Dawson_Creek"));
         String printed = f.print(expected);
         DateTime parsed = f.parseDateTime(printed);
         assertEquals(expected, parsed);
     }

     @Test
     public void testRoundtripZoneName() {
         DateTimeFormatter f = newZoneNameFormatter();
         DateTime expected = new DateTime(2007, 3, 4, 12, 30, 0, 0,
             DateTimeZone.forID("America/Dawson_Creek"));
         String printed = f.print(expected);
         DateTime parsed = f.parseDateTime(printed);
         assertEquals(expected, parsed);
     }

     @Test
     public void testParseZoneIdWithTrailingLiteral() {
         DateTimeFormatter f = new DateTimeFormatterBuilder()
             .appendPattern("yyyy-MM-dd HH:mm ")
             .appendTimeZoneId()
             .appendLiteral(" end")
             .toFormatter();
         DateTime dt = f.parseDateTime("2007-03-04 12:30 America/Dawson_Creek end");
         assertEquals(DateTimeZone.forID("America/Dawson_Creek"), dt.getZone());
     }

     @Test
     public void testParseZoneIdAtBeginning() {
         DateTimeFormatter f = new DateTimeFormatterBuilder()
             .appendTimeZoneId()
             .appendLiteral(" ")
             .appendPattern("yyyy-MM-dd HH:mm")
             .toFormatter();
         DateTime dt = f.parseDateTime("America/Dawson_Creek 2007-03-04 12:30");
         assertEquals(DateTimeZone.forID("America/Dawson_Creek"), dt.getZone());
     }

     @Test
     public void testParseInvalidZoneIdThrows() {
         // An unknown zone ID should throw IllegalArgumentException.
         DateTimeFormatter f = newZoneIdFormatter();
         try {
             f.parseDateTime("2007-03-04 12:30 Bad/Zone");
             fail("Expected IllegalArgumentException for unknown zone");
         } catch (IllegalArgumentException expected) {
             // expected
         }
     }

     @Test
     public void testParseInvalidZoneNameThrows() {
         DateTimeFormatter f = newZoneNameFormatter();
         try {
             f.parseDateTime("2007-03-04 12:30 Some/Random_Id");
             fail("Expected IllegalArgumentException for unresolvable zone name");
         } catch (IllegalArgumentException expected) {
             // expected
         }
     }

     @Test
     public void testDawsonCreekIsAvailable() {
         assertTrue("America/Dawson_Creek should be in available IDs",
             DateTimeZone.getAvailableIDs().contains("America/Dawson_Creek"));
     }
 }