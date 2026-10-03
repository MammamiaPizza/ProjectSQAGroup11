package org.apache.commons.csv;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Iterator;

import org.junit.Test;

public class CSVFormatTest {

    private enum Header {
        ID, Name
    }

    @Test
    public void testPredefinedFormatsAndValueOf() {
        assertSame(CSVFormat.DEFAULT, CSVFormat.valueOf("Default"));
        assertSame(CSVFormat.EXCEL, CSVFormat.Predefined.Excel.getFormat());

        assertEquals(',', CSVFormat.DEFAULT.getDelimiter());
        assertEquals(Character.valueOf('"'), CSVFormat.DEFAULT.getQuoteCharacter());
        assertEquals("\r\n", CSVFormat.DEFAULT.getRecordSeparator());
        assertTrue(CSVFormat.DEFAULT.getIgnoreEmptyLines());

        assertEquals('\t', CSVFormat.MYSQL.getDelimiter());
        assertNull(CSVFormat.MYSQL.getQuoteCharacter());
        assertEquals(Character.valueOf('\\'), CSVFormat.MYSQL.getEscapeCharacter());
        assertEquals("\\N", CSVFormat.MYSQL.getNullString());
        assertFalse(CSVFormat.MYSQL.getIgnoreEmptyLines());
    }

    @Test
    public void testNewFormatHasOnlySpecifiedDelimiterConfigured() {
        final CSVFormat format = CSVFormat.newFormat(';');

        assertEquals(';', format.getDelimiter());
        assertNull(format.getQuoteCharacter());
        assertNull(format.getEscapeCharacter());
        assertNull(format.getCommentMarker());
        assertNull(format.getRecordSeparator());
        assertNull(format.getNullString());
        assertFalse(format.getIgnoreEmptyLines());
        assertFalse(format.getIgnoreSurroundingSpaces());
    }

    @Test
    public void testFluentConfigurationIsImmutableAndPreservesSettings() {
        final CSVFormat original = CSVFormat.DEFAULT;
        final CSVFormat configured = original
                .withDelimiter(';')
                .withCommentMarker('#')
                .withEscape('\\')
                .withNullString("NULL")
                .withIgnoreEmptyLines(false)
                .withIgnoreHeaderCase()
                .withIgnoreSurroundingSpaces()
                .withSkipHeaderRecord()
                .withTrailingDelimiter()
                .withTrim();

        assertNotSame(original, configured);
        assertEquals(',', original.getDelimiter());
        assertNull(original.getEscapeCharacter());

        assertEquals(';', configured.getDelimiter());
        assertEquals(Character.valueOf('#'), configured.getCommentMarker());
        assertEquals(Character.valueOf('\\'), configured.getEscapeCharacter());
        assertEquals("NULL", configured.getNullString());
        assertFalse(configured.getIgnoreEmptyLines());
        assertTrue(configured.getIgnoreHeaderCase());
        assertTrue(configured.getIgnoreSurroundingSpaces());
        assertTrue(configured.getSkipHeaderRecord());
        assertTrue(configured.getTrailingDelimiter());
        assertTrue(configured.getTrim());
    }

    @Test
    public void testHeaderAndHeaderCommentsAreDefensivelyCopied() {
        final String[] header = { "id", "name" };
        final Object[] comments = { "created", Integer.valueOf(7), null };

        final CSVFormat format = CSVFormat.DEFAULT.withHeader(header).withHeaderComments(comments);

        header[0] = "changed";
        comments[0] = "changed";

        assertArrayEquals(new String[] { "id", "name" }, format.getHeader());
        assertArrayEquals(new String[] { "created", "7", null }, format.getHeaderComments());

        final String[] returnedHeader = format.getHeader();
        final String[] returnedComments = format.getHeaderComments();
        returnedHeader[1] = "mutated";
        returnedComments[1] = "mutated";

        assertArrayEquals(new String[] { "id", "name" }, format.getHeader());
        assertArrayEquals(new String[] { "created", "7", null }, format.getHeaderComments());
    }

    @Test
    public void testHeaderFromEnumAndFirstRecordAsHeader() throws Exception {
        final CSVFormat enumFormat = CSVFormat.DEFAULT.withHeader(Header.class);
        assertArrayEquals(new String[] { "ID", "Name" }, enumFormat.getHeader());

        final CSVFormat format = CSVFormat.DEFAULT.withFirstRecordAsHeader();
        assertArrayEquals(new String[0], format.getHeader());
        assertTrue(format.getSkipHeaderRecord());

        final CSVParser parser = format.parse(new StringReader("Name,Age\r\nAlice,30\r\n"));
        try {
            final Iterator<CSVRecord> records = parser.iterator();
            assertTrue(records.hasNext());

            final CSVRecord record = records.next();
            assertEquals("Alice", record.get("Name"));
            assertEquals("30", record.get("Age"));
            assertFalse(records.hasNext());
        } finally {
            parser.close();
        }
    }

