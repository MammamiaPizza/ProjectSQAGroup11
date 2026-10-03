package org.apache.commons.compress.compressors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorOutputStream;
import org.junit.Test;

public class CompressorStreamFactoryDeflateTest {

    @Test
    public void factoryDetectsAndDecompressesZlibDeflateStream() throws Exception {
        byte[] original = bytes("Factory detection must recognize a zlib deflate stream.");
        byte[] compressed = deflate(original);

        CompressorInputStream stream =
                new CompressorStreamFactory().createCompressorInputStream(
                        new ByteArrayInputStream(compressed));
        try {
            assertTrue(stream instanceof DeflateCompressorInputStream);
            assertArrayEquals(original, readAll(stream));
        } finally {
            stream.close();
        }
    }

    @Test
    public void directDeflateStreamReadsOneByteAtATimeAndEndsAtEof() throws Exception {
        byte[] original = bytes("single byte reads");
        DeflateCompressorInputStream stream =
                new DeflateCompressorInputStream(new ByteArrayInputStream(deflate(original)));
        try {
            ByteArrayOutputStream actual = new ByteArrayOutputStream();
            int value;
            while ((value = stream.read()) != -1) {
                actual.write(value);
            }

            assertArrayEquals(original, actual.toByteArray());
            assertEquals(-1, stream.read());
        } finally {
            stream.close();
        }
    }

    @Test
    public void bulkReadSupportsZeroLengthReadsAndReturnsEofAfterContent() throws Exception {
        byte[] original = bytes("bulk reads cross several buffer boundaries");
        DeflateCompressorInputStream stream =
                new DeflateCompressorInputStream(new ByteArrayInputStream(deflate(original)));
        try {
            byte[] buffer = new byte[7];
            assertEquals(0, stream.read(buffer, 0, 0));

            ByteArrayOutputStream actual = new ByteArrayOutputStream();
            int count;
            while ((count = stream.read(buffer, 0, buffer.length)) != -1) {
                actual.write(buffer, 0, count);
            }

            assertArrayEquals(original, actual.toByteArray());
            assertEquals(-1, stream.read(buffer, 0, buffer.length));
        } finally {
            stream.close();
        }
    }

    @Test
    public void skipAndAvailableOperateOnDecompressedData() throws Exception {
        byte[] original = bytes("0123456789");
        DeflateCompressorInputStream stream =
                new DeflateCompressorInputStream(new ByteArrayInputStream(deflate(original)));
        try {
            assertTrue(stream.available() > 0);
            assertEquals(4, stream.skip(4));
            assertEquals("456789", new String(readAll(stream), "UTF-8"));
            assertEquals(0, stream.available());
        } finally {
            stream.close();
        }
    }

    @Test
    public void closeClosesWrappedStreamAndPreventsFurtherReads() throws Exception {
        TrackingInputStream source = new TrackingInputStream(deflate(bytes("close behavior")));
        DeflateCompressorInputStream stream = new DeflateCompressorInputStream(source);

        stream.close();

        assertTrue(source.closed);
        try {
            stream.read();
            fail("Reading a closed stream should throw IOException");
        } catch (IOException expected) {
            assertTrue(expected.getMessage() == null || expected.getMessage().length() >= 0);
        }
    }

    @Test(expected = CompressorException.class)
    public void factoryRejectsUnrecognizedSignature() throws Exception {
        new CompressorStreamFactory().createCompressorInputStream(
                new ByteArrayInputStream(new byte[] { 0, 1, 2, 3, 4, 5, 6, 7 }));
    }

    @Test(expected = IllegalArgumentException.class)
    public void factoryRequiresMarkSupportedInput() throws Exception {
        InputStream unmarkable = new InputStream() {
            @Override
            public int read() {
                return -1;
            }

            @Override
            public boolean markSupported() {
                return false;
            }
        };

        new CompressorStreamFactory().createCompressorInputStream(unmarkable);
    }

    private static byte[] deflate(byte[] input) throws IOException {
        ByteArrayOutputStream compressed = new ByteArrayOutputStream();
        DeflateCompressorOutputStream output = new DeflateCompressorOutputStream(compressed);
        output.write(input);
        output.close();
        return compressed.toByteArray();
    }

    private static byte[] readAll(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[16];
        int count;
        while ((count = input.read(buffer)) != -1) {
            output.write(buffer, 0, count);
        }
        return output.toByteArray();
    }

    private static byte[] bytes(String value) throws Exception {
        return value.getBytes("UTF-8");
    }

    private static final class TrackingInputStream extends ByteArrayInputStream {
        private boolean closed;

        private TrackingInputStream(byte[] data) {
            super(data);
        }

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        }
    }
}
