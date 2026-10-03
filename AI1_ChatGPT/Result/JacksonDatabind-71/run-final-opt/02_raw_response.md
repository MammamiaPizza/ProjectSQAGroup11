import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdKeyDeserializer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class StdKeyDeserializerCharSequenceTest {

    @Test
    public void forTypeFindsDeserializerForCharSequence() throws Exception {
        StdKeyDeserializer deserializer = StdKeyDeserializer.forType(CharSequence.class);

        assertNotNull(deserializer);
        assertEquals(CharSequence.class, deserializer.getKeyClass());
        assertEquals("field-name", deserializer.deserializeKey("field-name", null));
    }

    @Test
    public void deserializesMapWithCharSequenceKeys() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Map<CharSequence, Integer> result = mapper.readValue(
                "{\"field-name\":7}",
                new TypeReference<Map<CharSequence, Integer>>() { });

        assertEquals(1, result.size());
        assertEquals(Integer.valueOf(7), result.get("field-name"));
        assertTrue(result.keySet().iterator().next() instanceof CharSequence);
    }

    @Test
    public void deserializesEmptyFieldNameAsCharSequenceKey() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Map<CharSequence, String> result = mapper.readValue(
                "{\"\":\"value\"}",
                new TypeReference<Map<CharSequence, String>>() { });

        assertEquals(1, result.size());
        assertEquals("value", result.get(""));
        assertEquals("", result.keySet().iterator().next().toString());
    }
}