package org.apache.commons.compress.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IOUtilsSkipTest {

    @Test
    public void skipFallsBackToReadWhenSkipAlwaysReturnsZero() throws IOException {
        final InputStream input = new ZeroSkippingInputStream(bytes(15));

        assertEquals(10, IOUtils.skip(input, 10));
        assertEquals(10, input.read());
    }

    @Test
    public void skipFallsBackToReadAfterPartialUnderlyingSkip() throws IOException {
        final InputStream input = new PartiallySkippingInputStream(bytes(15), 5);

        assertEquals(10, IOUtils.skip(input, 10));
        assertEquals(10, input.read());
    }

    @Test
    public void skipUsingReadStopsAtEndOfStreamWhenRequestExceedsInput() throws IOException {
        final InputStream input = new ZeroSkippingInputStream(bytes(4));

        assertEquals(4, IOUtils.skip(input, 10));
        assertEquals(-1, input.read());
    }

    @Test
    public void skipReturnsRequestedAmountForNormalSkippingStream() throws IOException {
        final InputStream input = new ByteArrayInputStream(bytes(8));

        assertEquals(6, IOUtils.skip(input, 6));
        assertEquals(6, input.read());
    }

    private static byte[] bytes(final int length) {
        final byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = (byte) i;
        }
        return result;
    }

    private static final class ZeroSkippingInputStream extends InputStream {
        private final byte[] data;
        private int position;

        private ZeroSkippingInputStream(final byte[] data) {
            this.data = data;
        }

        @Override
        public int read() {
            if (position >= data.length) {
                return -1;
            }
            return data[position++] & 0xff;
        }

        @Override
        public long skip(final long count) {
            return 0;
        }
    }

    private static final class PartiallySkippingInputStream extends InputStream {
        private final byte[] data;
        private final int firstSkipAmount;
        private int position;
        private boolean hasSkipped;

        private PartiallySkippingInputStream(final byte[] data, final int firstSkipAmount) {
            this.data = data;
            this.firstSkipAmount = firstSkipAmount;
        }

        @Override
        public int read() {
            if (position >= data.length) {
                return -1;
            }
            return data[position++] & 0xff;
        }

        @Override
        public long skip(final long count) {
            if (hasSkipped) {
                return 0;
            }
            hasSkipped = true;
            final int skipped = (int) Math.min(Math.min(count, firstSkipAmount), data.length - position);
            position += skipped;
            return skipped;
        }
    }

@org.junit.Test
public void copyCopiesAllBytesUsingBothOverloads() throws java.io.IOException {
    final byte[] source = new byte[] { 1, 2, 3, 4, 5 };

    final java.io.ByteArrayOutputStream defaultOutput = new java.io.ByteArrayOutputStream();
    org.junit.Assert.assertEquals(5L,
            org.apache.commons.compress.utils.IOUtils.copy(
                    new java.io.ByteArrayInputStream(source), defaultOutput));
    org.junit.Assert.assertEquals(5, defaultOutput.toByteArray().length);
    org.junit.Assert.assertEquals(1, defaultOutput.toByteArray()[0]);
    org.junit.Assert.assertEquals(5, defaultOutput.toByteArray()[4]);

    final java.io.ByteArrayOutputStream smallBufferOutput = new java.io.ByteArrayOutputStream();
    org.junit.Assert.assertEquals(5L,
            org.apache.commons.compress.utils.IOUtils.copy(
                    new java.io.ByteArrayInputStream(source), smallBufferOutput, 2));
    org.junit.Assert.assertEquals(5, smallBufferOutput.toByteArray().length);
    org.junit.Assert.assertEquals(3, smallBufferOutput.toByteArray()[2]);
}

@org.junit.Test
public void readFullyReturnsBytesReadForCompleteAndTruncatedInput() throws java.io.IOException {
    final byte[] complete = new byte[2];
    org.junit.Assert.assertEquals(2,
            org.apache.commons.compress.utils.IOUtils.readFully(
                    new java.io.ByteArrayInputStream(new byte[] { 3, 4 }), complete));
    org.junit.Assert.assertEquals(3, complete[0]);
    org.junit.Assert.assertEquals(4, complete[1]);

    final byte[] partial = new byte[] { 9, 9, 9, 9, 9 };
    org.junit.Assert.assertEquals(2,
            org.apache.commons.compress.utils.IOUtils.readFully(
                    new java.io.ByteArrayInputStream(new byte[] { 1, 2 }), partial, 1, 3));
    org.junit.Assert.assertEquals(9, partial[0]);
    org.junit.Assert.assertEquals(1, partial[1]);
    org.junit.Assert.assertEquals(2, partial[2]);
    org.junit.Assert.assertEquals(9, partial[3]);
}

@org.junit.Test
public void toByteArrayReturnsAllInputBytes() throws java.io.IOException {
    final byte[] result = org.apache.commons.compress.utils.IOUtils.toByteArray(
            new java.io.ByteArrayInputStream(new byte[] { 7, 8, 9 }));

    org.junit.Assert.assertEquals(3, result.length);
    org.junit.Assert.assertEquals(7, result[0]);
    org.junit.Assert.assertEquals(8, result[1]);
    org.junit.Assert.assertEquals(9, result[2]);
}

@org.junit.Test
public void closeQuietlyClosesAndSuppressesIOException() {
    final boolean[] closed = new boolean[] { false };

    org.apache.commons.compress.utils.IOUtils.closeQuietly(new java.io.Closeable() {
        public void close() throws java.io.IOException {
            closed[0] = true;
            throw new java.io.IOException("expected");
        }
    });
    org.junit.Assert.assertTrue(closed[0]);

    org.apache.commons.compress.utils.IOUtils.closeQuietly(null);
}
}
