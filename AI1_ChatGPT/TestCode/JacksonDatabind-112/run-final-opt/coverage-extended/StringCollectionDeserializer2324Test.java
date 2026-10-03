package com.fasterxml.jackson.databind.deser.std;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class StringCollectionDeserializer2324Test
{
    @Test
    public void testDeserializeBagOfStringsUsingArrayDelegatingCreator() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        ImmutableBag bag = mapper.readValue("[\"first\",\"second\",\"third\"]", ImmutableBag.class);

        assertEquals(3, bag.size());
        assertEquals(Arrays.asList("first", "second", "third"), bag.asList());
    }

    @Test
    public void testArrayDelegatingCreatorDoesNotRequireDefaultConstructor() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        ImmutableBag bag = mapper.readValue("[]", ImmutableBag.class);

        assertEquals(0, bag.size());
        assertFalse(bag.iterator().hasNext());
    }

    @Test
    public void testNormalStringCollectionArrayDeserialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        List<String> values = mapper.readValue("[\"\",null,\"value\"]",
                new TypeReference<List<String>>() { });

        assertEquals(Arrays.asList("", null, "value"), values);
    }

    @Test
    public void testSingleStringCanBeAcceptedAsCollectionWhenEnabled() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        List<String> values = mapper.readValue("\"only\"",
                new TypeReference<List<String>>() { });

        assertEquals(Arrays.asList("only"), values);
    }

    @Test(expected = MismatchedInputException.class)
    public void testSingleStringIsRejectedAsCollectionByDefault() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        mapper.readValue("\"only\"", new TypeReference<List<String>>() { });
    }

    public static class ImmutableBag extends AbstractCollection<String>
    {
        private final String[] values;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public ImmutableBag(String[] values) {
            this.values = values.clone();
        }

        @Override
        public Iterator<String> iterator() {
            return Arrays.asList(values).iterator();
        }

        @Override
        public int size() {
            return values.length;
        }

        public List<String> asList() {
            return Arrays.asList(values.clone());
        }
    }

@org.junit.Test
public void testCustomStringDeserializerIsUsedForCollectionContentsAndNulls() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    com.fasterxml.jackson.databind.module.SimpleModule module =
            new com.fasterxml.jackson.databind.module.SimpleModule();
    module.addDeserializer(String.class, new com.fasterxml.jackson.databind.JsonDeserializer<String>() {
        @Override
        public String deserialize(com.fasterxml.jackson.core.JsonParser p,
                com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
            return "custom-" + p.getText();
        }

        @Override
        public String getNullValue(com.fasterxml.jackson.databind.DeserializationContext ctxt) {
            return "custom-null";
        }
    });
    mapper.registerModule(module);

    java.util.List<String> values = mapper.readValue("[\"first\",null,\"third\"]",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.List<String>>() { });

    org.junit.Assert.assertEquals(
            java.util.Arrays.asList("custom-first", "custom-null", "custom-third"), values);
}

@org.junit.Test
public void testCustomStringDeserializerSupportsSingleValueCollection() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper()
            .enable(com.fasterxml.jackson.databind.DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    com.fasterxml.jackson.databind.module.SimpleModule module =
            new com.fasterxml.jackson.databind.module.SimpleModule();
    module.addDeserializer(String.class, new com.fasterxml.jackson.databind.JsonDeserializer<String>() {
        @Override
        public String deserialize(com.fasterxml.jackson.core.JsonParser p,
                com.fasterxml.jackson.databind.DeserializationContext ctxt) throws java.io.IOException {
            return "custom-" + p.getText();
        }
    });
    mapper.registerModule(module);

    java.util.List<String> values = mapper.readValue("\"only\"",
            new com.fasterxml.jackson.core.type.TypeReference<java.util.List<String>>() { });

    org.junit.Assert.assertEquals(java.util.Arrays.asList("custom-only"), values);
}
}
