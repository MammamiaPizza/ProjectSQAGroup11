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

@Test
public void excelParsesAutomaticallyDetectedHeadersWithMultipleEmptyNames() throws java.io.IOException {
    final org.apache.commons.csv.CSVParser parser = CSVFormat.EXCEL.withHeader().parse(
            new java.io.StringReader("A,B,C,,\n1,2,3,4,5\n"));
    org.junit.Assert.assertTrue(parser.iterator().hasNext());
}

@Test
public void newFormatRejectsLineBreakDelimiters() {
    assertLineBreakDelimiterIsRejected('\n');
    assertLineBreakDelimiterIsRejected('\r');
}

@Test
public void equalsHandlesIdentityNullTypeAndDifferentDelimiter() {
    final CSVFormat format = CSVFormat.newFormat(';');

    org.junit.Assert.assertTrue(format.equals(format));
    org.junit.Assert.assertFalse(format.equals(null));
    org.junit.Assert.assertFalse(format.equals("format"));
    org.junit.Assert.assertFalse(format.equals(CSVFormat.newFormat(',')));
    org.junit.Assert.assertEquals(format, CSVFormat.newFormat(';'));
    org.junit.Assert.assertEquals(format.hashCode(), CSVFormat.newFormat(';').hashCode());
}

private void assertLineBreakDelimiterIsRejected(final char delimiter) {
    try {
        CSVFormat.newFormat(delimiter);
        org.junit.Assert.fail("Expected an IllegalArgumentException for a line break delimiter");
    } catch (final IllegalArgumentException expected) {
        org.junit.Assert.assertEquals("The delimiter cannot be a line break", expected.getMessage());
    }
}
}
