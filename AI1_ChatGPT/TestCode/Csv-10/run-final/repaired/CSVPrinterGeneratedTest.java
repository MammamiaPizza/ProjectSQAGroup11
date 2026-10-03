package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Arrays;

import org.junit.Test;

public class CSVPrinterGeneratedTest {

    @Test
    public void constructorPrintsConfiguredHeaderImmediately() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT.withHeader("C1", "C2", "C3");
        final StringBuilder output = new StringBuilder();

        new CSVPrinter(output, format);

        assertEquals("C1,C2,C3" + format.getRecordSeparator(), output.toString());
    }

    @Test
    public void constructorDoesNotPrintHeaderWhenHeaderRecordIsSkipped() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT.withHeader("C1", "C2").withSkipHeaderRecord(true);
        final StringBuilder output = new StringBuilder();

        new CSVPrinter(output, format);

        assertEquals("", output.toString());
    }

    @Test
    public void constructorRejectsNullOutput() throws IOException {
        try {
            new CSVPrinter((Appendable) null, CSVFormat.DEFAULT);
            fail("Expected IllegalArgumentException for null output");
        } catch (final IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("out"));
        }
    }

    @Test
    public void constructorRejectsNullFormat() throws IOException {
        try {
            new CSVPrinter(new StringBuilder(), null);
            fail("Expected IllegalArgumentException for null format");
        } catch (final IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("format"));
        }
    }

    @Test
    public void printUsesMinimalQuotingAndEscapesEmbeddedQuotes() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT;
        final StringBuilder output = new StringBuilder();
        final CSVPrinter printer = new CSVPrinter(output, format);

        printer.printRecord("plain", "a,b", "say \"hello\"", "line1\nline2", null);

        assertEquals(
                "plain,\"a,b\",\"say \"\"hello\"\"\",\"line1\nline2\","
                        + format.getRecordSeparator(),
                output.toString());
    }

    @Test
    public void printQuotesEmptyFirstValueButNotEmptySubsequentValue() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT;
        final StringBuilder output = new StringBuilder();
        final CSVPrinter printer = new CSVPrinter(output, format);

        printer.print("");
        printer.print("");
        printer.println();

        assertEquals("\"\","
                + format.getRecordSeparator(), output.toString());
    }

    @Test
    public void printUsesConfiguredNullString() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT.withNullString("NULL");
        final StringBuilder output = new StringBuilder();
        final CSVPrinter printer = new CSVPrinter(output, format);

        printer.printRecord("value", null);

        assertEquals("value,NULL" + format.getRecordSeparator(), output.toString());
    }

    @Test
    public void quoteAllQuotesNumbersAndStrings() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT.withQuotePolicy(Quote.ALL);
        final StringBuilder output = new StringBuilder();
        final CSVPrinter printer = new CSVPrinter(output, format);

        printer.printRecord(Integer.valueOf(12), "text");

        assertEquals("\"12\",\"text\"" + format.getRecordSeparator(), output.toString());
    }

    @Test
    public void quoteNonNumericLeavesNumbersUnquoted() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT.withQuotePolicy(Quote.NON_NUMERIC);
        final StringBuilder output = new StringBuilder();
        final CSVPrinter printer = new CSVPrinter(output, format);

        printer.printRecord(Integer.valueOf(12), "text");

        assertEquals("12,\"text\"" + format.getRecordSeparator(), output.toString());
    }

    @Test
    public void quoteNoneUsesEscapeCharacterForSpecialCharacters() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT
                .withQuotePolicy(Quote.NONE)
                .withEscape('\\');
        final StringBuilder output = new StringBuilder();
        final CSVPrinter printer = new CSVPrinter(output, format);

        printer.printRecord("a,b", "a\\b", "a\nb", "a\rb");

        assertEquals("a\\,b,a\\\\b,a\\nb,a\\rb"
                + format.getRecordSeparator(), output.toString());
    }

    @Test
    public void printCommentStartsOnNewLineAndHandlesAllLineEndings() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT.withCommentStart('#');
        final StringBuilder output = new StringBuilder();
        final CSVPrinter printer = new CSVPrinter(output, format);

        printer.print("value");
        printer.printComment("first\r\nsecond\nthird\rfourth");

        final String separator = format.getRecordSeparator();
        assertEquals("value" + separator
                + "# first" + separator
                + "# second" + separator
                + "# third" + separator
                + "# fourth" + separator,
                output.toString());
    }

    @Test
    public void printCommentDoesNothingWhenCommentsAreDisabled() throws IOException {
        final StringBuilder output = new StringBuilder();
        final CSVPrinter printer = new CSVPrinter(output, CSVFormat.DEFAULT);

        printer.printComment("ignored");

        assertEquals("", output.toString());
    }

    @Test
    public void printlnWithNullRecordSeparatorStillStartsANewRecord() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT.withRecordSeparator(null);
        final StringBuilder output = new StringBuilder();
        final CSVPrinter printer = new CSVPrinter(output, format);

        printer.print("first");
        printer.println();
        printer.print("second");

        assertEquals("firstsecond", output.toString());
    }

    @Test
    public void printRecordsHandlesArraysIterablesAndScalarValues() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT;
        final StringBuilder output = new StringBuilder();
        final CSVPrinter printer = new CSVPrinter(output, format);

        printer.printRecords(new Object[] {
                new Object[] { "a", "b" },
                Arrays.asList("c", "d"),
                "e"
        });

        assertEquals("a,b" + format.getRecordSeparator()
                + "c,d" + format.getRecordSeparator()
                + "e" + format.getRecordSeparator(),
                output.toString());
    }

    @Test
    public void printRecordsIterableHandlesArraysIterablesAndScalarValues() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT;
        final StringBuilder output = new StringBuilder();
        final CSVPrinter printer = new CSVPrinter(output, format);

        printer.printRecords(Arrays.<Object>asList(
                new Object[] { "a", "b" },
                Arrays.asList("c", "d"),
                "e"));

        assertEquals("a,b" + format.getRecordSeparator()
                + "c,d" + format.getRecordSeparator()
                + "e" + format.getRecordSeparator(),
                output.toString());
    }

    @Test
    public void printRecordsResultSetPrintsEveryColumnOfEveryRow() throws Exception {
        final CSVFormat format = CSVFormat.DEFAULT;
        final StringBuilder output = new StringBuilder();
        final CSVPrinter printer = new CSVPrinter(output, format);
        final ResultSet resultSet = resultSet(new String[][] {
                { "a", "b" },
                { "c", "d" }
        });

        printer.printRecords(resultSet);

        assertEquals("a,b" + format.getRecordSeparator()
                + "c,d" + format.getRecordSeparator(),
                output.toString());
    }

    @Test
    public void flushAndCloseDelegateWhenAppendableSupportsThem() throws IOException {
        final TrackingAppendable output = new TrackingAppendable();
        final CSVPrinter printer = new CSVPrinter(output, CSVFormat.DEFAULT);

        assertSame(output, printer.getOut());

        printer.flush();
        printer.close();

        assertTrue(output.flushed);
        assertTrue(output.closed);
    }

    @Test
    public void flushAndCloseDoNothingForPlainAppendable() throws IOException {
        final StringBuilder output = new StringBuilder();
        final CSVPrinter printer = new CSVPrinter(output, CSVFormat.DEFAULT);

        printer.flush();
        printer.close();

        assertEquals("", output.toString());
    }

    @Test
    public void printPropagatesAppendableIOException() throws IOException {
        final CSVPrinter printer = new CSVPrinter(new FailingAppendable(), CSVFormat.DEFAULT);

        try {
            printer.print("value");
            fail("Expected IOException from Appendable");
        } catch (final IOException expected) {
            assertEquals("append failure", expected.getMessage());
        }
    }

    private static ResultSet resultSet(final String[][] rows) {
        final ResultSetMetaData metadata = (ResultSetMetaData) Proxy.newProxyInstance(
                CSVPrinterGeneratedTest.class.getClassLoader(),
                new Class<?>[] { ResultSetMetaData.class },
                new InvocationHandler() {
                    @Override
                    public Object invoke(final Object proxy, final Method method, final Object[] args) {
                        if ("getColumnCount".equals(method.getName())) {
                            return Integer.valueOf(rows[0].length);
                        }
                        throw new UnsupportedOperationException(method.getName());
                    }
                });

        return (ResultSet) Proxy.newProxyInstance(
                CSVPrinterGeneratedTest.class.getClassLoader(),
                new Class<?>[] { ResultSet.class },
                new InvocationHandler() {
                    private int row = -1;

                    @Override
                    public Object invoke(final Object proxy, final Method method, final Object[] args) {
                        if ("getMetaData".equals(method.getName())) {
                            return metadata;
                        }
                        if ("next".equals(method.getName())) {
                            row++;
                            return Boolean.valueOf(row < rows.length);
                        }
                        if ("getString".equals(method.getName())) {
                            final int columnIndex = ((Integer) args[0]).intValue();
                            return rows[row][columnIndex - 1];
                        }
                        if ("isClosed".equals(method.getName())) {
                            return Boolean.FALSE;
                        }
                        throw new UnsupportedOperationException(method.getName());
                    }
                });
    }

    private static final class TrackingAppendable implements Appendable, Flushable, Closeable {
        private final StringBuilder delegate = new StringBuilder();
        private boolean flushed;
        private boolean closed;

        @Override
        public Appendable append(final CharSequence csq) {
            delegate.append(csq);
            return this;
        }

        @Override
        public Appendable append(final CharSequence csq, final int start, final int end) {
            delegate.append(csq, start, end);
            return this;
        }

        @Override
        public Appendable append(final char c) {
            delegate.append(c);
            return this;
        }

        @Override
        public void flush() {
            flushed = true;
        }

        @Override
        public void close() {
            closed = true;
        }

        @Override
        public String toString() {
            return delegate.toString();
        }
    }

    private static final class FailingAppendable implements Appendable {
        @Override
        public Appendable append(final CharSequence csq) throws IOException {
            throw new IOException("append failure");
        }

        @Override
        public Appendable append(final CharSequence csq, final int start, final int end) throws IOException {
            throw new IOException("append failure");
        }

        @Override
        public Appendable append(final char c) throws IOException {
            throw new IOException("append failure");
        }
    }
}
