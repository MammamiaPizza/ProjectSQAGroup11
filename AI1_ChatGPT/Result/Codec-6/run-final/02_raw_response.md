```java
package org.apache.commons.codec.binary;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class Base64InputStreamGeneratedTest {

    @Test
    public void defaultConstructorDecodesBase64UsingDirectDestinationBuffer() throws Exception {
        Base64InputStream stream = new Base64InputStream(
                new ByteArrayInputStream(ascii("SGVsbG8=")));

        byte[] actual = new byte[5];

        assertEquals(5, stream.read(actual, 0, actual.length));
        assertArrayEquals(ascii("Hello"), actual);
        assertEquals(-1, stream.read(actual, 0, actual.length));
    }

    @Test
    public void readWithOffsetAndShortLengthPreservesSurroundingBytesAndRetainsResults() throws Exception {
        Base64InputStream stream = new Base64InputStream(
                new ByteArrayInputStream(ascii("TWFu")));

        byte[] destination = new byte[] { 9, 0, 0, 9 };

        assertEquals(2, stream.read(destination, 1, 2));
        assertArrayEquals(new byte[] { 9, 'M', 'a', 9 }, destination);

        assertEquals(1, stream.read(destination, 1, 2));
        assertArrayEquals(new byte[] { 9, 'n', 'a', 9 }, destination);

        assertEquals(-1, stream.read(destination, 1, 2));
    }

    @Test
    public void singleByteReadReturnsUnsignedDecodedByteAndThenEof() throws Exception {
        Base64InputStream stream = new Base64InputStream(
                new ByteArrayInputStream(ascii("/w==")));

        assertEquals(255, stream.read());
        assertEquals(-1, stream.read());
    }

    @Test
    public void encodingConstructorEncodesRawInput() throws Exception {
        Base64InputStream stream = new Base64InputStream(
                new ByteArrayInputStream(ascii("Man")), true);

        byte[] encoded = new byte[4];

        assertEquals(4, stream.read(encoded, 0, encoded.length));
        assertArrayEquals(ascii("TWFu"), encoded);
        assertEquals(-1, stream.read(encoded, 0, encoded.length));
    }

    @Test
    public void encodingWithConfiguredLineLengthUsesConfiguredSeparator() throws Exception {
        Base64InputStream stream = new Base64InputStream(
                new ByteArrayInputStream(ascii("abcdef")),
                true,
                4,
                new byte[] { '\r', '\n' });

        byte[] encoded = new byte[12];

        assertEquals(12, stream.read(encoded, 0, encoded.length));
        assertArrayEquals(ascii("YWJj\r\nZGVm\r\n"), encoded);
    }

    @Test
    public void codec101SkipsChunkThatProducesNoDecodedBytesAndContinuesReading() throws Exception {
        InputStream source = new ChunkedInputStream(
                ascii("!!!!"),
                ascii("TQ=="));
        Base64InputStream stream = new Base64InputStream(source);

        byte[] result = new byte[1];

        assertEquals(1, stream.read(result, 0, result.length));
        assertArrayEquals(new byte[] { 'M' }, result);
        assertEquals(-1, stream.read(result, 0, result.length));
    }

    @Test
    public void zeroLengthReadReturnsZeroWithoutReadingWrappedStream() throws Exception {
        Base64InputStream stream = new Base64InputStream(new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("wrapped stream must not be read");
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                throw new IOException("wrapped stream must not be read");
            }
        });

        assertEquals(0, stream.read(new byte[1], 0, 0));
    }

    @Test
    public void readRejectsNullAndInvalidBufferRanges() throws Exception {
        Base64InputStream stream = new Base64InputStream(new ByteArrayInputStream(new byte[0]));

        try {
            stream.read(null, 0, 1);
            fail("Expected NullPointerException for a null destination buffer");
        } catch (NullPointerException expected) {
            // expected
        }

        try {
            stream.read(new byte[1], -1, 1);
            fail("Expected IndexOutOfBoundsException for a negative offset");
        } catch (IndexOutOfBoundsException expected) {
            // expected
        }

        try {
            stream.read(new byte[1], 0, -1);
            fail("Expected IndexOutOfBoundsException for a negative length");
        } catch (IndexOutOfBoundsException expected) {
            // expected
        }

        try {
            stream.read(new byte[1], 2, 0);
            fail("Expected IndexOutOfBoundsException for an offset beyond the buffer");
        } catch (IndexOutOfBoundsException expected) {
            // expected
        }

        try {
            stream.read(new byte[1], 1, 1);
            fail("Expected IndexOutOfBoundsException when offset plus length exceeds buffer");
        } catch (IndexOutOfBoundsException expected) {
            // expected
        }
    }

    @Test
    public void readPropagatesIOExceptionFromWrappedStream() throws Exception {
        final IOException expectedFailure = new IOException("source failure");
        Base64InputStream stream = new Base64InputStream(new InputStream() {
            @Override
            public int read() throws IOException {
                throw expectedFailure;
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                throw expectedFailure;
            }
        });

        try {
            stream.read(new byte[1], 0, 1);
            fail("Expected IOException from wrapped stream");
        } catch (IOException actual) {
            assertEquals(expectedFailure, actual);
        }
    }

    @Test
    public void markIsNotSupported() {
        Base64InputStream stream = new Base64InputStream(new ByteArrayInputStream(new byte[0]));

        assertFalse(stream.markSupported());
    }

    private static byte[] ascii(String value) throws Exception {
        return value.getBytes("US-ASCII");
    }

    /**
     * Returns one supplied chunk per bulk read, allowing the test to model a
     * wrapped stream that first supplies only non-Base64 bytes.
     */
    private static final class ChunkedInputStream extends InputStream {

        private final byte[][] chunks;
        private int chunkIndex;
        private int positionInChunk;

        private ChunkedInputStream(byte[]... chunks) {
            this.chunks = chunks;
        }

        @Override
        public int read() {
            while (chunkIndex < chunks.length) {
                byte[] chunk = chunks[chunkIndex];
                if (positionInChunk < chunk.length) {
                    return chunk[positionInChunk++] & 0xff;
                }
                chunkIndex++;
                positionInChunk = 0;
            }
            return -1;
        }

        @Override
        public int read(byte[] b, int off, int len) {
            if (len == 0) {
                return 0;
            }

            while (chunkIndex < chunks.length && positionInChunk == chunks[chunkIndex].length) {
                chunkIndex++;
                positionInChunk = 0;
            }

            if (chunkIndex >= chunks.length) {
                return -1;
            }

            byte[] chunk = chunks[chunkIndex];
            int count = Math.min(len, chunk.length - positionInChunk);
            System.arraycopy(chunk, positionInChunk, b, off, count);
            positionInChunk += count;
            return count;
        }
    }
}
```

