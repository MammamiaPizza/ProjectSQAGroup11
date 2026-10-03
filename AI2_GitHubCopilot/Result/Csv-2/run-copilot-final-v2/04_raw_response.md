@Test
 public void testGetRecordNumber() {
     final Map<String, Integer> mapping = new java.util.HashMap<>();
     mapping.put("first", 0);
     final CSVRecord record = new CSVRecord(new String[]{"x"}, mapping, null, 42L);
     assertEquals(42L, record.getRecordNumber());
 }

 @Test
 public void testGetComment() {
     final Map<String, Integer> mapping = new java.util.HashMap<>();
     mapping.put("first", 0);
     final CSVRecord recordWithComment = new CSVRecord(new String[]{"x"}, mapping, "test comment",
1L);
     assertEquals("test comment", recordWithComment.getComment());
     final CSVRecord recordWithoutComment = new CSVRecord(new String[]{"x"}, mapping, null, 1L);
     assertNull(recordWithoutComment.getComment());
 }

 @Test
 public void testSize() {
     final Map<String, Integer> mapping = new java.util.HashMap<>();
     mapping.put("first", 0);
     mapping.put("second", 1);
     final CSVRecord record = new CSVRecord(new String[]{"x", "y"}, mapping, null, 1L);
     assertEquals(2, record.size());
 }

 @Test
 public void testNullValuesYieldsEmptyRecord() {
     final Map<String, Integer> mapping = new java.util.HashMap<>();
     final CSVRecord record = new CSVRecord(null, mapping, null, 1L);
     assertEquals(0, record.size());
     assertTrue(record.isConsistent());
 }