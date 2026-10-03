import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ExternalTypeHandlerBug29Test {

    private final ObjectMapper mapper = new ObjectMapper();

    public static class Holder {
        @JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
                include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
                property = "kind")
        @JsonSubTypes({
                @JsonSubTypes.Type(value = Dog.class, name = "dog")
        })
        public Animal value;
    }

    public static abstract class Animal {
        public String name;
    }

    public static class Dog extends Animal {
        public Dog() {
        }
    }

    @Test
    public void nullExternalTypeIdWithNullValueAfterTypeIdDeserializesHolder() throws Exception {
        Holder result = mapper.readValue("{\"kind\":null,\"value\":null}", Holder.class);

        assertNull(result.value);
    }

    @Test
    public void nullExternalTypeIdWithNullValueBeforeTypeIdDeserializesHolder() throws Exception {
        Holder result = mapper.readValue("{\"value\":null,\"kind\":null}", Holder.class);

        assertNull(result.value);
    }

    @Test
    public void nonNullExternalTypeIdDeserializesValueWhenTypeIdComesFirst() throws Exception {
        Holder result = mapper.readValue(
                "{\"kind\":\"dog\",\"value\":{\"name\":\"Fido\"}}", Holder.class);

        assertEquals(Dog.class, result.value.getClass());
        assertEquals("Fido", result.value.name);
    }

    @Test
    public void nonNullExternalTypeIdDeserializesValueWhenValueComesFirst() throws Exception {
        Holder result = mapper.readValue(
                "{\"value\":{\"name\":\"Rex\"},\"kind\":\"dog\"}", Holder.class);

        assertEquals(Dog.class, result.value.getClass());
        assertEquals("Rex", result.value.name);
    }

    @Test
    public void missingBothExternalValueAndTypeIdLeavesPropertyUnset() throws Exception {
        Holder result = mapper.readValue("{}", Holder.class);

        assertNull(result.value);
    }

    @Test(expected = JsonMappingException.class)
    public void objectValueWithoutExternalTypeIdFails() throws Exception {
        mapper.readValue("{\"value\":{\"name\":\"Fido\"}}", Holder.class);
    }

    @Test(expected = JsonMappingException.class)
    public void externalTypeIdWithoutValueFails() throws Exception {
        mapper.readValue("{\"kind\":\"dog\"}", Holder.class);
    }

    @Test(expected = JsonMappingException.class)
    public void scalarValueWithoutExternalTypeIdFails() throws Exception {
        mapper.readValue("{\"value\":\"not-an-animal\"}", Holder.class);
    }
}
