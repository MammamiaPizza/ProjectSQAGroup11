```java
package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
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
    public void testGetHeaderMapWithoutConfiguredHeaderIsEmptyAndModifiableCopy() throws Exception {
        final CSVParser parser = CSVParser.parse("a,b\nc,d\n", CSVFormat.DEFAULT);
        try {
            final Map<String, Integer> headerMap = parser.getHeaderMap();

            assertTrue("A parser without a configured header must expose an empty header map", headerMap.isEmpty());

            headerMap.put("locallyAdded", Integer.valueOf(99));
            assertTrue("getHeaderMap must return a copy rather than parser state",
                    parser.getHeaderMap().isEmpty());
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
                assertTrue(true);
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

Test coverage targets:

- `testGetHeaderMapWithoutConfiguredHeaderIsEmptyAndModifiableCopy`: Targets CSV-100. A parser created without headers must return an empty map instead of throwing `NullPointerException`; also verifies the returned map is defensive.
- `testAutomaticallyReadHeaderMapsNamesAndExcludesHeaderRecord`: Covers automatic header initialization and normal record parsing.
- `testExplicitHeaderCanSkipInputHeaderRecord`: Covers explicit-header initialization and the `skipHeaderRecord` branch.
- `testNullStringIsConvertedCaseInsensitively`: Covers null-string conversion and normal non-null values.
- `testIteratorCachesHasNextAndRejectsOperationsAfterExhaustion`: Covers iterator caching, normal iteration, end-of-input behavior, and unsupported `remove`.
- `testGetRecordsStartsAtCurrentParserPosition`: Verifies `getRecords()` consumes only records remaining after prior iteration.
- `testCloseMarksParserClosedAndIteratorCannotReadAfterClose`: Covers closed-parser iterator branches.
- `testStringFactoryRejectsNullArguments` and `testConstructorRejectsNullReaderAndNullFormat`: Cover required null argument validation.
- `testFileAndUrlFactoriesParseLocalCsvData`: Covers successful `File` and `URL` factory methods without external network dependency.
- `testUrlFactoryRejectsNullRequiredArguments`: Covers null validation branches for the URL factory method.