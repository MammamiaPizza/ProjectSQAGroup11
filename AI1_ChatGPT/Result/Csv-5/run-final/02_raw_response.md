```java
package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import java.util.List;

import org.junit.Test;

public class CSVPrinterGeneratedTest {

    @Test
    public void testNullRecordSeparatorDoesNotWriteLiteralNullCsv106() throws Exception {
        final StringBuilder out = new StringBuilder();
        final CSVFormat format = CSVFormat.DEFAULT.withRecordSeparator(null);
        final CSVPrinter printer = new CSVPrinter(out, format);

        printer.print("left");
        printer.println();
        printer.print("right");

        assertEquals("leftright", out.toString());
    }

    @Test
    public void testPrintRecordQuotesMinimalSpecialValuesAndNull() throws Exception {
        final StringBuilder out = new StringBuilder();
        final CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withRecordSeparator("\n"));

        printer.printRecord("plain", "a,b", "a\"b", null);

        assertEquals("plain,\"a,b\",\"a\"\"b\",\n", out.toString());
    }

    @Test
    public void testMinimalQuotingHandlesEmptyLeadingCommentLikeAndTrailingWhitespaceValues() throws Exception {
        final StringBuilder out = new StringBuilder();
        final CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withRecordSeparator("\n"));

        printer.printRecord("", " simple", "#comment", "end ", "safe");

        assertEquals("\"\",\" simple\",\"#comment\",\"end \",safe\n", out.toString());
    }

    @Test
    public void testQuoteAllAndNonNumericPolicies() throws Exception {
        final StringBuilder allOut = new StringBuilder();
        final CSVPrinter allPrinter = new CSVPrinter(allOut,
                CSVFormat.DEFAULT.withQuotePolicy(Quote.ALL).withRecordSeparator("\n"));

        allPrinter.printRecord(Integer.valueOf(12), "text", null);

        assertEquals("\"12\",\"text\",\"\"\n", allOut.toString());

        final StringBuilder nonNumericOut = new StringBuilder();
        final CSVPrinter nonNumericPrinter = new CSVPrinter(nonNumericOut,
                CSVFormat.DEFAULT.withQuotePolicy(Quote.NON_NUMERIC).withRecordSeparator("\n"));

        nonNumericPrinter.printRecord(Integer.valueOf(12), "12", null);

        assertEquals("12,\"12\",\"\"\n", nonNumericOut.toString());
    }

    @Test
    public void testEscapingWithoutQuoteCharacterEscapesDelimiterLineBreaksAndEscapeCharacter() throws Exception {
        final StringBuilder out = new StringBuilder();
        final CSVFormat format = CSVFormat.DEFAULT
                .withQuoteChar(null)
                .withEscape('\\')
                .withRecordSeparator("\n");
        final CSVPrinter printer = new CSVPrinter(out, format);

        printer.printRecord("a,b\nc\rd\\e");

        assertEquals("a\\,b\\nc\\rd\\\\e\n", out.toString());
    }

    @Test
    public void testConfiguredNullStringIsPrintedForNullValue() throws Exception {
        final StringBuilder out = new StringBuilder();
        final CSVFormat format = CSVFormat.DEFAULT
                .withNullString("NULL")
                .withRecordSeparator("\n");
        final CSVPrinter printer = new CSVPrinter(out, format);

        printer.printRecord((Object) null);

        assertEquals("NULL\n", out.toString());
    }

    @Test
    public void testCommentsAreDisabledByDefaultEvenForNullComment() throws Exception {
        final StringBuilder out = new StringBuilder();
        final CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withRecordSeparator("\n"));

        printer.printComment(null);

        assertEquals("", out.toString());
    }

    @Test
    public void testCommentStartsOnNewRecordAndSplitsAllLineEndingVariants() throws Exception {
        final StringBuilder out = new StringBuilder();
        final CSVFormat format = CSVFormat.DEFAULT
                .withCommentStart('#')
                .withRecordSeparator("\n");
        final CSVPrinter printer = new CSVPrinter(out, format);

        printer.print("value");
        printer.printComment("first\r\nsecond\nthird\rfourth");

        assertEquals("value\n# first\n# second\n# third\n# fourth\n", out.toString());
    }

    @Test
    public void testPrintRecordsArrayDispatchesArrayIterableAndScalarRows() throws Exception {
        final StringBuilder out = new StringBuilder();
        final CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withRecordSeparator("\n"));

        final Object[] rows = {
                new Object[] { "a", "b" },
                Arrays.asList("c", "d"),
                "e"
        };
        printer.printRecords(rows);

        assertEquals("a,b\nc,d\ne\n", out.toString());
    }

    @Test
    public void testPrintRecordsIterableDispatchesNestedIterableAndScalarRows() throws Exception {
        final StringBuilder out = new StringBuilder();
        final CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withRecordSeparator("\n"));

        final List<Object> rows = Arrays.<Object>asList(Arrays.asList("x", "y"), "z");
        printer.printRecords(rows);

        assertEquals("x,y\nz\n", out.toString());
    }

    @Test
    public void testPrintRecordsFromResultSetPrintsEveryColumnOfEveryRow() throws Exception {
        final StringBuilder out = new StringBuilder();
        final CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withRecordSeparator("\n"));

        final String[][] rows = {
                { "one", "two" },
                { "three", null }
        };

        final ResultSetMetaData metadata = (ResultSetMetaData) Proxy.newProxyInstance(
                ResultSetMetaData.class.getClassLoader(),
                new Class<?>[] { ResultSetMetaData.class },
                new InvocationHandler() {
                    public Object invoke(final Object proxy, final Method method, final Object[] args) {
                        if ("getColumnCount".equals(method.getName())) {
                            return Integer.valueOf(2);
                        }
                        throw new UnsupportedOperationException(method.getName());
                    }
                });

        final ResultSet resultSet = (ResultSet) Proxy.newProxyInstance(
                ResultSet.class.getClassLoader(),
                new Class<?>[] { ResultSet.class },
                new InvocationHandler() {
                    private int row = -1;

                    public Object invoke(final Object proxy, final Method method, final Object[] args) {
                        if ("getMetaData".equals(method.getName())) {
                            return metadata;
                        }
                        if ("next".equals(method.getName())) {
                            row++;
                            return Boolean.valueOf(row < rows.length);
                        }
                        if ("getString".equals(method.getName())) {
                            final int column = ((Integer) args[0]).intValue();
                            return rows[row][column - 1];
                        }
                        throw new UnsupportedOperationException(method.getName());
                    }
                });

        printer.printRecords(resultSet);

        assertEquals("one,two\nthree,\n", out.toString());
    }

    @Test
    public void testFlushCloseAndGetOutDelegateToAppendableWhenSupported() throws Exception {
        final TrackingAppendable out = new TrackingAppendable();
        final CSVPrinter printer = new CSVPrinter(out, CSVFormat.DEFAULT.withRecordSeparator("\n"));

        printer.print("value");
        printer.flush();
        printer.close();

        assertSame(out, printer.getOut());
        assertEquals("value", out.contents.toString());
        assertTrue(out.flushed);
        assertTrue(out.closed);
    }

    @Test
    public void testConstructorRejectsNullOutAndNullFormat() {
        try {
            new CSVPrinter(null, CSVFormat.DEFAULT);
            fail("Expected IllegalArgumentException for null output");
        } catch (final IllegalArgumentException expected) {
            // Expected.
        }

        try {
            new CSVPrinter(new StringBuilder(), null);
            fail("Expected IllegalArgumentException for null format");
        } catch (final IllegalArgumentException expected) {
            // Expected.
        }
    }

    @Test
    public void testAppendIOExceptionIsPropagated() throws Exception {
        final CSVPrinter printer = new CSVPrinter(new FailingAppendable(), CSVFormat.DEFAULT);

        try {
            printer.print("value");
            fail("Expected IOException from underlying Appendable");
        } catch (final IOException expected) {
            assertEquals("append failure", expected.getMessage());
        }
    }

    private static final class TrackingAppendable implements Appendable, Flushable, Closeable {
        private final StringBuilder contents = new StringBuilder();
        private boolean flushed;
        private boolean closed;

        public Appendable append(final CharSequence csq) {
            contents.append(csq);
            return this;
        }

        public Appendable append(final CharSequence csq, final int start, final int end) {
            contents.append(csq, start, end);
            return this;
        }

        public Appendable append(final char c) {
            contents.append(c);
            return this;
        }

        public void flush() {
            flushed = true;
        }

        public void close() {
            closed = true;
        }
    }

    private static final class FailingAppendable implements Appendable {
        public Appendable append(final CharSequence csq) throws IOException {
            throw new IOException("append failure");
        }

        public Appendable append(final CharSequence csq, final int start, final int end) throws IOException {
            throw new IOException("append failure");
        }

        public Appendable append(final char c) throws IOException {
            throw new IOException("append failure");
        }
    }
}
```

