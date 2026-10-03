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

@org.junit.Test
public void singleValueArrayContainingEmptyLocaleDeserializesToLocaleRoot() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.enable(com.fasterxml.jackson.databind.DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);

    java.util.Locale result = mapper.readValue("[\"\"]", java.util.Locale.class);

    org.junit.Assert.assertSame(java.util.Locale.ROOT, result);
}

@org.junit.Test
public void multiValueLocaleArrayFailsWhenUnwrappingSingleValueArrays() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    mapper.enable(com.fasterxml.jackson.databind.DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS);

    try {
        mapper.readValue("[\"en_US\",\"fr_FR\"]", java.util.Locale.class);
        org.junit.Assert.fail("Expected JsonMappingException for multiple array values");
    } catch (com.fasterxml.jackson.databind.JsonMappingException expected) {
        // expected
    }
}

@org.junit.Test
public void findDeserializerSupportsEveryAdvertisedTypeAndRejectsUnsupportedType() {
    for (Class<?> type : com.fasterxml.jackson.databind.deser.std.FromStringDeserializer.types()) {
        org.junit.Assert.assertNotNull(
                com.fasterxml.jackson.databind.deser.std.FromStringDeserializer.findDeserializer(type));
    }

    org.junit.Assert.assertNull(
            com.fasterxml.jackson.databind.deser.std.FromStringDeserializer.findDeserializer(String.class));
}
}
