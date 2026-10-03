package com.fasterxml.jackson.databind.struct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BeanDeserializerIssue2088Test
{
    private final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void testUnwrappedFieldsAfterLastCreatorProperty() throws Exception {
        CreatorBean result = MAPPER.readValue(
                "{\"first\":1,\"second\":2,\"after\":4}",
                CreatorBean.class);

        assertEquals(1, result.first);
        assertEquals(2, result.second);
        assertNotNull(result.unwrapped);
        assertEquals(4, result.unwrapped.after);
    }

    @Test
    public void testUnwrappedFieldsBeforeAndAfterCreatorCompletion() throws Exception {
        CreatorBean result = MAPPER.readValue(
                "{\"before\":3,\"first\":1,\"second\":2,\"after\":4}",
                CreatorBean.class);

        assertEquals(1, result.first);
        assertEquals(2, result.second);
        assertNotNull(result.unwrapped);
        assertEquals(3, result.unwrapped.before);
        assertEquals(4, result.unwrapped.after);
    }

    public static class CreatorBean {
        public final int first;
        public final int second;

        @JsonUnwrapped
        public UnwrappedValues unwrapped;

        @JsonCreator
        public CreatorBean(@JsonProperty("first") int first,
                @JsonProperty("second") int second) {
            this.first = first;
            this.second = second;
        }
    }

    public static class UnwrappedValues {
        public int before;
        public int after;
    }
}
