package org.apache.commons.compress.compressors.bzip2;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

public class BZip2CompressorInputStreamTest {

    @Test
    public void truncatedStreamReturnsBytesAlreadyDecodedByPartialBulkRead() throws Exception {
        byte[] expected = sampleData(97);
        byte[] compressed = compress(expected);
        byte[] truncated = truncateLastByte(compressed);

        BZip2CompressorInputStream in =
                new BZip2CompressorInputStream(new ByteArrayInputStream(truncated));
        byte[] buffer = new byte[13];
        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = (byte) 0x5a;
        }

        int first = in.read(buffer, 3, 7);
        assertEquals(7, first);
        assertEquals((byte) 0x5a, buffer[0]);
        assertEquals((byte) 0x5a, buffer[1]);
        assertEquals((byte) 0x5a, buffer[2]);
        for (int i = 0; i < first; i++) {
            assertEquals(expected[i], buffer[3 + i]);
        }
        assertEquals((byte) 0x5a, buffer[10]);

        int total = first;
        while (total < expected.length) {
            int read = in.read(buffer, 0, buffer.length);
            assertTrue("A partial bulk read must return decoded bytes before reporting truncation",
                    read > 0);
            for (int i = 0; i < read; i++) {
                assertEquals(expected[total + i], buffer[i]);
            }
            total += read;
        }
        assertEquals(expected.length, total);

        try {
            in.read();
            fail("The missing bzip2 trailer must be reported after decoded bytes are returned");
        } catch (IOException expectedException) {
            assertTrue(expectedException.getMessage().contains("unexpected end of stream"));
        } finally {
            in.close();
        }
    }

    @Test
    public void truncatedStreamReportsErrorAfterSingleByteReadsHaveReturnedData() throws Exception {
        byte[] expected = sampleData(31);
        byte[] truncated = truncateLastByte(compress(expected));
        BZip2CompressorInputStream in =
                new BZip2CompressorInputStream(new ByteArrayInputStream(truncated));

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i] & 0xff, in.read());
        }

        try {
            in.read();
            fail("Truncation must not be converted into normal EOF");
        } catch (IOException expectedException) {
            assertTrue(expectedException.getMessage().contains("unexpected end of stream"));
        } finally {
            in.close();
        }
    }

    @Test
    public void completeCompressedStreamRoundTripsAndEndsAtEof() throws Exception {
        byte[] expected = sampleData(211);
        BZip2CompressorInputStream in = new BZip2CompressorInputStream(
                new ByteArrayInputStream(compress(expected)));

        ByteArrayOutputStream decoded = new ByteArrayOutputStream();
        byte[] buffer = new byte[19];
        int read;
        while ((read = in.read(buffer)) != -1) {
            assertTrue(read > 0);
            decoded.write(buffer, 0, read);
        }

        assertArrayEquals(expected, decoded.toByteArray());
        assertEquals(-1, in.read());
        in.close();
    }

    @Test
    public void emptyBzip2StreamIsRecognizedAsEmpty() throws Exception {
        byte[] emptyBzip2 = new byte[] {
            'B', 'Z', 'h', '9',
            0x17, 0x72, 0x45, 0x38, 0x50, (byte) 0x90,
            0x00, 0x00, 0x00, 0x00
        };

        BZip2CompressorInputStream in =
                new BZip2CompressorInputStream(new ByteArrayInputStream(emptyBzip2));

        assertEquals(-1, in.read());
        in.close();
    }

    @Test
    public void rejectsInvalidHeadersAndRecognizesBzip2Signature() throws Exception {
        assertTrue(BZip2CompressorInputStream.matches(new byte[] { 'B', 'Z', 'h' }, 3));
        assertTrue(BZip2CompressorInputStream.matches(
                new byte[] { 'B', 'Z', 'h', '9' }, 4));
        assertFalse(BZip2CompressorInputStream.matches(new byte[] { 'B', 'Z' }, 2));
        assertFalse(BZip2CompressorInputStream.matches(new byte[] { 'B', 'Y', 'h' }, 3));

        try {
            new BZip2CompressorInputStream(
                    new ByteArrayInputStream(new byte[] { 'B', 'Z', 'h', '0' }));
            fail("An invalid bzip2 block size must be rejected");
        } catch (IOException expected) {
            assertTrue(expected.getMessage().contains("block size"));
        }

        try {
            new BZip2CompressorInputStream(
                    new ByteArrayInputStream(new byte[] { 'n', 'o', 'p', 'e' }));
            fail("A non-bzip2 header must be rejected");
        } catch (IOException expected) {
            assertTrue(expected.getMessage().contains("BZip2 format"));
        }
    }

    @Test
    public void validatesBulkReadBoundsAndRejectsReadsAfterClose() throws Exception {
        byte[] emptyBzip2 = new byte[] {
            'B', 'Z', 'h', '9',
            0x17, 0x72, 0x45, 0x38, 0x50, (byte) 0x90,
            0x00, 0x00, 0x00, 0x00
        };
        BZip2CompressorInputStream in =
                new BZip2CompressorInputStream(new ByteArrayInputStream(emptyBzip2));

        try {
            in.read(new byte[4], -1, 1);
            fail("Negative offsets must be rejected");
        } catch (IndexOutOfBoundsException expected) {
            assertTrue(expected.getMessage().contains("offs"));
        }

        try {
            in.read(new byte[4], 3, 2);
            fail("Reads extending past the destination must be rejected");
        } catch (IndexOutOfBoundsException expected) {
            assertTrue(expected.getMessage().contains("dest.length"));
        }

        in.close();
        try {
            in.read();
            fail("Reads after close must fail");
        } catch (IOException expected) {
            assertTrue(expected.getMessage().contains("closed"));
        }
    }

    private static byte[] compress(byte[] input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        BZip2CompressorOutputStream compressor = new BZip2CompressorOutputStream(output);
        try {
            compressor.write(input);
        } finally {
            compressor.close();
        }
        return output.toByteArray();
    }

    private static byte[] truncateLastByte(byte[] input) {
        byte[] result = new byte[input.length - 1];
        System.arraycopy(input, 0, result, 0, result.length);
        return result;
    }

    private static byte[] sampleData(int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) ('A' + (i % 26));
        }
        return result;
    }
}
