package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

public class CSVRecordShortRecordRegressionTest {

    private Map<String, Integer> threeColumnMapping() {
        final Map<String, Integer> mapping = new LinkedHashMap<String, Integer>();
        mapping.put("first", Integer.valueOf(0));
        mapping.put("second", Integer.valueOf(1));
        mapping.put("third", Integer.valueOf(2));
        return mapping;
    }

    @Test
    public void toMapOmitsHeaderWhoseIndexIsBeyondShortRecord() {
        final CSVRecord record = new CSVRecord(
                new String[] { "one", "two" }, threeColumnMapping(), null, 1L);

        final Map<String, String> result = record.toMap();

        assertEquals(2, result.size());
        assertEquals("one", result.get("first"));
        assertEquals("two", result.get("second"));
        assertFalse(result.containsKey("third"));
    }

    @Test
    public void putInPopulatesAvailableValuesFromShortRecord() {
        final CSVRecord record = new CSVRecord(
                new String[] { "one", "two" }, threeColumnMapping(), null, 1L);
        final Map<String, String> destination = new HashMap<String, String>();

        final Map<String, String> returned = record.putIn(destination);

        assertSame(destination, returned);
        assertEquals(2, destination.size());
        assertEquals("one", destination.get("first"));
        assertEquals("two", destination.get("second"));
        assertFalse(destination.containsKey("third"));
    }

    @Test
    public void toMapOfEmptyRecordWithHeadersIsEmpty() {
        final CSVRecord record = new CSVRecord(
                new String[0], threeColumnMapping(), null, 1L);

        final Map<String, String> result = record.toMap();

        assertTrue(result.isEmpty());
    }

    @Test
    public void toMapContainsEveryMappedValueForCompleteRecord() {
        final CSVRecord record = new CSVRecord(
                new String[] { "one", "two", "three" }, threeColumnMapping(), null, 1L);

        final Map<String, String> result = record.toMap();

        assertEquals(3, result.size());
        assertEquals("one", result.get("first"));
        assertEquals("two", result.get("second"));
        assertEquals("three", result.get("third"));
    }

    @Test
    public void shortRecordReportsOnlyAvailableHeadersAsSet() {
        final CSVRecord record = new CSVRecord(
                new String[] { "one", "two" }, threeColumnMapping(), null, 1L);

        assertTrue(record.isSet("first"));
        assertTrue(record.isSet("second"));
        assertFalse(record.isSet("third"));
        assertFalse(record.isConsistent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getByNameRejectsMappedHeaderMissingFromShortRecord() {
        final CSVRecord record = new CSVRecord(
                new String[] { "one", "two" }, threeColumnMapping(), null, 1L);

        record.get("third");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getByNameRejectsUnknownHeader() {
        final CSVRecord record = new CSVRecord(
                new String[] { "one" }, threeColumnMapping(), null, 1L);

        record.get("unknown");
    }

    @Test(expected = IllegalStateException.class)
    public void getByNameWithoutHeaderMappingIsIllegal() {
        final CSVRecord record = new CSVRecord(
                new String[] { "one" }, null, null, 1L);

        record.get("first");
    }
}