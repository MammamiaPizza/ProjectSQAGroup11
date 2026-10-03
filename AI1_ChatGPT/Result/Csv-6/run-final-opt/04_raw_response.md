@Test
public void accessorsExposeRecordMetadataValuesAndIteration() {
    final String[] values = new String[] { "one", "two" };
    final java.util.Map<String, Integer> mapping = new java.util.HashMap<String, Integer>();
    mapping.put("SECONDS", Integer.valueOf(0));
    mapping.put("second", Integer.valueOf(1));
    final CSVRecord record = new CSVRecord(values, mapping, "comment", 17L);

    assertEquals("one", record.get(java.util.concurrent.TimeUnit.SECONDS));
    assertEquals("two", record.get(1));
    assertEquals("comment", record.getComment());
    assertEquals(17L, record.getRecordNumber());
    assertEquals(2, record.size());
    assertTrue(record.isConsistent());
    assertSame(values, record.values());
    assertEquals("[one, two]", record.toString());

    final java.util.Iterator<String> iterator = record.iterator();
    assertTrue(iterator.hasNext());
    assertEquals("one", iterator.next());
    assertTrue(iterator.hasNext());
    assertEquals("two", iterator.next());
    assertFalse(iterator.hasNext());
}

@Test
public void recordWithoutValuesOrMappingUsesEmptyValuesAndReportsNoMappings() {
    final CSVRecord record = new CSVRecord(null, null, null, 0L);

    assertEquals(0, record.size());
    assertEquals(null, record.getComment());
    assertTrue(record.isConsistent());
    assertFalse(record.isMapped("missing"));
    assertFalse(record.isSet("missing"));
    assertFalse(record.iterator().hasNext());
}