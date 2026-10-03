```java
package org.apache.commons.csv;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

public class CSVRecordTest {

    @Test
    public void testIndexedAccessMetadataIteratorAndStringRepresentation() {
        final String[] values = new String[] { "first", null, "third" };
        final CSVRecord record = new CSVRecord(values, null, "a comment", 42L);

        assertEquals("first", record.get(0));
        assertNull(record.get(1));
        assertEquals("third", record.get(2));
        assertEquals(3, record.size());
        assertEquals("a comment", record.getComment());
        assertEquals(42L, record.getRecordNumber());
        assertEquals("[first, null, third]", record.toString());
        assertArrayEquals(values, record.values());

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
    public void testRecordWithoutHeaderMappingIsConsistentButCannotBeAccessedByName() {
        final CSVRecord record = new CSVRecord(new String[] { "value" }, null, null, 1L);

        assertTrue(record.isConsistent());
        assertFalse(record.isMapped("column"));
        assertFalse(record.isSet("column"));

        try {
            record.get("column");
        } catch (final IllegalStateException expected) {
            return;
        }

        throw new AssertionError("Expected IllegalStateException when no header mapping is supplied");
    }

    @Test
    public void testNamedAccessAndMappingQueriesForConsistentRecord() {
        final Map<String, Integer> mapping = new HashMap<String, Integer>();
        mapping.put("name", Integer.valueOf(0));
        mapping.put("age", Integer.valueOf(1));

        final CSVRecord record = new CSVRecord(new String[] { "Alice", "30" }, mapping, null, 2L);

        assertTrue(record.isConsistent());
        assertTrue(record.isMapped("name"));
        assertTrue(record.isMapped("age"));
        assertFalse(record.isMapped("missing"));
        assertTrue(record.isSet("name"));
        assertTrue(record.isSet("age"));
        assertFalse(record.isSet("missing"));
        assertEquals("Alice", record.get("name"));
        assertEquals("30", record.get("age"));
        assertNull(record.get("missing"));
    }

    @Test
    public void testNullValuesCreateAnEmptyRecord() {
        final CSVRecord record = new CSVRecord(null, null, null, 0L);

        assertEquals(0, record.size());
        assertTrue(record.isConsistent());
        assertEquals("[]", record.toString());
        assertFalse(record.iterator().hasNext());
        assertArrayEquals(new String[0], record.values());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetByNameRejectsInconsistentRecordWhenMappedColumnHasNoValue() {
        final Map<String, Integer> mapping = new HashMap<String, Integer>();
        mapping.put("first", Integer.valueOf(0));
        mapping.put("second", Integer.valueOf(1));

        final CSVRecord record = new CSVRecord(new String[] { "only-first" }, mapping, null, 3L);

        assertFalse(record.isConsistent());
        assertTrue(record.isMapped("second"));
        assertFalse(record.isSet("second"));

        record.get("second");
    }

    @Test
    public void testGetByNameReturnsExistingValueFromRecordWithExtraValues() {
        final Map<String, Integer> mapping = new HashMap<String, Integer>();
        mapping.put("first", Integer.valueOf(0));

        final CSVRecord record = new CSVRecord(
                new String[] { "first-value", "unexpected-extra-value" }, mapping, null, 4L);

        assertFalse(record.isConsistent());
        assertTrue(record.isSet("first"));
        assertEquals("first-value", record.get("first"));
    }
}
```

No additional test cases were added because the supplied coverage report already shows complete coverage for `CSVRecord`:

- **Statements/lines:** 25 of 25 covered.
- **Conditions/branches:** 16 of 16 covered.
- **Methods:** All reachable methods are exercised by the current suite.

The existing fault-detection test, `testGetByNameRejectsInconsistentRecordWhenMappedColumnHasNoValue`, specifically targets Csv-2 / CSV-96:

- It creates an inconsistent record where the header mapping contains `"second"` at index `1`, while the record contains only one value at index `0`.
- It verifies `isConsistent()` is `false`, `isMapped("second")` is `true`, and `isSet("second")` is `false`.
- It requires `get("second")` to throw `IllegalArgumentException`, as documented by `CSVRecord.get(String)`.
- The buggy implementation directly evaluates `values[index.intValue()]`, producing `ArrayIndexOutOfBoundsException` instead. Therefore, this test distinguishes the reported incorrect behavior from the required API behavior.

The other existing tests collectively cover:

- Indexed retrieval, including a `null` value.
- Metadata accessors.
- Iteration and string representation.
- Construction with `null` values.
- Named access with no header mapping, including the required `IllegalStateException`.
- Named access with an existing mapping.
- Missing mapped-name lookup returning `null`.
- Consistent and inconsistent mapping/value-size combinations.
- `isMapped` and `isSet` true and false outcomes.
