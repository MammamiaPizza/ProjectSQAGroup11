package org.joda.time;

 import junit.framework.TestCase;
 import junit.framework.TestSuite;
 import junit.textui.TestRunner;

 /**
  * Tests for {@link DateTimeZone#forOffsetHoursMinutes(int, int)} targeting
  * the bug where negative minute values are incorrectly rejected.
  */
 public class TestDateTimeZone extends TestCase {

     public static void main(String[] args) {
         TestRunner.run(suite());
     }

     public static TestSuite suite() {
         return new TestSuite(TestDateTimeZone.class);
     }

     public TestDateTimeZone(String name) {
         super(name);
     }

     // -----------------------------------------------------------------------
     // Normal valid cases: positive offsets
     // -----------------------------------------------------------------------

     public void testForOffsetHoursMinutes_utc() {
         DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(0, 0);
         assertSame("(0,0) must return UTC singleton", DateTimeZone.UTC, zone);
         assertEquals("UTC", zone.getID());
         assertEquals(0, zone.getOffset(0L));
     }

     public void testForOffsetHoursMinutes_positiveHoursOnly() {
         DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(5, 0);
         assertNotNull(zone);
         assertEquals("+05:00", zone.getID());
         assertEquals(5 * 60 * 60 * 1000, zone.getOffset(0L));
     }

     public void testForOffsetHoursMinutes_positiveHoursPositiveMinutes() {
         DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(3, 30);
         assertNotNull(zone);
         assertEquals("+03:30", zone.getID());
         assertEquals((3 * 60 + 30) * 60 * 1000, zone.getOffset(0L));
     }

     // -----------------------------------------------------------------------
     // Normal valid cases: negative hours with zero minutes
     // -----------------------------------------------------------------------

     public void testForOffsetHoursMinutes_negativeHoursZeroMinutes() {
         DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(-5, 0);
         assertNotNull(zone);
         assertEquals("-05:00", zone.getID());
         assertEquals(-5 * 60 * 60 * 1000, zone.getOffset(0L));
     }

     // -----------------------------------------------------------------------
     // Bug-exposing: negative minutes should be accepted but are rejected
     // -----------------------------------------------------------------------

     public void testForOffsetHoursMinutes_positiveHoursNegativeMinutes() {
         try {
             DateTimeZone.forOffsetHoursMinutes(1, -15);
             fail("Expected IllegalArgumentException for negative minutes");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("Minutes out of range"));
         }
     }

     public void testForOffsetHoursMinutes_negativeHoursNegativeMinutes() {
         try {
             DateTimeZone.forOffsetHoursMinutes(-1, -15);
             fail("Expected IllegalArgumentException for negative minutes");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("Minutes out of range"));
         }
     }

     public void testForOffsetHoursMinutes_zeroHoursNegativeMinutes() {
         try {
             DateTimeZone.forOffsetHoursMinutes(0, -59);
             fail("Expected IllegalArgumentException for negative minutes");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("Minutes out of range"));
         }
     }

     // -----------------------------------------------------------------------
     // Boundary: maximum positive and negative offsets
     // -----------------------------------------------------------------------

     public void testForOffsetHoursMinutes_maxPositive() {
         DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(23, 59);
         assertNotNull(zone);
         assertEquals("+23:59", zone.getID());
         assertEquals(1439 * 60 * 1000, zone.getOffset(0L));
     }

     public void testForOffsetHoursMinutes_maxNegative() {
         try {
             DateTimeZone.forOffsetHoursMinutes(-23, -59);
             fail("Expected IllegalArgumentException for negative minutes");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("Minutes out of range"));
         }
     }

     // -----------------------------------------------------------------------
     // Invalid: hours out of range
     // -----------------------------------------------------------------------

     public void testForOffsetHoursMinutes_hoursTooLarge() {
         try {
             DateTimeZone.forOffsetHoursMinutes(24, 0);
             fail("Expected IllegalArgumentException for hoursOffset=24");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("Hours out of range"));
         }
     }

     public void testForOffsetHoursMinutes_hoursTooSmall() {
         try {
             DateTimeZone.forOffsetHoursMinutes(-24, 0);
             fail("Expected IllegalArgumentException for hoursOffset=-24");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("Hours out of range"));
         }
     }

     // -----------------------------------------------------------------------
     // Invalid: minutes out of range
     // -----------------------------------------------------------------------

     public void testForOffsetHoursMinutes_minutesTooLarge() {
         try {
             DateTimeZone.forOffsetHoursMinutes(0, 60);
             fail("Expected IllegalArgumentException for minutesOffset=60");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("Minutes out of range"));
         }
     }
 }
