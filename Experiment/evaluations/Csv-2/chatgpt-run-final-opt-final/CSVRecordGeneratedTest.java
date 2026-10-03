package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

public class CSVRecordGeneratedTest {

    @Test(expected = IllegalArgumentException.class)
    public void getByNameThrowsIllegalArgumentExceptionWhenMappedIndexExceedsRecordSize() {
        final Map<String, Integer> mapping = new HashMap<String, Integer>();
        mapping.put("first", Integer.valueOf(0));
        mapping.put("second", Integer.valueOf(1));

        final CSVRecord record = new CSVRecord(
                new String[] { "value" }, mapping, null, 1L);

        record.get("second");
    }

    @Test
    public void getByNameReturnsMappedValueForConsistentRecord() {
        final Map<String, Integer> mapping = new HashMap<String, Integer>();
        mapping.put("first", Integer.valueOf(0));
        mapping.put("second", Integer.valueOf(1));

        final CSVRecord record = new CSVRecord(
                new String[] { "one", "two" }, mapping, null, 3L);

        assertEquals("one", record.get("first"));
        assertEquals("two", record.get("second"));
        assertTrue(record.isConsistent());
    }

    @Test
    public void getByUnknownNameReturnsNullWhenHeaderMappingExists() {
        final Map<String, Integer> mapping = new HashMap<String, Integer>();
        mapping.put("known", Integer.valueOf(0));

        final CSVRecord record = new CSVRecord(
                new String[] { "value" }, mapping, null, 1L);

        assertNull(record.get("unknown"));
    }

    @Test(expected = IllegalStateException.class)
    public void getByNameWithoutHeaderMappingThrowsIllegalStateException() {
        final CSVRecord record = new CSVRecord(
                new String[] { "value" }, null, null, 1L);

        record.get("column");
    }

    @Test
    public void inconsistentRecordIsReportedAndOnlyExistingMappedValuesAreSet() {
        final Map<String, Integer> mapping = new HashMap<String, Integer>();
        mapping.put("available", Integer.valueOf(0));
        mapping.put("missing", Integer.valueOf(1));

        final CSVRecord record = new CSVRecord(
                new String[] { "value" }, mapping, null, 1L);

        assertFalse(record.isConsistent());
        assertTrue(record.isMapped("available"));
        assertTrue(record.isMapped("missing"));
        assertTrue(record.isSet("available"));
        assertFalse(record.isSet("missing"));
    }

    @Test
    public void recordWithoutMappingIsConsistentAndHasNoMappedColumns() {
        final CSVRecord record = new CSVRecord(
                new String[] { "value" }, null, null, 1L);

        assertTrue(record.isConsistent());
        assertFalse(record.isMapped("column"));
        assertFalse(record.isSet("column"));
    }

    @Test
    public void emptyValuesAreUsedWhenConstructorReceivesNullValues() {
        final CSVRecord record = new CSVRecord(null, null, "comment", 7L);

        assertEquals(0, record.size());
        assertEquals("comment", record.getComment());
        assertEquals(7L, record.getRecordNumber());
        assertTrue(record.isConsistent());
    }

    @Test
    public void positionalAccessAndIteratorPreserveValueOrder() {
        final CSVRecord record = new CSVRecord(
                new String[] { "first", "second" }, null, null, 1L);

        assertEquals("second", record.get(1));

        final Iterator<String> iterator = record.iterator();
        assertTrue(iterator.hasNext());
        assertEquals("first", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("second", iterator.next());
        assertFalse(iterator.hasNext());
    }

@Test
public void getCommentReturnsRecordComment() {
    final CSVRecord record = new CSVRecord(new String[] { "value" }, null, "comment", 1L);

    assertEquals("comment", record.getComment());
}
}
