package org.apache.commons.csv;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;

import org.junit.Test;

public class CSVFormatHeaderValidationTest {

    @Test
    public void excelAcceptsEmptyHeaderNameAndParsesData() throws Exception {
        final CSVFormat format = CSVFormat.EXCEL.withHeader("A", "B", "C", "");

        assertArrayEquals(new String[] { "A", "B", "C", "" }, format.getHeader());

        final CSVParser parser = format.parse(new StringReader("1,2,3,4,5\r\n"));
        try {
            assertTrue(parser.iterator().hasNext());
        } finally {
            parser.close();
        }
    }

    @Test
    public void excelAllowsMissingColumnNames() {
        assertTrue(CSVFormat.EXCEL.getAllowMissingColumnNames());
    }

    @Test(expected = IllegalArgumentException.class)
    public void allowingMissingColumnNamesStillRejectsRepeatedEmptyHeaders() {
        CSVFormat.newFormat(',')
                .withAllowMissingColumnNames(true)
                .withHeader("", "");
    }

    @Test
    public void disallowingMissingColumnNamesAllowsEmptyHeaderName() {
        final CSVFormat format = CSVFormat.newFormat(',').withHeader("A", "");

        assertArrayEquals(new String[] { "A", "" }, format.getHeader());
    }

    @Test(expected = IllegalArgumentException.class)
    public void duplicateNonEmptyHeaderNamesRemainInvalidWhenMissingNamesAreAllowed() {
        CSVFormat.newFormat(',')
                .withAllowMissingColumnNames(true)
                .withHeader("A", "A");
    }
}