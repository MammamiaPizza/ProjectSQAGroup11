@org.junit.Test
public void testEnumLookupUsesEnumTextAsHeaderName() {
    final java.util.Map<String, Integer> mapping = new java.util.HashMap<String, Integer>();
    mapping.put("SECONDS", Integer.valueOf(0));
    final CSVRecord record = new CSVRecord(new String[] { "second" }, mapping, null, 1L);

    org.junit.Assert.assertEquals("second", record.get(java.util.concurrent.TimeUnit.SECONDS));
    org.junit.Assert.assertTrue(record.isConsistent());
}

@org.junit.Test
public void testNullValuesProduceAnEmptyIterableRecordWithoutHeaders() {
    final CSVRecord record = new CSVRecord(null, null, null, 1L);

    org.junit.Assert.assertEquals(0, record.size());
    org.junit.Assert.assertFalse(record.iterator().hasNext());
    org.junit.Assert.assertTrue(record.isConsistent());
    org.junit.Assert.assertFalse(record.isMapped("value"));
    org.junit.Assert.assertFalse(record.isSet("value"));
}

@org.junit.Test
public void testMappingQueriesDistinguishUnsetAndUnknownColumns() {
    final java.util.Map<String, Integer> mapping = new java.util.HashMap<String, Integer>();
    mapping.put("present", Integer.valueOf(0));
    mapping.put("missing", Integer.valueOf(1));
    final CSVRecord record = new CSVRecord(new String[] { "value" }, mapping, null, 1L);

    org.junit.Assert.assertFalse(record.isConsistent());
    org.junit.Assert.assertTrue(record.isMapped("present"));
    org.junit.Assert.assertFalse(record.isMapped("unknown"));
    org.junit.Assert.assertTrue(record.isSet("present"));
    org.junit.Assert.assertFalse(record.isSet("missing"));
}

@org.junit.Test
public void testValuesReturnsRecordValuesArray() {
    final String[] values = new String[] { "value" };
    final CSVRecord record = new CSVRecord(values, null, null, 1L);

    org.junit.Assert.assertSame(values, record.values());
}