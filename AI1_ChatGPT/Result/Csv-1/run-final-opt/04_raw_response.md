@org.junit.Test
public void testZeroLengthBulkReadDoesNotConsumeInput() throws Exception {
    ExtendedBufferedReader reader = new ExtendedBufferedReader(new java.io.StringReader("a"));
    char[] buffer = new char[1];

    org.junit.Assert.assertEquals(0, reader.read(buffer, 0, 0));
    org.junit.Assert.assertEquals(0, reader.getLineNumber());
    org.junit.Assert.assertEquals(1, reader.read(buffer, 0, 1));
    org.junit.Assert.assertEquals('a', buffer[0]);
}

@org.junit.Test
public void testBulkReadAtEndOfStreamRecordsEndOfStream() throws Exception {
    ExtendedBufferedReader reader = new ExtendedBufferedReader(new java.io.StringReader(""));
    char[] buffer = new char[1];

    org.junit.Assert.assertEquals(ExtendedBufferedReader.END_OF_STREAM, reader.read(buffer, 0, 1));
    org.junit.Assert.assertEquals(ExtendedBufferedReader.END_OF_STREAM, reader.readAgain());
    org.junit.Assert.assertEquals(0, reader.getLineNumber());
}

@org.junit.Test
public void testLookAheadDoesNotConsumeCharacter() throws Exception {
    ExtendedBufferedReader reader = new ExtendedBufferedReader(new java.io.StringReader("x"));

    org.junit.Assert.assertEquals('x', reader.lookAhead());
    org.junit.Assert.assertEquals('x', reader.read());
    org.junit.Assert.assertEquals(0, reader.getLineNumber());
}