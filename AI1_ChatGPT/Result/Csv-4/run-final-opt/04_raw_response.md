@Test
public void configuredNullStringIsMatchedIgnoringCase() throws Exception {
    final CSVParser parser = new CSVParser(new java.io.StringReader("NULL,NuLl,value\n"),
            CSVFormat.DEFAULT.withNullString("NULL"));
    try {
        final CSVRecord record = parser.getRecords().get(0);
        org.junit.Assert.assertNull(record.get(0));
        org.junit.Assert.assertNull(record.get(1));
        org.junit.Assert.assertEquals("value", record.get(2));
    } finally {
        parser.close();
    }
}

@Test
public void explicitHeadersAreAvailableAndReturnedMapIsIndependent() throws Exception {
    final CSVParser parser = new CSVParser(new java.io.StringReader("a,b\n"),
            CSVFormat.DEFAULT.withHeader("first", "second"));
    try {
        org.junit.Assert.assertEquals(Integer.valueOf(0), parser.getHeaderMap().get("first"));
        org.junit.Assert.assertEquals(Integer.valueOf(1), parser.getHeaderMap().get("second"));

        parser.getHeaderMap().clear();

        org.junit.Assert.assertEquals(Integer.valueOf(0), parser.getHeaderMap().get("first"));
        org.junit.Assert.assertEquals(Integer.valueOf(1), parser.getHeaderMap().get("second"));
    } finally {
        parser.close();
    }
}

@Test
public void getRecordsAdvancesRecordNumberAndLineNumber() throws Exception {
    final CSVParser parser = new CSVParser(new java.io.StringReader("a,b\nc,d\n"), CSVFormat.DEFAULT);
    try {
        final long initialLineNumber = parser.getCurrentLineNumber();

        final java.util.List<CSVRecord> records = parser.getRecords();

        org.junit.Assert.assertEquals(2, records.size());
        org.junit.Assert.assertEquals(2L, parser.getRecordNumber());
        org.junit.Assert.assertTrue(parser.getCurrentLineNumber() > initialLineNumber);
    } finally {
        parser.close();
    }
}

@Test
public void closeChangesClosedState() throws Exception {
    final CSVParser parser = new CSVParser(new java.io.StringReader("a,b\n"), CSVFormat.DEFAULT);

    org.junit.Assert.assertFalse(parser.isClosed());

    parser.close();

    org.junit.Assert.assertTrue(parser.isClosed());
}