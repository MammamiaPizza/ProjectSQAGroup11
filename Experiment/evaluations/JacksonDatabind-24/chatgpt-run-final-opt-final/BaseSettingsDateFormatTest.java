package com.fasterxml.jackson.databind.cfg;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

public class BaseSettingsDateFormatTest
{
    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");
    private static final TimeZone LOS_ANGELES = TimeZone.getTimeZone("America/Los_Angeles");

    private BaseSettings settings(DateFormat dateFormat, TimeZone timeZone) {
        return new BaseSettings(null, null, null, null, null, null,
                dateFormat, null, Locale.US, timeZone, null);
    }

    private DateFormat dateFormat(TimeZone timeZone) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        format.setTimeZone(timeZone);
        return format;
    }

    @Test
    public void withDateFormatUsesConfiguredDateFormatTimeZone() {
        DateFormat format = dateFormat(LOS_ANGELES);
        BaseSettings original = settings(dateFormat(GMT), GMT);

        BaseSettings changed = original.withDateFormat(format);

        assertEquals("GMT", changed.getTimeZone().getID());
        assertEquals("America/Los_Angeles", changed.getDateFormat().getTimeZone().getID());
    }

    @Test
    public void withDateFormatCanChangeNonGmtSettingsToGmt() {
        DateFormat format = dateFormat(GMT);
        BaseSettings original = settings(dateFormat(LOS_ANGELES), LOS_ANGELES);

        BaseSettings changed = original.withDateFormat(format);

        assertEquals("America/Los_Angeles", changed.getTimeZone().getID());
        assertEquals("GMT", changed.getDateFormat().getTimeZone().getID());
    }

    @Test
    public void explicitTimeZoneFollowedByDateFormatUsesDateFormatTimeZone() {
        BaseSettings original = settings(dateFormat(GMT), GMT);

        BaseSettings changed = original.with(LOS_ANGELES).withDateFormat(dateFormat(GMT));

        assertEquals("America/Los_Angeles", changed.getTimeZone().getID());
        assertEquals("GMT", changed.getDateFormat().getTimeZone().getID());
    }

    @Test
    public void explicitTimeZoneAfterDateFormatUpdatesBothSettingsAndDateFormat() {
        DateFormat originalFormat = dateFormat(GMT);
        BaseSettings original = settings(originalFormat, GMT);

        BaseSettings changed = original.withDateFormat(originalFormat).with(LOS_ANGELES);

        assertEquals("America/Los_Angeles", changed.getTimeZone().getID());
        assertEquals("America/Los_Angeles", changed.getDateFormat().getTimeZone().getID());
        assertEquals("GMT", original.getTimeZone().getID());
        assertEquals("GMT", original.getDateFormat().getTimeZone().getID());
        assertNotSame(original.getDateFormat(), changed.getDateFormat());
    }

@org.junit.Test
public void withLocaleReturnsSameInstanceForSameLocaleAndRetainsNewLocale() throws Exception {
    com.fasterxml.jackson.databind.cfg.BaseSettings original = newBaseSettingsWithNullComponents();

    org.junit.Assert.assertSame(original, original.with((java.util.Locale) null));

    com.fasterxml.jackson.databind.cfg.BaseSettings changed = original.with(java.util.Locale.CANADA);
    org.junit.Assert.assertNotSame(original, changed);
    org.junit.Assert.assertSame(java.util.Locale.CANADA, changed.getLocale());
}

@org.junit.Test(expected = IllegalArgumentException.class)
public void withNullTimeZoneRejectsNull() throws Exception {
    newBaseSettingsWithNullComponents().with((java.util.TimeZone) null);
}

private static com.fasterxml.jackson.databind.cfg.BaseSettings newBaseSettingsWithNullComponents()
        throws Exception {
    for (java.lang.reflect.Constructor<?> constructor
            : com.fasterxml.jackson.databind.cfg.BaseSettings.class.getDeclaredConstructors()) {
        if (constructor.getParameterTypes().length == 11) {
            constructor.setAccessible(true);
            return (com.fasterxml.jackson.databind.cfg.BaseSettings) constructor.newInstance(new Object[11]);
        }
    }
    throw new AssertionError("BaseSettings constructor not found");
}
}
