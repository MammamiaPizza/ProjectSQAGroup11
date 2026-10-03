@Test
public void testFlushWhenOutNotFlushable() throws java.io.IOException {
    StringBuilder sb = new StringBuilder();
    CSVFormat fmt = CSVFormat.DEFAULT;
    CSVPrinter printer = new CSVPrinter(sb, fmt);
    printer.flush();
    printer.close();
    assertFalse(sb.toString().isEmpty());
}

@Test
public void testPrintValueWithLineFeed() throws java.io.IOException {
    java.io.StringWriter sw = new java.io.StringWriter();
    CSVFormat fmt = CSVFormat.DEFAULT;
    CSVPrinter printer = new CSVPrinter(sw, fmt);
    printer.printRecord("a\nb");
    printer.close();
    String out = sw.toString();
    assertTrue(out.contains(""a\nb""));
}

@Test
public void testPrintValueWithCarriageReturn() throws java.io.IOException {
    java.io.StringWriter sw = new java.io.StringWriter();
    CSVFormat fmt = CSVFormat.DEFAULT;
    CSVPrinter printer = new CSVPrinter(sw, fmt);
    printer.printRecord("a\rb");
    printer.close();
    String out = sw.toString();
    assertTrue(out.contains(""a\rb""));
}

@Test
public void testPrintValueWithDelimiter() throws java.io.IOException {
    java.io.StringWriter sw = new java.io.StringWriter();
    CSVFormat fmt = CSVFormat.DEFAULT;
    CSVPrinter printer = new CSVPrinter(sw, fmt);
    printer.printRecord("A,B");
    printer.close();
    String out = sw.toString();
    assertTrue(out.contains(""A\,B""));
}