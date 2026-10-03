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
     // Bug-exposing: negative minutes should be accepted
     // -----------------------------------------------------------------------

     public void testForOffsetHoursMinutes_positiveHoursNegativeMinutes() {
         // (1, -15) = 60 + (-15) = 45 minutes total
         try {
             DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(1, -15);
             assertNotNull(zone);
             assertEquals("+00:45", zone.getID());
             assertEquals(45 * 60 * 1000, zone.getOffset(0L));
         } catch (IllegalArgumentException e) {
             fail("forOffsetHoursMinutes(1, -15) should not throw: " + e.getMessage());
         }
     }

     public void testForOffsetHoursMinutes_negativeHoursNegativeMinutes() {
         // (-1, -15) = -60 + (-15) = -75 minutes total  -- MAIN BUG
         try {
             DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(-1, -15);
             assertNotNull(zone);
             assertEquals("-01:15", zone.getID());
             assertEquals(-75 * 60 * 1000, zone.getOffset(0L));
         } catch (IllegalArgumentException e) {
             fail("forOffsetHoursMinutes(-1, -15) should not throw: " + e.getMessage());
         }
     }

     public void testForOffsetHoursMinutes_zeroHoursNegativeMinutes() {
         // (0, -59) = 0 + (-59) = -59 minutes
         try {
             DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(0, -59);
             assertNotNull(zone);
             assertEquals("-00:59", zone.getID());
             assertEquals(-59 * 60 * 1000, zone.getOffset(0L));
         } catch (IllegalArgumentException e) {
             fail("forOffsetHoursMinutes(0, -59) should not throw: " + e.getMessage());
         }
     }

     // -----------------------------------------------------------------------
     // Boundary: maximum positive and negative offsets
     // -----------------------------------------------------------------------

     public void testForOffsetHoursMinutes_maxPositive() {
         // (23, 59) = 23*60 + 59 = 1439 minutes
         DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(23, 59);
         assertNotNull(zone);
         assertEquals("+23:59", zone.getID());
         assertEquals(1439 * 60 * 1000, zone.getOffset(0L));
     }

     public void testForOffsetHoursMinutes_maxNegative() {
         // (-23, -59) = -23*60 + (-59) = -1439 minutes
         try {
             DateTimeZone zone = DateTimeZone.forOffsetHoursMinutes(-23, -59);
             assertNotNull(zone);
             assertEquals("-23:59", zone.getID());
             assertEquals(-1439 * 60 * 1000, zone.getOffset(0L));
         } catch (IllegalArgumentException e) {
             fail("forOffsetHoursMinutes(-23, -59) should not throw: " + e.getMessage());
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