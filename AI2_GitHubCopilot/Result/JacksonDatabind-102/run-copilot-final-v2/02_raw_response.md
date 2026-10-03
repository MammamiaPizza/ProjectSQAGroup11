package com.fasterxml.jackson.databind.ser.std;

 import static org.junit.Assert.*;

 import java.sql.Date;
 import java.util.Locale;
 import java.util.TimeZone;

 import org.junit.Before;
 import org.junit.Test;

 import com.fasterxml.jackson.annotation.JsonFormat;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.SerializationFeature;

 public class DateTimeSerializerBaseTest {

     private ObjectMapper mapper;

     @Before
     public void setUp() {
         mapper = new ObjectMapper();
     }

     // Helper to serialize and trim quotes if needed for easy assertion
     private String ser(Object value) throws Exception {
         return mapper.writeValueAsString(value);
     }

     // -----------------------------------------------------------------------
     // Wrappers to apply @JsonFormat on specific fields
     // -----------------------------------------------------------------------

     public static class SqlDateStringCustom {
         @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy+MM+dd")
         public java.sql.Date date;
         public SqlDateStringCustom() {}
         public SqlDateStringCustom(java.sql.Date date) { this.date = date; }
     }

     public static class SqlDateStringNoPattern {
         @JsonFormat(shape = JsonFormat.Shape.STRING)
         public java.sql.Date date;
         public SqlDateStringNoPattern() {}
         public SqlDateStringNoPattern(java.sql.Date date) { this.date = date; }
     }

     public static class SqlDateNumeric {
         @JsonFormat(shape = JsonFormat.Shape.NUMBER)
         public java.sql.Date date;
         public SqlDateNumeric() {}
         public SqlDateNumeric(java.sql.Date date) { this.date = date; }
     }

     public static class SqlDateCustomNoShape {
         @JsonFormat(pattern = "yyyy+MM+dd")
         public java.sql.Date date;
         public SqlDateCustomNoShape() {}
         public SqlDateCustomNoShape(java.sql.Date date) { this.date = date; }
     }

     public static class UtilDateStringCustom {
         @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy+MM+dd")
         public java.util.Date date;
         public UtilDateStringCustom() {}
         public UtilDateStringCustom(java.util.Date date) { this.date = date; }
     }

     public static class UtilDateStringNoPattern {
         @JsonFormat(shape = JsonFormat.Shape.STRING)
         public java.util.Date date;
         public UtilDateStringNoPattern() {}
         public UtilDateStringNoPattern(java.util.Date date) { this.date = date; }
     }

     public static class SqlDateStringAndTimeZone {
         @JsonFormat(shape = JsonFormat.Shape.STRING, timezone = "GMT+2")
         public java.sql.Date date;
         public SqlDateStringAndTimeZone() {}
         public SqlDateStringAndTimeZone(java.sql.Date date) { this.date = date; }
     }

     public static class SqlDateStringAndLocale {
         @JsonFormat(shape = JsonFormat.Shape.STRING, locale = "de")
         public java.sql.Date date;
         public SqlDateStringAndLocale() {}
         public SqlDateStringAndLocale(java.sql.Date date) { this.date = date; }
     }

     // -----------------------------------------------------------------------
     // Tests
     // -----------------------------------------------------------------------

     /**
      * Regression test for the bug: shape=STRING with custom pattern must produce a
      * formatted string, not a timestamp number.
      */
     @Test
     public void testSqlDateStringShapeCustomPattern() throws Exception {
         java.sql.Date d = java.sql.Date.valueOf("1980-04-14");
         String json = ser(new SqlDateStringCustom(d));
         // The trigger test expects "1980+04+14" inside JSON
         assertTrue("Should contain custom-formatted date string",
                    json.contains("\"1980+04+14\""));
     }

     /**
      * shape=STRING (no pattern) must produce a string using default date format;
      * the buggy _asTimestamp incorrectly falls back to WRITE_DATES_AS_TIMESTAMPS
      * when no pattern is given and shape is STRING.
      */
     @Test
     public void testSqlDateStringShapeNoPattern() throws Exception {
         java.sql.Date d = java.sql.Date.valueOf("1980-04-14");
         // Default feature is to write dates as timestamps (true). Even then,
         // shape=STRING must override it to produce a string.
         String json = ser(new SqlDateStringNoPattern(d));
         // A string serialization must not be a bare number
         assertFalse("Shape STRING must serialize as string, not timestamp number",
                     json.matches("\\{\"date\":\\d+\\}"));
     }

     /**
      * shape=NUMBER must produce a numeric timestamp regardless of other settings.
      */
     @Test
     public void testSqlDateNumericShape() throws Exception {
         java.sql.Date d = java.sql.Date.valueOf("1980-04-14");
         // Disable the global timestamp feature and verify shape=NUMBER still emits number.
         mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
         String json = ser(new SqlDateNumeric(d));
         assertTrue("Shape NUMBER must produce a numeric timestamp",
                    json.matches("\\{\"date\":\\d+\\}"));
     }

     /**
      * Custom pattern without explicit shape: should be treated as string output.
      */
     @Test
     public void testSqlDateCustomPatternNoShape() throws Exception {
         java.sql.Date d = java.sql.Date.valueOf("1980-04-14");
         String json = ser(new SqlDateCustomNoShape(d));
         assertTrue("Custom pattern implies string serialization",
                    json.contains("\"1980+04+14\""));
     }

     /**
      * When the global WRITE_DATES_AS_TIMESTAMPS feature is disabled, the
      * default behaviour is to serialize as string using default format.
      */
     @Test
     public void testSqlDateDefaultWithTimestampsDisabled() throws Exception {
         java.sql.Date d = java.sql.Date.valueOf("1980-04-14");
         mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
         // Wrap in simple object to get JSON object
         // Use a generic map-like approach? Just serialize directly.
         // Direct serialization: mapper.writeValueAsString(d) gives bare value.
         // But we want to check it's a string, not number.
         String json = mapper.writeValueAsString(d);
         // With timestamps disabled default serialization is string
         assertTrue("Default (no override, timestamps disabled) must be string",
                    json.startsWith("\"") && json.endsWith("\""));
     }

     /**
      * Util.Date with shape=STRING and custom pattern.
      */
     @Test
     public void testUtilDateStringShapeCustomPattern() throws Exception {
         java.util.Date d = java.sql.Date.valueOf("1980-04-14"); // java.sql.Date is a
java.util.Date
         String json = ser(new UtilDateStringCustom(d));
         assertTrue(json.contains("\"1980+04+14\""));
     }

     /**
      * Util.Date with shape=STRING, no pattern – the same bug class applies.
      */
     @Test
     public void testUtilDateStringShapeNoPattern() throws Exception {
         java.util.Date d = java.sql.Date.valueOf("1980-04-14");
         String json = ser(new UtilDateStringNoPattern(d));
         assertFalse("Shape STRING must produce a string, even without pattern",
                     json.matches("\\{\"date\":\\d+\\}"));
     }

     /**
      * shape=STRING with a timezone override (no pattern) – must still serialize as
      * string using the configured timezone offset.
      */
     @Test
     public void testSqlDateStringShapeWithTimeZone() throws Exception {
         java.sql.Date d = java.sql.Date.valueOf("1980-04-14");
         // The exact default format is context-dependent; we just verify it is a string,
         // not a number. The bug would produce a number.
         String json = ser(new SqlDateStringAndTimeZone(d));
         assertFalse("Shape STRING must be respected, not overridden by missing pattern",
                     json.matches("\\{\"date\":\\d+\\}"));
     }

     /**
      * shape=STRING with a locale override (no pattern) – must still produce a string.
      */
     @Test
     public void testSqlDateStringShapeWithLocale() throws Exception {
         java.sql.Date d = java.sql.Date.valueOf("1980-04-14");
         String json = ser(new SqlDateStringAndLocale(d));
         assertFalse("Shape STRING must be respected, not overridden by missing pattern",
                     json.matches("\\{\"date\":\\d+\\}"));
     }

     /**
      * Verify that a null date serializes as JSON null.
      */
     @Test
     public void testNullSqlDate() throws Exception {
         String json = ser(new SqlDateStringCustom(null));
         assertTrue("Null date should serialize as null",
                    json.contains("\"date\":null"));
     }

     /**
      * When no override is present and global timestamps are enabled (default),
      * a java.sql.Date must serialize as a numeric timestamp.
      */
     @Test
     public void testDefaultBehaviorTimestampEnabled() throws Exception {
         java.sql.Date d = java.sql.Date.valueOf("1980-04-14");
         String json = mapper.writeValueAsString(d);
         // Should be a plain number
         assertTrue(json.matches("\\d+"));
     }

     /**
      * Bound check: epoch date serialized with shape=NUMBER returns 0.
      */
     @Test
     public void testEpochDateNumericShape() throws Exception {
         java.sql.Date epoch = new java.sql.Date(0);
         String json = ser(new SqlDateNumeric(epoch));
         assertTrue("Epoch date must be timestamp 0", json.contains("\"date\":0"));
     }
 }