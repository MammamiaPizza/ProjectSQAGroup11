package org.apache.commons.lang.time;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

public class FastDateFormatSerializationTest {

    @Test
    public void testPaddedYearMonthDayFormatterSurvivesSerialization() throws Exception {
        FastDateFormat original = FastDateFormat.getInstance(
                "yyyy-MM-dd", TimeZone.getTimeZone("UTC"), Locale.US);
        Date date = new Date(0L);

        assertEquals("1970-01-01", original.format(date));

        FastDateFormat restored = serializeAndDeserialize(original);

        assertNotSame(original, restored);
        assertEquals(original.format(date), restored.format(date));
        assertEquals("1970-01-01", restored.format(date));
    }

    @Test
    public void testMultiplePaddedNumberFieldsSurviveSerialization() throws Exception {
        FastDateFormat original = FastDateFormat.getInstance(
                "yyyyy-DDD-HHH-mm-ss-SSS", TimeZone.getTimeZone("UTC"), Locale.US);
        Date date = new Date(0L);

        assertEquals("01970-001-000-00-00-000", original.format(date));

        FastDateFormat restored = serializeAndDeserialize(original);

        assertEquals(original.format(date), restored.format(date));
        assertEquals("01970-001-000-00-00-000", restored.format(date));
    }

    @Test
    public void testCachedFormatterWithForcedTimeZoneAndLocaleSurvivesSerialization() throws Exception {
        TimeZone timeZone = TimeZone.getTimeZone("GMT+05:30");
        FastDateFormat original = FastDateFormat.getInstance(
                "yyyy-MM-dd HH:mm:ss Z", timeZone, Locale.US);
        Date date = new Date(0L);

        assertEquals("1970-01-01 05:30:00 +0530", original.format(date));

        FastDateFormat restored = serializeAndDeserialize(original);

        assertEquals(original.getPattern(), restored.getPattern());
        assertEquals(original.getTimeZone(), restored.getTimeZone());
        assertEquals(original.format(date), restored.format(date));
    }

    private FastDateFormat serializeAndDeserialize(FastDateFormat format) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(bytes);
        output.writeObject(format);
        output.close();

        ObjectInputStream input = new ObjectInputStream(
                new ByteArrayInputStream(bytes.toByteArray()));
        FastDateFormat restored = (FastDateFormat) input.readObject();
        input.close();
        return restored;
    }

@org.junit.Test
public void testNullPatternIsRejected() {
    try {
        org.apache.commons.lang.time.FastDateFormat.getInstance((String) null);
        org.junit.Assert.fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException ex) {
        org.junit.Assert.assertEquals("The pattern must not be null", ex.getMessage());
    }
}

@org.junit.Test
public void testDefaultArgumentsUseCurrentDefaultsAndRemainDistinctFromForcedDefaults() {
    java.util.TimeZone defaultTimeZone = java.util.TimeZone.getDefault();
    java.util.Locale defaultLocale = java.util.Locale.getDefault();
    org.apache.commons.lang.time.FastDateFormat implicit =
        org.apache.commons.lang.time.FastDateFormat.getInstance(
            "MMMM yyyy", (java.util.TimeZone) null, (java.util.Locale) null);
    org.apache.commons.lang.time.FastDateFormat explicit =
        org.apache.commons.lang.time.FastDateFormat.getInstance(
            "MMMM yyyy", defaultTimeZone, defaultLocale);

    org.junit.Assert.assertEquals(defaultTimeZone, implicit.getTimeZone());
    org.junit.Assert.assertEquals(
        implicit.format(new java.util.Date(0L)),
        explicit.format(new java.util.Date(0L)));
    org.junit.Assert.assertTrue(implicit.equals(implicit));
    org.junit.Assert.assertFalse(implicit.equals(explicit));
    org.junit.Assert.assertFalse(implicit.equals("MMMM yyyy"));
}

@org.junit.Test
public void testFormatObjectSupportsLongAndRejectsUnknownObjects() {
    org.apache.commons.lang.time.FastDateFormat format =
        org.apache.commons.lang.time.FastDateFormat.getInstance(
            "yyyy-MM-dd", java.util.TimeZone.getTimeZone("UTC"), java.util.Locale.US);
    StringBuffer buffer = new StringBuffer();

    format.format((Object) Long.valueOf(0L), buffer, new java.text.FieldPosition(0));
    org.junit.Assert.assertEquals("1970-01-01", buffer.toString());

    try {
        format.format((Object) new Object(), new StringBuffer(), new java.text.FieldPosition(0));
        org.junit.Assert.fail("Expected IllegalArgumentException");
    } catch (IllegalArgumentException ex) {
        org.junit.Assert.assertEquals("Unknown class: java.lang.Object", ex.getMessage());
    }
}

@org.junit.Test
public void testFormattingCalendarWithForcedTimeZoneDoesNotMutateCalendar() {
    java.util.TimeZone calendarTimeZone = java.util.TimeZone.getTimeZone("GMT+05:30");
    java.util.Calendar calendar = java.util.Calendar.getInstance(calendarTimeZone, java.util.Locale.US);
    calendar.setTimeInMillis(0L);
    org.apache.commons.lang.time.FastDateFormat format =
        org.apache.commons.lang.time.FastDateFormat.getInstance(
            "yyyy-MM-dd HH:mm Z", java.util.TimeZone.getTimeZone("UTC"), java.util.Locale.US);

    org.junit.Assert.assertEquals("1970-01-01 00:00 +0000", format.format(calendar));
    org.junit.Assert.assertEquals(calendarTimeZone, calendar.getTimeZone());
}
}
