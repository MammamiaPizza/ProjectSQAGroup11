```java
package org.apache.commons.csv;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

public class CSVFormatGeneratedTest {

    private enum Header {
        Name,
        Age
    }

    private static final Charset UTF_8 = Charset.forName("UTF-8");

    private void assertIllegalArgument(final Runnable runnable) {
        try {
            runnable.run();
        } catch (final IllegalArgumentException expected) {
            return;
        }
        throw new AssertionError("Expected IllegalArgumentException");
    }

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

    @Test
    public void testQuoteModesNonNumericAllNonNullAndNone() {
        final CSVFormat nonNumeric = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.NON_NUMERIC);
        assertEquals("12,\"12\"", nonNumeric.format(Integer.valueOf(12), "12"));

        final CSVFormat allNonNull = CSVFormat.DEFAULT
                .withNullString("NULL")
                .withQuoteMode(QuoteMode.ALL_NON_NULL);
        assertEquals("NULL,\"value\"", allNonNull.format(null, "value"));

        final CSVFormat noQuotes = CSVFormat.DEFAULT
                .withEscape('\\')
                .withQuoteMode(QuoteMode.NONE);
        assertEquals("a\\,b", noQuotes.format("a,b"));

        final CSVFormat neitherQuotesNorEscapes = CSVFormat.DEFAULT
                .withQuote((Character) null)
                .withEscape((Character) null);
        assertEquals("a,b", neitherQuotesNorEscapes.format("a,b"));
    }

    @Test
    public void testEnumAndNullHeaderConfiguration() {
        final CSVFormat enumHeaderFormat = CSVFormat.DEFAULT.withHeader(Header.class);

        assertArrayEquals(new String[] { "Name", "Age" }, enumHeaderFormat.getHeader());
        assertNull(CSVFormat.DEFAULT.withHeader((Class<? extends Enum<?>>) null).getHeader());
    }

    @Test
    public void testHeaderCommentsAreConvertedAndDefensivelyCopied() {
        final CSVFormat format = CSVFormat.DEFAULT.withHeaderComments("generated", Integer.valueOf(2), null);

        assertArrayEquals(new String[] { "generated", "2", null }, format.getHeaderComments());

        final String[] returnedComments = format.getHeaderComments();
        returnedComments[0] = "changed";

        assertArrayEquals(new String[] { "generated", "2", null }, format.getHeaderComments());
        assertNotSame(returnedComments, format.getHeaderComments());
    }

    @Test
    public void testPrintAppendableWritesConfiguredHeader() throws Exception {
        final StringBuilder output = new StringBuilder();
        final CSVPrinter printer = CSVFormat.DEFAULT.withHeader("name", "age").print(output);
        try {
            printer.printRecord("Ada", Integer.valueOf(5));
        } finally {
            printer.close();
        }

        assertEquals("name,age\r\nAda,5\r\n", output.toString());
    }

    @Test
    public void testPrintToFileAndPathWritesRecords() throws Exception {
        final File file = File.createTempFile("csv-format", ".csv");
        final Path path = Files.createTempFile("csv-format", ".csv");

        try {
            final CSVPrinter filePrinter = CSVFormat.DEFAULT.withRecordSeparator('\n').print(file, UTF_8);
            try {
                filePrinter.printRecord("file");
            } finally {
                filePrinter.close();
            }

            final CSVPrinter pathPrinter = CSVFormat.DEFAULT.withRecordSeparator('\n').print(path, UTF_8);
            try {
                pathPrinter.printRecord("path");
            } finally {
                pathPrinter.close();
            }

            assertEquals("file\n", new String(Files.readAllBytes(file.toPath()), UTF_8));
            assertEquals("path\n", new String(Files.readAllBytes(path), UTF_8));
        } finally {
            file.delete();
            Files.deleteIfExists(path);
        }
    }

    @Test
    public void testValidationRejectsOtherConflictingSpecialCharacters() {
        assertIllegalArgument(new Runnable() {
            @Override
            public void run() {
                CSVFormat.DEFAULT.withEscape('\n');
            }
        });
        assertIllegalArgument(new Runnable() {
            @Override
            public void run() {
                CSVFormat.DEFAULT.withEscape(',');
            }
        });
        assertIllegalArgument(new Runnable() {
            @Override
            public void run() {
                CSVFormat.DEFAULT.withCommentMarker('"');
            }
        });
        assertIllegalArgument(new Runnable() {
            @Override
            public void run() {
                CSVFormat.DEFAULT.withEscape('#').withCommentMarker('#');
            }
        });
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
```

### New test coverage targets

- **`testQuoteModesNonNumericAllNonNullAndNone`**
  - Covers previously untested `QuoteMode.NON_NUMERIC`, `QuoteMode.ALL_NON_NULL`, and valid `QuoteMode.NONE` branches in `printAndQuote`.
  - Covers the no-quote/no-escape fallback branch in `print`.
  - Verifies that `ALL_NON_NULL` does not quote a null value while quoting a non-null value.

- **`testEnumAndNullHeaderConfiguration`**
  - Covers `withHeader(Class<? extends Enum<?>>)` for both non-null enum input and null input.
  - Covers enum-name-to-header conversion.

- **`testHeaderCommentsAreConvertedAndDefensivelyCopied`**
  - Covers `withHeaderComments(Object...)`, including object-to-string conversion and null header comments.
  - Covers `getHeaderComments()` defensive copying behavior.

- **`testPrintAppendableWritesConfiguredHeader`**
  - Covers `CSVFormat.print(Appendable)`.
  - Verifies configured headers are emitted before records by the created printer.

- **`testPrintToFileAndPathWritesRecords`**
  - Covers both `CSVFormat.print(File, Charset)` and `CSVFormat.print(Path, Charset)`.
  - Verifies returned printers write correctly and close their underlying resources.

- **`testValidationRejectsOtherConflictingSpecialCharacters`**
  - Covers validation branches for:
    - Line-break escape characters.
    - Escape character equal to delimiter.
    - Comment marker equal to quote character.
    - Comment marker equal to escape character.

- **Existing `testMinimalQuoteDoesNotQuoteEuroAsFirstCharacter`**
  - Directly targets Csv-15 / CSV-219.
  - Distinguishes the buggy behavior, which incorrectly quotes a value beginning with `€`, from the expected minimal-quoting behavior: `€,Deux`.