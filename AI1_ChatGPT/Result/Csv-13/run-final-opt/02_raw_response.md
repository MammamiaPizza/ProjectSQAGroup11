package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.junit.Test;

public class CSVMySqlNullHandlingTest {

    @Test
    public void mysqlFormatDefinesBackslashNAsItsDefaultNullString() {
        assertEquals("\\N", CSVFormat.MYSQL.getNullString());
    }

    @Test
    public void printerWritesMySqlDefaultNullStringForANullValue() throws Exception {
        final StringWriter output = new StringWriter();
        final CSVPrinter printer = new CSVPrinter(output, CSVFormat.MYSQL);

        printer.print((Object) null);
        printer.println();

        assertEquals("\\N\n", output.toString());
    }

    @Test
    public void printRecordPreservesMySqlDelimitersAroundNullFields() throws Exception {
        final StringWriter output = new StringWriter();
        final CSVPrinter printer = new CSVPrinter(output, CSVFormat.MYSQL);

        printer.printRecord("first", null, "last");

        assertEquals("first\t\\N\tlast\n", output.toString());
    }

    @Test
    public void configuredNullStringIsWrittenInsteadOfLiteralJavaNull() throws Exception {
        final CSVFormat format = CSVFormat.MYSQL.withNullString("NULL");
        final StringWriter output = new StringWriter();
        final CSVPrinter printer = new CSVPrinter(output, format);

        printer.printRecord("value", null);

        assertEquals("value\tNULL\n", output.toString());
        assertEquals("\\N", CSVFormat.MYSQL.getNullString());
    }

    @Test
    public void nullStringCanBeClearedToWriteAnEmptyField() throws Exception {
        final StringWriter output = new StringWriter();
        final CSVPrinter printer = new CSVPrinter(output, CSVFormat.MYSQL.withNullString(null));

        printer.printRecord("before", null, "after");

        assertEquals("before\t\tafter\n", output.toString());
    }
}