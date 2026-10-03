package com.fasterxml.jackson.databind.ser.std;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class DateTimeSerializerBaseTest {

 private final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();

 @Test
 public void testDefaultSerializationIsString() throws Exception {
     Date date = new Date(0);
     String json = DEFAULT_MAPPER.writeValueAsString(date);
     assertTrue("Default serialization should produce a JSON string, got: " + json,
             json.startsWith("\"") && json.endsWith("\""));
 }

 @Test
 public void testTimestampTrueProducesNumeric() throws Exception {
     ObjectMapper mapper = new ObjectMapper();
     mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
     Date date = new Date(0);
     assertEquals("0", mapper.writeValueAsString(date));
     date = new Date(100000L);
     assertEquals("100000", mapper.writeValueAsString(date));
 }

 @Test
 public void testTimestampFalseProducesString() throws Exception {
     ObjectMapper mapper = new ObjectMapper();
     mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
     Date date = new Date(0);
     String json = mapper.writeValueAsString(date);
     assertTrue(json.startsWith("\""));
     assertFalse("0".equals(json));
 }

 @Test
 public void testNullDateProducesNull() throws Exception {
     Date date = null;
     assertEquals("null", DEFAULT_MAPPER.writeValueAsString(date));
 }

 @Test
 public void testEpochZeroIsNotNumericDefault() throws Exception {
     Date date = new Date(0);
     String json = DEFAULT_MAPPER.writeValueAsString(date);
     assertFalse("Epoch zero should not be numeric '0'", "0".equals(json));
     assertTrue("Epoch zero should be a JSON string", json.startsWith("\""));
 }

 @Test
 public void testFarFutureDateIsString() throws Exception {
     Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
     cal.set(3000, Calendar.JANUARY, 1, 0, 0, 0);
     cal.set(Calendar.MILLISECOND, 0);
     String json = DEFAULT_MAPPER.writeValueAsString(cal.getTime());
     assertTrue("Far future date should serialize as string", json.startsWith("\""));
 }

 @Test
 public void testFarPastDateIsString() throws Exception {
     Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
     cal.set(1800, Calendar.JANUARY, 1, 0, 0, 0);
     cal.set(Calendar.MILLISECOND, 0);
     String json = DEFAULT_MAPPER.writeValueAsString(cal.getTime());
     assertTrue("Far past date should serialize as string", json.startsWith("\""));
 }

 @Test
 public void testCustomDateFormatFormatting() throws Exception {
     ObjectMapper mapper = new ObjectMapper();
     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
     sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
     mapper.setDateFormat(sdf);
     Date date = new Date(0);
     assertEquals("\"1970-01-01\"", mapper.writeValueAsString(date));
 }

 @Test
 public void testTimestampTrueOverridesCustomFormat() throws Exception {
     ObjectMapper mapper = new ObjectMapper();
     mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
     SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
     mapper.setDateFormat(sdf);
     Date date = new Date(1000L);
     assertEquals("1000", mapper.writeValueAsString(date));
 }

 @Test
 public void testCalendarDefaultShape() throws Exception {
     Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
     cal.setTimeInMillis(0);
     String json = DEFAULT_MAPPER.writeValueAsString(cal);
     assertTrue("Calendar default serialization should be string", json.startsWith("\""));
 }

 @Test
 public void testSqlDateDefaultShape() throws Exception {
     java.sql.Date sqlDate = new java.sql.Date(0);
     String json = DEFAULT_MAPPER.writeValueAsString(sqlDate);
     assertTrue("SQL Date default should be string", json.startsWith("\""));
 }

 @Test
 public void testSqlDateTimestampTrue() throws Exception {
     ObjectMapper mapper = new ObjectMapper();
     mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
     java.sql.Date sqlDate = new java.sql.Date(100000L);
     assertEquals("100000", mapper.writeValueAsString(sqlDate));
 }

}
