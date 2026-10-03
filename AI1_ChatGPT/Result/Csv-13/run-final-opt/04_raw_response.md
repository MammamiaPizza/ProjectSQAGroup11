@org.junit.Test
public void printerPrintThenPrintlnUsesMySqlNullString() throws java.io.IOException {
    final java.io.StringWriter output = new java.io.StringWriter();
    final CSVPrinter printer = new CSVPrinter(output, CSVFormat.MYSQL);

    printer.print(null);
    printer.println();

    org.junit.Assert.assertEquals("\\N\n", output.toString());
}

@org.junit.Test
public void printerWritesMySqlNullStringAtBothRecordBoundaries() throws java.io.IOException {
    final java.io.StringWriter output = new java.io.StringWriter();
    final CSVPrinter printer = new CSVPrinter(output, CSVFormat.MYSQL);

    printer.printRecord(null, "value", null);

    org.junit.Assert.assertEquals("\\N\tvalue\t\\N\n", output.toString());
}