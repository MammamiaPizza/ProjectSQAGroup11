```java
package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;

public class ExtendedBufferedReaderTest {

    @Test
    public void testSingleCharacterReadsCountCarriageReturnAndLineFeed() throws IOException {
        ExtendedBufferedReader reader =
                new ExtendedBufferedReader(new StringReader("\rX\n"));

        assertEquals(0, reader.getLineNumber());
        assertEquals(ExtendedBufferedReader.UNDEFINED, reader.readAgain());

        assertEquals('\r', reader.read());
        assertEquals("A carriage return terminates one line", 1, reader.getLineNumber());
        assertEquals('\r', reader.readAgain());

        assertEquals('X', reader.read());
        assertEquals(1, reader.getLineNumber());
        assertEquals('X', reader.readAgain());

        assertEquals('\n', reader.read());
        assertEquals("A line feed terminates one additional line", 2, reader.getLineNumber());
        assertEquals('\n', reader.readAgain());
    }

    @Test
    public void testSingleCharacterReadsTreatCrLfAsOneLineTerminator() throws IOException {
        ExtendedBufferedReader reader =
                new ExtendedBufferedReader(new StringReader("\r\n"));

        assertEquals('\r', reader.read());
        assertEquals("CR terminates the first line", 1, reader.getLineNumber());
        assertEquals('\r', reader.readAgain());

        assertEquals('\n', reader.read());
        assertEquals("LF immediately following CR must not terminate a second line", 1,
                reader.getLineNumber());
        assertEquals('\n', reader.readAgain());

        assertEquals(ExtendedBufferedReader.END_OF_STREAM, reader.read());
        assertEquals(1, reader.getLineNumber());
    }

    @Test
    public void testLookAheadDoesNotConsumeCharacterOrChangeLastCharacter() throws IOException {
        ExtendedBufferedReader reader =
                new ExtendedBufferedReader(new StringReader("a"));

        assertEquals(ExtendedBufferedReader.UNDEFINED, reader.readAgain());
        assertEquals('a', reader.lookAhead());
        assertEquals("Looking ahead must not update the last character", ExtendedBufferedReader.UNDEFINED,
                reader.readAgain());
        assertEquals(0, reader.getLineNumber());

        assertEquals('a', reader.read());
        assertEquals('a', reader.readAgain());

        assertEquals(ExtendedBufferedReader.END_OF_STREAM, reader.lookAhead());
        assertEquals("Looking ahead at EOF must not update the last character", 'a', reader.readAgain());

        assertEquals(ExtendedBufferedReader.END_OF_STREAM, reader.read());
        assertEquals(ExtendedBufferedReader.END_OF_STREAM, reader.readAgain());
    }

    @Test
    public void testBulkReadCountsCrLfAsOneLineAndUpdatesLastCharacterAtEof() throws IOException {
        ExtendedBufferedReader reader =
                new ExtendedBufferedReader(new StringReader("\r\nx\ny\r"));
        char[] buffer = new char[8];

        assertEquals(0, reader.read(buffer, 0, 0));
        assertEquals(ExtendedBufferedReader.UNDEFINED, reader.readAgain());
        assertEquals(0, reader.getLineNumber());

        assertEquals(6, reader.read(buffer, 1, 7));
        assertEquals('\r', buffer[1]);
        assertEquals('\n', buffer[2]);
        assertEquals('x', buffer[3]);
        assertEquals('\n', buffer[4]);
        assertEquals('y', buffer[5]);
        assertEquals('\r', buffer[6]);

        assertEquals("CRLF, LF, and CR each terminate one line", 3, reader.getLineNumber());
        assertEquals('\r', reader.readAgain());

        assertEquals(ExtendedBufferedReader.END_OF_STREAM, reader.read(buffer, 0, buffer.length));
        assertEquals(ExtendedBufferedReader.END_OF_STREAM, reader.readAgain());
    }

    @Test
    public void testBulkReadRecognizesCrLfSplitAcrossSeparateReads() throws IOException {
        ExtendedBufferedReader reader =
                new ExtendedBufferedReader(new StringReader("\r\n"));
        char[] buffer = new char[1];

        assertEquals(1, reader.read(buffer, 0, 1));
        assertEquals('\r', buffer[0]);
        assertEquals(1, reader.getLineNumber());

        assertEquals(1, reader.read(buffer, 0, 1));
        assertEquals('\n', buffer[0]);
        assertEquals("LF following a previously-read CR must not add another line", 1,
                reader.getLineNumber());
        assertEquals('\n', reader.readAgain());
    }

    @Test
    public void testBulkReadCountsLeadingLfWhenPreviousReadWasNotCr() throws IOException {
        ExtendedBufferedReader reader =
                new ExtendedBufferedReader(new StringReader("x\n"));
        char[] buffer = new char[1];

        assertEquals('x', reader.read());
        assertEquals(0, reader.getLineNumber());
        assertEquals('x', reader.readAgain());

        assertEquals(1, reader.read(buffer, 0, 1));
        assertEquals('\n', buffer[0]);
        assertEquals("An LF at the start of a bulk-read buffer follows lastChar", 1,
                reader.getLineNumber());
        assertEquals('\n', reader.readAgain());
    }

    @Test
    public void testReadLineCountsNonEmptyAndEmptyLinesAndSetsEndOfStreamState() throws IOException {
        ExtendedBufferedReader reader =
                new ExtendedBufferedReader(new StringReader("first\n\nsecond\r\n"));

        assertEquals("first", reader.readLine());
        assertEquals(1, reader.getLineNumber());
        assertEquals('t', reader.readAgain());

        assertEquals("", reader.readLine());
        assertEquals(2, reader.getLineNumber());

        assertEquals("second", reader.readLine());
        assertEquals(3, reader.getLineNumber());
        assertEquals('d', reader.readAgain());

        assertEquals(null, reader.readLine());
        assertEquals(ExtendedBufferedReader.END_OF_STREAM, reader.readAgain());
        assertEquals(3, reader.getLineNumber());
    }

    @Test
    public void testBulkReadWithNullBufferThrowsNullPointerExceptionWithoutChangingState()
            throws IOException {
        ExtendedBufferedReader reader =
                new ExtendedBufferedReader(new StringReader("value"));

        try {
            reader.read(null, 0, 1);
            fail("A null destination buffer must throw NullPointerException");
        } catch (NullPointerException expected) {
            assertEquals(ExtendedBufferedReader.UNDEFINED, reader.readAgain());
            assertEquals(0, reader.getLineNumber());
        }
    }
}
```

New test coverage and fault-detection targets:

- `testSingleCharacterReadsTreatCrLfAsOneLineTerminator`
  - Targets the CRLF-specific branch in single-character `read()`: after a `'\r'`, a following `'\n'` must not increment the line number again.
  - Complements the existing CR-only regression coverage for CSV-75 by testing the related boundary case where the carriage return is part of a CRLF terminator.
  - Distinguishes correct CRLF handling from an implementation that counts both characters as separate line terminators.

- `testBulkReadCountsLeadingLfWhenPreviousReadWasNotCr`
  - Targets the bulk-read condition for a line feed at index zero of the supplied buffer.
  - Exercises the `i == 0` path, where the implementation must compare the line feed with `lastChar` rather than a preceding character in the current buffer.
  - Covers the case where `lastChar` is not `'\r'`, so the leading line feed must increment the line counter. This complements the existing split-CRLF test, which covers the `i == 0` path when `lastChar` is `'\r'` and no increment is required.