package org.joda.time.format;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;

 import java.util.Locale;

 import org.joda.time.DateTime;
 import org.joda.time.DateTimeFieldType;
 import org.joda.time.DateTimeZone;
 import org.joda.time.IllegalFieldValueException;
 import org.joda.time.chrono.ISOChronology;
 import org.junit.Test;

 public class DateTimeParserBucketTest {

     private DateTimeParserBucket newBucket() {
         return new DateTimeParserBucket(0L, ISOChronology.getInstanceUTC(), Locale.US, null);
     }

     private long utcMillis(int year, int month, int day) {
         return new DateTime(year, month, day, 0, 0, DateTimeZone.UTC).getMillis();
     }

     @Test
     public void testWeekyearMonthWeekDay_2010() {
         DateTimeParserBucket bucket = newBucket();
         bucket.saveField(DateTimeFieldType.weekyear(), 2010);
         bucket.saveField(DateTimeFieldType.monthOfYear(), 1);
         bucket.saveField(DateTimeFieldType.weekOfWeekyear(), 1);
         bucket.saveField(DateTimeFieldType.dayOfWeek(), 1);
         assertEquals(utcMillis(2010, 1, 4), bucket.computeMillis());
     }

     @Test
     public void testWeekyearMonthWeekDay_2011() {
         DateTimeParserBucket bucket = newBucket();
         bucket.saveField(DateTimeFieldType.weekyear(), 2011);
         bucket.saveField(DateTimeFieldType.monthOfYear(), 1);
         bucket.saveField(DateTimeFieldType.weekOfWeekyear(), 1);
         bucket.saveField(DateTimeFieldType.dayOfWeek(), 1);
         assertEquals(utcMillis(2011, 1, 3), bucket.computeMillis());
     }

     @Test
     public void testWeekyearMonthWeekDay_2012() {
         DateTimeParserBucket bucket = newBucket();
         bucket.saveField(DateTimeFieldType.weekyear(), 2012);
         bucket.saveField(DateTimeFieldType.monthOfYear(), 1);
         bucket.saveField(DateTimeFieldType.weekOfWeekyear(), 1);
         bucket.saveField(DateTimeFieldType.dayOfWeek(), 1);
         assertEquals(utcMillis(2012, 1, 2), bucket.computeMillis());
     }

     @Test
     public void testYearMonthWeekDay_2010() {
         DateTimeParserBucket bucket = newBucket();
         bucket.saveField(DateTimeFieldType.year(), 2010);
         bucket.saveField(DateTimeFieldType.monthOfYear(), 1);
         bucket.saveField(DateTimeFieldType.weekOfWeekyear(), 1);
         bucket.saveField(DateTimeFieldType.dayOfWeek(), 1);
         assertEquals(utcMillis(2010, 1, 4), bucket.computeMillis());
     }

     @Test
     public void testYearMonthWeekDay_2011() {
         DateTimeParserBucket bucket = newBucket();
         bucket.saveField(DateTimeFieldType.year(), 2011);
         bucket.saveField(DateTimeFieldType.monthOfYear(), 1);
         bucket.saveField(DateTimeFieldType.weekOfWeekyear(), 1);
         bucket.saveField(DateTimeFieldType.dayOfWeek(), 1);
         assertEquals(utcMillis(2011, 1, 3), bucket.computeMillis());
     }

     @Test
     public void testYearMonthWeekDay_2012() {
         DateTimeParserBucket bucket = newBucket();
         bucket.saveField(DateTimeFieldType.year(), 2012);
         bucket.saveField(DateTimeFieldType.monthOfYear(), 1);
         bucket.saveField(DateTimeFieldType.weekOfWeekyear(), 1);
         bucket.saveField(DateTimeFieldType.dayOfWeek(), 1);
         assertEquals(utcMillis(2012, 1, 2), bucket.computeMillis());
     }

     @Test
     public void testYearMonthWeekDay_2016() {
         DateTimeParserBucket bucket = newBucket();
         bucket.saveField(DateTimeFieldType.year(), 2016);
         bucket.saveField(DateTimeFieldType.monthOfYear(), 1);
         bucket.saveField(DateTimeFieldType.weekOfWeekyear(), 1);
         bucket.saveField(DateTimeFieldType.dayOfWeek(), 1);
         assertEquals(utcMillis(2016, 1, 4), bucket.computeMillis());
     }

     @Test(expected = NullPointerException.class)
     public void testNullChronologyShouldDefault() {
         new DateTimeParserBucket(0L, null, Locale.US, null);
     }

     @Test(expected = NullPointerException.class)
     public void testSaveFieldWithNullFieldType() {
         newBucket().saveField((DateTimeFieldType) null, 1);
     }

     @Test
     public void testComputeMillisWithResetFields() {
         // base with a non-zero minute-of-hour to see the effect of resetting
         DateTimeParserBucket bucket = new DateTimeParserBucket(60000L,
                 ISOChronology.getInstanceUTC(), Locale.US, null);
         bucket.saveField(DateTimeFieldType.hourOfDay(), 12);
         long millisNoReset = bucket.computeMillis(false);
         long millisReset = bucket.computeMillis(true);
         // with reset, smaller units (minute, second, millis) should be zeroed;
         // without reset, they keep the base value (1 minute from 60000L)
         assertTrue(millisReset % 3600000L == 0); // whole hour
         assertEquals(1, (millisNoReset / 60000L) % 60); // minute stays 1
     }

     @Test
     public void testSaveAndRestoreState() {
         DateTimeParserBucket bucket = newBucket();
         bucket.saveField(DateTimeFieldType.year(), 2010);
         bucket.saveField(DateTimeFieldType.monthOfYear(), 6);
         bucket.saveField(DateTimeFieldType.dayOfMonth(), 15);

         Object state = bucket.saveState();
         long originalMillis = bucket.computeMillis();

         // modify fields
         bucket.saveField(DateTimeFieldType.year(), 2020);
         bucket.computeMillis();

         assertTrue(bucket.restoreState(state));
         assertEquals(originalMillis, bucket.computeMillis());
     }

     @Test
     public void testInvalidMonthThrowsExceptionWithText() {
         DateTimeParserBucket bucket = newBucket();
         bucket.saveField(DateTimeFieldType.year(), 2010);
         bucket.saveField(DateTimeFieldType.monthOfYear(), 13);
         bucket.saveField(DateTimeFieldType.dayOfMonth(), 1);
         try {
             bucket.computeMillis(true, "2010-13-01");
             fail("Expected IllegalFieldValueException");
         } catch (IllegalFieldValueException e) {
             assertTrue(e.getMessage().contains("Cannot parse \"2010-13-01\""));
         }
     }
 }
