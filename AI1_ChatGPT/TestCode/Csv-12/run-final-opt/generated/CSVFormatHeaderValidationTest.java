package org.apache.commons.csv;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;

import org.junit.Test;

public class CSVFormatHeaderValidationTest {

    @Test
    public void excelAcceptsRepeatedEmptyHeaderNamesAndParsesData() throws Exception {
        final CSVFormat format = CSVFormat.EXCEL.withHeader("A", "B", "C", "", "");

        assertArrayEquals(new String[] { "A", "B", "C", "", "" }, format.getHeader());

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

    @Test
    public void allowingMissingColumnNamesAcceptsRepeatedEmptyHeaders() {
        final CSVFormat format = CSVFormat.newFormat(',')
                .withAllowMissingColumnNames(true)
                .withHeader("", "");

        assertArrayEquals(new String[] { "", "" }, format.getHeader());
    }

    @Test(expected = IllegalArgumentException.class)
    public void disallowingMissingColumnNamesRejectsEmptyHeaderName() {
        CSVFormat.newFormat(',').withHeader("A", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void duplicateNonEmptyHeaderNamesRemainInvalidWhenMissingNamesAreAllowed() {
        CSVFormat.newFormat(',')
                .withAllowMissingColumnNames(true)
                .withHeader("A", "A");
    }
}
