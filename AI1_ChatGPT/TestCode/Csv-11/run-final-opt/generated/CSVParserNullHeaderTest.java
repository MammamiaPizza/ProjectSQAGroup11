package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Map;

import org.junit.Test;

public class CSVParserNullHeaderTest {

    @Test
    public void parsedNullHeaderIsMappedWithoutFailure() throws Exception {
        final CSVFormat format = CSVFormat.DEFAULT.withHeader().withNullString("NULL");
        final CSVParser parser = CSVParser.parse("first,NULL,last\none,two,three\n", format);
        try {
            final Map<String, Integer> headerMap = parser.getHeaderMap();

            assertNotNull(headerMap);
            assertEquals(3, headerMap.size());
            assertEquals(Integer.valueOf(0), headerMap.get("first"));
            assertTrue(headerMap.containsKey(null));
            assertEquals(Integer.valueOf(1), headerMap.get(null));
            assertEquals(Integer.valueOf(2), headerMap.get("last"));
        } finally {
            parser.close();
        }
    }

    @Test
    public void nullStringInHeaderDoesNotPreventReadingFollowingRecords() throws Exception {
        final CSVFormat format = CSVFormat.DEFAULT.withHeader().withNullString("NULL");
        final CSVParser parser = CSVParser.parse("a,NULL,c\nvalue,NULL,last\n", format);
        try {
            final List<CSVRecord> records = parser.getRecords();

            assertEquals(1, records.size());
            assertEquals("value", records.get(0).get(0));
            assertNull(records.get(0).get(1));
            assertEquals("last", records.get(0).get(2));
        } finally {
            parser.close();
        }
    }

    @Test
    public void duplicateNullHeadersAreRejectedWhenEmptyHeadersAreNotIgnored() throws Exception {
        final CSVFormat format = CSVFormat.DEFAULT.withHeader().withNullString("NULL");

        try {
            CSVParser.parse("NULL,NULL\n", format);
            fail("Duplicate null headers must be rejected");
        } catch (final IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("duplicate name"));
        }
    }

    @Test
    public void automaticHeaderOnEmptyInputProducesAnEmptyHeaderMap() throws Exception {
        final CSVParser parser = CSVParser.parse("", CSVFormat.DEFAULT.withHeader());
        try {
            final Map<String, Integer> headerMap = parser.getHeaderMap();

            assertNotNull(headerMap);
            assertTrue(headerMap.isEmpty());
            assertFalse(parser.iterator().hasNext());
        } finally {
            parser.close();
        }
    }

    @Test
    public void singleEmptyHeaderIsRetainedInHeaderMap() throws Exception {
        final CSVParser parser = CSVParser.parse(",name\nleft,right\n", CSVFormat.DEFAULT.withHeader());
        try {
            final Map<String, Integer> headerMap = parser.getHeaderMap();

            assertEquals(2, headerMap.size());
            assertEquals(Integer.valueOf(0), headerMap.get(""));
            assertEquals(Integer.valueOf(1), headerMap.get("name"));
        } finally {
            parser.close();
        }
    }

    @Test
    public void formatWithoutHeaderConfigurationHasNoHeaderMap() throws Exception {
        final CSVParser parser = CSVParser.parse("a,b\n", CSVFormat.DEFAULT);
        try {
            assertNull(parser.getHeaderMap());
            assertEquals(1, parser.getRecords().size());
        } finally {
            parser.close();
        }
    }
}
