```java
package org.apache.commons.csv;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

public class CSVRecordTest {

    private enum Header {
        FIRST
    }

    @Test
    public void testAccessValuesByIndexNameAndEnum() {
        final Map<String, Integer> mapping = new HashMap<String, Integer>();
        mapping.put("first", Integer.valueOf(0));
        mapping.put(Header.FIRST.toString(), Integer.valueOf(1));

        final CSVRecord record = new CSVRecord(
                new String[] { "value-0", "value-1" }, mapping, "a comment", 7L);

        assertEquals("value-0", record.get(0));
        assertEquals("value-0", record.get("first"));
        assertEquals("value-1", record.get(Header.FIRST));
        assertEquals("a comment", record.getComment());
        assertEquals(7L, record.getRecordNumber());
        assertEquals(2, record.size());
        assertEquals("[value-0, value-1]", record.toString());
    }

    @Test
    public void testGetByNameWithoutHeaderMappingThrowsIllegalStateException() {
        final CSVRecord record = new CSVRecord(new String[] { "value" }, null, null, 1L);

        try {
            record.get("header");
            fail("Expected get(String) to reject records without a header mapping");
        } catch (final IllegalStateException expected) {
            assertTrue(expected.getMessage().contains("No header mapping"));
        }
    }

    @Test
    public void testGetByUnknownNameThrowsIllegalArgumentException() {
        final Map<String, Integer> mapping = new HashMap<String, Integer>();
        mapping.put("known", Integer.valueOf(0));
        final CSVRecord record = new CSVRecord(new String[] { "value" }, mapping, null, 1L);

        try {
            record.get("unknown");
            fail("Expected get(String) to reject an unmapped header");
        } catch (final IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("unknown"));
        }
    }

    @Test
    public void testGetByMappedNameBeyondShortRecordThrowsIllegalArgumentException() {
        final Map<String, Integer> mapping = new HashMap<String, Integer>();
        mapping.put("third", Integer.valueOf(2));
        final CSVRecord record = new CSVRecord(new String[] { "only-value" }, mapping, null, 1L);

        try {
            record.get("third");
            fail("Expected get(String) to reject a mapped column missing from the record");
        } catch (final IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("third"));
        }
    }

    @Test
    public void testGetByInvalidIndexThrowsArrayIndexOutOfBoundsException() {
        final CSVRecord record = new CSVRecord(new String[] { "value" }, null, null, 1L);

        try {
            record.get(1);
            fail("Expected an out-of-range index to fail");
        } catch (final ArrayIndexOutOfBoundsException expected) {
            assertEquals(1, record.size());
        }
    }

    @Test
    public void testMappingConsistencyAndSetStatusForCompleteAndShortRecords() {
        final Map<String, Integer> mapping = new HashMap<String, Integer>();
        mapping.put("first", Integer.valueOf(0));
        mapping.put("second", Integer.valueOf(1));
        mapping.put("third", Integer.valueOf(2));

        final CSVRecord shortRecord = new CSVRecord(
                new String[] { "one", "two" }, mapping, null, 1L);
        final CSVRecord completeRecord = new CSVRecord(
                new String[] { "one", "two", "three" }, mapping, null, 2L);
        final CSVRecord recordWithoutMapping = new CSVRecord(
                new String[] { "one" }, null, null, 3L);

        assertFalse(shortRecord.isConsistent());
        assertTrue(shortRecord.isMapped("third"));
        assertFalse(shortRecord.isSet("third"));
        assertTrue(shortRecord.isSet("second"));
        assertFalse(shortRecord.isMapped("missing"));
        assertFalse(shortRecord.isSet("missing"));

        assertTrue(completeRecord.isConsistent());
        assertTrue(completeRecord.isSet("third"));

        assertTrue(recordWithoutMapping.isConsistent());
        assertFalse(recordWithoutMapping.isMapped("anything"));
        assertFalse(recordWithoutMapping.isSet("anything"));
    }

    @Test
    public void testToMapWithShortRecordContainsOnlyAvailableMappedValues() {
        final Map<String, Integer> mapping = new LinkedHashMap<String, Integer>();
        mapping.put("first", Integer.valueOf(0));
        mapping.put("second", Integer.valueOf(1));
        mapping.put("third", Integer.valueOf(2));

        final CSVRecord record = new CSVRecord(
                new String[] { "one", "two" }, mapping, null, 1L);

        final Map<String, String> result = record.toMap();

        assertEquals(2, result.size());
        assertEquals("one", result.get("first"));
        assertEquals("two", result.get("second"));
        assertFalse(result.containsKey("third"));
    }

    @Test
    public void testPutInReturnsAndPopulatesProvidedMap() {
        final Map<String, Integer> mapping = new HashMap<String, Integer>();
        mapping.put("column", Integer.valueOf(0));
        final CSVRecord record = new CSVRecord(new String[] { "value" }, mapping, null, 1L);

        final Map<String, String> destination = new HashMap<String, String>();
        destination.put("existing", "preserved");

        final Map<String, String> result = record.putIn(destination);

        assertSame(destination, result);
        assertEquals("preserved", result.get("existing"));
        assertEquals("value", result.get("column"));
    }

    @Test
    public void testToMapWithEmptyHeaderMappingIsEmpty() {
        final Map<String, Integer> mapping = new HashMap<String, Integer>();
        final CSVRecord record = new CSVRecord(new String[] { "value" }, mapping, null, 1L);

        final Map<String, String> result = record.toMap();

        assertTrue(result.isEmpty());
    }

    @Test
    public void testNullValuesAreRepresentedAsEmptyRecordAndIteratorIsEmpty() {
        final CSVRecord record = new CSVRecord(null, null, null, 5L);

        final Iterator<String> iterator = record.iterator();

        assertEquals(0, record.size());
        assertFalse(iterator.hasNext());
        assertNull(record.getComment());
        assertEquals("[]", record.toString());
    }

    @Test
    public void testIteratorReturnsValuesInRecordOrderIncludingNulls() {
        final CSVRecord record = new CSVRecord(
                new String[] { "first", null, "third" }, null, null, 1L);

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
    public void testValuesReturnsAllRecordValues() {
        final String[] values = new String[] { "first", null, "third" };
        final CSVRecord record = new CSVRecord(values, null, null, 1L);

        assertArrayEquals(values, record.values());
    }
}
```

New test coverage target:

- `testValuesReturnsAllRecordValues`
  - Covers the package-private `CSVRecord.values()` method, including its previously uncovered `return values;` statement (reported as the sole uncovered line).
  - Verifies that all stored record values are exposed through the internal accessor, including a `null` CSV value, without duplicating the existing iterator behavior tests.
  - The supplied coverage report already indicates that all reported conditions and branches are covered; this test specifically adds method and statement coverage for the remaining uncovered accessor.