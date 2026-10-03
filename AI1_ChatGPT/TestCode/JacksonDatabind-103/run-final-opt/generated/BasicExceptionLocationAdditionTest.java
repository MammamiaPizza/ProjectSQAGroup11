import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class BasicExceptionLocationAdditionTest
{
    private enum ABC {
        A, B, C
    }

    @Test
    public void validEnumMapKeysDeserializeNormally() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Map<ABC, Integer> result = mapper.readValue(
                "{\"A\":1,\"C\":3}",
                new TypeReference<Map<ABC, Integer>>() { });

        assertEquals(2, result.size());
        assertEquals(Integer.valueOf(1), result.get(ABC.A));
        assertEquals(Integer.valueOf(3), result.get(ABC.C));
    }

    @Test
    public void invalidEnumMapKeyHasSingleLocationMarker() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.readValue("{\"value\":1}",
                    new TypeReference<Map<ABC, Integer>>() { });
            fail("Expected invalid enum map key to fail");
        } catch (InvalidFormatException e) {
            String message = e.getMessage();

            assertNotNull(message);
            assertTrue(message.contains("Cannot deserialize Map key"));
            assertTrue(message.contains("\"value\""));
            assertEquals("Location information must not be appended twice",
                    1, countOccurrences(message, "at ["));
        }
    }

    private static int countOccurrences(String value, String token) {
        int count = 0;
        int index = 0;
        while ((index = value.indexOf(token, index)) >= 0) {
            ++count;
            index += token.length();
        }
        return count;
    }
}
