package org.joda.time;

 import org.junit.Test;
 import junit.framework.TestCase;
 import junit.framework.Assert;

 public class TestDateTimeZoneForOffsetHoursMinutes extends TestCase {

     @Test
     public void testForOffsetHoursMinutes_positiveHoursAndMinutes() {
         DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(5, 30);
         assertNotNull(zone);
         int expectedOffsetMillis = 5 * 3600000 + 30 * 60000;
         assertEquals(expectedOffsetMillis, zone.getOffset(0));
     }

     @Test
     public void testForOffsetHoursMinutes_negativeHoursAndMinutes() {
         DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(-3, -45);
         assertNotNull(zone);
         int expectedOffsetMillis = -3 * 3600000 - 45 * 60000;
         assertEquals(expectedOffsetMillis, zone.getOffset(0));
     }

     @Test
     public void testForOffsetHoursMinutes_positiveHoursNegativeMinutes() {
         try {
             DateTimeZone.forOffsetHoursMinutes(2, -15);
             fail("Expected IllegalArgumentException for negative minutes");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("Minutes out of range"));
         }
     }

     @Test
     public void testForOffsetHoursMinutes_negativeHoursPositiveMinutes() {
         try {
             DateTimeZone.forOffsetHoursMinutes(-2, 15);
             fail("Expected IllegalArgumentException for negative minutes");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("Minutes out of range"));
         }
     }

     @Test
     public void testForOffsetHoursMinutes_zeroHoursAndMinutes_returnsUTC() {
         DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(0, 0);
         assertSame(DateTimeZone.UTC, zone);
     }

     @Test
     public void testForOffsetHoursMinutes_zeroMinutesSameAsForOffsetHours() {
         DateTimeZone zone1 = DateTimeZone.forOffsetHoursMinutes(5, 0);
         DateTimeZone zone2 = DateTimeZone.forOffsetHours(5);
         assertEquals(zone1.getOffset(0), zone2.getOffset(0));
     }

     @Test
     public void testForOffsetHoursMinutes_boundaryPositiveHoursAndMinutes() {
         DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(23, 59);
         assertNotNull(zone);
         int expectedOffsetMillis = 23 * 3600000 + 59 * 60000;
         assertEquals(expectedOffsetMillis, zone.getOffset(0));
     }

     @Test
     public void testForOffsetHoursMinutes_boundaryNegativeHoursAndMinutes() {
         DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(-23, -59);
         assertNotNull(zone);
         int expectedOffsetMillis = -23 * 3600000 - 59 * 60000;
         assertEquals(expectedOffsetMillis, zone.getOffset(0));
     }

     @Test
     public void testForOffsetHoursMinutes_hoursExceedPositiveRange() {
         try {
             DateTimeZone.forOffsetHoursMinutes(24, 0);
             fail("Expected IllegalArgumentException for hours > 23");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("Offset is too large"));
         }
     }

     @Test
     public void testForOffsetHoursMinutes_hoursExceedNegativeRange() {
         try {
             DateTimeZone.forOffsetHoursMinutes(-24, 0);
             fail("Expected IllegalArgumentException for hours < -23");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("Offset is too large"));
         }
     }

     @Test
     public void testForOffsetHoursMinutes_minutesExceedPositiveRange() {
         try {
             DateTimeZone.forOffsetHoursMinutes(0, 60);
             fail("Expected IllegalArgumentException for minutes > 59");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("Minutes out of range"));
         }
     }

     @Test
     public void testForOffsetHoursMinutes_overflowPrevention() {
         try {
             DateTimeZone.forOffsetHoursMinutes(Integer.MAX_VALUE, 59);
             fail("Expected IllegalArgumentException for offset overflow");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("Offset is too large"));
         }
     }
 }
