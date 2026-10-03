package com.fasterxml.jackson.databind.convert;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

public class ObjectReaderUpdateValueIssue744Test
{
    public static class DataA {
        public int i;
    }

    public static class DataB {
        @JsonUnwrapped
        public DataA da;

        public int k;
    }

    @Test
    public void testUpdateValueAcceptsUnwrappedProperty() throws Exception {
        DataB target = new DataB();
        target.da = new DataA();
        target.da.i = 1;
        target.k = 2;

        ObjectReader reader = new ObjectMapper()
                .readerFor(DataB.class)
                .withValueToUpdate(target);

        DataB result = reader.readValue("{\"i\":3,\"k\":4}");

        assertSame(target, result);
        assertEquals(3, target.da.i);
        assertEquals(4, target.k);
    }

    @Test
    public void testUpdateValueRetainsUnspecifiedProperties() throws Exception {
        DataB target = new DataB();
        target.da = new DataA();
        target.da.i = 12;
        target.k = 7;

        DataB result = new ObjectMapper()
                .readerFor(DataB.class)
                .withValueToUpdate(target)
                .readValue("{\"i\":15}");

        assertSame(target, result);
        assertEquals(15, target.da.i);
        assertEquals(7, target.k);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithValueToUpdateRejectsNullTarget() {
        new ObjectMapper().readerFor(DataB.class).withValueToUpdate(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithValueToUpdateRejectsArrayTarget() {
        int[] target = new int[] { 1, 2 };
        new ObjectMapper().readerFor(int[].class).withValueToUpdate(target);
    }
}