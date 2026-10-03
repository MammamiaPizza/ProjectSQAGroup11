package org.apache.commons.csv;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;

import org.junit.Test;

public class CSVFormatGeneratedTest {

    @Test
    public void testPredefinedFormatsExposeDocumentedCoreSettings() {
        assertEquals(',', CSVFormat.DEFAULT.getDelimiter());
        assertEquals(Character.valueOf('"'), CSVFormat.DEFAULT.getQuoteChar());
        assertEquals("\r\n", CSVFormat.DEFAULT.getRecordSeparator());
        assertTrue(CSVFormat.DEFAULT.getIgnoreEmptyLines());
        assertFalse(CSVFormat.DEFAULT.getIgnoreSurroundingSpaces());

        assertFalse(CSVFormat.RFC4180.getIgnoreEmptyLines());
        assertFalse(CSVFormat.EXCEL.getIgnoreEmptyLines());

        assertEquals('\t', CSVFormat.TDF.getDelimiter());
        assertTrue(CSVFormat.TDF.getIgnoreSurroundingSpaces());

        assertEquals('\t', CSVFormat.MYSQL.getDelimiter());
        assertEquals(Character.valueOf('\\'), CSVFormat.MYSQL.getEscape());
        assertFalse(CSVFormat.MYSQL.isQuoting());
        assertEquals("\n", CSVFormat.MYSQL.getRecordSeparator());
    }

    @Test
    public void testBuilderMethodsCreateIndependentConfiguredFormat() {
        final CSVFormat original = CSVFormat.DEFAULT;
        final CSVFormat configured = original
                .withDelimiter(';')
                .withQuoteChar('\'')
                .withCommentStart('#')
                .withEscape('\\')
                .withIgnoreEmptyLines(false)
                .withIgnoreSurroundingSpaces(true)
                .withNullString("NULL")
                .withRecordSeparator('\n')
                .withHeader("first", "second")
                .withSkipHeaderRecord(true);

        assertNotSame(original, configured);

        assertEquals(';', configured.getDelimiter());
        assertEquals(Character.valueOf('\''), configured.getQuoteChar());
        assertEquals(Character.valueOf('#'), configured.getCommentStart());
        assertEquals(Character.valueOf('\\'), configured.getEscape());
        assertFalse(configured.getIgnoreEmptyLines());
        assertTrue(configured.getIgnoreSurroundingSpaces());
        assertEquals("NULL", configured.getNullString());
        assertEquals("\n", configured.getRecordSeparator());
        assertArrayEquals(new String[] { "first", "second" }, configured.getHeader());
        assertTrue(configured.getSkipHeaderRecord());

        assertTrue(configured.isQuoting());
        assertTrue(configured.isCommentingEnabled());
        assertTrue(configured.isEscaping());
        assertTrue(configured.isNullHandling());

        assertEquals(',', original.getDelimiter());
        assertNull(original.getCommentStart());
        assertNull(original.getEscape());
        assertNull(original.getNullString());
        assertFalse(original.getSkipHeaderRecord());
    }

    @Test
    public void testHeaderIsDefensivelyCopiedOnInputAndOutput() {
        final String[] suppliedHeader = new String[] { "name", "age" };
        final CSVFormat format = CSVFormat.DEFAULT.withHeader(suppliedHeader);

        suppliedHeader[0] = "changed-after-construction";
        assertArrayEquals(new String[] { "name", "age" }, format.getHeader());

        final String[] returnedHeader = format.getHeader();
        returnedHeader[1] = "changed-after-access";
        assertArrayEquals(new String[] { "name", "age" }, format.getHeader());
    }

    @Test
    public void testNullAndEmptyHeadersHaveDifferentDocumentedRepresentations() {
        final CSVFormat automaticHeaderFormat = CSVFormat.DEFAULT.withHeader();
        final CSVFormat disabledHeaderFormat = CSVFormat.DEFAULT.withHeader((String[]) null);

        assertNotNull(automaticHeaderFormat.getHeader());
        assertEquals(0, automaticHeaderFormat.getHeader().length);
        assertNull(disabledHeaderFormat.getHeader());
    }

