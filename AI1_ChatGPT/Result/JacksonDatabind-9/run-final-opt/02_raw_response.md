package com.fasterxml.jackson.databind.ser;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public class StdKeySerializerBug682Test
{
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void classKeyUsesClassNameInsteadOfClassToString() throws Exception {
        Map<Class<?>, Integer> values = new LinkedHashMap<Class<?>, Integer>();
        values.put(String.class, Integer.valueOf(2));

        assertEquals("{\"java.lang.String\":2}", mapper.writeValueAsString(values));
    }

    @Test
    public void ordinaryStringKeyIsSerializedAsFieldName() throws Exception {
        Map<String, Integer> values = new LinkedHashMap<String, Integer>();
        values.put("regular-key", Integer.valueOf(1));

        assertEquals("{\"regular-key\":1}", mapper.writeValueAsString(values));
    }
}