Test coverage targets:

- `defaultConstructorDecodesBase64UsingDirectDestinationBuffer`: default decode mode, normal decoding, EOF, and the direct destination-buffer optimization.
- `readWithOffsetAndShortLengthPreservesSurroundingBytesAndRetainsResults`: offset/length reads and consumption of already buffered decoded results.
- `singleByteReadReturnsUnsignedDecodedByteAndThenEof`: `read()` conversion of signed Java bytes to the required unsigned `0..255` range.
- `encodingConstructorEncodesRawInput`: encoding mode through the boolean constructor.
- `encodingWithConfiguredLineLengthUsesConfiguredSeparator`: line-length and line-separator constructor behavior.
- `codec101SkipsChunkThatProducesNoDecodedBytesAndContinuesReading`: CODEC-101 behavior; a chunk containing only non-Base64 bytes must not cause a positive-length bulk read to return zero when later source data can produce decoded output.
- `zeroLengthReadReturnsZeroWithoutReadingWrappedStream`: zero-length read branch and absence of wrapped-stream access.
- `readRejectsNullAndInvalidBufferRanges`: null buffer, negative offset/length, offset beyond the array, and offset-plus-length overflow of the destination range.
- `readPropagatesIOExceptionFromWrappedStream`: I/O exception propagation.
- `markIsNotSupported`: documented `markSupported()` behavior.