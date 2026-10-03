package org.apache.commons.lang.time;

 import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertNotNull;
 import static org.junit.Assert.fail;

 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.ObjectInputStream;
 import java.io.ObjectOutputStream;
 import java.util.Calendar;
 import java.util.Date;
 import java.util.GregorianCalendar;
 import java.util.Locale;
 import java.util.TimeZone;

 import org.junit.Test;

 public class FastDateFormatLang56Test {

     private static Date fixedDate() {
         Calendar cal = new GregorianCalendar(2009, Calendar.MARCH, 15, 12, 30, 45);
         cal.set(Calendar.MILLISECOND, 123);
         cal.setTimeZone(TimeZone.getTimeZone("UTC"));
         return cal.getTime();
     }

     private static FastDateFormat roundTrip(FastDateFormat fmt) throws Exception {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         try (ObjectOutputStream oos = new ObjectOutputStream(bos)) {
             oos.writeObject(fmt);
         }
         try (ObjectInputStream ois = new ObjectInputStream(new
ByteArrayInputStream(bos.toByteArray()))) {
             return (FastDateFormat) ois.readObject();
         }
     }

     @Test
     public void testSerializePaddedPatternHHmmssSSS() throws Exception {
         FastDateFormat original = FastDateFormat.getInstance("HH:mm:ss.SSS",
TimeZone.getTimeZone("UTC"), Locale.US);
         FastDateFormat deserialized = roundTrip(original);
         assertNotNull(deserialized);
     }

     @Test
     public void testPaddedPatternRoundTripFormatLongEquals() throws Exception {
         FastDateFormat original = FastDateFormat.getInstance("HH:mm:ss.SSS",
TimeZone.getTimeZone("UTC"), Locale.US);
         long millis = fixedDate().getTime();
         FastDateFormat deserialized = roundTrip(original);
         assertEquals(original.format(millis), deserialized.format(millis));
     }

     @Test
     public void testPaddedPatternRoundTripFormatDateEquals() throws Exception {
         FastDateFormat original = FastDateFormat.getInstance("dd-MM-yyyy HH:mm",
TimeZone.getTimeZone("UTC"), Locale.US);
         Date date = fixedDate();
         FastDateFormat deserialized = roundTrip(original);
         assertEquals(original.format(date), deserialized.format(date));
     }

     @Test
     public void testIsoDateTimePatternRoundTrip() throws Exception {
         String pattern = "yyyy-MM-dd'T'HH:mm:ss";
         FastDateFormat original = FastDateFormat.getInstance(pattern, TimeZone.getTimeZone("UTC"),
Locale.US);
         long millis = fixedDate().getTime();
         FastDateFormat deserialized = roundTrip(original);
         assertEquals(pattern, deserialized.getPattern());
         assertEquals(original.format(millis), deserialized.format(millis));
     }

     @Test
     public void testDefaultTimezonePaddedPatternRoundTrip() throws Exception {
         FastDateFormat original = FastDateFormat.getInstance("MM/dd/yyyy hh:mm a");
         Date date = fixedDate();
         FastDateFormat deserialized = roundTrip(original);
         assertEquals(original.format(date), deserialized.format(date));
     }

     @Test
     public void testUnpaddedOnlyPatternRoundTrip() throws Exception {
         FastDateFormat original = FastDateFormat.getInstance("yyyy", TimeZone.getTimeZone("UTC"),
Locale.US);
         long millis = fixedDate().getTime();
         FastDateFormat deserialized = roundTrip(original);
         assertEquals("2009", deserialized.format(millis));
     }

     @Test
     public void testTextFieldOnlyPatternRoundTrip() throws Exception {
         FastDateFormat original = FastDateFormat.getInstance("MMMM", TimeZone.getTimeZone("UTC"),
Locale.US);
         Date date = fixedDate();
         FastDateFormat deserialized = roundTrip(original);
         assertEquals(original.format(date), deserialized.format(date));
     }

     @Test
     public void testRoundTripPreservesEquality() throws Exception {
         FastDateFormat original = FastDateFormat.getInstance("HH:mm:ss",
TimeZone.getTimeZone("UTC"), Locale.US);
         FastDateFormat deserialized = roundTrip(original);
         assertEquals(original, deserialized);
     }

     @Test
     public void testRoundTripPreservesPatternTimezoneLocale() throws Exception {
         TimeZone tz = TimeZone.getTimeZone("America/Chicago");
         FastDateFormat original = FastDateFormat.getInstance("yyyy-MM-dd", tz, Locale.GERMANY);
         FastDateFormat deserialized = roundTrip(original);
         assertEquals("yyyy-MM-dd", deserialized.getPattern());
         assertEquals(tz, deserialized.getTimeZone());
         assertEquals(Locale.GERMANY, deserialized.getLocale());
     }

     @Test
     public void testEmptyPatternRoundTrip() throws Exception {
         FastDateFormat original = FastDateFormat.getInstance("", TimeZone.getTimeZone("UTC"),
Locale.US);
         String expected = "";
         FastDateFormat deserialized = roundTrip(original);
         assertEquals(expected, deserialized.format(fixedDate()));
     }

     @Test
     public void testNullPatternThrowsIllegalArgument() {
         try {
             FastDateFormat.getInstance(null);
             fail("Expected IllegalArgumentException for null pattern");
         } catch (IllegalArgumentException e) {
             assertNotNull(e.getMessage());
         }
     }

     @Test
     public void testPaddedFieldsWithFixedKnownOutput() throws Exception {
         FastDateFormat original = FastDateFormat.getInstance("dd/MM/yyyy HH:mm:ss:SSS",
                 TimeZone.getTimeZone("UTC"), Locale.US);
         FastDateFormat deserialized = roundTrip(original);
         Date date = fixedDate();
         assertEquals("15/03/2009 12:30:45:123", deserialized.format(date));
         assertEquals(original.format(date), deserialized.format(date));
     }
 }
