package org.apache.commons.lang.time;

import java.util.Locale;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FastDateFormatDefaultLocaleCacheTest {

    private Locale originalDefaultLocale;

    @Before
    public void saveDefaultLocale() {
        originalDefaultLocale = Locale.getDefault();
    }

    @After
    public void restoreDefaultLocale() {
        Locale.setDefault(originalDefaultLocale);
    }

    @Test
    public void dateInstanceUsesCurrentDefaultLocaleAfterDefaultChanges() {
        Locale.setDefault(Locale.US);
        Assert.assertSame(Locale.US,
                FastDateFormat.getDateInstance(FastDateFormat.FULL).getLocale());

        Locale.setDefault(Locale.GERMANY);
        Assert.assertSame(Locale.GERMANY,
                FastDateFormat.getDateInstance(FastDateFormat.FULL).getLocale());
    }

    @Test
    public void dateTimeInstanceUsesCurrentDefaultLocaleAfterDefaultChanges() {
        Locale.setDefault(Locale.US);
        Assert.assertSame(Locale.US,
                FastDateFormat.getDateTimeInstance(
                        FastDateFormat.LONG, FastDateFormat.SHORT).getLocale());

        Locale.setDefault(Locale.GERMANY);
        Assert.assertSame(Locale.GERMANY,
                FastDateFormat.getDateTimeInstance(
                        FastDateFormat.LONG, FastDateFormat.SHORT).getLocale());
    }

    @Test
    public void dateInstanceWithTimeZoneUsesCurrentDefaultLocaleAfterDefaultChanges() {
        TimeZone utc = TimeZone.getTimeZone("UTC");

        Locale.setDefault(Locale.US);
        Assert.assertSame(Locale.US,
                FastDateFormat.getDateInstance(FastDateFormat.MEDIUM, utc).getLocale());

        Locale.setDefault(Locale.GERMANY);
        Assert.assertSame(Locale.GERMANY,
                FastDateFormat.getDateInstance(FastDateFormat.MEDIUM, utc).getLocale());
    }

    @Test
    public void dateTimeInstanceWithTimeZoneUsesCurrentDefaultLocaleAfterDefaultChanges() {
        TimeZone utc = TimeZone.getTimeZone("UTC");

        Locale.setDefault(Locale.US);
        Assert.assertSame(Locale.US,
                FastDateFormat.getDateTimeInstance(
                        FastDateFormat.MEDIUM, FastDateFormat.SHORT, utc).getLocale());

        Locale.setDefault(Locale.GERMANY);
        Assert.assertSame(Locale.GERMANY,
                FastDateFormat.getDateTimeInstance(
                        FastDateFormat.MEDIUM, FastDateFormat.SHORT, utc).getLocale());
    }

    @Test
    public void explicitDateInstanceLocaleIsNotAffectedByDefaultLocaleChanges() {
        Locale.setDefault(Locale.US);
        FastDateFormat format =
                FastDateFormat.getDateInstance(FastDateFormat.SHORT, Locale.FRANCE);

        Locale.setDefault(Locale.GERMANY);

        Assert.assertSame(Locale.FRANCE, format.getLocale());
        Assert.assertSame(Locale.FRANCE,
                FastDateFormat.getDateInstance(FastDateFormat.SHORT, Locale.FRANCE).getLocale());
    }

    @Test
    public void explicitDateTimeInstanceLocaleIsNotAffectedByDefaultLocaleChanges() {
        Locale.setDefault(Locale.US);
        FastDateFormat format = FastDateFormat.getDateTimeInstance(
                FastDateFormat.SHORT, FastDateFormat.MEDIUM, Locale.JAPAN);

        Locale.setDefault(Locale.GERMANY);

        Assert.assertSame(Locale.JAPAN, format.getLocale());
        Assert.assertSame(Locale.JAPAN, FastDateFormat.getDateTimeInstance(
                FastDateFormat.SHORT, FastDateFormat.MEDIUM, Locale.JAPAN).getLocale());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullPatternIsRejected() {
        FastDateFormat.getInstance((String) null);
    }
}
