@Test
    public void testDuplicateHeadersNoException() throws IOException {
        CSVParser parser = CSVParser.parse("a,a\n1,2", CSVFormat.DEFAULT.withHeader());
        java.util.Map<String, Integer> headerMap = parser.getHeaderMap();
        assertNotNull(headerMap);
        assertEquals(1, headerMap.size());
        assertEquals(Integer.valueOf(0), headerMap.get("a"));
        CSVRecord record = parser.next();
        assertEquals("2", record.get("a"));
    }

 @Test
 public void testGetRecordsWithProvidedCollection() throws IOException {
     CSVParser parser = CSVParser.parse("h1,h2\nv1,v2\nv3,v4", CSVFormat.DEFAULT.withHeader());
     java.util.List<CSVRecord> records = parser.getRecords(new java.util.ArrayList<CSVRecord>());
     assertEquals(2, records.size());
     assertEquals("v1", records.get(0).get("h1"));
     assertEquals("v4", records.get(1).get("h2"));
 }

 @Test
 public void testCloseParser() throws IOException {
     CSVParser parser = CSVParser.parse("a,b\n1,2", CSVFormat.DEFAULT);
     parser.close();
     assertTrue(parser.isClosed());
 }

 @Test
 public void testGetLineAndRecordNumbers() throws IOException {
     CSVParser parser = CSVParser.parse("1,2\n3,4", CSVFormat.DEFAULT);
     java.util.List<CSVRecord> records = parser.getRecords();
     assertEquals(2L, parser.getRecordNumber());
     assertEquals(2L, parser.getCurrentLineNumber());
 }