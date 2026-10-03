package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.junit.Test;

public class CSVParserGeneratedTest {

    @Test
    public void parsesRecordsCreatedFromStringFactory() throws Exception {
        CSVParser parser = CSVParser.parse("first,last\nAda,Lovelace\nAlan,Turing", CSVFormat.DEFAULT);
        try {
            List<CSVRecord> records = parser.getRecords();

            assertEquals(3, records.size());
            assertEquals("first", records.get(0).get(0));
            assertEquals("Lovelace", records.get(1).get(1));
            assertEquals("Alan", records.get(2).get(0));
            assertEquals(3L, parser.getRecordNumber());
        } finally {
            parser.close();
        }
    }

    @Test
    public void parsesRecordsCreatedFromFileFactory() throws Exception {
        File file = File.createTempFile("csv-parser", ".csv");
        FileWriter writer = new FileWriter(file);
        writer.write("id,name\n1,Ada");
        writer.close();

        CSVParser parser = null;
        try {
            parser = CSVParser.parse(file, CSVFormat.DEFAULT);
            List<CSVRecord> records = parser.getRecords();

            assertEquals(2, records.size());
            assertEquals("id", records.get(0).get(0));
            assertEquals("Ada", records.get(1).get(1));
        } finally {
            if (parser != null) {
                parser.close();
            }
            file.delete();
        }
    }

    @Test
    public void parsesRecordsCreatedFromUrlFactory() throws Exception {
        File file = File.createTempFile("csv-parser-url", ".csv");
        FileWriter writer = new FileWriter(file);
        writer.write("id,name\n2,Grace");
        writer.close();

        CSVParser parser = null;
        try {
            URL url = file.toURI().toURL();
            parser = CSVParser.parse(url, Charset.forName("UTF-8"), CSVFormat.DEFAULT);

            List<CSVRecord> records = parser.getRecords();
            assertEquals(2, records.size());
            assertEquals("2", records.get(1).get(0));
            assertEquals("Grace", records.get(1).get(1));
        } finally {
            if (parser != null) {
                parser.close();
            }
            file.delete();
        }
    }

