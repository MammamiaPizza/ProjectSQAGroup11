package com.fasterxml.jackson.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.StringWriter;

import org.junit.Test;

public class JsonGeneratorEmbeddedObjectTest
{
    @Test
    public void testWriteEmbeddedObjectWithBinaryWritesBase64() throws Exception {
        StringWriter output = new StringWriter();
        JsonGenerator generator = new JsonFactory().createGenerator(output);

        generator.writeEmbeddedObject(new byte[] { 1, 2, 3 });
        generator.close();

        assertEquals("\"AQID\"", output.toString());
    }

    @Test
    public void testWriteEmbeddedObjectWithEmptyBinaryWritesEmptyBase64String() throws Exception {
        StringWriter output = new StringWriter();
        JsonGenerator generator = new JsonFactory().createGenerator(output);

        generator.writeEmbeddedObject(new byte[0]);
        generator.close();

        assertEquals("\"\"", output.toString());
    }

    @Test
    public void testWriteObjectWithBinaryUsesBinaryOutput() throws Exception {
        StringWriter output = new StringWriter();
        JsonGenerator generator = new JsonFactory().createGenerator(output);

        generator.writeObject(new byte[] { 0, 127, -1 });
        generator.close();

        assertEquals("\"AH//\"", output.toString());
    }

    @Test
    public void testWriteObjectFieldWithBinaryUsesBinaryOutput() throws Exception {
        StringWriter output = new StringWriter();
        JsonGenerator generator = new JsonFactory().createGenerator(output);

        generator.writeStartObject();
        generator.writeObjectField("data", new byte[] { 1, 2, 3 });
        generator.writeEndObject();
        generator.close();

        assertEquals("{\"data\":\"AQID\"}", output.toString());
    }

    @Test
    public void testWriteEmbeddedObjectWithNullWritesNull() throws Exception {
        StringWriter output = new StringWriter();
        JsonGenerator generator = new JsonFactory().createGenerator(output);

        generator.writeEmbeddedObject(null);
        generator.close();

        assertEquals("null", output.toString());
    }

    @Test
    public void testWriteEmbeddedObjectWithUnsupportedValueStillFails() throws Exception {
        JsonGenerator generator = new JsonFactory().createGenerator(new StringWriter());

        try {
            generator.writeEmbeddedObject(new Object());
            fail("Expected JsonGenerationException for unsupported embedded object");
        } catch (JsonGenerationException e) {
            assertEquals("No native support for writing embedded objects", e.getOriginalMessage());
        } finally {
            generator.close();
        }
    }
}
