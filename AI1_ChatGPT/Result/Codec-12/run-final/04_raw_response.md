```java
package org.apache.commons.codec.binary;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class BaseNCodecInputStreamTest {

    private BaseNCodecInputStream decodingStream(final String encoded) throws IOException {
        return new BaseNCodecInputStream(
                new ByteArrayInputStream(encoded.getBytes("US-ASCII")),
                new Base64(),
                false);
    }

    private BaseNCodecInputStream encodingStream(final String plainText) throws IOException {
        return new BaseNCodecInputStream(
                new ByteArrayInputStream(plainText.getBytes("US-ASCII")),
                new Base64(),
                true);
    }

    private String readFully(final InputStream input) throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final byte[] buffer = new byte[3];
        int count;
        while ((count = input.read(buffer)) != -1) {
            assertTrue("A non-EOF bulk read must return at least one byte", count > 0);
            output.write(buffer, 0, count);
        }
        return new String(output.toByteArray(), "US-ASCII");
    }

    @Test
    public void testReadSingleByteAndBulkReadDecodeBase64Data() throws Exception {
        final BaseNCodecInputStream input = decodingStream("SGVsbG8gV29ybGQ=");

        assertEquals('H', input.read());

        final byte[] remainder = new byte[10];
        assertEquals(10, input.read(remainder, 0, remainder.length));
        assertEquals("ello World", new String(remainder, "US-ASCII"));

        assertEquals(-1, input.read());
    }

    @Test
    public void testReadSingleByteReturnsUnsignedValue() throws Exception {
        final BaseNCodecInputStream input = decodingStream("/w==");

        assertEquals(255, input.read());
        assertEquals(-1, input.read());
    }

    @Test
    public void testReadEncodesDataWhenConfiguredForEncoding() throws Exception {
        final BaseNCodecInputStream input = encodingStream("Man");

        assertEquals("TWFu", readFully(input));
    }

    @Test
    public void testReadValidatesNullAndInvalidBufferArguments() throws Exception {
        final BaseNCodecInputStream input = decodingStream("TQ==");

        try {
            input.read(null, 0, 1);
            fail("A null destination buffer must throw NullPointerException");
        } catch (final NullPointerException expected) {
            assertTrue(true);
        }

        try {
            input.read(new byte[1], -1, 1);
            fail("A negative offset must throw IndexOutOfBoundsException");
        } catch (final IndexOutOfBoundsException expected) {
            assertTrue(true);
        }

        try {
            input.read(new byte[1], 0, -1);
            fail("A negative length must throw IndexOutOfBoundsException");
        } catch (final IndexOutOfBoundsException expected) {
            assertTrue(true);
        }

        try {
            input.read(new byte[1], 1, 1);
            fail("An offset and length beyond the destination buffer must throw IndexOutOfBoundsException");
        } catch (final IndexOutOfBoundsException expected) {
            assertTrue(true);
        }

        try {
            input.read(new byte[1], 1, Integer.MAX_VALUE);
            fail("An overflowing offset/length combination must throw IndexOutOfBoundsException");
        } catch (final IndexOutOfBoundsException expected) {
            assertTrue(true);
        }
    }

    @Test
    public void testReadRejectsOffsetBeyondArrayEvenWhenLengthIsZero() throws Exception {
        final BaseNCodecInputStream input = decodingStream("TQ==");

        try {
            input.read(new byte[1], 2, 0);
            fail("An offset beyond the destination buffer must throw IndexOutOfBoundsException");
        } catch (final IndexOutOfBoundsException expected) {
            assertTrue(true);
        }
    }

    @Test
    public void testReadWithZeroLengthReturnsZeroWithoutConsumingData() throws Exception {
        final BaseNCodecInputStream input = decodingStream("TQ==");
        final byte[] destination = new byte[1];

        assertEquals(0, input.read(destination, 0, 0));
        assertEquals('M', input.read());
        assertEquals(-1, input.read());
    }

    @Test
    public void testSingleByteReadRetriesAfterOverriddenBulkReadReturnsZero() throws Exception {
        final BaseNCodecInputStream input = new BaseNCodecInputStream(
                new ByteArrayInputStream("TQ==".getBytes("US-ASCII")),
                new Base64(),
                false) {

            private boolean returnZero = true;

            @Override
            public int read(final byte[] b, final int offset, final int len) throws IOException {
                if (returnZero) {
                    returnZero = false;
                    return 0;
                }
                return super.read(b, offset, len);
            }
        };

        assertEquals('M', input.read());
        assertEquals(-1, input.read());
    }

    @Test
    public void testReadRetriesWhenIgnoredEncodedDataProducesNoDecodedBytes() throws Exception {
        final InputStream chunkedInput = new InputStream() {

            private final byte[][] chunks = {
                    "!!!!".getBytes(),
                    "TQ==".getBytes()
            };

            private int nextChunk;

            @Override
            public int read() {
                return -1;
            }

            @Override
            public int read(final byte[] b, final int offset, final int len) {
                if (nextChunk == chunks.length) {
                    return -1;
                }
                final byte[] chunk = chunks[nextChunk++];
                System.arraycopy(chunk, 0, b, offset, chunk.length);
                return chunk.length;
            }
        };

        final BaseNCodecInputStream input =
                new BaseNCodecInputStream(chunkedInput, new Base64(), false);

        assertEquals('M', input.read());
        assertEquals(-1, input.read());
    }

    @Test
    public void testAvailableReportsOneUntilEndOfDecodedStreamThenZero() throws Exception {
        final BaseNCodecInputStream input = decodingStream("TQ==");

        assertEquals(1, input.available());
        assertEquals('M', input.read());

        // EOF is established only after a subsequent read attempts to refill the codec.
        assertEquals(-1, input.read());
        assertEquals(0, input.available());
    }

    @Test
    public void testSkipOperatesOnDecodedBytesRatherThanEncodedSourceBytes() throws Exception {
        final BaseNCodecInputStream input = decodingStream("SGVsbG8gV29ybGQ=");

        assertEquals(1L, input.skip(1));
        assertEquals("ello World", readFully(input));
    }

    @Test
    public void testSkipPastEndReturnsOnlyAvailableDecodedBytesAndReachesEof() throws Exception {
        final BaseNCodecInputStream input = decodingStream("SGVsbG8h");

        assertEquals(6L, input.skip(100));
        assertEquals(-1, input.read());
        assertEquals(0L, input.skip(1));
    }

    @Test
    public void testSkipAcrossMultipleInternalChunksUsesTransformedByteCount() throws Exception {
        final StringBuilder plainText = new StringBuilder();
        for (int i = 0; i < 600; i++) {
            plainText.append('a');
        }

        final String encoded = readFully(encodingStream(plainText.toString()));
        final BaseNCodecInputStream input = decodingStream(encoded);

        assertEquals(600L, input.skip(600));
        assertEquals(-1, input.read());
        assertEquals(0, input.available());
    }

    @Test
    public void testSkipZeroAndNegativeLengths() throws Exception {
        final BaseNCodecInputStream input = decodingStream("TQ==");

        assertEquals(0L, input.skip(0));

        try {
            input.skip(-1);
            fail("A negative skip length must throw IllegalArgumentException");
        } catch (final IllegalArgumentException expected) {
            assertTrue(true);
        }

        assertEquals('M', input.read());
    }

    @Test
    public void testMarkIsNotSupported() throws Exception {
        final BaseNCodecInputStream input = decodingStream("TQ==");

        assertFalse(input.markSupported());
    }

    @Test
    public void testReadPropagatesIOExceptionFromUnderlyingStream() throws Exception {
        final InputStream failingInput = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("simulated input failure");
            }
        };

        final BaseNCodecInputStream input =
                new BaseNCodecInputStream(failingInput, new Base64(), false);

        try {
            input.read();
            fail("IOException from the wrapped stream must be propagated");
        } catch (final IOException expected) {
            assertEquals("simulated input failure", expected.getMessage());
        }
    }
}
```