    @Test
    public void parsesHeaderFromFirstRecordAndSupportsNamedLookup() throws Exception {
        CSVFormat format = CSVFormat.DEFAULT.withHeader();
        CSVParser parser = new CSVParser(new StringReader("id,name\n7,Ada"), format);
        try {
            Map<String, Integer> headerMap = parser.getHeaderMap();
            assertEquals(2, headerMap.size());
            assertEquals(Integer.valueOf(0), headerMap.get("id"));
            assertEquals(Integer.valueOf(1), headerMap.get("name"));

            Iterator<CSVRecord> iterator = parser.iterator();
            assertTrue(iterator.hasNext());

            CSVRecord record = iterator.next();
            assertEquals("7", record.get("id"));
            assertEquals("Ada", record.get("name"));
        } finally {
            parser.close();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void rejectsDuplicateNamesInHeaderReadFromInput() throws Exception {
        new CSVParser(new StringReader("id,id\n1,2"), CSVFormat.DEFAULT.withHeader());
    }

    @Test(expected = IllegalStateException.class)
    public void rejectsDuplicateNamesInExplicitHeaderConfiguration() throws Exception {
        new CSVParser(new StringReader("1,2"), CSVFormat.DEFAULT.withHeader("id", "id"));
    }

    @Test
    public void headerReadFromEmptyInputProducesAnEmptyHeaderMap() throws Exception {
        CSVParser parser = new CSVParser(new StringReader(""), CSVFormat.DEFAULT.withHeader());
        try {
            Map<String, Integer> headerMap = parser.getHeaderMap();

            assertTrue(headerMap.isEmpty());
            assertEquals(0L, parser.getRecordNumber());
            assertFalse(parser.iterator().hasNext());
        } finally {
            parser.close();
        }
    }

    @Test
    public void explicitHeaderCanSkipTheFirstInputRecord() throws Exception {
        CSVFormat format = CSVFormat.DEFAULT.withHeader("id", "name").withSkipHeaderRecord();
        CSVParser parser = new CSVParser(new StringReader("ignored,ignored\n7,Ada"), format);
        try {
            Map<String, Integer> headerMap = parser.getHeaderMap();
            assertEquals(Integer.valueOf(0), headerMap.get("id"));
            assertEquals(Integer.valueOf(1), headerMap.get("name"));

            List<CSVRecord> records = parser.getRecords();
            assertEquals(1, records.size());
            assertEquals("7", records.get(0).get("id"));
            assertEquals("Ada", records.get(0).get("name"));
            assertEquals(2L, parser.getRecordNumber());
        } finally {
            parser.close();
        }
    }

    @Test
    public void headerMapIsReturnedAsIndependentCopy() throws Exception {
        CSVParser parser = new CSVParser(new StringReader("1,Ada"),
                CSVFormat.DEFAULT.withHeader("id", "name"));
        try {
            Map<String, Integer> firstMap = parser.getHeaderMap();
            firstMap.put("extra", Integer.valueOf(99));

            Map<String, Integer> secondMap = parser.getHeaderMap();
            assertFalse(secondMap.containsKey("extra"));
            assertEquals(Integer.valueOf(0), secondMap.get("id"));
            assertEquals(Integer.valueOf(1), secondMap.get("name"));
        } finally {
            parser.close();
        }
    }

    @Test
    public void returnsNullHeaderMapWhenFormatDoesNotDefineHeader() throws Exception {
        CSVParser parser = new CSVParser(new StringReader("a,b"), CSVFormat.DEFAULT);
        try {
            assertNull(parser.getHeaderMap());
        } finally {
            parser.close();
        }
    }

    @Test
    public void getRecordsStartsAtCurrentParserPositionAndAddsToProvidedCollection() throws Exception {
        CSVParser parser = new CSVParser(new StringReader("a,b\nc,d\ne,f"), CSVFormat.DEFAULT);
        try {
            CSVRecord first = parser.iterator().next();
            assertEquals("a", first.get(0));

            List<CSVRecord> destination = new ArrayList<CSVRecord>();
            List<CSVRecord> returned = parser.getRecords(destination);

            assertSame(destination, returned);
            assertEquals(2, destination.size());
            assertEquals("c", destination.get(0).get(0));
            assertEquals("f", destination.get(1).get(1));
            assertEquals(3L, parser.getRecordNumber());
        } finally {
            parser.close();
        }
    }

    @Test
    public void convertsConfiguredNullStringCaseInsensitively() throws Exception {
        CSVFormat format = CSVFormat.DEFAULT.withNullString("NULL");
        CSVParser parser = new CSVParser(new StringReader("NULL,null,value"), format);
        try {
            CSVRecord record = parser.iterator().next();

            assertNull(record.get(0));
            assertNull(record.get(1));
            assertEquals("value", record.get(2));
        } finally {
            parser.close();
        }
    }

    @Test
    public void commentsBeforeARecordAreAttachedToThatRecord() throws Exception {
        CSVFormat format = CSVFormat.DEFAULT.withCommentMarker('#');
        CSVParser parser = new CSVParser(new StringReader("# first comment\n# second comment\na,b"), format);
        try {
            CSVRecord record = parser.iterator().next();

            assertEquals("a", record.get(0));
            assertEquals("b", record.get(1));
            assertEquals("first comment\nsecond comment", record.getComment());
        } finally {
            parser.close();
        }
    }

    @Test
    public void currentLineNumberTracksConsumedLineTerminators() throws Exception {
        CSVParser parser = new CSVParser(new StringReader("first\nsecond"), CSVFormat.DEFAULT);
        try {
            CSVRecord first = parser.iterator().next();

            assertEquals("first", first.get(0));
            assertEquals(1L, parser.getCurrentLineNumber());
        } finally {
            parser.close();
        }
    }

    @Test
    public void iteratorCachesLookaheadAndReportsEndOfInput() throws Exception {
        CSVParser parser = new CSVParser(new StringReader("one\ntwo"), CSVFormat.DEFAULT);
        try {
            Iterator<CSVRecord> iterator = parser.iterator();

            assertTrue(iterator.hasNext());
            assertTrue(iterator.hasNext());
            assertEquals("one", iterator.next().get(0));

            assertEquals("two", iterator.next().get(0));
            assertFalse(iterator.hasNext());

            try {
                iterator.next();
                fail("next() after the final record must throw NoSuchElementException");
            } catch (NoSuchElementException expected) {
                assertTrue(expected.getMessage().contains("No more CSV records"));
            }

            try {
                iterator.remove();
                fail("Iterator.remove() must not be supported");
            } catch (UnsupportedOperationException expected) {
                assertTrue(true);
            }
        } finally {
            parser.close();
        }
    }

    @Test
    public void closedParserMakesIteratorEmptyAndNextThrows() throws Exception {
        CSVParser parser = new CSVParser(new StringReader("a,b"), CSVFormat.DEFAULT);
        Iterator<CSVRecord> iterator = parser.iterator();

        parser.close();

        assertTrue(parser.isClosed());
        assertFalse(iterator.hasNext());

        try {
            iterator.next();
            fail("next() on an iterator backed by a closed parser must throw");
        } catch (NoSuchElementException expected) {
            assertTrue(expected.getMessage().contains("closed"));
        }
    }

    @Test
    public void iteratorWrapsReadIOExceptionInRuntimeException() throws Exception {
        Reader failingReader = new Reader() {
            @Override
            public int read(char[] cbuf, int off, int len) throws IOException {
                throw new IOException("simulated read failure");
            }

            @Override
            public void close() {
                // Nothing to close.
            }
        };

        CSVParser parser = new CSVParser(failingReader, CSVFormat.DEFAULT);
        try {
            try {
                parser.iterator().hasNext();
                fail("I/O failures during iteration must be wrapped");
            } catch (RuntimeException expected) {
                assertTrue(expected.getCause() instanceof IOException);
            }
        } finally {
            parser.close();
        }
    }

    @Test
    public void emptyInputProducesNoRecords() throws Exception {
        CSVParser parser = new CSVParser(new StringReader(""), CSVFormat.DEFAULT);
        try {
            List<CSVRecord> records = parser.getRecords();

            assertTrue(records.isEmpty());
            assertEquals(0L, parser.getRecordNumber());
        } finally {
            parser.close();
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNullReader() throws Exception {
        new CSVParser((Reader) null, CSVFormat.DEFAULT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNullFormat() throws Exception {
        new CSVParser(new StringReader("a,b"), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void stringFactoryRejectsNullString() throws Exception {
        CSVParser.parse((String) null, CSVFormat.DEFAULT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void fileFactoryRejectsNullFile() throws Exception {
        CSVParser.parse((File) null, CSVFormat.DEFAULT);
    }

    @Test(expected = IllegalArgumentException.class)
    public void urlFactoryRejectsNullUrl() throws Exception {
        CSVParser.parse((URL) null, Charset.forName("UTF-8"), CSVFormat.DEFAULT);
    }
}
