package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BeanDeserializerBaseBug68Test
{
    private ObjectMapper mapper() {
        return new ObjectMapper()
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    }

    @Test
    public void singleStringIsPassedToArrayDelegateCreator() throws Exception {
        Bean1421B result = mapper().readValue("\"test2\"", Bean1421B.class);

        assertNotNull(result);
        assertArrayEquals(new String[] { "test2" }, result.values);
    }

    @Test
    public void regularArrayIsPassedToArrayDelegateCreator() throws Exception {
        Bean1421B result = mapper().readValue("[\"first\",\"second\"]", Bean1421B.class);

        assertNotNull(result);
        assertArrayEquals(new String[] { "first", "second" }, result.values);
    }

    @Test
    public void chainedArrayCreatorsDeserializeNestedArrays() throws Exception {
        Messages result = mapper().readValue("[[\"first\",\"second\"],[\"third\"]]", Messages.class);

        assertNotNull(result);
        assertEquals(2, result.messages.length);
        assertArrayEquals(new String[] { "first", "second" }, result.messages[0].values);
        assertArrayEquals(new String[] { "third" }, result.messages[1].values);
    }

    @Test
    public void chainedArrayCreatorsAcceptOneScalarAtEachLevel() throws Exception {
        Messages result = mapper().readValue("\"only\"", Messages.class);

        assertNotNull(result);
        assertEquals(1, result.messages.length);
        assertArrayEquals(new String[] { "only" }, result.messages[0].values);
    }

    public static class Bean1421B {
        public final String[] values;

        @JsonCreator
        public Bean1421B(String[] values) {
            this.values = values;
        }
    }

    public static class Messages {
        public final Bean1421B[] messages;

        @JsonCreator
        public Messages(Bean1421B[] messages) {
            this.messages = messages;
        }
    }
}