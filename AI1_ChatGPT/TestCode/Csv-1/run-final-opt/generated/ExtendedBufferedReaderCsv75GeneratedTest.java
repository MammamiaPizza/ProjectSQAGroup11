package org.apache.commons.csv;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.StringReader;

import org.junit.Test;

public class ExtendedBufferedReaderCsv75GeneratedTest {

    @Test
    public void testReadSingleCRIncrementsLineNumberAndEOFIsRecorded() throws Exception {
        ExtendedBufferedReader reader = new ExtendedBufferedReader(new StringReader("\r"));

        assertEquals('\r', reader.read());
        assertEquals(1, reader.getLineNumber());

        assertEquals(ExtendedBufferedReader.END_OF_STREAM, reader.read());
        assertEquals(ExtendedBufferedReader.END_OF_STREAM, reader.readAgain());
        assertEquals(1, reader.getLineNumber());
    }

    @Test
    public void testReadCRLFCountsAsOneLine() throws Exception {
        ExtendedBufferedReader reader = new ExtendedBufferedReader(new StringReader("\r\n"));

        assertEquals('\r', reader.read());
        assertEquals(1, reader.getLineNumber());

        assertEquals('\n', reader.read());
        assertEquals(1, reader.getLineNumber());
    }

    @Test
    public void testReadLFIncrementsLineNumber() throws Exception {
        ExtendedBufferedReader reader = new ExtendedBufferedReader(new StringReader("x\n"));

        assertEquals('x', reader.read());
        assertEquals(0, reader.getLineNumber());

        assertEquals('\n', reader.read());
        assertEquals(1, reader.getLineNumber());
    }

    @Test
    public void testBulkReadCountsCRLFAndLFAsLineTerminators() throws Exception {
        ExtendedBufferedReader reader = new ExtendedBufferedReader(new StringReader("a\r\nb\n"));
        char[] buffer = new char[5];

        assertEquals(5, reader.read(buffer, 0, buffer.length));
        assertEquals("a\r\nb\n", new String(buffer));
        assertEquals(2, reader.getLineNumber());
        assertEquals('\n', reader.readAgain());
    }

    @Test
    public void testBulkReadWithNonZeroOffsetDoesNotUseCharacterOutsideReadRange() throws Exception {
        ExtendedBufferedReader reader = new ExtendedBufferedReader(new StringReader("\nZ"));
        char[] buffer = new char[] { '\r', '?', '?' };

        assertEquals(2, reader.read(buffer, 1, 2));
        assertEquals('\r', buffer[0]);
        assertEquals('\n', buffer[1]);
        assertEquals('Z', buffer[2]);
        assertEquals(1, reader.getLineNumber());
    }

    @Test
    public void testReadLineCountsCRCRLFLFAndEmptyLines() throws Exception {
        ExtendedBufferedReader reader = new ExtendedBufferedReader(
                new StringReader("first\rsecond\r\n\nlast"));

        assertEquals("first", reader.readLine());
        assertEquals(1, reader.getLineNumber());

        assertEquals("second", reader.readLine());
        assertEquals(2, reader.getLineNumber());

        assertEquals("", reader.readLine());
        assertEquals(3, reader.getLineNumber());

        assertEquals("last", reader.readLine());
        assertEquals(4, reader.getLineNumber());

        assertNull(reader.readLine());
        assertEquals(4, reader.getLineNumber());
        assertEquals(ExtendedBufferedReader.END_OF_STREAM, reader.readAgain());
    }
}
