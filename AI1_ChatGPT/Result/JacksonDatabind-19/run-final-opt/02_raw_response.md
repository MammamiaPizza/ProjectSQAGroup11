package com.fasterxml.jackson.databind.type;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class TypeFactoryPropertiesTest
{
    @Test
    public void constructTypeResolvesPropertiesAsStringMap() {
        JavaType type = TypeFactory.defaultInstance().constructType(Properties.class);

        assertSame(Properties.class, type.getRawClass());
        assertSame(String.class, type.getKeyType().getRawClass());
        assertSame(String.class, type.getContentType().getRawClass());
    }

    @Test
    public void findTypeParametersResolvesPropertiesMapParameters() {
        TypeFactory factory = TypeFactory.defaultInstance();
        JavaType[] parameters = factory.findTypeParameters(
                factory.constructType(Properties.class), Map.class);

        assertEquals(2, parameters.length);
        assertSame(String.class, parameters[0].getRawClass());
        assertSame(String.class, parameters[1].getRawClass());
    }

    @Test
    public void readNumericJsonValueIntoPropertiesAsString() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Properties properties = mapper.readValue("{\"answer\":123}", Properties.class);

        assertEquals("123", properties.getProperty("answer"));
    }

    @Test
    public void convertMapWithNumericValueIntoPropertiesAsString() {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> source = new LinkedHashMap<String, Object>();
        source.put("answer", Integer.valueOf(129));

        Properties properties = mapper.convertValue(source, Properties.class);

        assertEquals("129", properties.getProperty("answer"));
    }
}