import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class DeserializerCacheCustomKeyTest
{
    public static class PrefixingKeyDeserializer extends KeyDeserializer
    {
        @Override
        public Object deserializeKey(String key, DeserializationContext ctxt) {
            return "custom-" + key;
        }
    }

    public static class CustomKeyMapHolder
    {
        @JsonDeserialize(keyUsing = PrefixingKeyDeserializer.class)
        public Map<String, String> data;
    }

    @Test
    public void customKeyDeserializerIsUsedAfterPlainMapDeserializerWasCached() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, String> plain = mapper.readValue(
                "{\"plain\":\"value\"}", new TypeReference<Map<String, String>>() { });
        assertEquals("value", plain.get("plain"));

        CustomKeyMapHolder result = mapper.readValue(
                "{\"data\":{\"1st\":\"onedata\",\"2nd\":\"twodata\"}}",
                CustomKeyMapHolder.class);

        assertNotNull(result.data);
        assertEquals(2, result.data.size());
        assertEquals("onedata", result.data.get("custom-1st"));
        assertEquals("twodata", result.data.get("custom-2nd"));
        assertFalse(result.data.containsKey("1st"));
    }

    @Test
    public void repeatedCustomKeyMapDeserializationRetainsCustomKeyHandling() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        CustomKeyMapHolder first = mapper.readValue(
                "{\"data\":{\"1st\":\"onedata\"}}", CustomKeyMapHolder.class);
        CustomKeyMapHolder second = mapper.readValue(
                "{\"data\":{\"2nd\":\"twodata\"}}", CustomKeyMapHolder.class);

        assertEquals("onedata", first.data.get("custom-1st"));
        assertFalse(first.data.containsKey("1st"));
        assertEquals("twodata", second.data.get("custom-2nd"));
        assertFalse(second.data.containsKey("2nd"));
    }

    @Test
    public void customKeyDeserializerDoesNotLeakIntoPlainMaps() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();

        CustomKeyMapHolder customized = mapper.readValue(
                "{\"data\":{\"key\":\"value\"}}", CustomKeyMapHolder.class);
        assertEquals("value", customized.data.get("custom-key"));

        Map<String, String> plain = mapper.readValue(
                "{\"key\":\"value\"}", new TypeReference<Map<String, String>>() { });

        assertEquals(1, plain.size());
        assertEquals("value", plain.get("key"));
        assertFalse(plain.containsKey("custom-key"));
    }
}
