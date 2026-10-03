package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;

public class CSVPrinterNullRecordSeparatorTest {

    @Test
    public void printlnWithNullRecordSeparatorProducesNoTextAndStartsNewRecord() throws IOException {
        final StringBuilder output = new StringBuilder();
        final CSVPrinter printer = new CSVPrinter(output,
                CSVFormat.DEFAULT.withRecordSeparator((String) null));

        printer.print("first");
        printer.println();
        printer.print("second");

        assertEquals("firstsecond", output.toString());
    }

    @Test
    public void printRecordVarargsWithNullRecordSeparatorPrintsFieldsWithoutLiteralNull() throws IOException {
        final StringBuilder output = new StringBuilder();
        final CSVPrinter printer = new CSVPrinter(output,
                CSVFormat.DEFAULT.withRecordSeparator((String) null));

        printer.printRecord("left", "right");

        assertEquals("left,right", output.toString());
    }

    @Test
    public void printRecordIterableWithNullRecordSeparatorPrintsEachRecordWithoutSeparator() throws IOException {
        final StringBuilder output = new StringBuilder();
        final CSVPrinter printer = new CSVPrinter(output,
                CSVFormat.DEFAULT.withRecordSeparator((String) null));

        printer.printRecord(Arrays.asList("one", "two"));
        printer.printRecord(Arrays.asList("three", "four"));

        assertEquals("one,twothree,four", output.toString());
    }

    @Test
    public void printRecordWithNonNullRecordSeparatorStillTerminatesRecord() throws IOException {
        final StringBuilder output = new StringBuilder();
        final CSVPrinter printer = new CSVPrinter(output,
                CSVFormat.DEFAULT.withRecordSeparator("\n"));

        printer.printRecord("left", "right");

        assertEquals("left,right\n", output.toString());
    }
}