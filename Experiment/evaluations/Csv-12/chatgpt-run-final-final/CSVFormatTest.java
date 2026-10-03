package org.apache.commons.csv;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.io.StringWriter;
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

    @Test
    public void testDefaultRejectsMissingAutomaticHeaderNamesWhileExcelAcceptsThem() throws Exception {
        try {
            final CSVParser parser = CSVFormat.DEFAULT.withHeader().parse(new StringReader(
                    "A,B,,\r\n"
                    + "1,2,3,4\r\n"));
            parser.getRecords();
            fail("Formats that disallow missing column names must reject an automatic header with empty names");
        } catch (final IllegalArgumentException expected) {
            assertTrue(expected.getMessage().length() > 0);
        }
    }

    @Test
    public void testConflictingQuoteCommentAndEscapeCommentMarkersAreRejected() {
        assertIllegalArgument(new Runnable() {
            @Override
            public void run() {
                CSVFormat.newFormat(';').withQuote('"').withCommentMarker('"');
            }
        });

        assertIllegalArgument(new Runnable() {
            @Override
            public void run() {
                CSVFormat.newFormat(';').withEscape('\\').withCommentMarker('\\');
            }
        });
    }

    @Test
    public void testNullableOptionalSettersDisableFeaturesAndMinimalToStringOmitsThem() {
        final CSVFormat format = CSVFormat.DEFAULT
                .withQuote((Character) null)
                .withEscape((Character) null)
                .withCommentMarker((Character) null)
                .withNullString(null)
                .withRecordSeparator((String) null);

        assertNull(format.getQuoteCharacter());
        assertNull(format.getEscapeCharacter());
        assertNull(format.getCommentMarker());
        assertNull(format.getNullString());
        assertNull(format.getRecordSeparator());
        assertFalse(format.isQuoteCharacterSet());
        assertFalse(format.isEscapeCharacterSet());
        assertFalse(format.isCommentMarkerSet());
        assertFalse(format.isNullStringSet());

        assertEquals("Delimiter=<;> SkipHeaderRecord:false", CSVFormat.newFormat(';').toString());
    }

    @Test
    public void testPrintCreatesPrinterAndWritesConfiguredHeaderAndRecord() throws Exception {
        final StringWriter output = new StringWriter();
        final CSVPrinter printer = CSVFormat.DEFAULT.withHeader("name", "email").print(output);

        printer.printRecord("Alice", "alice@example.com");
        printer.close();

        assertEquals("name,email\r\nAlice,alice@example.com\r\n", output.toString());
    }

    @Test
    public void testEqualsDetectsEveryConfiguredSettingDifference() {
        final CSVFormat format = fullyConfiguredFormat();
        final CSVFormat equivalent = fullyConfiguredFormat();

        assertTrue(format.equals(equivalent));
        assertEquals(format.hashCode(), equivalent.hashCode());

        assertFalse(format.equals(format.withDelimiter(':')));
        assertFalse(format.equals(format.withQuoteMode(QuoteMode.MINIMAL)));
        assertFalse(format.equals(format.withQuote('\'')));
        assertFalse(format.equals(format.withCommentMarker('!')));
        assertFalse(format.equals(format.withEscape('/')));
        assertFalse(format.equals(format.withNullString("NIL")));
        assertFalse(format.equals(format.withHeader("other", "email")));
        assertFalse(format.equals(format.withIgnoreSurroundingSpaces(false)));
        assertFalse(format.equals(format.withIgnoreEmptyLines(false)));
        assertFalse(format.equals(format.withSkipHeaderRecord(false)));
        assertFalse(format.equals(format.withRecordSeparator("\r\n")));

        assertFalse(CSVFormat.DEFAULT.equals(CSVFormat.DEFAULT.withQuote((Character) null)));
        assertFalse(CSVFormat.DEFAULT.equals(CSVFormat.DEFAULT.withCommentMarker('#')));
        assertFalse(CSVFormat.DEFAULT.equals(CSVFormat.DEFAULT.withEscape('\\')));
        assertFalse(CSVFormat.newFormat(';').equals(CSVFormat.newFormat(';').withRecordSeparator("\n")));
    }

    private static CSVFormat fullyConfiguredFormat() {
        return CSVFormat.newFormat(';')
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