    @Test
    public void testQuotePoliciesAndMinimalQuoting() {
        assertEquals("\"text\",\"12\"",
                CSVFormat.DEFAULT.withQuoteMode(QuoteMode.ALL).format("text", Integer.valueOf(12)));

        assertEquals("\"text\",12",
                CSVFormat.DEFAULT.withQuoteMode(QuoteMode.NON_NUMERIC).format("text", Integer.valueOf(12)));

        assertEquals("\"a,b\"", CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL).format("a,b"));

        assertEquals("a\\,b",
                CSVFormat.DEFAULT.withEscape('\\').withQuoteMode(QuoteMode.NONE).format("a,b"));
    }

    @Test
    public void testEscapeCharacterEscapesBackslashInsteadOfQuotingIt() {
        final CSVFormat format = CSVFormat.DEFAULT.withEscape('\\');

        assertEquals("\\\\", format.format("\\"));

        final StringWriter out = new StringWriter();
        try {
            format.print("\\", out, true);
        } catch (final Exception e) {
            fail("Printing to StringWriter should not fail: " + e);
        }
        assertEquals("\\\\", out.toString());
    }

    @Test
    public void testNullStringIsWrittenRawEvenWhenQuoteAndEscapeAreConfigured() {
        final CSVFormat format = CSVFormat.DEFAULT.withEscape('\\').withNullString("\\");

        assertEquals("\\", format.format((Object) null));

        final StringWriter out = new StringWriter();
        try {
            format.print(null, out, true);
        } catch (final Exception e) {
            fail("Printing to StringWriter should not fail: " + e);
        }
        assertEquals("\\", out.toString());
    }

    @Test
    public void testPrintRecordTrimAndTrailingDelimiter() throws Exception {
        final CSVFormat format = CSVFormat.DEFAULT
                .withTrim()
                .withTrailingDelimiter()
                .withRecordSeparator('\n');

        final StringWriter out = new StringWriter();
        format.printRecord(out, "  first  ", " second ");

        assertEquals("first,second,\n", out.toString());
    }

    @Test
    public void testPrintAndParseNullStringAndEscapedDelimiter() throws Exception {
        final CSVFormat format = CSVFormat.newFormat(',')
                .withQuote(null)
                .withEscape('\\')
                .withNullString("NULL")
                .withRecordSeparator('\n');

        final StringWriter out = new StringWriter();
        format.printRecord(out, "a,b", null);

        assertEquals("a\\,b,NULL\n", out.toString());

        final CSVParser parser = format.parse(new StringReader(out.toString()));
        try {
            final CSVRecord record = parser.iterator().next();
            assertEquals("a,b", record.get(0));
            assertNull(record.get(1));
        } finally {
            parser.close();
        }
    }

    @Test
    public void testInvalidDelimiterAndCharacterCombinationsAreRejected() {
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
                CSVFormat.DEFAULT.withEscape(',').withQuote(null);
            }
        });
        assertIllegalArgument(new Runnable() {
            @Override
            public void run() {
                CSVFormat.DEFAULT.withHeader("duplicate", "duplicate");
            }
        });
        assertIllegalArgument(new Runnable() {
            @Override
            public void run() {
                CSVFormat.DEFAULT.withQuote(null).withQuoteMode(QuoteMode.NONE);
            }
        });
    }

    @Test
    public void testFormatEqualityHashCodeAndStringRepresentation() {
        final CSVFormat first = CSVFormat.DEFAULT
                .withDelimiter(';')
                .withEscape('\\')
                .withCommentMarker('#')
                .withNullString("NULL")
                .withHeader("a", "b");

        final CSVFormat equal = CSVFormat.DEFAULT
                .withDelimiter(';')
                .withEscape('\\')
                .withCommentMarker('#')
                .withNullString("NULL")
                .withHeader("a", "b");

        final CSVFormat different = equal.withDelimiter('|');

        assertEquals(first, equal);
        assertEquals(first.hashCode(), equal.hashCode());
        assertFalse(first.equals(different));

        final String description = first.toString();
        assertTrue(description.contains("Delimiter=<;>"));
        assertTrue(description.contains("Escape=<\\>"));
        assertTrue(description.contains("QuoteChar=<\">"));
        assertTrue(description.contains("CommentStart=<#>"));
        assertTrue(description.contains("NullString=<NULL>"));
        assertTrue(description.contains("Header:[a, b]"));
    }

    private static void assertIllegalArgument(final Runnable runnable) {
        try {
            runnable.run();
            fail("Expected IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            assertTrue(expected.getMessage().length() > 0);
        }
    }
}