Test coverage summary:

- `testNullRecordSeparatorDoesNotWriteLiteralNullCsv106`: Targets CSV-106. A `null` record separator must not be appended as the literal text `"null"` while still starting a new record.
- `testPrintRecordQuotesMinimalSpecialValuesAndNull`: Covers ordinary values, delimiters, embedded quote doubling, null-value handling, and record termination.
- `testMinimalQuotingHandlesEmptyLeadingCommentLikeAndTrailingWhitespaceValues`: Exercises minimal-quoting branches for an empty first value, leading whitespace, comment-like values, trailing whitespace, and unquoted safe content.
- `testQuoteAllAndNonNumericPolicies`: Covers `Quote.ALL` and `Quote.NON_NUMERIC`.
- `testEscapingWithoutQuoteCharacterEscapesDelimiterLineBreaksAndEscapeCharacter`: Covers escape-mode handling of delimiters, CR/LF, and the escape character itself.
- `testConfiguredNullStringIsPrintedForNullValue`: Verifies configured null-string substitution.
- `testCommentsAreDisabledByDefaultEvenForNullComment`: Covers the early return when comments are disabled.
- `testCommentStartsOnNewRecordAndSplitsAllLineEndingVariants`: Covers enabled comments, termination of a partial record, CRLF/LF/CR splitting, and comment prefixes.
- `testPrintRecordsArrayDispatchesArrayIterableAndScalarRows`: Covers `printRecords(Object[])` dispatch paths.
- `testPrintRecordsIterableDispatchesNestedIterableAndScalarRows`: Covers `printRecords(Iterable<?>)` dispatch paths.
- `testPrintRecordsFromResultSetPrintsEveryColumnOfEveryRow`: Covers JDBC `ResultSet` iteration, metadata column count retrieval, and null database values.
- `testFlushCloseAndGetOutDelegateToAppendableWhenSupported`: Covers `Flushable`, `Closeable`, and `getOut`.
- `testConstructorRejectsNullOutAndNullFormat`: Covers constructor argument validation.
- `testAppendIOExceptionIsPropagated`: Verifies `IOException` propagation from the target `Appendable`.