package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.util.Map;

import org.junit.Test;

public class CSVParserDuplicateHeaderTest {

    @Test(expected = IllegalStateException.class)
    public void duplicateHeadersReadFromInputAreRejected() throws Exception {
        new CSVParser(new StringReader("name,age,name\nAlice,30,Alice\n"), CSVFormat.DEFAULT.withHeader());
    }

    @Test(expected = IllegalStateException.class)
    public void duplicateHeadersSuppliedByFormatAreRejected() throws Exception {
        new CSVParser(new StringReader("Alice,30\n"), CSVFormat.DEFAULT.withHeader("name", "name"));
    }

    @Test
    public void distinctHeadersProduceUsableHeaderMapAndRecords() throws Exception {
        final CSVParser parser = new CSVParser(
                new StringReader("name,age\nAlice,30\n"),
                CSVFormat.DEFAULT.withHeader());
        try {
            final Map<String, Integer> headerMap = parser.getHeaderMap();

            assertNotNull(headerMap);
            assertEquals(2, headerMap.size());
            assertTrue(headerMap.containsKey("name"));
            assertTrue(headerMap.containsKey("age"));
            assertEquals(1, parser.getRecords().size());
        } finally {
            parser.close();
        }
    }
}
