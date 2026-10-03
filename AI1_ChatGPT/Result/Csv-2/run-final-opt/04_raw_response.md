@Test
public void getCommentReturnsRecordComment() {
    final CSVRecord record = new CSVRecord(new String[] { "value" }, null, "comment", 1L);

    assertEquals("comment", record.getComment());
}