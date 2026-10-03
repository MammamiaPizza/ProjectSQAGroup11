@Test
public void testPrintNullValue() throws IOException {
    CSVFormat format = CSVFormat.DEFAULT;
    StringBuilder sb = new StringBuilder();
    CSVPrinter printer = new CSVPrinter(sb, format);
    printer.print("hello");
    printer.print(null);
    printer.print("world");
    printer.flush();
    assertEquals("helloworld", sb.toString());
}

@Test
public void testPrintWithEscaping() throws IOException {
    CSVFormat format = CSVFormat.DEFAULT.withEscape('\').withQuoteChar(null);
    StringBuilder sb = new StringBuilder();
    CSVPrinter printer = new CSVPrinter(sb, format);
    printer.print("a,b\nc\d\r");
    printer.flush();
    assertEquals("a\,b\nc\\d\r", sb.toString());
}

@Test
public void testCloseAndFlushWithCloseableFlushable() throws IOException {
    java.io.StringWriter sw = new java.io.StringWriter();
    CSVPrinter printer = new CSVPrinter(sw, CSVFormat.DEFAULT);
    printer.print("test");
    printer.flush();
    printer.close();
    assertEquals("test", sw.toString());
}