```java
package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.Test;

public class CSVParserGeneratedTest {

    @Test
    public void testGetHeaderMapWithoutConfiguredHeaderReturnsNull() throws Exception {
        final CSVParser parser = CSVParser.parse("a,b\nc,d\n", CSVFormat.DEFAULT);
        try {
            assertNull("A parser without a configured header has no header map",
                    parser.getHeaderMap());
        } finally {
            parser.close();
        }
    }

    @Test
    public void testAutomaticallyReadHeaderMapsNamesAndExcludesHeaderRecord() throws Exception {
        final CSVFormat format = CSVFormat.DEFAULT.withHeader();
        final CSVParser parser = CSVParser.parse("name,age\nAlice,30\nBob,41\n", format);
        try {
            final Map<String, Integer> headerMap = parser.getHeaderMap();
            assertEquals(Integer.valueOf(0), headerMap.get("name"));
            assertEquals(Integer.valueOf(1), headerMap.get("age"));

            final List<CSVRecord> records = parser.getRecords();
            assertEquals(2, records.size());
            assertEquals("Alice", records.get(0).get(0));
            assertEquals("30", records.get(0).get(1));
            assertEquals("Bob", records.get(1).get(0));
            assertEquals("41", records.get(1).get(1));
        } finally {
            parser.close();
        }
    }

    @Test
    public void testExplicitHeaderCanSkipInputHeaderRecord() throws Exception {
        final CSVFormat format = CSVFormat.DEFAULT
                .withHeader("first", "second")
                .withSkipHeaderRecord(true);
        final CSVParser parser = CSVParser.parse("discard,discard\nleft,right\n", format);
        try {
            assertEquals(Integer.valueOf(0), parser.getHeaderMap().get("first"));
            assertEquals(Integer.valueOf(1), parser.getHeaderMap().get("second"));

            final List<CSVRecord> records = parser.getRecords();
            assertEquals(1, records.size());
            assertEquals("left", records.get(0).get(0));
            assertEquals("right", records.get(0).get(1));
        } finally {
            parser.close();
        }
    }

    @Test
    public void testExplicitHeaderWithoutSkippingRetainsFirstInputRecordAndReturnsDefensiveMapCopy()
            throws Exception {
        final CSVFormat format = CSVFormat.DEFAULT.withHeader("first", "second");
        final CSVParser parser = CSVParser.parse("left,right\n", format);
        try {
            final Map<String, Integer> headerMap = parser.getHeaderMap();
            assertEquals(Integer.valueOf(0), headerMap.get("first"));
            assertEquals(Integer.valueOf(1), headerMap.get("second"));

            headerMap.clear();
            assertEquals("The returned header map must be a copy",
                    Integer.valueOf(0), parser.getHeaderMap().get("first"));

            final List<CSVRecord> records = parser.getRecords();
            assertEquals(1, records.size());
            assertEquals("left", records.get(0).get(0));
            assertEquals("right", records.get(0).get(1));
        } finally {
            parser.close();
        }
    }

    @Test
    public void testAutomaticHeaderOnEmptyInputReturnsEmptyHeaderMapAndNoRecords() throws Exception {
        final CSVParser parser = CSVParser.parse("", CSVFormat.DEFAULT.withHeader());
        try {
            final Map<String, Integer> headerMap = parser.getHeaderMap();
            assertTrue("A configured automatic header creates a header map", headerMap != null);
            assertTrue(headerMap.isEmpty());
            assertTrue(parser.getRecords().isEmpty());
        } finally {
            parser.close();
        }
    }

    @Test
    public void testNullStringIsConvertedCaseInsensitively() throws Exception {
        final CSVFormat format = CSVFormat.DEFAULT.withNullString("NULL");
        final CSVParser parser = CSVParser.parse("NULL,null,value\n", format);
        try {
            final List<CSVRecord> records = parser.getRecords();

            assertEquals(1, records.size());
            assertNull(records.get(0).get(0));
            assertNull(records.get(0).get(1));
            assertEquals("value", records.get(0).get(2));
        } finally {
            parser.close();
        }
    }

    @Test
    public void testFinalRecordWithoutRecordSeparatorIsReturned() throws Exception {
        final CSVParser parser = CSVParser.parse("first,second", CSVFormat.DEFAULT);
        try {
            final List<CSVRecord> records = parser.getRecords();

            assertEquals(1, records.size());
            assertEquals("first", records.get(0).get(0));
            assertEquals("second", records.get(0).get(1));
            assertEquals(1L, parser.getRecordNumber());
        } finally {
            parser.close();
        }
    }

    @Test
    public void testCommentLinesAreAttachedToFollowingRecordAndNotReturnedAsData() throws Exception {
        final CSVFormat format = CSVFormat.DEFAULT.withCommentMarker('#');
        final CSVParser parser = CSVParser.parse("# first comment\n# second comment\nvalue\n", format);
        try {
            final List<CSVRecord> records = parser.getRecords();

            assertEquals(1, records.size());
            assertEquals("value", records.get(0).get(0));
            assertEquals("first comment\nsecond comment", records.get(0).getComment());
        } finally {
            parser.close();
        }
    }

    @Test
    public void testMalformedQuotedInputCausesIOException() throws Exception {
        final CSVParser parser = CSVParser.parse("\"unterminated", CSVFormat.DEFAULT);
        try {
            try {
                parser.getRecords();
                fail("An unterminated quoted value is an invalid CSV parse sequence");
            } catch (IOException expected) {
                // Expected parse failure.
            }
        } finally {
            parser.close();
        }
    }

    @Test
    public void testIteratorCachesHasNextAndRejectsOperationsAfterExhaustion() throws Exception {
        final CSVParser parser = CSVParser.parse("one\ntwo\n", CSVFormat.DEFAULT);
        try {
            final Iterator<CSVRecord> iterator = parser.iterator();

            assertTrue(iterator.hasNext());
            assertTrue("Repeated hasNext must not consume the cached record", iterator.hasNext());
            assertEquals("one", iterator.next().get(0));

            assertTrue(iterator.hasNext());
            assertEquals("two", iterator.next().get(0));
            assertFalse(iterator.hasNext());

            try {
                iterator.next();
                fail("next after the final record must throw NoSuchElementException");
            } catch (NoSuchElementException expected) {
                assertEquals("No more CSV records available", expected.getMessage());
            }

            try {
                iterator.remove();
                fail("CSVParser iterators do not support remove");
            } catch (UnsupportedOperationException expected) {
                // Expected.
            }
        } finally {
            parser.close();
        }
    }

    @Test
    public void testIteratorWrapsIOExceptionAsRuntimeException() throws Exception {
        final IOException readFailure = new IOException("simulated read failure");
        final Reader failingReader = new Reader() {
            @Override
            public int read(final char[] buffer, final int offset, final int length) throws IOException {
                throw readFailure;
            }

            @Override
            public void close() throws IOException {
                // Nothing to close.
            }
        };

        final CSVParser parser = new CSVParser(failingReader, CSVFormat.DEFAULT);
        try {
            try {
                parser.iterator().hasNext();
                fail("Iterator I/O failures must be wrapped in a RuntimeException");
            } catch (RuntimeException expected) {
                assertEquals(readFailure, expected.getCause());
            }
        } finally {
            parser.close();
        }
    }

    @Test
    public void testGetRecordsStartsAtCurrentParserPosition() throws Exception {
        final CSVParser parser = CSVParser.parse("first\nsecond\nthird\n", CSVFormat.DEFAULT);
        try {
            final Iterator<CSVRecord> iterator = parser.iterator();
            assertEquals("first", iterator.next().get(0));

            final List<CSVRecord> remainingRecords = parser.getRecords();
            assertEquals(2, remainingRecords.size());
            assertEquals("second", remainingRecords.get(0).get(0));
            assertEquals("third", remainingRecords.get(1).get(0));
        } finally {
            parser.close();
        }
    }

    @Test
    public void testCloseMarksParserClosedAndIteratorCannotReadAfterClose() throws Exception {
        final CSVParser parser = CSVParser.parse("value\n", CSVFormat.DEFAULT);
        parser.close();

        assertTrue(parser.isClosed());

        final Iterator<CSVRecord> iterator = parser.iterator();
        assertFalse(iterator.hasNext());
        try {
            iterator.next();
            fail("An iterator must reject next after its parser has been closed");
        } catch (NoSuchElementException expected) {
            assertEquals("CSVParser has been closed", expected.getMessage());
        }
    }

    @Test
    public void testStringFactoryRejectsNullArguments() throws Exception {
        try {
            CSVParser.parse((String) null, CSVFormat.DEFAULT);
            fail("A null CSV string must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("string"));
        }

        try {
            CSVParser.parse("value", null);
            fail("A null format must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("format"));
        }
    }

    @Test
    public void testFileFactoryRejectsNullArguments() throws Exception {
        try {
            CSVParser.parse((File) null, CSVFormat.DEFAULT);
            fail("A null file must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("file"));
        }

        try {
            CSVParser.parse(new File("does-not-need-to-exist.csv"), null);
            fail("A null format must be rejected before opening the file");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("format"));
        }
    }

    @Test
    public void testConstructorRejectsNullReaderAndNullFormat() throws Exception {
        try {
            new CSVParser(null, CSVFormat.DEFAULT);
            fail("A null reader must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("reader"));
        }

        try {
            new CSVParser(new StringReader("value"), null);
            fail("A null format must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("format"));
        }
    }

    @Test
    public void testFileAndUrlFactoriesParseLocalCsvData() throws Exception {
        final File file = File.createTempFile("csv-parser-", ".csv");
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write("alpha,beta\n");
            writer.close();
            writer = null;

            CSVParser parser = CSVParser.parse(file, CSVFormat.DEFAULT);
            try {
                assertEquals("alpha", parser.getRecords().get(0).get(0));
            } finally {
                parser.close();
            }

            final URL url = file.toURI().toURL();
            parser = CSVParser.parse(url, Charset.forName("UTF-8"), CSVFormat.DEFAULT);
            try {
                assertEquals("beta", parser.getRecords().get(0).get(1));
            } finally {
                parser.close();
            }
        } finally {
            if (writer != null) {
                writer.close();
            }
            file.delete();
        }
    }

    @Test
    public void testUrlFactoryRejectsNullRequiredArguments() throws Exception {
        final URL url = new File(".").toURI().toURL();

        try {
            CSVParser.parse(null, Charset.forName("UTF-8"), CSVFormat.DEFAULT);
            fail("A null URL must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("url"));
        }

        try {
            CSVParser.parse(url, null, CSVFormat.DEFAULT);
            fail("A null charset must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("charset"));
        }

        try {
            CSVParser.parse(url, Charset.forName("UTF-8"), null);
            fail("A null format must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("format"));
        }
    }
}
```

