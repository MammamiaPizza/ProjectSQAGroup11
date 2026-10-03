package org.apache.commons.csv;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class CSVFormatDuplicateHeaderTest {

    @Test(expected = IllegalArgumentException.class)
    public void withHeaderRejectsDuplicateHeaderNames() {
        CSVFormat.DEFAULT.withHeader("id", "name", "id");
    }

    @Test
    public void withHeaderAcceptsDistinctHeaderNames() {
        CSVFormat format = CSVFormat.DEFAULT.withHeader("id", "name", "email");

        assertArrayEquals(new String[] { "id", "name", "email" }, format.getHeader());
    }

@org.junit.Test(expected = java.lang.IllegalArgumentException.class)
public void withHeaderRejectsDuplicateNullHeaderNames() {
    org.apache.commons.csv.CSVFormat.DEFAULT.withHeader("id", null, null);
}

@org.junit.Test(expected = java.lang.IllegalArgumentException.class)
public void newFormatRejectsLineBreakDelimiter() {
    org.apache.commons.csv.CSVFormat.newFormat('\n');
}

@org.junit.Test
public void equalsDistinguishesNullableConfigurationValues() {
    final org.apache.commons.csv.CSVFormat format =
            org.apache.commons.csv.CSVFormat.DEFAULT
                    .withQuoteChar((java.lang.Character) null)
                    .withCommentStart((java.lang.Character) null)
                    .withEscape((java.lang.Character) null);

    org.junit.Assert.assertEquals(format, format);
    org.junit.Assert.assertFalse(format.equals(null));
    org.junit.Assert.assertFalse(format.equals("format"));
    org.junit.Assert.assertFalse(format.equals(format.withQuoteChar('"')));
    org.junit.Assert.assertFalse(format.equals(format.withCommentStart('#')));
    org.junit.Assert.assertFalse(format.equals(format.withEscape('\\')));
    org.junit.Assert.assertFalse(format.equals(format.withNullString("NULL")));
}
}
