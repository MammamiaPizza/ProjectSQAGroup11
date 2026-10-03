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
}