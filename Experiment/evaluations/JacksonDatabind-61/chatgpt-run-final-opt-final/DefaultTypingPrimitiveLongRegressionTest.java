package com.fasterxml.jackson.databind.jsontype;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DefaultTypingPrimitiveLongRegressionTest
{
    public static class Data {
        public long key;

        public Data() { }

        public Data(long key) {
            this.key = key;
        }
    }

    private ObjectMapper defaultTypingMapper() {
        return new ObjectMapper().enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
    }

    @Test
    public void testDefaultTypingRoundTripsPrimitiveLongPropertyDirectly() throws Exception {
        ObjectMapper mapper = defaultTypingMapper();

        Data input = new Data(Long.MIN_VALUE);
        String json = mapper.writeValueAsString(input);
        Data result = mapper.readValue(json, Data.class);

        assertEquals(Long.MIN_VALUE, result.key);
    }

    @Test
    public void testDefaultTypingRoundTripsPrimitiveLongsInsideHashMapValues() throws Exception {
        ObjectMapper mapper = defaultTypingMapper();
        Map<String, Object> input = new HashMap<String, Object>();
        input.put("zero", new Data(0L));
        input.put("negative", new Data(-1234567890123456789L));
        input.put("minimum", new Data(Long.MIN_VALUE));
        input.put("maximum", new Data(Long.MAX_VALUE));

        String json = mapper.writeValueAsString(input);
        HashMap<?, ?> result = mapper.readValue(json, HashMap.class);

        assertTrue(result.get("zero") instanceof Data);
        assertTrue(result.get("negative") instanceof Data);
        assertTrue(result.get("minimum") instanceof Data);
        assertTrue(result.get("maximum") instanceof Data);
        assertEquals(0L, ((Data) result.get("zero")).key);
        assertEquals(-1234567890123456789L, ((Data) result.get("negative")).key);
        assertEquals(Long.MIN_VALUE, ((Data) result.get("minimum")).key);
        assertEquals(Long.MAX_VALUE, ((Data) result.get("maximum")).key);
    }

    @Test
    public void testStdTypeResolverDoesNotCreateTypeHandlersForPrimitiveLong() {
        ObjectMapper mapper = new ObjectMapper();
        StdTypeResolverBuilder builder = new StdTypeResolverBuilder()
                .init(JsonTypeInfo.Id.CLASS, null)
                .inclusion(JsonTypeInfo.As.WRAPPER_ARRAY);

        assertNull(builder.buildTypeSerializer(
                mapper.getSerializationConfig(),
                mapper.getTypeFactory().constructType(Long.TYPE),
                Collections.<NamedType>emptyList()));
        assertNull(builder.buildTypeDeserializer(
                mapper.getDeserializationConfig(),
                mapper.getTypeFactory().constructType(Long.TYPE),
                Collections.<NamedType>emptyList()));

        assertNotNull(builder.buildTypeSerializer(
                mapper.getSerializationConfig(),
                mapper.getTypeFactory().constructType(Long.class),
                Collections.<NamedType>emptyList()));
        assertNotNull(builder.buildTypeDeserializer(
                mapper.getDeserializationConfig(),
                mapper.getTypeFactory().constructType(Long.class),
                Collections.<NamedType>emptyList()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStdTypeResolverRejectsNullInclusion() {
        new StdTypeResolverBuilder().inclusion(null);
    }

@org.junit.Test
public void testCopiedMapperRetainsNonFinalDefaultTypingConfiguration() throws Exception {
    com.fasterxml.jackson.databind.ObjectMapper original = new com.fasterxml.jackson.databind.ObjectMapper();
    original.enableDefaultTyping(com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping.NON_FINAL);

    com.fasterxml.jackson.databind.ObjectMapper copy = original.copy();
    java.util.ArrayList<String> input = new java.util.ArrayList<String>();
    input.add("value");

    String json = copy.writeValueAsString(input);
    org.junit.Assert.assertTrue(json.contains(java.util.ArrayList.class.getName()));

    Object result = copy.readValue(json, Object.class);
    org.junit.Assert.assertTrue(result instanceof java.util.ArrayList);
    org.junit.Assert.assertEquals("value", ((java.util.ArrayList<?>) result).get(0));
}

@org.junit.Test
public void testNoTypeInfoBuilderSkipsHandlersForObjectType() {
    com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder builder =
            com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder.noTypeInfoBuilder();
    com.fasterxml.jackson.databind.JavaType objectType =
            mapper.getTypeFactory().constructType(Object.class);

    org.junit.Assert.assertNull(builder.buildTypeSerializer(
            mapper.getSerializationConfig(), objectType, null));
    org.junit.Assert.assertNull(builder.buildTypeDeserializer(
            mapper.getDeserializationConfig(), objectType, null));
}

@org.junit.Test
public void testStdTypeResolverBuilderRetainsConfiguredProperties() {
    com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder builder =
            new com.fasterxml.jackson.databind.jsontype.impl.StdTypeResolverBuilder()
                    .init(com.fasterxml.jackson.annotation.JsonTypeInfo.Id.CLASS, null)
                    .inclusion(com.fasterxml.jackson.annotation.JsonTypeInfo.As.EXISTING_PROPERTY)
                    .typeProperty("kind")
                    .defaultImpl(java.util.ArrayList.class)
                    .typeIdVisibility(true);

    org.junit.Assert.assertEquals("kind", builder.getTypeProperty());
    org.junit.Assert.assertSame(java.util.ArrayList.class, builder.getDefaultImpl());
    org.junit.Assert.assertTrue(builder.isTypeIdVisible());
}
}
