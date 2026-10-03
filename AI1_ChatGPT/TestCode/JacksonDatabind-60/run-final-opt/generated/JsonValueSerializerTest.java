import java.util.Date;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JsonValueSerializerTest {

    public static class CreatorBackedBinaryValue {
        private final byte[] value;

        @JsonCreator
        public CreatorBackedBinaryValue(byte[] value) {
            this.value = value;
        }

        @JsonValue
        public byte[] value() {
            return value;
        }
    }

    public static class CreatorBackedStringValue {
        private final String value;

        @JsonCreator
        public CreatorBackedStringValue(String value) {
            this.value = value;
        }

        @JsonValue
        public String value() {
            return value;
        }
    }

    public static class Thingy {
        private final Date value;

        @JsonCreator
        public Thingy(Date value) {
            this.value = value;
        }

        @JsonValue
        public Date value() {
            return value;
        }
    }

    public static class ExternalTypeContainer {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "type")
        @JsonSubTypes({
                @JsonSubTypes.Type(value = Thingy.class, name = "thingy"),
                @JsonSubTypes.Type(value = Date.class, name = "date")
        })
        public Object value;

        public ExternalTypeContainer() {
        }

        public ExternalTypeContainer(Object value) {
            this.value = value;
        }
    }

    @Test
    public void defaultTypingUsesJsonValueBeanTypeRatherThanByteArrayType() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        CreatorBackedBinaryValue input =
                new CreatorBackedBinaryValue(new byte[] { 1, 2, 3, 4 });

        Object restored = mapper.readValue(mapper.writeValueAsString(input), Object.class);

        assertEquals(CreatorBackedBinaryValue.class, restored.getClass());
        assertArrayEquals(input.value(), ((CreatorBackedBinaryValue) restored).value());
    }

    @Test
    public void externalTypeIdUsesJsonValueBeanTypeRatherThanDateValueType() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ExternalTypeContainer input =
                new ExternalTypeContainer(new Thingy(new Date(12345L)));

        JsonNode json = mapper.readTree(mapper.writeValueAsString(input));

        assertEquals(12345L, json.get("value").asLong());
        assertEquals("thingy", json.get("type").asText());
    }

    @Test
    public void naturalStringJsonValueRetainsBeanTypeWithDefaultTyping() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        Object restored = mapper.readValue(
                mapper.writeValueAsString(new CreatorBackedStringValue("value")),
                Object.class);

        assertEquals(CreatorBackedStringValue.class, restored.getClass());
        assertEquals("value", ((CreatorBackedStringValue) restored).value());
    }

    @Test
    public void jsonValueWithoutPolymorphicTypingSerializesItsScalarValue() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(new CreatorBackedStringValue("plain"));

        assertEquals("\"plain\"", json);
        assertTrue(mapper.readTree(json).isTextual());
    }
}
