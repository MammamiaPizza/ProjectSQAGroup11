```java
package org.apache.commons.csv;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.List;

import org.junit.Test;

public class CSVFormatTest {

    @Test
    public void testExcelAllowsMissingColumnNamesAndParsesRepeatedEmptyHeaders() throws Exception {
        final CSVFormat format = CSVFormat.EXCEL.withHeader();
        final CSVParser parser = format.parse(new StringReader(
                "A,B,C,,\r\n"
                + "1,2,3,4,5\r\n"));

        final List<CSVRecord> records = parser.getRecords();

        assertTrue("Excel format must allow missing column names", CSVFormat.EXCEL.getAllowMissingColumnNames());
        assertEquals(1, records.size());
        assertEquals(5, records.get(0).size());
        assertEquals("1", records.get(0).get(0));
        assertEquals("5", records.get(0).get(4));
    }

    @Test
    public void testDefaultFormatConfigurationAndDerivedFormats() {
        assertEquals(Character.valueOf('"'), CSVFormat.DEFAULT.getQuoteCharacter());
        assertEquals(Character.valueOf(','), Character.valueOf(CSVFormat.DEFAULT.getDelimiter()));
        assertEquals("\r\n", CSVFormat.DEFAULT.getRecordSeparator());
        assertTrue(CSVFormat.DEFAULT.getIgnoreEmptyLines());
        assertFalse(CSVFormat.DEFAULT.getAllowMissingColumnNames());

        assertFalse(CSVFormat.RFC4180.getIgnoreEmptyLines());
        assertFalse(CSVFormat.RFC4180.getAllowMissingColumnNames());

        assertEquals(Character.valueOf('\t'), Character.valueOf(CSVFormat.TDF.getDelimiter()));
        assertTrue(CSVFormat.TDF.getIgnoreSurroundingSpaces());

        assertEquals(Character.valueOf('\t'), Character.valueOf(CSVFormat.MYSQL.getDelimiter()));
        assertNull(CSVFormat.MYSQL.getQuoteCharacter());
        assertEquals(Character.valueOf('\\'), CSVFormat.MYSQL.getEscapeCharacter());
        assertEquals("\n", CSVFormat.MYSQL.getRecordSeparator());
    }

    @Test
    public void testWithMethodsReturnIndependentFormatWithConfiguredValues() {
        final CSVFormat base = CSVFormat.newFormat(';');
        final CSVFormat configured = base
                .withQuote('"')
                .withQuoteMode(QuoteMode.ALL)
                .withCommentMarker('#')
                .withEscape('\\')
                .withIgnoreSurroundingSpaces(true)
                .withIgnoreEmptyLines(true)
                .withAllowMissingColumnNames(true)
                .withNullString("NULL")
                .withRecordSeparator('\n')
                .withHeader("name", "email")
                .withSkipHeaderRecord(true);

        assertEquals(';', configured.getDelimiter());
        assertEquals(Character.valueOf('"'), configured.getQuoteCharacter());
        assertEquals(QuoteMode.ALL, configured.getQuoteMode());
        assertEquals(Character.valueOf('#'), configured.getCommentMarker());
        assertEquals(Character.valueOf('\\'), configured.getEscapeCharacter());
        assertTrue(configured.getIgnoreSurroundingSpaces());
        assertTrue(configured.getIgnoreEmptyLines());
        assertTrue(configured.getAllowMissingColumnNames());
        assertEquals("NULL", configured.getNullString());
        assertEquals("\n", configured.getRecordSeparator());
        assertArrayEquals(new String[] { "name", "email" }, configured.getHeader());
        assertTrue(configured.getSkipHeaderRecord());

        assertEquals(';', base.getDelimiter());
        assertNull(base.getQuoteCharacter());
        assertNull(base.getHeader());
        assertFalse(base.getSkipHeaderRecord());
    }

    @Test
    public void testHeaderIsDefensivelyCopiedOnInputAndOutput() {
        final String[] suppliedHeader = new String[] { "first", "second" };
        final CSVFormat format = CSVFormat.DEFAULT.withHeader(suppliedHeader);

        suppliedHeader[0] = "changed";
        final String[] returnedHeader = format.getHeader();
        returnedHeader[1] = "changed-again";

        assertArrayEquals(new String[] { "first", "second" }, format.getHeader());
        assertNotSame(returnedHeader, format.getHeader());
    }

    @Test
    public void testHeaderCanBeDisabledOrReadAutomatically() {
        final CSVFormat noHeader = CSVFormat.DEFAULT.withHeader((String[]) null);
        final CSVFormat automaticHeader = CSVFormat.DEFAULT.withHeader();

        assertNull(noHeader.getHeader());
        assertArrayEquals(new String[0], automaticHeader.getHeader());
    }

    @Test
    public void testDuplicateExplicitHeaderNamesAreRejected() {
        try {
            CSVFormat.DEFAULT.withHeader("id", "id");
            fail("Duplicate header names must be rejected");
        } catch (final IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("duplicate"));
        }
    }

    @Test
    public void testInvalidLineBreakCharactersAndConflictingMarkersAreRejected() {
        assertIllegalArgument(new Runnable() {
            @Override
            public void run() {
                CSVFormat.newFormat('\n');
            }
        });

        assertIllegalArgument(new Runnable() {
            @Override
            public void run() {
                CSVFormat.DEFAULT.withDelimiter('\r');
            }
        });

        assertIllegalArgument(new Runnable() {
            @Override
            public void run() {
                CSVFormat.DEFAULT.withQuote('\n');
            }
        });

        assertIllegalArgument(new Runnable() {
            @Override
            public void run() {
                CSVFormat.DEFAULT.withEscape('\r');
            }
        });

        assertIllegalArgument(new Runnable() {
            @Override
            public void run() {
                CSVFormat.DEFAULT.withCommentMarker('\n');
            }
        });

        assertIllegalArgument(new Runnable() {
            @Override
            public void run() {
                CSVFormat.DEFAULT.withQuote(',');
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
                CSVFormat.DEFAULT.withCommentMarker(',');
            }
        });
    }

    @Test
    public void testQuoteModeNoneRequiresEscapeCharacter() {
        assertIllegalArgument(new Runnable() {
            @Override
            public void run() {
                CSVFormat.newFormat(',').withQuoteMode(QuoteMode.NONE);
            }
        });

        final CSVFormat format = CSVFormat.newFormat(',')
                .withEscape('\\')
                .withQuoteMode(QuoteMode.NONE);

        assertEquals(QuoteMode.NONE, format.getQuoteMode());
        assertEquals(Character.valueOf('\\'), format.getEscapeCharacter());
    }

    @Test
    public void testEqualsAndHashCodeReflectFormatSettings() {
        final CSVFormat equivalent = CSVFormat.DEFAULT.withDelimiter(',');
        final CSVFormat different = CSVFormat.DEFAULT.withNullString("NULL");

        assertTrue(CSVFormat.DEFAULT.equals(CSVFormat.DEFAULT));
        assertTrue(CSVFormat.DEFAULT.equals(equivalent));
        assertEquals(CSVFormat.DEFAULT.hashCode(), equivalent.hashCode());

        assertFalse(CSVFormat.DEFAULT.equals(different));
        assertFalse(CSVFormat.DEFAULT.equals(null));
        assertFalse(CSVFormat.DEFAULT.equals("not a CSV format"));
    }

    @Test
    public void testFormatQuotesSpecialCharactersAndWritesNullAsEmptyByDefault() {
        final String formatted = CSVFormat.DEFAULT.format("a,b", "\"quoted\"", null);

        assertEquals("\"a,b\",\"\"\"quoted\"\"\",", formatted);
    }

    @Test
    public void testToStringIncludesEnabledOptionalSettings() {
        final CSVFormat format = CSVFormat.newFormat(';')
                .withQuote('"')
                .withEscape('\\')
                .withCommentMarker('#')
                .withNullString("NULL")
                .withRecordSeparator('\n')
                .withIgnoreEmptyLines(true)
                .withIgnoreSurroundingSpaces(true)
                .withHeader("a")
                .withSkipHeaderRecord(true);

        final String description = format.toString();

        assertTrue(description.contains("Delimiter=<;>"));
        assertTrue(description.contains("Escape=<\\>"));
        assertTrue(description.contains("QuoteChar=<\">"));
        assertTrue(description.contains("CommentStart=<#>"));
        assertTrue(description.contains("NullString=<NULL>"));
        assertTrue(description.contains("RecordSeparator=<"));
        assertTrue(description.contains("EmptyLines:ignored"));
        assertTrue(description.contains("SurroundingSpaces:ignored"));
        assertTrue(description.contains("SkipHeaderRecord:true"));
        assertTrue(description.contains("Header:[a]"));
    }

    private static void assertIllegalArgument(final Runnable action) {
        try {
            action.run();
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            assertTrue(expected.getMessage().length() > 0);
        }
    }
}
```

