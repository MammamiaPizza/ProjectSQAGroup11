package com.fasterxml.jackson.databind.deser;

import java.util.Locale;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FromStringDeserializerLocaleTest
{
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void deserializesUnderscoreSeparatedLanguageAndCountry() throws Exception
    {
        Locale locale = mapper.readValue("\"en_US\"", Locale.class);

        assertEquals(new Locale("en", "US"), locale);
        assertEquals("en_US", locale.toString());
    }

    @Test
    public void deserializesLanguageOnlyLocale() throws Exception
    {
        Locale locale = mapper.readValue("\"fr\"", Locale.class);

        assertEquals(new Locale("fr"), locale);
        assertEquals("fr", locale.toString());
    }

    @Test
    public void deserializesLocaleWithVariant() throws Exception
    {
        Locale locale = mapper.readValue("\"en_US_POSIX\"", Locale.class);

        assertEquals(new Locale("en", "US", "POSIX"), locale);
        assertEquals("en_US_POSIX", locale.toString());
    }

    @Test
    public void deserializesEmptyStringAsRootLocale() throws Exception
    {
        Locale locale = mapper.readValue("\"\"", Locale.class);

        assertEquals(Locale.ROOT, locale);
    }

    @Test
    public void deserializesWhitespaceOnlyStringAsRootLocale() throws Exception
    {
        Locale locale = mapper.readValue("\"   \"", Locale.class);

        assertEquals(Locale.ROOT, locale);
    }

    @Test
    public void deserializesJsonNullAsNullLocale() throws Exception
    {
        Locale locale = mapper.readValue("null", Locale.class);

        assertNull(locale);
    }
}