### Added test coverage

- `testReadRejectsOffsetBeyondArrayEvenWhenLengthIsZero`
  - Covers the distinct invalid-buffer condition where `offset > b.length`.
  - Complements existing negative-offset, negative-length, oversized-range, and overflow-related cases.
  - Targets the partially covered bounds-validation branch around the `offset > b.length` condition.

- `testSingleByteReadRetriesAfterOverriddenBulkReadReturnsZero`
  - Covers the `while (r == 0)` retry path in `read()`.
  - Verifies that single-byte reads do not incorrectly return EOF or a zero-like result when an intermediate bulk read returns zero.

- `testReadRetriesWhenIgnoredEncodedDataProducesNoDecodedBytes`
  - Covers the documented `while (readLen == 0)` behavior in bulk reads.
  - Uses a first chunk containing only Base64-invalid data, which produces no decoded results, followed by valid Base64 data.
  - Verifies that the stream continues reading rather than returning zero, as required for interoperability with consumers such as `InputStreamReader`.

- `testSkipAcrossMultipleInternalChunksUsesTransformedByteCount`
  - Adds coverage for the skip loop across more than one internal 512-byte skip chunk.
  - Directly targets CODEC-130 behavior: skipping must be based on transformed/decoded bytes rather than the number of bytes in the encoded underlying stream.
  - Distinguishes the expected behavior from the buggy implementation that delegated skip behavior to the wrapped encoded stream.