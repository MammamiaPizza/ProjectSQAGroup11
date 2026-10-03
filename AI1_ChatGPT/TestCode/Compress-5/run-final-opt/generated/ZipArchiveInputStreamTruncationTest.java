package org.apache.commons.compress.archivers.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;

import junit.framework.TestCase;

public class ZipArchiveInputStreamTruncationTest extends TestCase {

    public void testReadsCompleteStoredEntryAndThenEndsEntry() throws Exception {
        byte[] contents = "abcd".getBytes("UTF-8");
        ZipArchiveInputStream in = new ZipArchiveInputStream(
                new ByteArrayInputStream(localEntry("file.txt", 0, contents.length,
                        contents.length, contents)));

        ZipArchiveEntry entry = in.getNextZipEntry();
        assertNotNull(entry);
        assertEquals("file.txt", entry.getName());
        assertEquals(4L, entry.getSize());

        byte[] buffer = new byte[2];
        assertEquals(2, in.read(buffer, 0, buffer.length));
        assertEquals("ab", new String(buffer, "UTF-8"));
        assertEquals(2, in.read(buffer, 0, buffer.length));
        assertEquals("cd", new String(buffer, "UTF-8"));
        assertEquals(-1, in.read(buffer, 0, buffer.length));
        assertNull(in.getNextZipEntry());

        in.close();
    }

    public void testStoredEntryWithMissingPayloadMustThrowInsteadOfReturningEof() throws Exception {
        byte[] available = "abc".getBytes("UTF-8");
        ZipArchiveInputStream in = new ZipArchiveInputStream(
                new ByteArrayInputStream(localEntry("truncated.txt", 0, 6, 6, available)));

        ZipArchiveEntry entry = in.getNextZipEntry();
        assertNotNull(entry);
        assertEquals(6L, entry.getSize());

        byte[] buffer = new byte[8];
        assertEquals(3, in.read(buffer, 0, buffer.length));
        assertEquals("abc", new String(buffer, 0, 3, "UTF-8"));

        boolean failed = false;
        try {
            in.read(buffer, 0, buffer.length);
        } catch (IOException expected) {
            failed = true;
        }
        assertTrue("A stored entry whose declared data is missing must be reported as truncated", failed);

        in.close();
    }

    public void testDeflatedEntryWithMissingCompressedTailMustThrow() throws Exception {
        byte[] original = "deflated content that requires a complete deflate stream"
                .getBytes("UTF-8");
        byte[] compressed = deflateRaw(original);
        assertTrue(compressed.length > 1);

        byte[] truncated = new byte[compressed.length - 1];
        System.arraycopy(compressed, 0, truncated, 0, truncated.length);

        ZipArchiveInputStream in = new ZipArchiveInputStream(
                new ByteArrayInputStream(localEntry("deflated.txt", 8, compressed.length,
                        original.length, truncated)));

        assertNotNull(in.getNextZipEntry());

        byte[] buffer = new byte[11];
        boolean failed = false;
        for (int i = 0; i < 64; i++) {
            try {
                int read = in.read(buffer, 0, buffer.length);
                if (read <= 0) {
                    fail("A truncated deflated entry must not silently return EOF or zero bytes");
                }
            } catch (IOException expected) {
                failed = true;
                break;
            }
        }
        assertTrue("A missing deflate-stream tail must be reported as an error", failed);

        in.close();
    }

    public void testTruncatedLocalHeaderNameIsReportedAsIOException() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        writeLocalHeader(bytes, 0, 0, 0, 5);
        bytes.write("ab".getBytes("UTF-8"));

        ZipArchiveInputStream in = new ZipArchiveInputStream(
                new ByteArrayInputStream(bytes.toByteArray()));

        boolean failed = false;
        try {
            in.getNextZipEntry();
        } catch (IOException expected) {
            failed = true;
        }
        assertTrue("An incomplete local file header must not be accepted as an entry", failed);

        in.close();
    }

    private static byte[] localEntry(String name, int method, int compressedSize,
            int uncompressedSize, byte[] data) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] nameBytes = name.getBytes("UTF-8");
        writeLocalHeader(out, method, compressedSize, uncompressedSize, nameBytes.length);
        out.write(nameBytes);
        out.write(data);
        return out.toByteArray();
    }

    private static void writeLocalHeader(ByteArrayOutputStream out, int method,
            int compressedSize, int uncompressedSize, int nameLength) {
        writeInt(out, 0x04034b50);
        writeShort(out, 20);
        writeShort(out, 0);
        writeShort(out, method);
        writeShort(out, 0);
        writeShort(out, 0);
        writeInt(out, 0);
        writeInt(out, compressedSize);
        writeInt(out, uncompressedSize);
        writeShort(out, nameLength);
        writeShort(out, 0);
    }

    private static void writeShort(ByteArrayOutputStream out, int value) {
        out.write(value & 0xff);
        out.write((value >>> 8) & 0xff);
    }

    private static void writeInt(ByteArrayOutputStream out, int value) {
        out.write(value & 0xff);
        out.write((value >>> 8) & 0xff);
        out.write((value >>> 16) & 0xff);
        out.write((value >>> 24) & 0xff);
    }

    private static byte[] deflateRaw(byte[] input) {
        Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
        deflater.setInput(input);
        deflater.finish();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[64];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            out.write(buffer, 0, count);
        }
        deflater.end();
        return out.toByteArray();
    }
}
