package com.fasterxml.jackson.databind.deser.std;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class NumberDeserializersEmptyPrimitiveTest
{
    private ObjectMapper failingPrimitiveNullMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
    }

    private void assertEmptyStringFailsForPrimitive(Class<?> primitiveType) throws Exception {
        try {
            failingPrimitiveNullMapper().readValue("\"\"", primitiveType);
            fail("Empty String must not be accepted as a value for primitive "
                    + primitiveType.getName() + " when FAIL_ON_NULL_FOR_PRIMITIVES is enabled");
        } catch (JsonMappingException e) {
            assertNotNull(e.getMessage());
        }
    }

    @Test
    public void emptyStringFailsForPrimitiveBooleanWhenNullsAreForbidden() throws Exception {
        assertEmptyStringFailsForPrimitive(Boolean.TYPE);
    }

    @Test
    public void emptyStringFailsForPrimitiveByteWhenNullsAreForbidden() throws Exception {
        assertEmptyStringFailsForPrimitive(Byte.TYPE);
    }

    @Test
    public void emptyStringFailsForPrimitiveShortWhenNullsAreForbidden() throws Exception {
        assertEmptyStringFailsForPrimitive(Short.TYPE);
    }

    @Test
    public void emptyStringFailsForPrimitiveCharacterWhenNullsAreForbidden() throws Exception {
        assertEmptyStringFailsForPrimitive(Character.TYPE);
    }

    @Test
    public void emptyStringFailsForPrimitiveIntegerWhenNullsAreForbidden() throws Exception {
        assertEmptyStringFailsForPrimitive(Integer.TYPE);
    }

    @Test
    public void emptyStringFailsForPrimitiveLongWhenNullsAreForbidden() throws Exception {
        assertEmptyStringFailsForPrimitive(Long.TYPE);
    }

    @Test
    public void emptyStringFailsForPrimitiveFloatWhenNullsAreForbidden() throws Exception {
        assertEmptyStringFailsForPrimitive(Float.TYPE);
    }

    @Test
    public void emptyStringFailsForPrimitiveDoubleWhenNullsAreForbidden() throws Exception {
        assertEmptyStringFailsForPrimitive(Double.TYPE);
    }

    @Test
    public void emptyStringProducesNullForWrapperTypes() throws Exception {
        ObjectMapper mapper = failingPrimitiveNullMapper();

        assertNull(mapper.readValue("\"\"", Boolean.class));
        assertNull(mapper.readValue("\"\"", Byte.class));
        assertNull(mapper.readValue("\"\"", Short.class));
        assertNull(mapper.readValue("\"\"", Character.class));
        assertNull(mapper.readValue("\"\"", Integer.class));
        assertNull(mapper.readValue("\"\"", Long.class));
        assertNull(mapper.readValue("\"\"", Float.class));
        assertNull(mapper.readValue("\"\"", Double.class));
    }

    @Test
    public void emptyStringUsesPrimitiveNullDefaultsWhenNullsAreAllowed() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        assertEquals(Boolean.FALSE, mapper.readValue("\"\"", Boolean.TYPE));
        assertEquals(Byte.valueOf((byte) 0), mapper.readValue("\"\"", Byte.TYPE));
        assertEquals(Short.valueOf((short) 0), mapper.readValue("\"\"", Short.TYPE));
        assertEquals(Character.valueOf('\0'), mapper.readValue("\"\"", Character.TYPE));
        assertEquals(Integer.valueOf(0), mapper.readValue("\"\"", Integer.TYPE));
        assertEquals(Long.valueOf(0L), mapper.readValue("\"\"", Long.TYPE));
        assertEquals(Float.valueOf(0.0f), mapper.readValue("\"\"", Float.TYPE));
        assertEquals(Double.valueOf(0.0d), mapper.readValue("\"\"", Double.TYPE));
    }

    @Test
    public void validPrimitiveScalarValuesAreDeserializedNormally() throws Exception {
        ObjectMapper mapper = failingPrimitiveNullMapper();

        assertEquals(Boolean.TRUE, mapper.readValue("true", Boolean.TYPE));
        assertEquals(Byte.valueOf(Byte.MAX_VALUE), mapper.readValue("127", Byte.TYPE));
        assertEquals(Short.valueOf(Short.MIN_VALUE), mapper.readValue("-32768", Short.TYPE));
        assertEquals(Character.valueOf('Z'), mapper.readValue("\"Z\"", Character.TYPE));
        assertEquals(Integer.valueOf(Integer.MAX_VALUE),
                mapper.readValue("2147483647", Integer.TYPE));
        assertEquals(Long.valueOf(Long.MIN_VALUE),
                mapper.readValue("-9223372036854775808", Long.TYPE));
        assertEquals(Float.valueOf(1.25f), mapper.readValue("1.25", Float.TYPE));
        assertEquals(Double.valueOf(-2.5d), mapper.readValue("-2.5", Double.TYPE));
    }
}
