package org.joda.time;

import static org.junit.Assert.*;
import org.junit.Test;

public class TestDateTimeZone {

 @Test
 public void testForID_String_old() {
     DateTimeZone zone = DateTimeZone.forID("WET");
     assertEquals("WET", zone.getID());
 }

 @Test
 public void testForID_UTC() {
     DateTimeZone zone = DateTimeZone.forID("UTC");
     assertNotNull(zone);
     assertEquals("UTC", zone.getID());
     assertSame(DateTimeZone.UTC, zone);
 }

 @Test
 public void testForID_Null() {
     DateTimeZone defaultZone = DateTimeZone.getDefault();
     DateTimeZone zone = DateTimeZone.forID(null);
     assertNotNull(zone);
     assertSame(defaultZone, zone);
 }

 @Test(expected = IllegalArgumentException.class)
 public void testForID_Unknown() {
     DateTimeZone.forID("Unknown/Invalid_Zone");
 }

 @Test(expected = IllegalArgumentException.class)
 public void testForID_EmptyString() {
     DateTimeZone.forID("");
 }

 @Test
 public void testForID_CanonicalZone() {
     DateTimeZone zone = DateTimeZone.forID("Europe/London");
     assertNotNull(zone);
     assertEquals("Europe/London", zone.getID());
 }

 @Test
 public void testForID_PositiveOffset() {
     DateTimeZone zone = DateTimeZone.forID("+02:00");
     assertNotNull(zone);
     assertEquals("+02:00", zone.getID());
     assertEquals(2 * 3600 * 1000, zone.getOffset(0L));
 }

 @Test
 public void testForID_NegativeOffset() {
     DateTimeZone zone = DateTimeZone.forID("-05:00");
     assertNotNull(zone);
     assertEquals("-05:00", zone.getID());
     assertEquals(-5 * 3600 * 1000, zone.getOffset(0L));
 }

 @Test
 public void testForID_OffsetWithMinutes() {
     DateTimeZone zone = DateTimeZone.forID("+05:30");
     assertNotNull(zone);
     assertEquals("+05:30", zone.getID());
     assertEquals((5 * 60 + 30) * 60 * 1000, zone.getOffset(0L));
 }

 @Test
 public void testForID_ZeroOffsetReturnsUTC() {
     DateTimeZone zone = DateTimeZone.forID("+00:00");
     assertNotNull(zone);
     assertEquals("UTC", zone.getID());
     assertSame(DateTimeZone.UTC, zone);
 }

 @Test
 public void testForID_DefaultConsistency() {
     DateTimeZone defaultZone = DateTimeZone.getDefault();
     DateTimeZone fromNull = DateTimeZone.forID(null);
     assertSame(defaultZone, fromNull);
 }

 @Test
 public void testForID_OffsetNotEqualToUTC() {
     DateTimeZone zone = DateTimeZone.forID("+01:00");
     assertNotSame(DateTimeZone.UTC, zone);
     assertFalse(zone.getID().equals("UTC"));
 }

@Test
public void testConvertLocalToUTC_Strict_Gap() {
    DateTimeZone zone = DateTimeZone.forID("America/New_York");
    // 2023-03-12 02:30:00 local time falls in the spring-forward gap
    long localMillis = new DateTime(2023, 3, 12, 2, 30, 0, 0, DateTimeZone.UTC).getMillis();
    try {
        zone.convertLocalToUTC(localMillis, true);
        fail("Expected IllegalArgumentException for local time in DST gap");
    } catch (IllegalArgumentException expected) {
        // expected
    }
}

@Test
public void testConvertLocalToUTC_NonStrict_Gap() {
    DateTimeZone zone = DateTimeZone.forID("America/New_York");
    long localMillis = new DateTime(2023, 3, 12, 2, 30, 0, 0, DateTimeZone.UTC).getMillis();
    long utcMillis = zone.convertLocalToUTC(localMillis, false);
    assertNotNull(zone);
    // The returned UTC millis should not equal the local millis because an offset is applied
    assertNotEquals(localMillis, utcMillis);
}

@Test
public void testAdjustOffset_FixedZone() {
    long instant = 123456789L;
    assertEquals(instant, DateTimeZone.UTC.adjustOffset(instant, true));
    assertEquals(instant, DateTimeZone.UTC.adjustOffset(instant, false));
}

@Test
public void testForID_OldID_MET() {
    DateTimeZone zone = DateTimeZone.forID("MET");
    assertNotNull(zone);
    assertEquals("MET", zone.getID());
}
}
