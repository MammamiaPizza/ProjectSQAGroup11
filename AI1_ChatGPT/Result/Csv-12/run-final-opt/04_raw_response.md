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