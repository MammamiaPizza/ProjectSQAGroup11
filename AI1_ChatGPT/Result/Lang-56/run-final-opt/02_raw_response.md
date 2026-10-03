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
}