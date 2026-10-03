package com.fasterxml.jackson.databind;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BeanPropertyWriterSerializationTest
{
    enum JsonValueEnum {
        FIRST("first"),
        SECOND("second");

        private final String value;

        JsonValueEnum(String value) {
            this.value = value;
        }

        @JsonValue
        public String value() {
            return value;
        }
    }

    public static class EnumBean {
        public JsonValueEnum value;

        public EnumBean() { }

        public EnumBean(JsonValueEnum value) {
            this.value = value;
        }
    }

    @Test
    public void serializedMapperRetainsJsonValueEnumPropertyHandler() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        EnumBean input = new EnumBean(JsonValueEnum.FIRST);

        assertEquals("{\"value\":\"first\"}", mapper.writeValueAsString(input));

        ObjectMapper restored = roundTrip(mapper);
        assertNotNull(restored);
        assertEquals("{\"value\":\"second\"}",
                restored.writeValueAsString(new EnumBean(JsonValueEnum.SECOND)));
    }

    @Test
    public void serializedMapperCanStillDeserializeJsonValueEnum() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValueAsString(new EnumBean(JsonValueEnum.FIRST));

        ObjectMapper restored = roundTrip(mapper);
        EnumBean result = restored.readValue("{\"value\":\"second\"}", EnumBean.class);

        assertNotNull(result);
        assertEquals(JsonValueEnum.SECOND, result.value);
    }

    private ObjectMapper roundTrip(ObjectMapper mapper) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(bytes);
        output.writeObject(mapper);
        output.close();

        ObjectInputStream input = new ObjectInputStream(
                new ByteArrayInputStream(bytes.toByteArray()));
        ObjectMapper result = (ObjectMapper) input.readObject();
        input.close();
        return result;
    }
}
