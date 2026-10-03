package com.fasterxml.jackson.core.json;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class JsonWriteContextBugTest
{
    @Test
    public void objectValueWithoutFieldNameMustExpectNameAndNotChangeState() {
        JsonWriteContext object = JsonWriteContext.createRootContext().createChildObjectContext();

        assertEquals(JsonWriteContext.STATUS_EXPECT_NAME, object.writeValue());
        assertEquals(JsonWriteContext.STATUS_OK_AS_IS, object.writeFieldName("name"));
        assertEquals(JsonWriteContext.STATUS_OK_AFTER_COLON, object.writeValue());
        assertEquals(JsonWriteContext.STATUS_EXPECT_NAME, object.writeValue());
    }

    @Test
    public void objectFieldNameAndValueTransitionsUseCommaAndColonStatuses() throws Exception {
        JsonWriteContext object = JsonWriteContext.createRootContext().createChildObjectContext();

        assertEquals(JsonWriteContext.STATUS_OK_AS_IS, object.writeFieldName("first"));
        assertEquals(JsonWriteContext.STATUS_EXPECT_VALUE, object.writeFieldName("second"));
        assertEquals(JsonWriteContext.STATUS_OK_AFTER_COLON, object.writeValue());
        assertEquals(JsonWriteContext.STATUS_OK_AFTER_COMMA, object.writeFieldName("second"));
        assertEquals(JsonWriteContext.STATUS_OK_AFTER_COLON, object.writeValue());
    }

    @Test
    public void arrayValuesUseAsIsThenComma() {
        JsonWriteContext array = JsonWriteContext.createRootContext().createChildArrayContext();

        assertEquals(JsonWriteContext.STATUS_OK_AS_IS, array.writeValue());
        assertEquals(JsonWriteContext.STATUS_OK_AFTER_COMMA, array.writeValue());
    }

    @Test
    public void rootValuesUseAsIsThenSpace() {
        JsonWriteContext root = JsonWriteContext.createRootContext();

        assertEquals(JsonWriteContext.STATUS_OK_AS_IS, root.writeValue());
        assertEquals(JsonWriteContext.STATUS_OK_AFTER_SPACE, root.writeValue());
    }

    @Test
    public void reusedChildContextIsResetForNewObject() {
        JsonWriteContext root = JsonWriteContext.createRootContext();
        JsonWriteContext child = root.createChildArrayContext();
        child.writeValue();
        child.setCurrentValue("old value");

        JsonWriteContext reused = root.createChildObjectContext();

        assertSame(child, reused);
        assertNull(reused.getCurrentValue());
        assertEquals(JsonWriteContext.STATUS_EXPECT_NAME, reused.writeValue());
        assertEquals(JsonWriteContext.STATUS_OK_AS_IS, reused.writeFieldName("field"));
    }

    @Test
    public void writerGeneratorRejectsStringWhenObjectExpectsFieldName() throws Exception {
        StringWriter output = new StringWriter();
        JsonGenerator generator = new JsonFactory().createGenerator(output);
        generator.writeStartObject();

        try {
            generator.writeString("a");
            fail("writeString() must not be accepted when an object expects a field name");
        } catch (JsonGenerationException expected) {
            // expected
        } finally {
            generator.close();
        }
    }

    @Test
    public void utf8GeneratorRejectsStringWhenObjectExpectsFieldName() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JsonGenerator generator = new JsonFactory().createGenerator(output, JsonEncoding.UTF8);
        generator.writeStartObject();

        try {
            generator.writeString("a");
            fail("writeString() must not be accepted when an object expects a field name");
        } catch (JsonGenerationException expected) {
            // expected
        } finally {
            generator.close();
        }
    }
}
