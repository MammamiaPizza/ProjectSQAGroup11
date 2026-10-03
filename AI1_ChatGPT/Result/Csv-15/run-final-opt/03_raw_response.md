package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class CSVFormatEuroEscapingTest {

    @Test
    public void formatDoesNotQuoteEuroWhenItIsTheFirstCharacter() {
        final String result = CSVFormat.DEFAULT.format("\u20AC", "Deux");

        assertEquals("\u20AC,Deux\r\n", result);
        assertFalse(result.startsWith("\""));
    }

    @Test
    public void formatTreatsEuroFirstFieldLikeOrdinaryNonSpecialText() {
        assertEquals("Alpha,Deux\r\n", CSVFormat.DEFAULT.format("Alpha", "Deux"));
        assertEquals("\u20AC,Deux\r\n", CSVFormat.DEFAULT.format("\u20AC", "Deux"));
    }

    @Test
    public void formatQuotesFieldsContainingCsvSpecialCharacters() {
        assertEquals("\"a,b\",\"a\"\"b\"\"\",\"line\nbreak\"\r\n",
                CSVFormat.DEFAULT.format("a,b", "a\"b\"", "line\nbreak"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void withDelimiterRejectsLineBreakDelimiter() {
        CSVFormat.DEFAULT.withDelimiter('\n');
    }
}