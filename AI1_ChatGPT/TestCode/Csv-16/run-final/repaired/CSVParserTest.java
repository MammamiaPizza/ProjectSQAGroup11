package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.Test;

public class CSVParserTest {

    @Test
    public void parsesRecordsAndReportsRecordAndLineNumbers() throws IOException {
        final CSVParser parser = CSVParser.parse("a,b\nc,d\n", CSVFormat.DEFAULT);

        assertEquals(0L, parser.getRecordNumber());
        assertEquals(0L, parser.getCurrentLineNumber());

        final Iterator<CSVRecord> iterator = parser.iterator();
        assertTrue(iterator.hasNext());

        final CSVRecord first = iterator.next();
        assertEquals("a", first.get(0));
        assertEquals("b", first.get(1));
        assertEquals(1L, first.getRecordNumber());
        assertEquals(1L, parser.getRecordNumber());
        assertEquals(1L, parser.getCurrentLineNumber());

        final CSVRecord second = iterator.next();
        assertEquals("c", second.get(0));
        assertEquals("d", second.get(1));
        assertEquals(2L, second.getRecordNumber());
        assertEquals(2L, parser.getRecordNumber());

        assertFalse(iterator.hasNext());
        assertEquals(2L, parser.getCurrentLineNumber());
    }

    @Test
    public void iteratorMaintainsSequenceWhenHasNextAndNextAreInterleaved() throws IOException {
        final CSVParser parser = CSVParser.parse("1\n2\n3\n", CSVFormat.DEFAULT);
        final Iterator<CSVRecord> iterator = parser.iterator();

        assertTrue(iterator.hasNext());
        assertEquals("1", iterator.next().get(0));

        assertTrue(iterator.hasNext());
        assertTrue(iterator.hasNext());
        assertEquals("2", iterator.next().get(0));

        assertTrue(iterator.hasNext());
        assertEquals("3", iterator.next().get(0));
        assertFalse(iterator.hasNext());
    }

    @Test
    public void getRecordsStartsAtCurrentParserPosition() throws IOException {
        final CSVParser parser = CSVParser.parse("first\nsecond\nthird\n", CSVFormat.DEFAULT);

        final CSVRecord first = parser.iterator().next();
        assertEquals("first", first.get(0));

        final List<CSVRecord> remaining = parser.getRecords();
        assertEquals(2, remaining.size());
        assertEquals("second", remaining.get(0).get(0));
        assertEquals("third", remaining.get(1).get(0));
        assertEquals(3L, parser.getRecordNumber());
    }

    @Test
    public void headerIsReadFromInputAndHeaderMapIsDefensiveCopy() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT.withHeader();
        final CSVParser parser = CSVParser.parse("name,age\nAlice,30\n", format);

        final Map<String, Integer> headerMap = parser.getHeaderMap();
        assertEquals(2, headerMap.size());
        assertEquals(Integer.valueOf(0), headerMap.get("name"));
        assertEquals(Integer.valueOf(1), headerMap.get("age"));

        headerMap.put("changed", Integer.valueOf(99));
        assertFalse(parser.getHeaderMap().containsKey("changed"));

