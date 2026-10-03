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
    public void testReadWithZeroLengthReturnsZeroWithoutConsumingData() throws Exception {
        final BaseNCodecInputStream input = decodingStream("TQ==");
        final byte[] destination = new byte[1];

        assertEquals(0, input.read(destination, 0, 0));
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

Test coverage summary:

- `testReadSingleByteAndBulkReadDecodeBase64Data` verifies normal decoding through both `read()` and `read(byte[], int, int)`.
- `testReadSingleByteReturnsUnsignedValue` verifies that negative Java bytes are returned as unsigned values in the `0..255` range.
- `testReadEncodesDataWhenConfiguredForEncoding` covers the encoding branch controlled by `doEncode`.
- `testReadValidatesNullAndInvalidBufferArguments` covers null, negative, out-of-range, and overflow-prone bulk-read arguments.
- `testReadWithZeroLengthReturnsZeroWithoutConsumingData` covers the zero-length read branch.
- `testAvailableReportsOneUntilEndOfDecodedStreamThenZero` targets CODEC-130 availability behavior.
- `testSkipOperatesOnDecodedBytesRatherThanEncodedSourceBytes` targets the primary CODEC-130 failure: skipping must apply to transformed output, not raw Base64 input.
- `testSkipPastEndReturnsOnlyAvailableDecodedBytesAndReachesEof` verifies bounded skip behavior and EOF handling.
- `testSkipZeroAndNegativeLengths` verifies zero skip and the documented `IllegalArgumentException` for negative skip values.
- `testMarkIsNotSupported` covers the explicit `markSupported()` contract.
- `testReadPropagatesIOExceptionFromUnderlyingStream` verifies exceptional behavior from the wrapped stream.