    @Test
    public void testNullableMarkersCanBeDisabled() {
        final CSVFormat noMarkers = CSVFormat.DEFAULT
                .withCommentStart((Character) null)
                .withEscape((Character) null)
                .withQuoteChar((Character) null)
                .withNullString(null)
                .withRecordSeparator((String) null)
                .withQuotePolicy(null);

        assertNull(noMarkers.getCommentStart());
        assertNull(noMarkers.getEscape());
        assertNull(noMarkers.getQuoteChar());
        assertNull(noMarkers.getNullString());
        assertNull(noMarkers.getRecordSeparator());
        assertNull(noMarkers.getQuotePolicy());
        assertFalse(noMarkers.isCommentingEnabled());
        assertFalse(noMarkers.isEscaping());
        assertFalse(noMarkers.isQuoting());
        assertFalse(noMarkers.isNullHandling());
    }

    @Test
    public void testFormatQuotesSpecialValuesAndUsesConfiguredNullString() {
        assertEquals("plain,\"needs,quote\",\"a\"\"b\"",
                CSVFormat.DEFAULT.format("plain", "needs,quote", "a\"b"));

        final CSVFormat nullFormat = CSVFormat.DEFAULT.withNullString("NULL");
        assertEquals("NULL,value", nullFormat.format(new Object[] { null, "value" }));
    }

    @Test
    public void testEqualsAndHashCodeForEquivalentAndDifferentFormats() {
        final CSVFormat format = CSVFormat.DEFAULT
                .withDelimiter(';')
                .withHeader("name", "email")
                .withNullString("NULL")
                .withSkipHeaderRecord(true);

        final CSVFormat equivalent = format.withDelimiter(';');
        final CSVFormat differentNullString = format.withNullString("OTHER");
        final CSVFormat differentSeparator = format.withRecordSeparator('\n');

        assertTrue(format.equals(format));
        assertTrue(format.equals(equivalent));
        assertEquals(format.hashCode(), equivalent.hashCode());

        assertFalse(format.equals(null));
        assertFalse(format.equals("not a CSV format"));
        assertFalse(format.equals(differentNullString));
        assertFalse(format.equals(differentSeparator));
    }

    @Test
    public void testToStringContainsEnabledConfigurationDetails() {
        final CSVFormat format = CSVFormat.DEFAULT
                .withDelimiter(';')
                .withEscape('\\')
                .withCommentStart('#')
                .withNullString("NULL")
                .withHeader("id")
                .withSkipHeaderRecord(true);

        final String description = format.toString();

        assertTrue(description.contains("Delimiter=<;>"));
        assertTrue(description.contains("Escape=<\\>"));
        assertTrue(description.contains("QuoteChar=<\">"));
        assertTrue(description.contains("CommentStart=<#>"));
        assertTrue(description.contains("NullString=<NULL>"));
        assertTrue(description.contains("SkipHeaderRecord:true"));
        assertTrue(description.contains("Header:[id]"));
    }

    @Test
    public void testParseCreatesParserForValidInput() throws Exception {
        final CSVParser parser = CSVFormat.DEFAULT.parse(new StringReader("name,age\r\nAlice,30\r\n"));

        assertNotNull(parser);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewFormatRejectsCarriageReturnDelimiter() {
        CSVFormat.newFormat('\r');
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithDelimiterRejectsLineFeed() {
        CSVFormat.DEFAULT.withDelimiter('\n');
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithCommentStartRejectsLineBreak() {
        CSVFormat.DEFAULT.withCommentStart('\r');
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithEscapeRejectsLineBreak() {
        CSVFormat.DEFAULT.withEscape(Character.valueOf('\n'));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithQuoteCharRejectsLineBreak() {
        CSVFormat.DEFAULT.withQuoteChar(Character.valueOf('\r'));
    }

    @Test(expected = IllegalStateException.class)
    public void testValidateRejectsQuoteCharacterEqualToDelimiter() {
        CSVFormat.newFormat(';').withQuoteChar(';').validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidateRejectsEscapeCharacterEqualToDelimiter() {
        CSVFormat.newFormat(';').withEscape(';').validate();
    }

    @Test(expected = IllegalStateException.class)
    public void testValidateRejectsCommentCharacterEqualToDelimiter() {
        CSVFormat.newFormat(';').withCommentStart(';').validate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateHeaderElementsAreReportedAsIllegalArgumentException() {
        CSVFormat.DEFAULT.withHeader("name", "name").validate();
    }
}
