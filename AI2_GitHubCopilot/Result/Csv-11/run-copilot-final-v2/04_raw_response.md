@Test
public void testParseFromFile() throws Exception {
    java.io.File tempFile = java.io.File.createTempFile("csv", ".csv");
    tempFile.deleteOnExit();
    java.io.PrintWriter writer = new java.io.PrintWriter(tempFile);
    writer.println("name,age");
    writer.println("John,30");
    writer.close();
    CSVParser parser = CSVParser.parse(tempFile, java.nio.charset.StandardCharsets.UTF_8,
            CSVFormat.DEFAULT.withHeader());
    assertNotNull(parser);
    java.util.List<CSVRecord> records = parser.getRecords();
    assertEquals(1, records.size());
    assertEquals("John", records.get(0).get("name"));
    assertEquals("30", records.get(0).get("age"));
    // cover getCurrentLineNumber
    assertTrue(parser.getCurrentLineNumber() > 0);
    parser.close();
}

@Test
public void testEmptyInputWithHeader() throws Exception {
    CSVParser parser = CSVParser.parse("", CSVFormat.DEFAULT.withHeader("col1", "col2"));
    assertNotNull(parser.getHeaderMap());
    assertEquals(2, parser.getHeaderMap().size());
    assertTrue(parser.getRecords().isEmpty());
    parser.close();
}

@Test
public void testCloseMultipleTimes() throws Exception {
    CSVParser parser = CSVParser.parse("a,b\n1,2", CSVFormat.DEFAULT.withHeader());
    parser.close();
    // second close must not throw; covers lexer-null branch
    parser.close();
}

@Test
public void testCommentHandling() throws Exception {
    CSVFormat format = CSVFormat.DEFAULT.withCommentMarker('#').withHeader();
    CSVParser parser = CSVParser.parse("# comment1\n# comment2\nname,age\nJohn,30", format);
    java.util.List<CSVRecord> records = parser.getRecords();
    assertEquals(1, records.size());
    assertEquals("John", records.get(0).get("name"));
    assertEquals("30", records.get(0).get("age"));
    parser.close();
}