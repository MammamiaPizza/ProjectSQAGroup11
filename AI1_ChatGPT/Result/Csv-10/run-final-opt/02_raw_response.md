package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.Test;

public class CSVPrinterHeaderRegressionTest {

    @Test
    public void constructorPrintsConfiguredHeaderAsFirstRecord() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT.withHeader("C1", "C2", "C3");
        final StringWriter out = new StringWriter();

        new CSVPrinter(out, format);

        assertEquals("C1,C2,C3" + format.getRecordSeparator(), out.toString());
    }

    @Test
    public void headerIsPrintedOnceBeforeSubsequentRecords() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT.withHeader("C1", "C2");
        final StringWriter out = new StringWriter();
        final CSVPrinter printer = new CSVPrinter(out, format);

        printer.printRecord("one", "two");
        printer.printRecord("three", "four");

        assertEquals("C1,C2" + format.getRecordSeparator()
                + "one,two" + format.getRecordSeparator()
                + "three,four" + format.getRecordSeparator(), out.toString());
    }

    @Test
    public void constructorWithoutHeaderDoesNotWriteAnEmptyRecord() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT;
        final StringWriter out = new StringWriter();
        final CSVPrinter printer = new CSVPrinter(out, format);

        assertEquals("", out.toString());

        printer.print("left");
        printer.print("right");
        printer.println();

        assertEquals("left,right" + format.getRecordSeparator(), out.toString());
    }

    @Test
    public void headerValuesUseNormalCsvQuotingRules() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT.withHeader("first,part", "say \"hi\"");
        final StringWriter out = new StringWriter();

        new CSVPrinter(out, format);

        assertEquals("\"first,part\",\"say \"\"hi\"\"\"" + format.getRecordSeparator(), out.toString());
    }

    @Test
    public void printRecordsFollowsTheHeaderWithSeparateRecords() throws IOException {
        final CSVFormat format = CSVFormat.DEFAULT.withHeader("H1", "H2");
        final StringWriter out = new StringWriter();
        final CSVPrinter printer = new CSVPrinter(out, format);

        printer.printRecords(new Object[] {
                new Object[] { "a", "b" },
                new Object[] { "c", "d" }
        });

        assertEquals("H1,H2" + format.getRecordSeparator()
                + "a,b" + format.getRecordSeparator()
                + "c,d" + format.getRecordSeparator(), out.toString());
    }

    @Test(expected = IOException.class)
    public void constructorPropagatesAppendFailureWhenWritingHeader() throws IOException {
        new CSVPrinter(new FailingAppendable(), CSVFormat.DEFAULT.withHeader("C1"));
    }

    private static final class FailingAppendable implements Appendable {

        @Override
        public Appendable append(final CharSequence csq) throws IOException {
            throw new IOException("append failed");
        }

        @Override
        public Appendable append(final CharSequence csq, final int start, final int end) throws IOException {
            throw new IOException("append failed");
        }

        @Override
        public Appendable append(final char c) throws IOException {
            throw new IOException("append failed");
        }
    }
}