        final CSVRecord record = parser.iterator().next();
        assertEquals("Alice", record.get("name"));
        assertEquals("30", record.get("age"));
        assertEquals(1L, record.getRecordNumber());
    }

    @Test
    public void explicitHeaderCanSkipInputHeaderAndSupportsCaseInsensitiveLookup() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT
                .withHeader("Name", "Age")
                .withSkipHeaderRecord()
                .withIgnoreHeaderCase();
        final CSVParser parser = CSVParser.parse("ignored,ignored\nBob,40\n", format);

        final CSVRecord record = parser.iterator().next();
        assertEquals("Bob", record.get("name"));
        assertEquals("40", record.get("AGE"));
        assertEquals(Integer.valueOf(0), parser.getHeaderMap().get("NAME"));
        assertEquals(1L, record.getRecordNumber());
    }

    @Test
    public void parsesCommentsNullValuesTrimmedValuesAndTrailingDelimiter() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT
                .withCommentMarker('#')
                .withTrim()
                .withNullString("NULL")
                .withTrailingDelimiter();
        final CSVParser parser = CSVParser.parse("# source comment\n  value  , NULL ,\n", format);

        final CSVRecord record = parser.iterator().next();
        assertEquals("source comment", record.getComment());
        assertEquals(2, record.size());
        assertEquals("value", record.get(0));
        assertNull(record.get(1));
        assertFalse(parser.iterator().hasNext());
    }

    @Test
    public void detectsDuplicateHeaderNamesUnlessMissingNamesAreAllowed() throws IOException {
        try {
            CSVParser.parse("name,name\nvalue,value\n", CSVFormat.DEFAULT.withHeader());
            fail("Duplicate non-empty header names must be rejected");
        } catch (final IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("duplicate name"));
        }

        final CSVParser parser = CSVParser.parse(
                ",\nleft,right\n",
                CSVFormat.DEFAULT.withHeader().withAllowMissingColumnNames());

        assertEquals(Integer.valueOf(1), parser.getHeaderMap().get(""));
        assertEquals("left", parser.iterator().next().get(0));
    }

    @Test
    public void inputStreamFactoryParsesUsingProvidedCharset() throws IOException {
        final byte[] bytes = "café,42\n".getBytes(StandardCharsets.UTF_8);
        final CSVParser parser = CSVParser.parse(
                new ByteArrayInputStream(bytes), StandardCharsets.UTF_8, CSVFormat.DEFAULT);

        final CSVRecord record = parser.iterator().next();
        assertEquals("café", record.get(0));
        assertEquals("42", record.get(1));
    }

    @Test
    public void closeMakesIteratorUnavailableAndMarksParserClosed() throws IOException {
        final CSVParser parser = new CSVParser(new StringReader("a\n"), CSVFormat.DEFAULT);
        final Iterator<CSVRecord> iterator = parser.iterator();

        parser.close();

        assertTrue(parser.isClosed());
        assertFalse(iterator.hasNext());

        try {
            iterator.next();
            fail("next() on a closed parser must fail");
        } catch (final NoSuchElementException expected) {
            assertTrue(expected.getMessage().contains("closed"));
        }
    }

    @Test
    public void iteratorConvertsReadFailureToIllegalStateException() throws IOException {
        final Reader failingReader = new Reader() {
            @Override
            public int read(final char[] buffer, final int offset, final int length) throws IOException {
                throw new IOException("forced read failure");
            }

            @Override
            public void close() {
                // Nothing to close.
            }
        };

        final CSVParser parser = new CSVParser(failingReader, CSVFormat.DEFAULT);

        try {
            parser.iterator().hasNext();
            fail("I/O errors during iterator traversal must be wrapped");
        } catch (final IllegalStateException expected) {
            assertTrue(expected.getMessage().contains("IOException reading next record"));
            assertTrue(expected.getCause() instanceof IOException);
        }
    }

    @Test
    public void rejectsNullRequiredArguments() throws IOException {
        try {
            CSVParser.parse((String) null, CSVFormat.DEFAULT);
            fail("A null input string must be rejected");
        } catch (final IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("string"));
        }

        try {
            new CSVParser((Reader) null, CSVFormat.DEFAULT);
            fail("A null reader must be rejected");
        } catch (final IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("reader"));
        }

        try {
            CSVParser.parse("value", null);
            fail("A null format must be rejected");
        } catch (final IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("format"));
        }
    }

    @Test
    public void exposesFirstEncounteredEndOfLine() throws IOException {
        final CSVParser parser = CSVParser.parse("one\r\ntwo\n", CSVFormat.DEFAULT);

        assertNull(parser.getFirstEndOfLine());
        assertEquals("one", parser.iterator().next().get(0));
        assertEquals("\r\n", parser.getFirstEndOfLine());
    }
}
