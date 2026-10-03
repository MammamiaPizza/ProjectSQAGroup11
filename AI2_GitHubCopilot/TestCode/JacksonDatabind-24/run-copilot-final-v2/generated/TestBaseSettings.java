package com.fasterxml.jackson.databind.cfg;

import static org.junit.Assert.*;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.Base64Variants;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class TestBaseSettings {

 private BaseSettings createDefaultSettings() {
     return new BaseSettings(null, null, null, null,
             TypeFactory.defaultInstance(), null,
             null, null, Locale.US,
             TimeZone.getTimeZone("GMT"), Base64Variants.getDefaultVariant());
 }

 @Test
 public void testWithDateFormatUpdatesTimeZone() {
     BaseSettings settings = createDefaultSettings();
     DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
     df.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
     BaseSettings updated = settings.withDateFormat(df);
     assertEquals("TimeZone should be updated from DateFormat",
             TimeZone.getTimeZone("America/Los_Angeles"), updated.getTimeZone());
 }

 @Test
 public void testWithDateFormatKeepsTimeZoneOnSameInstance() {
     BaseSettings settings = createDefaultSettings();
     DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
     df.setTimeZone(TimeZone.getTimeZone("America/Chicago"));
     BaseSettings updated1 = settings.withDateFormat(df);
     BaseSettings updated2 = updated1.withDateFormat(df); // same instance
     assertSame("Should return same instance when DateFormat unchanged", updated1, updated2);
     assertEquals("TimeZone should remain after same DateFormat call",
             TimeZone.getTimeZone("America/Chicago"), updated2.getTimeZone());
 }

 @Test
 public void testWithDateFormatNullKeepsPreviousTimeZone() {
     BaseSettings settings = createDefaultSettings();
     BaseSettings updated = settings.withDateFormat(null);
     assertEquals("TimeZone should remain previous when null DateFormat",
             TimeZone.getTimeZone("GMT"), updated.getTimeZone());
 }

 @Test
 public void testWithDateFormatMultipleCallsLastWins() {
     BaseSettings settings = createDefaultSettings();
     DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
     df1.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
     DateFormat df2 = new SimpleDateFormat("MM-dd-yyyy");
     df2.setTimeZone(TimeZone.getTimeZone("PST"));
     BaseSettings updated = settings.withDateFormat(df1).withDateFormat(df2);
     assertEquals("Last DateFormat's timezone should apply",
             TimeZone.getTimeZone("PST"), updated.getTimeZone());
     assertEquals("DateFormat should be the last one", df2, updated.getDateFormat());
 }

 @Test
 public void testWithTimeZoneOverrides() {
     BaseSettings settings = createDefaultSettings();
     BaseSettings updated = settings.with(TimeZone.getTimeZone("America/New_York"));
     assertEquals("TimeZone should be overridden by with(TimeZone)",
             TimeZone.getTimeZone("America/New_York"), updated.getTimeZone());
 }

 @Test
 public void testWithTimeZoneOverridesAfterDateFormat() {
     BaseSettings settings = createDefaultSettings();
     DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
     df.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
     BaseSettings updated = settings.withDateFormat(df)
             .with(TimeZone.getTimeZone("GMT"));
     assertEquals("Explicit with(TimeZone) should override DateFormat timezone",
             TimeZone.getTimeZone("GMT"), updated.getTimeZone());
 }

 @Test
 public void testWithDateFormatOverridesAfterWithTimeZone() {
     BaseSettings settings = createDefaultSettings();
     settings = settings.with(TimeZone.getTimeZone("America/New_York"));
     DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
     df.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
     BaseSettings updated = settings.withDateFormat(df);
     assertEquals("withDateFormat should override previously set TimeZone",
             TimeZone.getTimeZone("America/Los_Angeles"), updated.getTimeZone());
 }

 @Test
 public void testConstructionPreservesTimeZone() {
     TimeZone tz = TimeZone.getTimeZone("Europe/London");
     BaseSettings settings = new BaseSettings(null, null, null, null,
             TypeFactory.defaultInstance(), null,
             null, null, Locale.UK, tz, Base64Variants.getDefaultVariant());
     assertEquals("Constructor should set given TimeZone", tz, settings.getTimeZone());
 }

 @Test
 public void testWithDateFormatDoesNotAffectOtherProperties() {
     BaseSettings original = createDefaultSettings();
     DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
     df.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
     BaseSettings updated = original.withDateFormat(df);
     assertEquals(original.getLocale(), updated.getLocale());
     assertEquals(original.getTypeFactory(), updated.getTypeFactory());
     assertEquals(original.getBase64Variant(), updated.getBase64Variant());
 }

 @Test
 public void testWithReturnsNewInstance() {
     BaseSettings settings = createDefaultSettings();
     DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
     assertNotSame("withDateFormat should return new instance", settings,
settings.withDateFormat(df));
     assertNotSame("with(TimeZone) should return new instance", settings,
settings.with(TimeZone.getTimeZone("PST")));
 }

 @Test
 public void testTimeZoneSyncedWithDateFormat() {
     BaseSettings settings = createDefaultSettings();
     DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
     df.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
     BaseSettings updated = settings.withDateFormat(df);
     assertEquals(df.getTimeZone(), updated.getTimeZone());
     assertEquals(df, updated.getDateFormat());
 }

}
