@Test
public void suppliedHeaderCanSkipTheInputHeaderRecord() throws Exception {
    final CSVParser parser = CSVParser.parse("name,age\nAlice,30\n",
            CSVFormat.DEFAULT.withHeader("name", "age").withSkipHeaderRecord());
    try {
        assertEquals(2, parser.getHeaderMap().size());
        assertEquals(1, parser.getRecords().size());
    } finally {
        parser.close();
    }
}

@Test
public void recordNumberAndClosedStateReflectConsumedRecords() throws Exception {
    final CSVParser parser = CSVParser.parse("first\nsecond\n", CSVFormat.DEFAULT);
    try {
        assertTrue(!parser.isClosed());
        assertEquals(0, parser.getRecordNumber());
        assertEquals(2, parser.getRecords().size());
        assertEquals(2, parser.getRecordNumber());
    } finally {
        parser.close();
    }
    assertTrue(parser.isClosed());
}

@Test(expected = IllegalStateException.class)
public void iteratorWrapsReaderIOException() throws Exception {
    final CSVParser parser = new CSVParser(new java.io.Reader() {
        @Override
        public int read(final char[] cbuf, final int off, final int len) throws java.io.IOException {
            throw new java.io.IOException("read failure");
        }

        @Override
        public void close() {
        }
    }, CSVFormat.DEFAULT);

    parser.iterator().hasNext();
}