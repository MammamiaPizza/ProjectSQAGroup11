package org.apache.commons.compress.archivers.zip;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;

import org.junit.Test;

public class ZipArchiveInputStreamStoredEntryTest {

    @Test
    public void readsFirstStoredEntryBytesUnchanged() throws Exception {
        byte[] payload = new byte[] { 100, 101, 102, 103, 104 };
        ZipArchiveInputStream input = new ZipArchiveInputStream(
                new ByteArrayInputStream(storedArchive("first", payload)));

        try {
            ZipArchiveEntry entry = input.getNextZipEntry();
            assertNotNull(entry);
            assertEquals("first", entry.getName());
            assertEquals(ZipArchiveOutputStream.STORED, entry.getMethod());

            byte[] actual = new byte[payload.length];
            assertEquals(payload.length, input.read(actual, 0, actual.length));
            assertArrayEquals(payload, actual);
        } finally {
            input.close();
        }
    }

    @Test
    public void readsStoredEntryUsingOffsetAndPartialReads() throws Exception {
        byte[] payload = new byte[] { 100, 101, 102, 103, 104, 105 };
        ZipArchiveInputStream input = new ZipArchiveInputStream(
                new ByteArrayInputStream(storedArchive("partial", payload)));

        try {
            assertNotNull(input.getNextZipEntry());

            byte[] firstBuffer = new byte[] { 77, 77, 77, 77, 77, 77, 77 };
            assertEquals(3, input.read(firstBuffer, 2, 3));
            assertEquals(77, firstBuffer[0]);
            assertEquals(77, firstBuffer[1]);
            assertEquals(100, firstBuffer[2]);
            assertEquals(101, firstBuffer[3]);
            assertEquals(102, firstBuffer[4]);
            assertEquals(77, firstBuffer[5]);
            assertEquals(77, firstBuffer[6]);

            byte[] remaining = new byte[3];
            assertEquals(3, input.read(remaining, 0, remaining.length));
            assertArrayEquals(new byte[] { 103, 104, 105 }, remaining);
        } finally {
            input.close();
        }
    }

    @Test
    public void returnsEndOfEntryWithoutWritingExtraBytes() throws Exception {
        byte[] payload = new byte[] { 100, 101 };
        ZipArchiveInputStream input = new ZipArchiveInputStream(
                new ByteArrayInputStream(storedArchive("eof", payload)));

        try {
            assertNotNull(input.getNextZipEntry());

            byte[] actual = new byte[payload.length];
            assertEquals(payload.length, input.read(actual, 0, actual.length));
            assertArrayEquals(payload, actual);

            byte[] untouched = new byte[] { 55, 55, 55 };
            assertEquals(-1, input.read(untouched, 0, untouched.length));
            assertArrayEquals(new byte[] { 55, 55, 55 }, untouched);
        } finally {
            input.close();
        }
    }

    @Test
    public void advancesToSecondEntryAfterPartiallyReadingFirstStoredEntry() throws Exception {
        byte[] first = new byte[] { 100, 101, 102, 103 };
        byte[] second = new byte[] { 42, 43, 44 };
        byte[] archive = concat(storedArchive("first", first), storedArchive("second", second));
        ZipArchiveInputStream input = new ZipArchiveInputStream(new ByteArrayInputStream(archive));

        try {
            ZipArchiveEntry firstEntry = input.getNextZipEntry();
            assertNotNull(firstEntry);
            assertEquals("first", firstEntry.getName());

            byte[] oneByte = new byte[1];
            assertEquals(1, input.read(oneByte, 0, 1));
            assertEquals(100, oneByte[0]);

            ZipArchiveEntry secondEntry = input.getNextZipEntry();
            assertNotNull(secondEntry);
            assertEquals("second", secondEntry.getName());

            byte[] actualSecond = new byte[second.length];
            assertEquals(second.length, input.read(actualSecond, 0, actualSecond.length));
            assertArrayEquals(second, actualSecond);
            assertNull(input.getNextZipEntry());
        } finally {
            input.close();
        }
    }

    private static byte[] storedArchive(String name, byte[] data) throws IOException {
        ByteArrayOutputStream archive = new ByteArrayOutputStream();
        byte[] nameBytes = name.getBytes("UTF-8");
        CRC32 crc = new CRC32();
        crc.update(data);

        writeInt(archive, 0x04034b50L);
        writeShort(archive, 20);
        writeShort(archive, 0);
        writeShort(archive, ZipArchiveOutputStream.STORED);
        writeShort(archive, 0);
        writeShort(archive, 0);
        writeInt(archive, crc.getValue());
        writeInt(archive, data.length);
        writeInt(archive, data.length);
        writeShort(archive, nameBytes.length);
        writeShort(archive, 0);
        archive.write(nameBytes);
        archive.write(data);
        return archive.toByteArray();
    }

    private static byte[] concat(byte[] first, byte[] second) throws IOException {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        result.write(first);
        result.write(second);
        return result.toByteArray();
    }

    private static void writeShort(ByteArrayOutputStream output, int value) {
        output.write(value & 0xff);
        output.write((value >>> 8) & 0xff);
    }

    private static void writeInt(ByteArrayOutputStream output, long value) {
        output.write((int) (value & 0xff));
        output.write((int) ((value >>> 8) & 0xff));
        output.write((int) ((value >>> 16) & 0xff));
        output.write((int) ((value >>> 24) & 0xff));
    }
}