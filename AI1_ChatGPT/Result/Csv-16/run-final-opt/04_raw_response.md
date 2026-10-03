@org.junit.Test
public void testGetRecordsAppliesTrimAndNullString() throws Exception {
    final org.apache.commons.csv.CSVParser parser = org.apache.commons.csv.CSVParser.parse(
            " value , NULL \n", org.apache.commons.csv.CSVFormat.DEFAULT.withTrim().withNullString("NULL"));
    try {
        final org.apache.commons.csv.CSVRecord record = parser.getRecords().get(0);
        org.junit.Assert.assertEquals("value", record.get(0));
        org.junit.Assert.assertNull(record.get(1));
    } finally {
        parser.close();
    }
}

@org.junit.Test
public void testFirstRecordHeaderMapIsCopiedAndDataRemainsAvailable() throws Exception {
    final org.apache.commons.csv.CSVParser parser = org.apache.commons.csv.CSVParser.parse(
            "first,second\n1,2\n", org.apache.commons.csv.CSVFormat.DEFAULT.withFirstRecordAsHeader());
    try {
        final java.util.Map<String, Integer> headers = parser.getHeaderMap();
        org.junit.Assert.assertEquals(Integer.valueOf(0), headers.get("first"));
        org.junit.Assert.assertEquals(Integer.valueOf(1), headers.get("second"));

        headers.clear();
        org.junit.Assert.assertEquals(Integer.valueOf(0), parser.getHeaderMap().get("first"));

        final org.apache.commons.csv.CSVRecord record = parser.getRecords().get(0);
        org.junit.Assert.assertEquals("1", record.get("first"));
        org.junit.Assert.assertEquals("2", record.get("second"));
    } finally {
        parser.close();
    }
}

@org.junit.Test
public void testExplicitHeaderCreatesMapWithoutConsumingFirstRecord() throws Exception {
    final org.apache.commons.csv.CSVParser parser = org.apache.commons.csv.CSVParser.parse(
            "1,2\n", org.apache.commons.csv.CSVFormat.DEFAULT.withHeader("left", "right"));
    try {
        org.junit.Assert.assertEquals(Integer.valueOf(0), parser.getHeaderMap().get("left"));
        org.junit.Assert.assertEquals(Integer.valueOf(1), parser.getHeaderMap().get("right"));

        final org.apache.commons.csv.CSVRecord record = parser.getRecords().get(0);
        org.junit.Assert.assertEquals("1", record.get("left"));
        org.junit.Assert.assertEquals("2", record.get("right"));
    } finally {
        parser.close();
    }
}

@org.junit.Test(expected = IllegalArgumentException.class)
public void testDuplicateFirstRecordHeadersAreRejected() throws Exception {
    org.apache.commons.csv.CSVParser.parse(
            "name,name\nvalue1,value2\n", org.apache.commons.csv.CSVFormat.DEFAULT.withFirstRecordAsHeader());
}