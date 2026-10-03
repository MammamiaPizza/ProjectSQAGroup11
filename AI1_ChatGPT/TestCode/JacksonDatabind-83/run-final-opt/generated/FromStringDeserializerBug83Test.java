import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URL;
import java.util.Locale;
import java.util.UUID;

import org.junit.Test;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class FromStringDeserializerBug83Test
{
    @Test
    public void testWeirdStringHandlerCanProvideValueForInvalidUUID() throws Exception
    {
        final UUID replacement = UUID.fromString("123e4567-e89b-12d3-a456-426655440000");
        final Class<?>[] handledType = new Class<?>[1];
        final String[] handledValue = new String[1];

        ObjectMapper mapper = new ObjectMapper();
        mapper.addHandler(new DeserializationProblemHandler() {
            @Override
            public Object handleWeirdStringValue(DeserializationContext ctxt,
                    Class<?> targetType, String valueToConvert, String failureMsg) {
                handledType[0] = targetType;
                handledValue[0] = valueToConvert;
                return replacement;
            }
        });

        UUID result = mapper.readValue("\"not a uuid!\"", UUID.class);

        assertSame(replacement, result);
        assertEquals(UUID.class, handledType[0]);
        assertEquals("not a uuid!", handledValue[0]);
    }

    @Test
    public void testValidUUIDIsDeserializedNormally() throws Exception
    {
        UUID expected = UUID.fromString("123e4567-e89b-12d3-a456-426655440000");

        UUID result = new ObjectMapper().readValue(
                "\"123e4567-e89b-12d3-a456-426655440000\"", UUID.class);

        assertEquals(expected, result);
    }

    @Test
    public void testEmptyURIProducesEmptyURI() throws Exception
    {
        URI result = new ObjectMapper().readValue("\"   \"", URI.class);

        assertEquals(URI.create(""), result);
    }

    @Test
    public void testEmptyLocaleProducesRootLocale() throws Exception
    {
        Locale result = new ObjectMapper().readValue("\"\"", Locale.class);

        assertEquals(Locale.ROOT, result);
    }

    @Test
    public void testHyphenSeparatedLocaleIsParsedIntoAllComponents() throws Exception
    {
        Locale result = new ObjectMapper().readValue("\"en-US-POSIX\"", Locale.class);

        assertEquals(new Locale("en", "US", "POSIX"), result);
    }

    @Test
    public void testMalformedURLIsReportedAsInvalidFormat() throws Exception
    {
        try {
            new ObjectMapper().readValue("\"ht!tp://bad\"", URL.class);
            fail("Malformed URL text should not deserialize successfully");
        } catch (InvalidFormatException e) {
            assertTrue(e.getMessage().contains("not a valid textual representation"));
        }
    }
}
