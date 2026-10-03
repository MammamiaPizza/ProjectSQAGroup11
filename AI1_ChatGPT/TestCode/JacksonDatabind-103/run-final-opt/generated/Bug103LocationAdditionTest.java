import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class Bug103LocationAdditionTest
{
    private enum ABC {
        A, B, C
    }

    @Test
    public void validEnumMapKeysDeserializeNormally() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        Map<ABC, Integer> result = mapper.readValue("{\"A\":1,\"B\":2}",
                new TypeReference<Map<ABC, Integer>>() { });

        assertEquals(2, result.size());
        assertEquals(Integer.valueOf(1), result.get(ABC.A));
        assertEquals(Integer.valueOf(2), result.get(ABC.B));
    }

    @Test
    public void invalidEnumMapKeyHasOnlyOneLocationSuffix() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.readValue("{\"value\":1}", new TypeReference<Map<ABC, Integer>>() { });
            fail("Expected invalid enum map key to fail");
        } catch (JsonMappingException e) {
            String message = e.getMessage();

            assertTrue(message.contains("Cannot deserialize Map key"));
            assertTrue(message.contains("\"value\""));
            assertEquals(1, occurrences(message, " at ["));
            assertTrue(hasInvalidFormatException(e));
        }
    }

    private static int occurrences(String value, String token)
    {
        int count = 0;
        int offset = 0;
        while ((offset = value.indexOf(token, offset)) >= 0) {
            ++count;
            offset += token.length();
        }
        return count;
    }

    private static boolean hasInvalidFormatException(Throwable throwable)
    {
        while (throwable != null) {
            if (throwable instanceof InvalidFormatException) {
                return true;
            }
            throwable = throwable.getCause();
        }
        return false;
    }
}
