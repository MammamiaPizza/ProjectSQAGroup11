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