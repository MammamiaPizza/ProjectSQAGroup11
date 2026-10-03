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
}
