package org.apache.commons.csv;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;

import org.junit.Test;

public class CSVFormatGeneratedTest {

    @Test
    public void testMinimalQuoteDoesNotQuoteEuroAsFirstCharacter() {
        final CSVFormat format = CSVFormat.DEFAULT;

        assertEquals("€,Deux", format.format("€", "Deux"));
    }

    @Test
    public void testMinimalQuoteQuotesSpecialValuesAndEscapesEmbeddedQuote() {
        final CSVFormat format = CSVFormat.DEFAULT;

        assertEquals("\"a,b\",\"a\"\"b\",\"#comment\"", format.format("a,b", "a\"b", "#comment"));
    }

    @Test
    public void testMinimalQuoteQuotesEmptyFirstValueButNotEmptyLaterValue() {
        final CSVFormat format = CSVFormat.DEFAULT;

        assertEquals("\"\",next", format.format("", "next"));
        assertEquals("first,", format.format("first", ""));
    }

    @Test
    public void testEscapeModeEscapesDelimiterLineBreakAndEscapeCharacter() throws Exception {
        final CSVFormat format = CSVFormat.DEFAULT
                .withQuote((Character) null)
                .withEscape('\\')
                .withRecordSeparator('\n');
        final StringBuilder output = new StringBuilder();

        format.printRecord(output, "a,b", "line\nbreak", "a\\b");

        assertEquals("a\\,b,line\\nbreak,a\\\\b\n", output.toString());
    }

    @Test
    public void testNullStringAndQuoteAllAreUsedWhenPrintingNull() {
        final CSVFormat format = CSVFormat.DEFAULT
                .withNullString("NULL")
                .withQuoteMode(QuoteMode.ALL);

        assertEquals("\"NULL\",\"value\"", format.format(null, "value"));
    }

    @Test
    public void testTrimAndTrailingDelimiterAreAppliedToOutput() throws Exception {
        final CSVFormat format = CSVFormat.DEFAULT
                .withTrim()
                .withTrailingDelimiter()
                .withRecordSeparator('\n');
        final StringBuilder output = new StringBuilder();

        format.printRecord(output, new StringBuilder("  value  "));

        assertEquals("value,\n", output.toString());
    }

    @Test
    public void testHeaderIsDefensivelyCopiedAndFirstRecordCanBeUsedAsHeader() throws Exception {
        final CSVFormat explicitHeaderFormat = CSVFormat.DEFAULT.withHeader("Name", "Age");
        final String[] returnedHeader = explicitHeaderFormat.getHeader();

        assertArrayEquals(new String[] { "Name", "Age" }, returnedHeader);
        returnedHeader[0] = "Changed";
        assertArrayEquals(new String[] { "Name", "Age" }, explicitHeaderFormat.getHeader());
        assertNotSame(returnedHeader, explicitHeaderFormat.getHeader());

        final CSVFormat inputHeaderFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader();
        final CSVParser parser = inputHeaderFormat.parse(new StringReader("Name,Age\r\nAda,5\r\n"));
        try {
            final CSVRecord record = parser.iterator().next();
            assertEquals("Ada", record.get("Name"));
            assertEquals("5", record.get("Age"));
        } finally {
            parser.close();
        }
    }

    @Test
    public void testConfigurationMethodsReturnNewFormatAndPreserveOriginal() {
        final CSVFormat configured = CSVFormat.DEFAULT
                .withDelimiter(';')
                .withCommentMarker('#')
                .withIgnoreEmptyLines(false)
                .withIgnoreSurroundingSpaces()
                .withIgnoreHeaderCase()
                .withAllowMissingColumnNames()
                .withSkipHeaderRecord()
                .withAutoFlush(true);

        assertEquals(',', CSVFormat.DEFAULT.getDelimiter());
        assertTrue(CSVFormat.DEFAULT.getIgnoreEmptyLines());

        assertEquals(';', configured.getDelimiter());
        assertEquals(Character.valueOf('#'), configured.getCommentMarker());
        assertTrue(configured.isCommentMarkerSet());
        assertFalse(configured.getIgnoreEmptyLines());
        assertTrue(configured.getIgnoreSurroundingSpaces());
        assertTrue(configured.getIgnoreHeaderCase());
        assertTrue(configured.getAllowMissingColumnNames());
        assertTrue(configured.getSkipHeaderRecord());
        assertTrue(configured.getAutoFlush());
    }

    @Test
    public void testPredefinedLookupAndDisabledOptions() {
        assertEquals(CSVFormat.DEFAULT, CSVFormat.valueOf("Default"));
        assertEquals(CSVFormat.EXCEL, CSVFormat.Predefined.Excel.getFormat());

        final CSVFormat disabled = CSVFormat.DEFAULT
                .withQuote((Character) null)
                .withEscape((Character) null)
                .withCommentMarker((Character) null)
                .withNullString(null)
                .withHeader((String[]) null);

        assertNull(disabled.getQuoteCharacter());
        assertNull(disabled.getEscapeCharacter());
        assertNull(disabled.getCommentMarker());
        assertNull(disabled.getNullString());
        assertNull(disabled.getHeader());
        assertFalse(disabled.isQuoteCharacterSet());
        assertFalse(disabled.isEscapeCharacterSet());
        assertFalse(disabled.isCommentMarkerSet());
        assertFalse(disabled.isNullStringSet());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewFormatRejectsLineBreakDelimiter() {
        CSVFormat.newFormat('\n');
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCommentMarkerRejectsLineBreak() {
        CSVFormat.DEFAULT.withCommentMarker('\r');
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDelimiterCannotEqualQuoteCharacter() {
        CSVFormat.DEFAULT.withDelimiter('"');
    }

    @Test(expected = IllegalArgumentException.class)
    public void testQuoteModeNoneRequiresEscapeCharacter() {
        CSVFormat.DEFAULT.withQuoteMode(QuoteMode.NONE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateHeaderNamesAreRejected() {
        CSVFormat.DEFAULT.withHeader("name", "name");
    }
}
