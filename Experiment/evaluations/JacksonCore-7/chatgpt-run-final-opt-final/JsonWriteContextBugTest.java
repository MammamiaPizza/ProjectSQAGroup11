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
    public void objectValueWithoutFieldNameMustExpectNameAndNotChangeState() throws Exception {
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
    public void reusedChildContextIsResetForNewObject() throws Exception {
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

@org.junit.Test
public void duplicateObjectFieldNamesAreRejectedWhenDupDetectionIsEnabled() throws Exception {
    com.fasterxml.jackson.core.json.DupDetector detector =
            com.fasterxml.jackson.core.json.DupDetector.rootDetector(
                    (com.fasterxml.jackson.core.JsonGenerator) null);
    com.fasterxml.jackson.core.json.JsonWriteContext object =
            com.fasterxml.jackson.core.json.JsonWriteContext.createRootContext(detector)
                    .createChildObjectContext();

    object.writeFieldName("name");
    object.writeValue();

    try {
        object.writeFieldName("name");
        org.junit.Assert.fail("Expected duplicate field name to be rejected");
    } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
        org.junit.Assert.assertTrue(e.getMessage().contains("Duplicate field 'name'"));
    }
}

@org.junit.Test
public void childContextsDescribeObjectArrayAndRootPaths() throws Exception {
    com.fasterxml.jackson.core.json.JsonWriteContext root =
            com.fasterxml.jackson.core.json.JsonWriteContext.createRootContext();
    com.fasterxml.jackson.core.json.JsonWriteContext array = root.createChildArrayContext();

    org.junit.Assert.assertSame(root, array.getParent());
    array.writeValue();
    array.writeValue();
    org.junit.Assert.assertEquals("[1]", array.toString());

    com.fasterxml.jackson.core.json.JsonWriteContext object = root.createChildObjectContext();
    org.junit.Assert.assertSame(array, object);
    org.junit.Assert.assertEquals("{?}", object.toString());
    object.writeFieldName("field");
    org.junit.Assert.assertEquals("{\"field\"}", object.toString());
    org.junit.Assert.assertEquals("/", root.toString());
}

@org.junit.Test
public void contextsRetainCurrentValuesAndExposeCurrentFieldName() throws Exception {
    com.fasterxml.jackson.core.json.JsonWriteContext root =
            com.fasterxml.jackson.core.json.JsonWriteContext.createRootContext();
    Object rootValue = new Object();
    root.setCurrentValue(rootValue);

    com.fasterxml.jackson.core.json.JsonWriteContext object = root.createChildObjectContext();
    object.writeFieldName("property");
    Object objectValue = new Object();
    object.setCurrentValue(objectValue);

    org.junit.Assert.assertSame(rootValue, root.getCurrentValue());
    org.junit.Assert.assertSame(objectValue, object.getCurrentValue());
    org.junit.Assert.assertEquals("property", object.getCurrentName());
}
}
