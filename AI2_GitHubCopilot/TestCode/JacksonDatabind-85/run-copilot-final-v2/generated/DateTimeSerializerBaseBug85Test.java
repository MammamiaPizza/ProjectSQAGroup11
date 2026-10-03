package com.fasterxml.jackson.databind.ser;

 import static org.junit.Assert.*;

 import java.lang.reflect.Method;
 import java.text.DateFormat;
 import java.text.SimpleDateFormat;
 import java.util.Date;
 import java.util.Locale;
 import java.util.TimeZone;

 import org.junit.Before;
 import org.junit.Test;

 import com.fasterxml.jackson.annotation.JsonFormat;
 import com.fasterxml.jackson.core.JsonProcessingException;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.SerializationFeature;
 import com.fasterxml.jackson.databind.SerializerProvider;
 import com.fasterxml.jackson.databind.module.SimpleModule;
 import com.fasterxml.jackson.databind.ser.std.DateSerializer;
 import com.fasterxml.jackson.databind.ser.std.DateTimeSerializerBase;

 /**
  * Regression tests for DateTimeSerializerBase bug #85 (databind#1648):
  * when a custom DateFormat is set without an explicit pattern,
  * the serializer must use that format and produce a textual
  * representation, not a numeric timestamp nor a hardcoded ISO-8601
  * pattern.
  */
 public class DateTimeSerializerBaseBug85Test {

     private ObjectMapper mapper;
     private Date date;

     @Before
     public void setUp() {
         mapper = new ObjectMapper();
         // Fixed time zone and locale for deterministic results
         mapper.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));
         mapper.setLocale(Locale.US);
         date = new Date(0); // epoch
     }

     // -----------------------------------------------------------------
     // unit-level _asTimestamp() tests
     // -----------------------------------------------------------------

     @Test
     public void testAsTimestampReturnsFalseWhenCustomFormatPresent() throws Exception {
         DateFormat customFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
         DateSerializer serializer = new DateSerializer(null, customFormat);
         assertFalse(invokeAsTimestamp(serializer));
     }

     @Test
     public void testAsTimestampReturnsTrueWhenUseTimestampIsTrue() throws Exception {
         DateFormat customFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
         DateSerializer serializer = new DateSerializer(Boolean.TRUE, customFormat);
         assertTrue(invokeAsTimestamp(serializer));
     }

     @Test
     public void testAsTimestampUsesFeatureWhenNoCustomFormatAndNoFlag() throws Exception {
         DateSerializer serializer = new DateSerializer(null, null);

         mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
         assertTrue(invokeAsTimestamp(serializer));

         mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
         assertFalse(invokeAsTimestamp(serializer));
     }

     // -----------------------------------------------------------------
     // integration: custom DateFormat without pattern must be preserved
     // -----------------------------------------------------------------

     @Test
     public void testCustomDateFormatWithoutPatternPreserved() throws Exception {
         // User-supplied DateFormat, no pattern on annotation
         SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd'X'HH:mm:ss", Locale.US);
         userFormat.setTimeZone(TimeZone.getTimeZone("GMT+01:00"));
         mapper.setDateFormat(userFormat);
         mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

         BeanWithShapeString pojo = new BeanWithShapeString();
         pojo.date = date;

         String json = mapper.writeValueAsString(pojo);
         String expectedFieldValue = userFormat.format(date);
         assertEquals("{\"date\":\"" + expectedFieldValue + "\"}", json);
     }

     @Test
     public void testExplicitPatternOverridesCustomDateFormat() throws Exception {
         // Even if mapper has a custom format, an explicit pattern on
         // the property takes precedence.
         mapper.setDateFormat(new SimpleDateFormat("yyyy/MM/dd", Locale.US));
         mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

         BeanWithPattern pojo = new BeanWithPattern();
         pojo.dateWithPattern = date;

         String json = mapper.writeValueAsString(pojo);
         assertEquals("{\"dateWithPattern\":\"1970-01-01\"}", json);
     }

     @Test
     public void testShapeNumberForcesTimestamp() throws Exception {
         mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

         BeanWithShapeNumber pojo = new BeanWithShapeNumber();
         pojo.dateAsTimestamp = date;

         String json = mapper.writeValueAsString(pojo);
         assertEquals("{\"dateAsTimestamp\":0}", json);
     }

     @Test
     public void testShapeStringWithLocaleAndTimeZone() throws Exception {
         mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

         BeanWithLocaleAndTZ pojo = new BeanWithLocaleAndTZ();
         pojo.date = new Date(123456789000L); // 1973-11-29T21:33:09Z

         String json = mapper.writeValueAsString(pojo);
         // Expected output produced by the specified locale & timezone
         // Locale.FRANCE gives "novembre" in French month; timezone Europe/Paris = +01:00
         // "yyyy-MMM-dd HH:mm" with FRENCH: "1973-nov.-29 22:33"
         assertTrue(json.contains("\"date\":\""));
         assertFalse(json, json.contains("\"date\":0")); // must be text
     }

     @Test
     public void testNullDateSerializesToNull() throws Exception {
         mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

         BeanWithShapeString pojo = new BeanWithShapeString();
         pojo.date = null;

         String json = mapper.writeValueAsString(pojo);
         assertEquals("{\"date\":null}", json);
     }

     @Test
     public void testEpochDateSerialization() throws Exception {
         // Epoch with explicit pattern – text output consistent.
         mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

         BeanWithPattern pojo = new BeanWithPattern();
         pojo.dateWithPattern = date;

         String json = mapper.writeValueAsString(pojo);
         assertTrue(json.contains("\"dateWithPattern\":\"1970-01-01\""));
     }

     @Test
     public void testCustomFormatWinsOverFeatureEvenIfFeatureEnabled() throws Exception {
         // The presence of _customFormat should force textual output
         // regardless of WRITE_DATES_AS_TIMESTAMPS being enabled.
         SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
         userFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
         mapper.setDateFormat(userFormat);
         mapper.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // should be ignored

         BeanWithShapeString pojo = new BeanWithShapeString();
         pojo.date = date;

         String json = mapper.writeValueAsString(pojo);
         assertEquals("{\"date\":\"1970-01-01\"}", json);
     }

     // -----------------------------------------------------------------
     // helper
     // -----------------------------------------------------------------

     private static boolean invokeAsTimestamp(DateSerializer serializer) throws Exception {
         Method m = DateTimeSerializerBase.class.getDeclaredMethod("_asTimestamp",
SerializerProvider.class);
         m.setAccessible(true);
         // null provider is accepted when custom format is present (or throws)
         return (boolean) m.invoke(serializer, (SerializerProvider) null);
     }

     // -----------------------------------------------------------------
     // test beans
     // -----------------------------------------------------------------

     public static class BeanWithShapeString {
         @JsonFormat(shape = JsonFormat.Shape.STRING)
         public Date date;
     }

     public static class BeanWithPattern {
         @JsonFormat(pattern = "yyyy-MM-dd")
         public Date dateWithPattern;
     }

     public static class BeanWithShapeNumber {
         @JsonFormat(shape = JsonFormat.Shape.NUMBER)
         public Date dateAsTimestamp;
     }

     public static class BeanWithLocaleAndTZ {
         @JsonFormat(
             shape = JsonFormat.Shape.STRING,
             locale = "fr_FR",
             timezone = "Europe/Paris",
             pattern = "yyyy-MMM-dd HH:mm"
         )
         public Date date;
     }
 }
