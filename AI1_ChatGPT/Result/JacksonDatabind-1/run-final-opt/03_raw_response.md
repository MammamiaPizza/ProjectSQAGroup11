package com.fasterxml.jackson.databind.ser;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;

public class BeanPropertyWriterNullColumnTest
{
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void nullSuppressedColumnIsOmittedWithoutAddingArrayNull() throws Exception
    {
        ArrayRow row = new ArrayRow(null, null, Collections.<String>emptyList(), "bar");

        assertEquals("[null,[null,],\"bar\"]", mapper.writeValueAsString(row));
    }

    @Test
    public void nonNullSuppressedColumnIsRetainedInItsOriginalOrder() throws Exception
    {
        ArrayRow row = new ArrayRow(null, "included", Collections.<String>emptyList(), "bar");

        assertEquals("[null,\"included\",[],\"bar\"]", mapper.writeValueAsString(row));
    }

    @Test
    public void emptyCollectionColumnIsSerializedAsEmptyArrayRatherThanOmitted() throws Exception
    {
        ArrayRow row = new ArrayRow("first", null, Collections.<String>emptyList(), "last");

        assertEquals("[\"first\",[null,],\"last\"]", mapper.writeValueAsString(row));
    }

    @JsonFormat(shape = JsonFormat.Shape.ARRAY)
    @JsonPropertyOrder({ "leading", "suppressed", "values", "tail" })
    public static class ArrayRow
    {
        public String leading;

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public String suppressed;

        public List<String> values;

        public String tail;

        public ArrayRow(String leading, String suppressed, List<String> values, String tail)
        {
            this.leading = leading;
            this.suppressed = suppressed;
            this.values = values;
            this.tail = tail;
        }
    }
}