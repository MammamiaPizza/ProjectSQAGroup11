package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.Test;

public class CSVParserGeneratedTest {

    @Test
    public void parsesStringAndReturnsRecordsInOrder() throws IOException {
        final CSVParser parser = CSVParser.parse("first,second\nthird,fourth", CSVFormat.DEFAULT);

        final List<CSVRecord> records = parser.getRecords();

        assertEquals(2, records.size());
        assertEquals("first", records.get(0).get(0));
        assertEquals("second", records.get(0).get(1));
        assertEquals("third", records.get(1).get(0));
        assertEquals("fourth", records.get(1).get(1));
        assertEquals(2L, parser.getRecordNumber());
        parser.close();
    }

    @Test
    public void getRecordsAddsToSuppliedCollectionAndReturnsSameCollection() throws IOException {
        final CSVParser parser = CSVParser.parse("a\nb", CSVFormat.DEFAULT);
        final List<CSVRecord> destination = new ArrayList<CSVRecord>();
        destination.add(new CSVRecord(new String[] { "existing" }, null, null, 0));

        final List<CSVRecord> returned = parser.getRecords(destination);

        assertSame(destination, returned);
        assertEquals(3, destination.size());
        assertEquals("existing", destination.get(0).get(0));
        assertEquals("a", destination.get(1).get(0));
        assertEquals("b", destination.get(2).get(0));
        parser.close();
    }

    @Test
    public void autoDetectedHeaderIsNotReturnedAsDataAndIsMapped() throws IOException {
        final CSVParser parser = CSVParser.parse("name,age\nAlice,30", CSVFormat.DEFAULT.withHeader());

        final Map<String, Integer> headerMap = parser.getHeaderMap();
        final List<CSVRecord> records = parser.getRecords();

        assertEquals(2, headerMap.size());
        assertEquals(Integer.valueOf(0), headerMap.get("name"));
        assertEquals(Integer.valueOf(1), headerMap.get("age"));
        assertEquals(1, records.size());
        assertEquals("Alice", records.get(0).get("name"));
        assertEquals("30", records.get(0).get("age"));
        assertEquals(2L, records.get(0).getRecordNumber());
        parser.close();
    }

    @Test
    public void explicitHeaderWithNullColumnNameIsAcceptedAndMapped() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT.withHeader("first", (String) null, "third");
        final CSVParser parser = CSVParser.parse("one,two,three", format);

        final Map<String, Integer> headerMap = parser.getHeaderMap();
        final List<CSVRecord> records = parser.getRecords();

