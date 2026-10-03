package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

public class CSVParserIteratorSequenceTest {

    @Test
    public void testRepeatedHasNextBuffersOnlyOneRecord() throws Exception {
        final CSVParser parser = CSVParser.parse("one\ntwo\nthree\n", CSVFormat.DEFAULT);
        final Iterator<CSVRecord> iterator = parser.iterator();

        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());

        final CSVRecord first = iterator.next();
        assertEquals("one", first.get(0));
        assertEquals(1L, first.getRecordNumber());
        assertEquals(1L, parser.getRecordNumber());

        parser.close();
    }

    @Test
    public void testIteratorSequenceDoesNotAdvancePastReturnedRecord() throws Exception {
        final CSVParser parser = CSVParser.parse("one\ntwo\nthree\nfour\n", CSVFormat.DEFAULT);
        final Iterator<CSVRecord> iterator = parser.iterator();

        assertEquals("one", iterator.next().get(0));

        assertTrue(iterator.hasNext());
        assertEquals("two", iterator.next().get(0));

        assertTrue(iterator.hasNext());
        final CSVRecord third = iterator.next();
        assertEquals("three", third.get(0));
        assertEquals(3L, third.getRecordNumber());
        assertEquals(3L, parser.getRecordNumber());

        assertTrue(iterator.hasNext());
        final CSVRecord fourth = iterator.next();
        assertEquals("four", fourth.get(0));
        assertEquals(4L, fourth.getRecordNumber());

        parser.close();
    }

    @Test(expected = NoSuchElementException.class)
    public void testNextAfterEndOfInputThrowsNoSuchElementException() throws Exception {
        final CSVParser parser = CSVParser.parse("only\n", CSVFormat.DEFAULT);
        final Iterator<CSVRecord> iterator = parser.iterator();

        assertTrue(iterator.hasNext());
        assertEquals("only", iterator.next().get(0));
        assertFalse(iterator.hasNext());

        try {
            iterator.next();
        } finally {
            parser.close();
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIteratorRemoveIsUnsupported() throws Exception {
        final CSVParser parser = CSVParser.parse("value\n", CSVFormat.DEFAULT);
        try {
            parser.iterator().remove();
        } finally {
            parser.close();
        }
    }
}