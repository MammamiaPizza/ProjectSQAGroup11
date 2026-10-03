package com.fasterxml.jackson.databind.deser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Locale;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FromStringDeserializerLocaleTest
{
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void emptyLocaleStringDeserializesToLocaleRoot() throws Exception
    {
        Locale result = mapper.readValue("\"\"", Locale.class);

        assertSame(Locale.ROOT, result);
    }

    @Test
    public void whitespaceOnlyLocaleStringDeserializesToLocaleRoot() throws Exception
    {
        Locale result = mapper.readValue("\"   \\t  \"", Locale.class);

        assertSame(Locale.ROOT, result);
    }

    @Test
    public void localeWithLanguageAndCountryDeserializesCorrectly() throws Exception
    {
        Locale result = mapper.readValue("\"en_US\"", Locale.class);

        assertEquals(new Locale("en", "US"), result);
    }

    @Test
    public void localeWithLanguageCountryAndVariantDeserializesCorrectly() throws Exception
    {
        Locale result = mapper.readValue("\"en_US_POSIX\"", Locale.class);

        assertEquals(new Locale("en", "US", "POSIX"), result);
    }

    @Test
    public void jsonNullDeserializesToNullLocale() throws Exception
    {
        Locale result = mapper.readValue("null", Locale.class);

        assertNull(result);
    }
}
