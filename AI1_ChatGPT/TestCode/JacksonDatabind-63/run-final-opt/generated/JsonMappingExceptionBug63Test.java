package com.fasterxml.jackson.databind;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.Test;

public class JsonMappingExceptionBug63Test
{
    static class Outer {
        public Inner inner;
    }

    static class Inner {
    }

    public static class DefaultFoo {
        public DefaultBar bar;

        public DefaultFoo() { }
    }

    public static class DefaultBar {
        public DefaultBar() { }

        public void setBaz(int value) {
            throw new IllegalArgumentException("default deserialization failure");
        }
    }

    public static class CreatorFoo {
        @JsonCreator
        public CreatorFoo(@JsonProperty("bar") CreatorBar bar) { }
    }

    public static class CreatorBar {
        @JsonCreator
        public CreatorBar(@JsonProperty("baz") int baz) {
            throw new IllegalArgumentException("creator deserialization failure");
        }
    }

    @Test
    public void referenceDescriptionUsesFullBinaryNameForInnerInstance() {
        JsonMappingException.Reference reference =
                new JsonMappingException.Reference(new Outer(), "inner");

        assertEquals(Outer.class.getName() + "[\"inner\"]", reference.getDescription());
        assertEquals(reference.getDescription(), reference.toString());
    }

    @Test
    public void referenceDescriptionUsesFullBinaryNameForClassAndIndex() {
        JsonMappingException.Reference reference =
                new JsonMappingException.Reference(Outer.class, 0);

        assertEquals(Outer.class.getName() + "[0]", reference.getDescription());
        assertEquals(0, reference.getIndex());
        assertEquals(Outer.class, reference.getFrom());
    }

    @Test
    public void prependPathKeepsOuterToInnerOrderAndMessagePath() {
        JsonMappingException exception = new JsonMappingException("problem");
        exception.prependPath(new Inner(), "value");
        exception.prependPath(new Outer(), "inner");

        String expectedPath = Outer.class.getName() + "[\"inner\"]->"
                + Inner.class.getName() + "[\"value\"]";

        assertEquals(expectedPath, exception.getPathReference());
        assertEquals(2, exception.getPath().size());
        assertEquals("problem (through reference chain: " + expectedPath + ")",
                exception.getMessage());
    }

    @Test
    public void referenceRejectsNullFieldName() {
        try {
            new JsonMappingException.Reference(new Outer(), (String) null);
            fail("Expected null field name to be rejected");
        } catch (NullPointerException e) {
            assertTrue(e.getMessage().contains("null fieldName"));
        }
    }

    @Test
    public void defaultDeserializationFailureIncludesFullBinaryNamesInPath() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.readValue("{\"bar\":{\"baz\":1}}", DefaultFoo.class);
            fail("Expected setter failure to be wrapped as JsonMappingException");
        } catch (JsonMappingException e) {
            String expectedPath = DefaultFoo.class.getName() + "[\"bar\"]->"
                    + DefaultBar.class.getName() + "[\"baz\"]";
            assertEquals(expectedPath, e.getPathReference());
            assertTrue(e.getMessage().contains(expectedPath));
        }
    }

    @Test
    public void creatorDeserializationFailureIncludesFullBinaryNamesInPath() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.readValue("{\"bar\":{\"baz\":1}}", CreatorFoo.class);
            fail("Expected creator failure to be wrapped as JsonMappingException");
        } catch (JsonMappingException e) {
            String expectedPath = CreatorFoo.class.getName() + "[\"bar\"]->"
                    + CreatorBar.class.getName() + "[\"baz\"]";
            assertEquals(expectedPath, e.getPathReference());
            assertTrue(e.getMessage().contains(expectedPath));
        }
    }
}
