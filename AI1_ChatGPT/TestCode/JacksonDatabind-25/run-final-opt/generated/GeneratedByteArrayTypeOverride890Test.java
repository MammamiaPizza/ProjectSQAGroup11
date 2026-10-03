package com.fasterxml.jackson.databind.deser;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.module.SimpleAbstractTypeResolver;

public class GeneratedByteArrayTypeOverride890Test
{
    public static class ObjectValueBean {
        @JsonDeserialize(as = byte[].class)
        public Object value;
    }

    public interface Payload {
    }

    public static class PayloadImpl implements Payload {
    }

    @Test
    public void testByteArrayTypeOverride890() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        ObjectValueBean result = mapper.readValue(
                "{\"value\":\"AQIDBA==\"}", ObjectValueBean.class);

        assertNotNull(result);
        assertTrue(result.value instanceof byte[]);
        assertArrayEquals(new byte[] { 1, 2, 3, 4 }, (byte[]) result.value);
    }

    @Test
    public void testRegularByteArrayDeserializationIncludingEmptyValue() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        byte[] value = mapper.readValue("\"AQID\"", byte[].class);
        byte[] empty = mapper.readValue("\"\"", byte[].class);

        assertArrayEquals(new byte[] { 1, 2, 3 }, value);
        assertEquals(0, empty.length);
    }

    @Test
    public void testSimpleAbstractTypeResolverFindsRegisteredMapping() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();
        resolver.addMapping(Payload.class, PayloadImpl.class);

        JavaType original = mapper.constructType(Payload.class);
        JavaType mapped = resolver.findTypeMapping(mapper.getDeserializationConfig(), original);

        assertNotNull(mapped);
        assertSame(PayloadImpl.class, mapped.getRawClass());
        assertNull(resolver.resolveAbstractType(mapper.getDeserializationConfig(), original));
    }

    @Test
    public void testSimpleAbstractTypeResolverRejectsIdentityMapping() {
        SimpleAbstractTypeResolver resolver = new SimpleAbstractTypeResolver();

        try {
            resolver.addMapping(Payload.class, Payload.class);
            fail("Identity abstract type mappings must be rejected");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("class to itself"));
        }
    }
}
