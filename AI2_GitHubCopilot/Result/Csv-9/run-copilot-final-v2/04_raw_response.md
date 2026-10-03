private enum TestHeader { A, B }

@Test
public void testGetWithEnum() {
    java.util.Map<String, Integer> mapping = new java.util.HashMap<String, Integer>();
    mapping.put("A", Integer.valueOf(0));
    mapping.put("B", Integer.valueOf(1));
    CSVRecord record = new CSVRecord(new String[]{"one", "two"}, mapping, null, 0L);
    assertEquals("one", record.get(TestHeader.A));
    assertEquals("two", record.get(TestHeader.B));
}

@Test(expected = IllegalArgumentException.class)
public void testGetStringIndexOutOfBounds() {
    java.util.Map<String, Integer> mapping = new java.util.HashMap<String, Integer>();
    mapping.put("Col5", Integer.valueOf(5));
    CSVRecord record = new CSVRecord(new String[]{"a", "b"}, mapping, null, 0L);
    record.get("Col5");
}

@Test
public void testValuesMethod() {
    java.util.Map<String, Integer> mapping = new java.util.HashMap<String, Integer>();
    mapping.put("col0", Integer.valueOf(0));
    CSVRecord record = new CSVRecord(new String[]{"x", "y"}, mapping, null, 0L);
    String[] vals = record.values();
    assertNotNull(vals);
    assertEquals(2, vals.length);
    assertEquals("x", vals[0]);
}

@Test
public void testConstructorWithNullValues() {
    java.util.Map<String, Integer> mapping = new java.util.HashMap<String, Integer>();
    CSVRecord record = new CSVRecord(null, mapping, null, 5L);
    assertEquals(0, record.size());
    assertEquals(5L, record.getRecordNumber());
    assertFalse(record.iterator().hasNext());
}