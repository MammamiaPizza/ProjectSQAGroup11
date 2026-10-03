package org.joda.time;

import junit.framework.TestCase;

public class TestMutableDateTime_AddZeroDST extends TestCase {

 private DateTimeZone berlin;
 private long overlapWinterMillisSecond; // 2011-10-30T02:30:00.000+01:00 CET (second occurrence)
 private long overlapWinterMillisFirst;  // 2011-10-30T02:30:00.000+02:00 CEST (first occurrence)

 protected void setUp() {
     berlin = DateTimeZone.forID("Europe/Berlin");
     // second occurrence: +01:00 offset (CET)
     DateTime tempSecond = new DateTime(2011, 10, 30, 2, 30, 0, 0, DateTimeZone.forOffsetHours(1));
     overlapWinterMillisSecond = tempSecond.getMillis();
     // first occurrence: +02:00 offset (CEST)
     DateTime tempFirst = new DateTime(2011, 10, 30, 2, 30, 0, 0, DateTimeZone.forOffsetHours(2));
     overlapWinterMillisFirst = tempFirst.getMillis();
 }

 public void testAddYears_zero_dstOverlapWinterSecond() {
     MutableDateTime mdt = new MutableDateTime(overlapWinterMillisSecond, berlin);
     long millisBefore = mdt.getMillis();
     int offsetBefore = berlin.getOffset(millisBefore);
     assertEquals("offset should be +01:00", 3600000, offsetBefore);
     mdt.addYears(0);
     assertEquals("millis unchanged", millisBefore, mdt.getMillis());
     assertEquals("offset unchanged", offsetBefore, berlin.getOffset(mdt.getMillis()));
 }

 public void testAddMonths_zero_dstOverlapWinterSecond() {
     MutableDateTime mdt = new MutableDateTime(overlapWinterMillisSecond, berlin);
     long millisBefore = mdt.getMillis();
     int offsetBefore = berlin.getOffset(millisBefore);
     assertEquals("offset should be +01:00", 3600000, offsetBefore);
     mdt.addMonths(0);
     assertEquals("millis unchanged", millisBefore, mdt.getMillis());
     assertEquals("offset unchanged", offsetBefore, berlin.getOffset(mdt.getMillis()));
 }

 public void testAddWeeks_zero_dstOverlapWinterSecond() {
     MutableDateTime mdt = new MutableDateTime(overlapWinterMillisSecond, berlin);
     long millisBefore = mdt.getMillis();
     int offsetBefore = berlin.getOffset(millisBefore);
     assertEquals("offset should be +01:00", 3600000, offsetBefore);
     mdt.addWeeks(0);
     assertEquals("millis unchanged", millisBefore, mdt.getMillis());
     assertEquals("offset unchanged", offsetBefore, berlin.getOffset(mdt.getMillis()));
 }

 public void testAddDays_zero_dstOverlapWinterSecond() {
     MutableDateTime mdt = new MutableDateTime(overlapWinterMillisSecond, berlin);
     long millisBefore = mdt.getMillis();
     int offsetBefore = berlin.getOffset(millisBefore);
     assertEquals("offset should be +01:00", 3600000, offsetBefore);
     mdt.addDays(0);
     assertEquals("millis unchanged", millisBefore, mdt.getMillis());
     assertEquals("offset unchanged", offsetBefore, berlin.getOffset(mdt.getMillis()));
 }

 public void testAddDurationFieldType_zero_dstOverlapWinterSecond() {
     MutableDateTime mdt = new MutableDateTime(overlapWinterMillisSecond, berlin);
     long millisBefore = mdt.getMillis();
     int offsetBefore = berlin.getOffset(millisBefore);
     assertEquals("offset should be +01:00", 3600000, offsetBefore);
     mdt.add(DurationFieldType.years(), 0);
     assertEquals("millis unchanged after add(years,0)", millisBefore, mdt.getMillis());
     assertEquals("offset unchanged after add(years,0)", offsetBefore,
berlin.getOffset(mdt.getMillis()));

     mdt.setMillis(overlapWinterMillisSecond); // reset
     mdt.add(DurationFieldType.months(), 0);
     assertEquals("millis unchanged after add(months,0)", overlapWinterMillisSecond,
mdt.getMillis());
     assertEquals("offset unchanged after add(months,0)", offsetBefore,
berlin.getOffset(mdt.getMillis()));
 }

 public void testAddYears_zero_dstOverlapWinterFirst() {
     MutableDateTime mdt = new MutableDateTime(overlapWinterMillisFirst, berlin);
     long millisBefore = mdt.getMillis();
     int offsetBefore = berlin.getOffset(millisBefore);
     assertEquals("offset should be +02:00", 7200000, offsetBefore);
     mdt.addYears(0);
     assertEquals("millis unchanged", millisBefore, mdt.getMillis());
     assertEquals("offset unchanged", offsetBefore, berlin.getOffset(mdt.getMillis()));
 }

 public void testAddYears_zero_summerNonDST() {
     MutableDateTime mdt = new MutableDateTime(2011, 7, 1, 12, 0, 0, 0, berlin);
     long millisBefore = mdt.getMillis();
     int offsetBefore = berlin.getOffset(millisBefore);
     assertEquals("offset should be +02:00 (CEST)", 7200000, offsetBefore);
     mdt.addYears(0);
     assertEquals("millis unchanged", millisBefore, mdt.getMillis());
     assertEquals("offset unchanged", offsetBefore, berlin.getOffset(mdt.getMillis()));
 }

 public void testAddYears_one_crossesDST() {
     // 2011-06-01T12:00+02:00  -> 2012-06-01T12:00+02:00 (both CEST)
     MutableDateTime mdt = new MutableDateTime(2011, 6, 1, 12, 0, 0, 0, berlin);
     mdt.addYears(1);
     assertEquals("year should advance", 2012, mdt.getYear());
     assertEquals("month", 6, mdt.getMonthOfYear());
     assertEquals("day", 1, mdt.getDayOfMonth());
     assertEquals("offset should still be +02:00 in summer", 7200000,
berlin.getOffset(mdt.getMillis()));
 }

}