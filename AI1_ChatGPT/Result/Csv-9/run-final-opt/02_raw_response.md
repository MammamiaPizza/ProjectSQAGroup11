package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

public class CSVRecordGeneratedTest {

    @Test
    public void testToMapWithoutHeaderMappingIsEmpty() {
        CSVRecord record = new CSVRecord(new String[] { "first", "second" }, null, null, 1L);

        Map<String, String> result = record.toMap();

        assertTrue(result.isEmpty());
    }

    @Test
    public void testPutInWithoutHeaderMappingLeavesProvidedMapUnchanged() {
        CSVRecord record = new CSVRecord(new String[] { "value" }, null, null, 1L);
        Map<String, String> destination = new LinkedHashMap<String, String>();
        destination.put("existing", "preserved");

        Map<String, String> result = record.putIn(destination);

        assertSame(destination, result);
        assertEquals(1, destination.size());
        assertEquals("preserved", destination.get("existing"));
    }

    @Test
    public void testToMapUsesHeaderNamesAndValues() {
        Map<String, Integer> mapping = new LinkedHashMap<String, Integer>();
        mapping.put("name", Integer.valueOf(0));
        mapping.put("age", Integer.valueOf(1));
        CSVRecord record = new CSVRecord(new String[] { "Alice", "42" }, mapping, null, 3L);

        Map<String, String> result = record.toMap();

        assertEquals(2, result.size());
        assertEquals("Alice", result.get("name"));
        assertEquals("42", result.get("age"));
    }

    @Test
    public void testPutInPopulatesProvidedMapAndRetainsExistingEntries() {
        Map<String, Integer> mapping = new LinkedHashMap<String, Integer>();
        mapping.put("left", Integer.valueOf(0));
        mapping.put("right", Integer.valueOf(1));
        CSVRecord record = new CSVRecord(new String[] { "L", "R" }, mapping, null, 1L);
        Map<String, String> destination = new HashMap<String, String>();
        destination.put("other", "value");

        Map<String, String> result = record.putIn(destination);

        assertSame(destination, result);
        assertEquals(3, destination.size());
        assertEquals("L", destination.get("left"));
        assertEquals("R", destination.get("right"));
        assertEquals("value", destination.get("other"));
    }

    @Test
    public void testToMapSkipsMappedColumnsWithoutValues() {
        Map<String, Integer> mapping = new LinkedHashMap<String, Integer>();
        mapping.put("present", Integer.valueOf(0));
        mapping.put("missing", Integer.valueOf(2));
        CSVRecord record = new CSVRecord(new String[] { "available" }, mapping, null, 1L);

        Map<String, String> result = record.toMap();

        assertEquals(1, result.size());
        assertEquals("available", result.get("present"));
        assertFalse(result.containsKey("missing"));
    }

    @Test
    public void testNamedGetWithoutHeadersReportsMissingHeaderMapping() {
        CSVRecord record = new CSVRecord(new String[] { "value" }, null, null, 1L);

        try {
            record.get("column");
            fail("Expected IllegalStateException");
        } catch (IllegalStateException expected) {
            assertEquals("No header mapping was specified, the record values can't be accessed by name",
                    expected.getMessage());
        }
    }

    @Test
    public void testNamedGetForUnknownHeaderReportsAvailableHeaders() {
        Map<String, Integer> mapping = new LinkedHashMap<String, Integer>();
        mapping.put("known", Integer.valueOf(0));
        CSVRecord record = new CSVRecord(new String[] { "value" }, mapping, null, 1L);

        try {
            record.get("unknown");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertEquals("Mapping for unknown not found, expected one of [known]", expected.getMessage());
        }
    }

    @Test
    public void testNamedGetForHeaderBeyondRecordValuesReportsInconsistency() {
        Map<String, Integer> mapping = new LinkedHashMap<String, Integer>();
        mapping.put("first", Integer.valueOf(0));
        mapping.put("missing", Integer.valueOf(1));
        CSVRecord record = new CSVRecord(new String[] { "only" }, mapping, null, 1L);

        assertFalse(record.isConsistent());
        assertFalse(record.isSet("missing"));
        try {
            record.get("missing");
            fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            assertEquals("Index for header 'missing' is 1 but CSVRecord only has 1 values!",
                    expected.getMessage());
        }
    }

    @Test
    public void testBasicAccessAndMetadataAreRetained() {
        CSVRecord record = new CSVRecord(new String[] { "a", "b" }, null, "comment", 7L);

        assertEquals(2, record.size());
        assertEquals("a", record.get(0));
        assertEquals("b", record.get(1));
        assertEquals("comment", record.getComment());
        assertEquals(7L, record.getRecordNumber());
        assertEquals(Arrays.asList("a", "b"), Arrays.asList(record.values()));
    }
}