Test coverage summary:

- `testExcelAllowsMissingColumnNamesAndParsesRepeatedEmptyHeaders` targets CSV-128 directly: Excel parsing with automatic headers must permit multiple missing header names and still parse the data record.
- `testDefaultFormatConfigurationAndDerivedFormats` verifies documented predefined format settings.
- `testWithMethodsReturnIndependentFormatWithConfiguredValues` covers the immutable `with...` configuration methods and getters.
- `testHeaderIsDefensivelyCopiedOnInputAndOutput` verifies header array immutability.
- `testHeaderCanBeDisabledOrReadAutomatically` covers `null` and empty header behavior.
- `testDuplicateExplicitHeaderNamesAreRejected` checks duplicate explicit non-missing column names.
- `testInvalidLineBreakCharactersAndConflictingMarkersAreRejected` covers validation branches for invalid delimiters, markers, escape characters, and conflicting delimiter settings.
- `testQuoteModeNoneRequiresEscapeCharacter` covers the validation rule requiring escaping when `QuoteMode.NONE` is selected.
- `testEqualsAndHashCodeReflectFormatSettings` verifies equality/hash-code behavior for equal, unequal, null, and unrelated objects.
- `testFormatQuotesSpecialCharactersAndWritesNullAsEmptyByDefault` exercises formatting, quoting, escaping embedded quotes, and default null output.
- `testToStringIncludesEnabledOptionalSettings` covers optional `toString()` output branches.