        assertEquals(3, headerMap.size());
        assertEquals(Integer.valueOf(0), headerMap.get("first"));
        assertTrue(headerMap.containsKey(null));
        assertEquals(Integer.valueOf(1), headerMap.get(null));
        assertEquals(Integer.valueOf(2), headerMap.get("third"));
        assertEquals(1, records.size());
        assertEquals("two", records.get(0).get(1));
        parser.close();
    }

    @Test
    public void duplicateNullHeadersAreAllowedWhenEmptyHeadersAreIgnored() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT.withIgnoreEmptyHeaders()
                .withHeader((String) null, (String) null);
        final CSVParser parser = CSVParser.parse("left,right", format);

        final Map<String, Integer> headerMap = parser.getHeaderMap();
        final List<CSVRecord> records = parser.getRecords();

        assertEquals(1, headerMap.size());
        assertTrue(headerMap.containsKey(null));
        assertEquals(Integer.valueOf(1), headerMap.get(null));
        assertEquals(1, records.size());
        assertEquals("left", records.get(0).get(0));
        assertEquals("right", records.get(0).get(1));
        parser.close();
    }

    @Test
    public void duplicateNonEmptyHeadersAreRejected() throws IOException {
        try {
            new CSVParser(new java.io.StringReader("a,b"), CSVFormat.DEFAULT.withHeader("id", "id"));
            fail("Duplicate non-empty header names must be rejected");
        } catch (final IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("duplicate name"));
        }
    }

    @Test
    public void headerMapIsDefensiveCopy() throws IOException {
        final CSVParser parser = CSVParser.parse("value", CSVFormat.DEFAULT.withHeader("column"));

        final Map<String, Integer> firstMap = parser.getHeaderMap();
        firstMap.put("changed", Integer.valueOf(9));

        final Map<String, Integer> secondMap = parser.getHeaderMap();
        assertEquals(1, secondMap.size());
        assertEquals(Integer.valueOf(0), secondMap.get("column"));
        assertFalse(secondMap.containsKey("changed"));
        parser.close();
    }

    @Test
    public void parserWithoutHeaderReturnsNullHeaderMap() throws IOException {
        final CSVParser parser = CSVParser.parse("value", CSVFormat.DEFAULT);

        assertNull(parser.getHeaderMap());
        parser.close();
    }

    @Test
    public void nullStringIsReturnedAsNullRecordValue() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT.withNullString("NULL");
        final CSVParser parser = CSVParser.parse("value,NULL,null", format);

        final CSVRecord record = parser.iterator().next();

        assertEquals("value", record.get(0));
        assertNull(record.get(1));
        assertNull(record.get(2));
        parser.close();
    }

    @Test
    public void iteratorCachesNextRecordAndRejectsOperationsAfterExhaustion() throws IOException {
        final CSVParser parser = CSVParser.parse("a\nb", CSVFormat.DEFAULT);
        final Iterator<CSVRecord> iterator = parser.iterator();

        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertEquals("a", iterator.next().get(0));
        assertTrue(iterator.hasNext());
        assertEquals("b", iterator.next().get(0));
        assertFalse(iterator.hasNext());

        try {
            iterator.next();
            fail("next() after the final record must throw NoSuchElementException");
        } catch (final NoSuchElementException expected) {
            assertTrue(expected.getMessage().contains("No more CSV records available"));
        }

        try {
            iterator.remove();
            fail("Iterator remove is not supported");
        } catch (final UnsupportedOperationException expected) {
            assertTrue(true);
        }

        parser.close();
    }

    @Test
    public void closedParserMakesIteratorEmptyAndNextFails() throws IOException {
        final CSVParser parser = CSVParser.parse("a", CSVFormat.DEFAULT);
        final Iterator<CSVRecord> iterator = parser.iterator();

        assertFalse(parser.isClosed());
        parser.close();

        assertTrue(parser.isClosed());
        assertFalse(iterator.hasNext());

        try {
            iterator.next();
            fail("next() on an iterator backed by a closed parser must fail");
        } catch (final NoSuchElementException expected) {
            assertTrue(expected.getMessage().contains("closed"));
        }
    }

    @Test
    public void parseFileAndUrlUseProvidedCharset() throws Exception {
        final File file = File.createTempFile("csv-parser", ".csv");
        try {
            java.io.FileOutputStream output = new java.io.FileOutputStream(file);
            try {
                output.write("café\n".getBytes(Charset.forName("UTF-8")));
            } finally {
                output.close();
            }

            final CSVParser fileParser = CSVParser.parse(file, Charset.forName("UTF-8"), CSVFormat.DEFAULT);
            assertEquals("café", fileParser.iterator().next().get(0));
            fileParser.close();

            final CSVParser urlParser = CSVParser.parse(file.toURI().toURL(), Charset.forName("UTF-8"),
                    CSVFormat.DEFAULT);
            assertEquals("café", urlParser.iterator().next().get(0));
            urlParser.close();
        } finally {
            file.delete();
        }
    }

    @Test
    public void nullRequiredArgumentsAreRejected() throws IOException {
        try {
            CSVParser.parse((String) null, CSVFormat.DEFAULT);
            fail("A null input string must be rejected");
        } catch (final IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("string"));
        }

        try {
            CSVParser.parse("value", null);
            fail("A null format must be rejected");
        } catch (final IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("format"));
        }

        try {
            new CSVParser(null, CSVFormat.DEFAULT);
            fail("A null reader must be rejected");
        } catch (final IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("reader"));
        }

        try {
            CSVParser.parse((File) null, Charset.forName("UTF-8"), CSVFormat.DEFAULT);
            fail("A null file must be rejected");
        } catch (final IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("file"));
        }
    }
}
