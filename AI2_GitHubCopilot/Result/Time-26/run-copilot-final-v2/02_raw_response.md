package org.joda.time;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;

 import org.joda.time.chrono.LenientChronology;
 import org.joda.time.chrono.ZonedChronology;
 import org.junit.Test;

 /**
  * Tests addressing the DST cutover offset‑preservation bug (Time‑26b / bug #60).
  *
  * <p>When changing fields of a {@link DateTime} that falls into a DST overlap
  * or was created with a specific offset, the resulting DateTime must retain
  * the original offset rather than falling back to the default ambiguity
  * resolution of {@link DateTimeZone#convertLocalToUTC(long, boolean)}.
  *
  * <p>Covered classes:
  * <ul>
  *   <li>{@link ZonedChronology} – localToUTC, convertField</li>
  *   <li>{@link DateTimeZone} – convertLocalToUTC, getOffsetFromLocal</li>
  *   <li>{@link LenientDateTimeField} – set</li>
  * </ul>
  */
 public class TestZonedChronologyBug26 {

     // -----------------------------------------------------------------------
     // Normal – far from DST
     // -----------------------------------------------------------------------

     @Test
     public void testNormalSetFarFromDST() {
         DateTimeZone zone = DateTimeZone.forID("Europe/Paris");
         // June 15 is far from any DST transition.
         DateTime dt = new DateTime(2010, 6, 15, 4, 30, 45, 789, zone);
         int originalOffset = zone.getOffset(dt.getMillis());
         DateTime modified = dt.withSecondOfMinute(10);
         assertEquals("Offset must not change when far from DST",
                 originalOffset, zone.getOffset(modified.getMillis()));
     }

     // -----------------------------------------------------------------------
     // DST overlap (fall‑back) – *earlier* (summer) offset must be preserved
     // -----------------------------------------------------------------------

     @Test
     public void testSetSecondDuringFallBackWithSummerOffset() {
         DateTimeZone zone = DateTimeZone.forID("Europe/Paris");
         // 2010‑10‑31 02:30:00.123 local – ambiguous, constructor picks earlier +02:00
         DateTime dt = new DateTime(2010, 10, 31, 2, 30, 0, 123, zone);
         int originalOffset = zone.getOffset(dt.getMillis());
         assertEquals("Initial offset must be +02:00 (summer)", 7200000, originalOffset);

         DateTime modified = dt.withSecondOfMinute(10);
         assertEquals("Offset must remain +02:00 after setting second in overlap",
                 7200000, zone.getOffset(modified.getMillis()));
     }

     @Test
     public void testSetMinuteDuringFallBackWithSummerOffset() {
         DateTimeZone zone = DateTimeZone.forID("Europe/Paris");
         DateTime dt = new DateTime(2010, 10, 31, 2, 0, 10, 123, zone);
         assertEquals(7200000, zone.getOffset(dt.getMillis()));
         DateTime modified = dt.withMinuteOfHour(30);
         assertEquals("Offset must remain +02:00 after setting minute in overlap",
                 7200000, zone.getOffset(modified.getMillis()));
     }

     @Test
     public void testSetMillisDuringFallBackWithSummerOffset() {
         DateTimeZone zone = DateTimeZone.forID("Europe/Paris");
         DateTime dt = new DateTime(2010, 10, 31, 2, 30, 10, 123, zone);
         assertEquals(7200000, zone.getOffset(dt.getMillis()));
         DateTime modified = dt.withMillisOfSecond(456);
         assertEquals("Offset must remain +02:00 after setting millis in overlap",
                 7200000, zone.getOffset(modified.getMillis()));
     }

     // -----------------------------------------------------------------------
     // DST overlap (fall‑back) – *later* (winter) offset must be preserved
     // -----------------------------------------------------------------------

     @Test
     public void testSetSecondDuringFallBackWithWinterOffset() {
         DateTimeZone zone = DateTimeZone.forID("Europe/Paris");
         // Start at 03:00 (unambiguous winter, +01:00) then move back into ambiguous 02:00.
         DateTime dt = new DateTime(2010, 10, 31, 3, 0, 0, 123, zone);
         assertEquals(3600000, zone.getOffset(dt.getMillis()));
         DateTime moved = dt.withHourOfDay(2);   // must keep winter offset
         assertEquals("withHourOfDay(2) must retain winter offset",
                 3600000, zone.getOffset(moved.getMillis()));

         DateTime modified = moved.withSecondOfMinute(10);
         assertEquals("Second set must keep winter offset in overlap",
                 3600000, zone.getOffset(modified.getMillis()));
     }

     @Test
     public void testSetMinuteDuringFallBackWithWinterOffset() {
         DateTimeZone zone = DateTimeZone.forID("Europe/Paris");
         DateTime dt = new DateTime(2010, 10, 31, 3, 10, 0, 0, zone);
         assertEquals(3600000, zone.getOffset(dt.getMillis()));
         DateTime moved = dt.withHourOfDay(2);
         assertEquals(3600000, zone.getOffset(moved.getMillis()));
         DateTime modified = moved.withMinuteOfHour(30);
         assertEquals("Minute set must keep winter offset in overlap",
                 3600000, zone.getOffset(modified.getMillis()));
     }

     // -----------------------------------------------------------------------
     // Spring‑forward gap – field setting on the adjusted instant
     // -----------------------------------------------------------------------

     @Test
     public void testSetSecondAfterSpringForwardAdjustment() {
         DateTimeZone zone = DateTimeZone.forID("Europe/Paris");
         // 2010‑03‑28 02:30:00.123 does not exist – automatically adjusted to 03:30 +02:00.
         DateTime dt = new DateTime(2010, 3, 28, 2, 30, 0, 123, zone);
         // Must be at 03:30 local with CEST offset.
         assertEquals(7200000, zone.getOffset(dt.getMillis()));
         assertEquals("Adjusted hour should be 3", 3, dt.getHourOfDay());

         DateTime modified = dt.withSecondOfMinute(10);
         assertEquals("With second must keep offset after gap adjustment",
                 7200000, zone.getOffset(modified.getMillis()));
     }

     // -----------------------------------------------------------------------
     // LenientDateTimeField.set – must also preserve offset
     // -----------------------------------------------------------------------

     @Test
     public void testLenientDateTimeFieldSetDuringFallBack() {
         DateTimeZone zone = DateTimeZone.forID("Europe/Paris");
         Chronology lenient =
LenientChronology.getInstance(ISOChronology.getInstance().withZone(zone));
         DateTime dt = new DateTime(2010, 10, 31, 2, 30, 0, 123, lenient);
         int originalOffset = zone.getOffset(dt.getMillis());
         assertEquals(7200000, originalOffset);

         DateTime modified = dt.withSecondOfMinute(10);
         assertEquals("Lenient set must preserve offset in overlap",
                 7200000, zone.getOffset(modified.getMillis()));
     }

     // -----------------------------------------------------------------------
     // Bug 2182444 – US/Central (standard‑time offset preserved)
     // -----------------------------------------------------------------------

     @Test
     public void testBug2182444_usCentral() {
         DateTimeZone zone = DateTimeZone.forID("America/Chicago");
         // Create a DateTime representing 01:00 CST (‑06:00), the later occurrence.
         // Get to 00:00 CST, then add one hour – this guarantees the standard offset.
         DateTime dt = new DateTime(2008, 11, 2, 0, 0, 0, 0, zone);
         assertEquals(-21600000, zone.getOffset(dt.getMillis()));   // -06:00
         DateTime oneAM = dt.plusHours(1);
         assertEquals("Later occurrence 01:00 must have offset -06:00",
                 -21600000, zone.getOffset(oneAM.getMillis()));

         DateTime modified = oneAM.withMinuteOfHour(0);
         assertEquals("Offset must remain -06:00 after withMinuteOfHour",
                 -21600000, zone.getOffset(modified.getMillis()));
     }

     // -----------------------------------------------------------------------
     // Bug 2182444 – Australia/NSW (summer‑time offset preserved)
     // -----------------------------------------------------------------------

     @Test
     public void testBug2182444_ausNSW() {
         DateTimeZone zone = DateTimeZone.forID("Australia/NSW");
         // 2008‑04‑06 02:00:00 is ambiguous; constructor uses earlier +11:00.
         DateTime dt = new DateTime(2008, 4, 6, 2, 0, 0, 0, zone);
         assertEquals(39600000, zone.getOffset(dt.getMillis()));   // +11:00

         DateTime modified = dt.withSecondOfMinute(0);
         assertEquals("Offset must remain +11:00 after withSecondOfMinute",
                 39600000, zone.getOffset(modified.getMillis()));
     }

     // -----------------------------------------------------------------------
     // Edge: mock‑style fixed‑offset zone (proves convertField is not broken)
     // -----------------------------------------------------------------------

     @Test
     public void testFixedOffsetZonePreservesOffset() {
         DateTimeZone fixed = DateTimeZone.forOffsetHours(1); // +01:00
         DateTime dt = new DateTime(2010, 6, 15, 12, 30, 45, 789, fixed);
         int originalOffset = fixed.getOffset(dt.getMillis());
         DateTime modified = dt.withSecondOfMinute(10);
         assertEquals("Fixed offset must remain unchanged",
                 originalOffset, fixed.getOffset(modified.getMillis()));
     }
 }