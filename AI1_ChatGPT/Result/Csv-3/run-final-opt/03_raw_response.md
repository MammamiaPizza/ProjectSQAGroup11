package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

public class CSV58LexerEscapeTest {

    @Test
    public void mysqlNullMarkerMustRemainRecognizableAfterLexing() throws Exception {
        final List<CSVRecord> records = records("\\N", CSVFormat.MYSQL);

        assertEquals(1, records.size());
        assertEquals(1, records.get(0).size());
        assertNull(records.get(0).get(0));
    }

    @Test
    public void unknownEscapeInUnquotedFieldPreservesEscapeCharacter() throws Exception {
        final CSVFormat format = CSVFormat.DEFAULT.withEscapeCharacter('\\');

        final List<CSVRecord> records = records("before\\x,after", format);

        assertEquals(1, records.size());
        assertEquals("before\\x", records.get(0).get(0));
        assertEquals("after", records.get(0).get(1));
    }

    @Test
    public void unknownEscapeInQuotedFieldPreservesEscapeCharacter() throws Exception {
        final CSVFormat format = CSVFormat.DEFAULT.withEscapeCharacter('\\');

        final List<CSVRecord> records = records("\"quoted\\q value\",tail", format);

        assertEquals(1, records.size());
        assertEquals("quoted\\q value", records.get(0).get(0));
        assertEquals("tail", records.get(0).get(1));
    }

    @Test
    public void escapedDelimiterIsIncludedInFieldInsteadOfStartingNextField() throws Exception {
        final CSVFormat format = CSVFormat.DEFAULT.withEscapeCharacter('\\');

        final List<CSVRecord> records = records("left\\,right,tail", format);

        assertEquals(1, records.size());
        assertEquals(2, records.get(0).size());
        assertEquals("left,right", records.get(0).get(0));
        assertEquals("tail", records.get(0).get(1));
    }

    @Test
    public void recognizedEscapeSequencesAreConvertedInsideField() throws Exception {
        final CSVFormat format = CSVFormat.DEFAULT.withEscapeCharacter('\\');

        final List<CSVRecord> records = records("first\\tsecond\\nthird", format);

        assertEquals(1, records.size());
        assertEquals("first\tsecond\nthird", records.get(0).get(0));
    }

    @Test(expected = IOException.class)
    public void escapeAtEndOfInputIsRejected() throws Exception {
        final CSVFormat format = CSVFormat.DEFAULT.withEscapeCharacter('\\');

        records("unfinished\\", format);
    }

    private List<CSVRecord> records(final String input, final CSVFormat format) throws IOException {
        return CSVParser.parse(input, format).getRecords();
    }
}