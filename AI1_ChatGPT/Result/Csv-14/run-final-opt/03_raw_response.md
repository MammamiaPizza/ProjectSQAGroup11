package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Test;

public class CSVFormatEscapeTest {

    @Test
    public void nullEscapeDoesNotQuoteSingleBackslash() {
        final CSVFormat format = CSVFormat.DEFAULT.withEscape((Character) null);

        assertEquals("\\", format.format("\\"));
    }

    @Test
    public void nullEscapeDoesNotQuoteRepeatedBackslashes() {
        final CSVFormat format = CSVFormat.DEFAULT.withEscape((Character) null);

        assertEquals("\\\\", format.format("\\\\"));
    }

    @Test
    public void backslashEscapeDoesNotQuoteSingleBackslashWithSingleQuote() {
        final CSVFormat format = CSVFormat.DEFAULT.withQuote('\'').withEscape('\\');

        assertEquals("\\", format.format("\\"));
    }

    @Test
    public void backslashEscapeDoesNotQuoteRepeatedBackslashesWithSingleQuote() {
        final CSVFormat format = CSVFormat.DEFAULT.withQuote('\'').withEscape('\\');

        assertEquals("\\\\", format.format("\\\\"));
    }

    @Test
    public void printDoesNotAddQuotesForBackslashWhenEscapeIsNull() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT.withEscape((Character) null);
        final StringWriter out = new StringWriter();

        format.print("\\", out, false);

        assertEquals("\\", out.toString());
    }

    @Test
    public void printDoesNotAddQuotesForBackslashWhenBackslashIsEscape() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT.withQuote('\'').withEscape('\\');
        final StringWriter out = new StringWriter();

        format.print("\\\\", out, false);

        assertEquals("\\\\", out.toString());
    }

    @Test
    public void normalDelimitedRecordStillUsesDelimiterAndRecordSeparator() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT.withEscape((Character) null);
        final StringWriter out = new StringWriter();

        try (CSVPrinter printer = new CSVPrinter(out, format)) {
            printer.printRecord("left", "right");
        }

        assertEquals("left,right\r\n", out.toString());
    }
}