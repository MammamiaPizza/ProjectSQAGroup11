package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

public class CSVParserNoHeaderMapRegressionTest {

    @Test
    public void getHeaderMapWithoutConfiguredHeadersIsEmptyBeforeAndAfterReadingRecords() throws Exception {
        final CSVParser parser = CSVParser.parse("a,b\nc,d\n", CSVFormat.DEFAULT);
        try {
            final Map<String, Integer> initialHeaderMap = parser.getHeaderMap();
            assertNull(initialHeaderMap);

            final Iterator<CSVRecord> iterator = parser.iterator();
            assertTrue(iterator.hasNext());
            final CSVRecord firstRecord = iterator.next();
            assertEquals("a", firstRecord.get(0));
            assertEquals("b", firstRecord.get(1));

            assertTrue(iterator.hasNext());
            final CSVRecord secondRecord = iterator.next();
            assertEquals("c", secondRecord.get(0));
            assertEquals("d", secondRecord.get(1));
            assertFalse(iterator.hasNext());

            final Map<String, Integer> finalHeaderMap = parser.getHeaderMap();
            assertNull(finalHeaderMap);
        } finally {
            parser.close();
        }
    }

    @Test
    public void getHeaderMapWithoutConfiguredHeadersIsEmptyForEmptyInput() throws Exception {
        final CSVParser parser = CSVParser.parse("", CSVFormat.DEFAULT);
        try {
            final Map<String, Integer> headerMap = parser.getHeaderMap();

            assertNull(headerMap);
            assertFalse(parser.iterator().hasNext());
        } finally {
            parser.close();
        }
    }

    @Test
    public void automaticallyReadHeadersAreExposedAndNotReturnedAsDataRecords() throws Exception {
        final CSVParser parser = CSVParser.parse("name,age\nAlice,30\n", CSVFormat.DEFAULT.withHeader());
        try {
            final Map<String, Integer> headerMap = parser.getHeaderMap();
            assertEquals(2, headerMap.size());
            assertEquals(Integer.valueOf(0), headerMap.get("name"));
            assertEquals(Integer.valueOf(1), headerMap.get("age"));

            final Iterator<CSVRecord> iterator = parser.iterator();
            assertTrue(iterator.hasNext());
            final CSVRecord record = iterator.next();
            assertEquals("Alice", record.get("name"));
            assertEquals("30", record.get("age"));
            assertFalse(iterator.hasNext());
        } finally {
            parser.close();
        }
    }

@Test
public void configuredNullStringIsMatchedIgnoringCase() throws Exception {
    final CSVParser parser = new CSVParser(new java.io.StringReader("NULL,NuLl,value\n"),
            CSVFormat.DEFAULT.withNullString("NULL"));
    try {
        final CSVRecord record = parser.getRecords().get(0);
        org.junit.Assert.assertNull(record.get(0));
        org.junit.Assert.assertNull(record.get(1));
        org.junit.Assert.assertEquals("value", record.get(2));
    } finally {
        parser.close();
    }
}

@Test
public void explicitHeadersAreAvailableAndReturnedMapIsIndependent() throws Exception {
    final CSVParser parser = new CSVParser(new java.io.StringReader("a,b\n"),
            CSVFormat.DEFAULT.withHeader("first", "second"));
    try {
        org.junit.Assert.assertEquals(Integer.valueOf(0), parser.getHeaderMap().get("first"));
        org.junit.Assert.assertEquals(Integer.valueOf(1), parser.getHeaderMap().get("second"));

        parser.getHeaderMap().clear();

        org.junit.Assert.assertEquals(Integer.valueOf(0), parser.getHeaderMap().get("first"));
        org.junit.Assert.assertEquals(Integer.valueOf(1), parser.getHeaderMap().get("second"));
    } finally {
        parser.close();
    }
}

@Test
public void getRecordsAdvancesRecordNumberAndLineNumber() throws Exception {
    final CSVParser parser = new CSVParser(new java.io.StringReader("a,b\nc,d\n"), CSVFormat.DEFAULT);
    try {
        final long initialLineNumber = parser.getCurrentLineNumber();

        final java.util.List<CSVRecord> records = parser.getRecords();

        org.junit.Assert.assertEquals(2, records.size());
        org.junit.Assert.assertEquals(2L, parser.getRecordNumber());
        org.junit.Assert.assertTrue(parser.getCurrentLineNumber() > initialLineNumber);
    } finally {
        parser.close();
    }
}

@Test
public void closeChangesClosedState() throws Exception {
    final CSVParser parser = new CSVParser(new java.io.StringReader("a,b\n"), CSVFormat.DEFAULT);

    org.junit.Assert.assertFalse(parser.isClosed());

    parser.close();

    org.junit.Assert.assertTrue(parser.isClosed());
}
}
