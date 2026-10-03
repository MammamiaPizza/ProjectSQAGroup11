package com.fasterxml.jackson.databind.seq;

import java.io.ByteArrayInputStream;

import org.junit.Test;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ObjectReaderUTF32ReadValuesTest
{
    public static class RootBean {
        public int a;
        public String name;
    }

    @Test
    public void readsSequentialRootBeansFromUtf32BigEndianBytes() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        byte[] input = utf32BigEndian("{\"a\":1,\"name\":\"first\"} {\"a\":2,\"name\":\"second\"}");

        MappingIterator<RootBean> values = mapper.readerFor(RootBean.class).readValues(input);
        try {
            assertTrue(values.hasNext());
            RootBean first = values.next();
            assertEquals(1, first.a);
            assertEquals("first", first.name);

            assertTrue(values.hasNext());
            RootBean second = values.next();
            assertEquals(2, second.a);
            assertEquals("second", second.name);

            assertFalse(values.hasNext());
        } finally {
            values.close();
        }
    }

    @Test
    public void readsSequentialRootBeansFromUtf32LittleEndianStream() throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        byte[] input = utf32LittleEndian("{\"a\":3,\"name\":\"left\"}\n{\"a\":4,\"name\":\"right\"}");

        MappingIterator<RootBean> values = mapper.readerFor(RootBean.class)
                .readValues(new ByteArrayInputStream(input));
        try {
            assertTrue(values.hasNext());
            RootBean first = values.next();
            assertEquals(3, first.a);
            assertEquals("left", first.name);

            assertTrue(values.hasNext());
            RootBean second = values.next();
            assertEquals(4, second.a);
            assertEquals("right", second.name);

            assertFalse(values.hasNext());
        } finally {
            values.close();
        }
    }

    private static byte[] utf32BigEndian(String value)
    {
        byte[] result = new byte[4 + (value.length() * 4)];
        result[0] = 0;
        result[1] = 0;
        result[2] = (byte) 0xFE;
        result[3] = (byte) 0xFF;
        for (int i = 0; i < value.length(); ++i) {
            int offset = 4 + (i * 4);
            char c = value.charAt(i);
            result[offset] = 0;
            result[offset + 1] = 0;
            result[offset + 2] = (byte) (c >> 8);
            result[offset + 3] = (byte) c;
        }
        return result;
    }

    private static byte[] utf32LittleEndian(String value)
    {
        byte[] result = new byte[4 + (value.length() * 4)];
        result[0] = (byte) 0xFF;
        result[1] = (byte) 0xFE;
        result[2] = 0;
        result[3] = 0;
        for (int i = 0; i < value.length(); ++i) {
            int offset = 4 + (i * 4);
            char c = value.charAt(i);
            result[offset] = (byte) c;
            result[offset + 1] = (byte) (c >> 8);
            result[offset + 2] = 0;
            result[offset + 3] = 0;
        }
        return result;
    }

@Test
public void readsSequentialTreeValuesFromUtf32BigEndianStream() throws Exception {
    byte[] input = utf32Ascii("{\"a\":5} {\"a\":6}", true);
    com.fasterxml.jackson.databind.MappingIterator<com.fasterxml.jackson.databind.JsonNode> values =
            new com.fasterxml.jackson.databind.ObjectMapper()
                    .readerFor(com.fasterxml.jackson.databind.JsonNode.class)
                    .readValues(new java.io.ByteArrayInputStream(input));

    assertTrue(values.hasNext());
    assertEquals(5, values.next().get("a").asInt());
    assertTrue(values.hasNext());
    assertEquals(6, values.next().get("a").asInt());
    assertFalse(values.hasNext());
}

@Test
public void readsSequentialTreeValuesFromUtf32LittleEndianBytes() throws Exception {
    byte[] input = utf32Ascii("{\"a\":7} {\"a\":8}", false);
    com.fasterxml.jackson.databind.MappingIterator<com.fasterxml.jackson.databind.JsonNode> values =
            new com.fasterxml.jackson.databind.ObjectMapper()
                    .readerFor(com.fasterxml.jackson.databind.JsonNode.class)
                    .readValues(input);

    assertTrue(values.hasNext());
    assertEquals(7, values.next().get("a").asInt());
    assertTrue(values.hasNext());
    assertEquals(8, values.next().get("a").asInt());
    assertFalse(values.hasNext());
}

@Test
public void rejectsArrayValueToUpdate() {
    boolean thrown = false;
    try {
        new com.fasterxml.jackson.databind.ObjectMapper()
                .readerFor(int[].class)
                .withValueToUpdate(new int[1]);
    } catch (IllegalArgumentException e) {
        thrown = true;
        assertEquals("Can not update an array value", e.getMessage());
    }
    assertTrue(thrown);
}

private byte[] utf32Ascii(String text, boolean bigEndian) {
    byte[] result = new byte[text.length() * 4];
    for (int i = 0; i < text.length(); ++i) {
        int offset = i * 4;
        int ch = text.charAt(i);
        if (bigEndian) {
            result[offset + 3] = (byte) ch;
        } else {
            result[offset] = (byte) ch;
        }
    }
    return result;
}
}