New test coverage and fault-detection targets:

- `testExplicitHeaderWithoutSkippingRetainsFirstInputRecordAndReturnsDefensiveMapCopy`
  - Covers `initializeHeader()` with an explicitly supplied header and `skipHeaderRecord == false`.
  - Verifies the documented defensive-copy behavior of `getHeaderMap()`.

- `testAutomaticHeaderOnEmptyInputReturnsEmptyHeaderMapAndNoRecords`
  - Covers automatic-header initialization when the input contains no first record.
  - Exercises the `record == null` and `header == null` paths in `initializeHeader()`.

- `testFinalRecordWithoutRecordSeparatorIsReturned`
  - Covers the `EOF` token path where `reusableToken.isReady` is true.
  - Verifies a final record is retained when no trailing newline exists.

- `testCommentLinesAreAttachedToFollowingRecordAndNotReturnedAsData`
  - Covers the `COMMENT` token branch in `nextRecord()`, including accumulation of multiple comment lines.
  - Verifies comments do not become ordinary CSV records or fields.

- `testMalformedQuotedInputCausesIOException`
  - Covers exceptional parse-error behavior required by `getRecords()` / `nextRecord()` when the lexer detects invalid CSV input.

- `testIteratorWrapsIOExceptionAsRuntimeException`
  - Covers the iterator’s `getNextRecord()` exception-wrapping branch.
  - Verifies the documented behavior that iteration wraps `IOException` in `RuntimeException`.

- `testFileFactoryRejectsNullArguments`
  - Covers both null-validation branches of `CSVParser.parse(File, CSVFormat)`.

- The retained `testGetHeaderMapWithoutConfiguredHeaderReturnsNull` is the primary CSV-100 regression test.
  - In the supplied buggy implementation, `getHeaderMap()` executes `new LinkedHashMap<String, Integer>(this.headerMap)` even when `headerMap` is `null`, causing the reported `NullPointerException`.
  - The expected behavior is to return `null` when no header was configured.