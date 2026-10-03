package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

public class CSVRecordGeneratedTest {

    private enum Header {
        FIRST,
        SECOND;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    @Test
    public void testNullValuesCreateEmptyRecordAndPreserveMetadata() {
        final CSVRecord record = new CSVRecord(null, null, "comment", 17L);

        assertEquals(0, record.size());
        assertEquals("comment", record.getComment());
        assertEquals(17L, record.getRecordNumber());
        assertEquals("[]", record.toString());
        assertEquals(0, record.values().length);
        assertTrue(record.isConsistent());
    }

    @Test
    public void testGetByIndexNameAndEnum() {
        final Map<String, Integer> mapping = new HashMap<String, Integer>();
        mapping.put("first", Integer.valueOf(0));
        mapping.put("second", Integer.valueOf(1));

        final CSVRecord record = new CSVRecord(new String[] { "one", "two" }, mapping, null, 1L);

        assertEquals("one", record.get(0));
        assertEquals("two", record.get("second"));
        assertEquals("one", record.get(Header.FIRST));
        assertEquals("two", record.get(Header.SECOND));
    }

    @Test
    public void testGetByIndexRejectsOutOfRangeIndex() {
        final CSVRecord record = new CSVRecord(new String[] { "only" }, null, null, 1L);

        try {
            record.get(1);
            fail("An index beyond the record size must fail");
        } catch (final ArrayIndexOutOfBoundsException expected) {
            assertTrue(expected.getMessage() == null || expected.getMessage().length() >= 0);
        }
    }

    @Test
    public void testGetByNameRejectsNoMappingUnknownNameAndMissingValue() {
        final CSVRecord recordWithoutHeaders = new CSVRecord(new String[] { "value" }, null, null, 1L);

        try {
            recordWithoutHeaders.get("column");
            fail("Name-based access without headers must fail");
        } catch (final IllegalStateException expected) {
            assertTrue(expected.getMessage().contains("No header mapping"));
        }

        final Map<String, Integer> mapping = new HashMap<String, Integer>();
        mapping.put("present", Integer.valueOf(0));
        mapping.put("missingValue", Integer.valueOf(2));
        final CSVRecord record = new CSVRecord(new String[] { "value" }, mapping, null, 1L);

        try {
            record.get("unknown");
            fail("Accessing an unmapped header must fail");
        } catch (final IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Mapping for unknown not found"));
        }

        try {
            record.get("missingValue");
            fail("Accessing a mapped column beyond the available values must fail");
        } catch (final IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("CSVRecord only has 1 values"));
        }
    }

    @Test
    public void testMappedSetAndConsistencyStates() {
        final CSVRecord recordWithoutHeaders = new CSVRecord(new String[] { "value" }, null, null, 1L);
        assertFalse(recordWithoutHeaders.isMapped("column"));
        assertFalse(recordWithoutHeaders.isSet("column"));
        assertTrue(recordWithoutHeaders.isConsistent());

        final Map<String, Integer> mapping = new HashMap<String, Integer>();
        mapping.put("first", Integer.valueOf(0));
        mapping.put("second", Integer.valueOf(1));
        mapping.put("third", Integer.valueOf(2));

        final CSVRecord inconsistentRecord = new CSVRecord(new String[] { "one", "two" }, mapping, null, 1L);

        assertTrue(inconsistentRecord.isMapped("first"));
        assertFalse(inconsistentRecord.isMapped("unknown"));
        assertTrue(inconsistentRecord.isSet("second"));
        assertFalse(inconsistentRecord.isSet("third"));
        assertFalse(inconsistentRecord.isSet("unknown"));
        assertFalse(inconsistentRecord.isConsistent());

        mapping.remove("third");
        final CSVRecord consistentRecord = new CSVRecord(new String[] { "one", "two" }, mapping, null, 1L);
        assertTrue(consistentRecord.isConsistent());
    }

    @Test
    public void testIteratorReturnsAllValuesIncludingNull() {
        final CSVRecord record = new CSVRecord(new String[] { "first", null, "third" }, null, null, 1L);

        final Iterator<String> iterator = record.iterator();

        assertTrue(iterator.hasNext());
        assertEquals("first", iterator.next());
        assertTrue(iterator.hasNext());
        assertNull(iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("third", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testPutInPopulatesProvidedMapAndSkipsUnavailableColumns() {
        final Map<String, Integer> mapping = new HashMap<String, Integer>();
        mapping.put("first", Integer.valueOf(0));
        mapping.put("second", Integer.valueOf(1));
        mapping.put("unavailable", Integer.valueOf(3));

        final CSVRecord record = new CSVRecord(new String[] { "one", "two" }, mapping, null, 1L);
        final Map<String, String> destination = new HashMap<String, String>();
        destination.put("existing", "preserved");

        final Map<String, String> returned = record.putIn(destination);

        assertSame(destination, returned);
        assertEquals("preserved", destination.get("existing"));
        assertEquals("one", destination.get("first"));
        assertEquals("two", destination.get("second"));
        assertFalse(destination.containsKey("unavailable"));
    }

    @Test
    public void testToMapCopiesMappedValuesIntoIndependentMap() {
        final Map<String, Integer> mapping = new HashMap<String, Integer>();
        mapping.put("first", Integer.valueOf(0));
        mapping.put("second", Integer.valueOf(1));

        final CSVRecord record = new CSVRecord(new String[] { "one", "two" }, mapping, null, 1L);

        final Map<String, String> result = record.toMap();

        assertEquals(2, result.size());
        assertEquals("one", result.get("first"));
        assertEquals("two", result.get("second"));

        result.put("first", "changed");
        assertEquals("one", record.get("first"));
    }

    @Test
    public void testToMapWithoutHeaderMappingReturnsEmptyMap() {
        final CSVRecord record = new CSVRecord(new String[] { "one", "two" }, null, null, 1L);

        final Map<String, String> result = record.toMap();

        assertTrue("A record without headers must produce an empty map", result.isEmpty());
    }
}
