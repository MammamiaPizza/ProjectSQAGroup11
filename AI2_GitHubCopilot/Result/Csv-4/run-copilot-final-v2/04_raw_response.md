@Test
    public void testConstructorWithValidFormat() throws java.io.IOException {
        java.io.StringReader reader = new java.io.StringReader("a,b,c\n1,2,3");
        CSVFormat format = CSVFormat.DEFAULT;
        CSVParser parser = new CSVParser(reader, format);
        org.junit.Assert.assertNotNull(parser);
        org.junit.Assert.assertEquals(0, parser.getRecordNumber());
        org.junit.Assert.assertFalse(parser.isClosed());
        parser.close();
    }

 @Test
 public void testParseWithNullString() throws java.io.IOException {
     CSVFormat format = CSVFormat.DEFAULT.withNullString("NULL");
     CSVParser parser = CSVParser.parse("value,NULL,other", format);
     java.util.List<CSVRecord> records = parser.getRecords();
     org.junit.Assert.assertEquals(1, records.size());
     CSVRecord record = records.get(0);
     org.junit.Assert.assertNull(record.get(1));
     org.junit.Assert.assertEquals("value", record.get(0));
     org.junit.Assert.assertEquals("other", record.get(2));
 }

 @Test
 public void testClose() throws java.io.IOException {
     java.io.StringReader reader = new java.io.StringReader("a,b");
     CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
     parser.close();
     org.junit.Assert.assertTrue(parser.isClosed());
 }

 @Test
 public void testGetRecordsMultipleRecords() throws java.io.IOException {
     CSVParser parser = CSVParser.parse("a,b\nc,d\ne,f", CSVFormat.DEFAULT);
     java.util.List<CSVRecord> records = parser.getRecords();
     org.junit.Assert.assertEquals(2, records.size());
     org.junit.Assert.assertEquals("c", records.get(1).get(0));
     org.junit.Assert.assertEquals("d", records.get(1).get(1));
 }