package org.apache.commons.compress.archivers.zip;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ZipFileUnicodeExtraFieldRegressionTest {

    private static final String LEGACY_NAME = "entry.txt";
    private static final String UNICODE_NAME = "\u20ac_for_Dollar.txt";
    private static final byte[] CONTENT = new byte[] { 1, 2, 3, 4, 5 };

    @Test
    public void unicodeExtraFieldNameCanBeLookedUpAndRead() throws Exception {
        File archive = createArchiveWithUnicodePathExtraField();
        ZipFile zipFile = null;
        InputStream input = null;
        try {
            zipFile = new ZipFile(archive, "CP437", true);

            ZipArchiveEntry entry = zipFile.getEntry(UNICODE_NAME);
            assertNotNull("Unicode extra field name must resolve to an entry", entry);

            input = zipFile.getInputStream(entry);
            assertNotNull("Resolved Unicode entry must have readable data", input);
            assertArrayEquals(CONTENT, readFully(input));

            assertNull("An absent entry name must not resolve to an entry",
                    zipFile.getEntry("does-not-exist.txt"));
        } finally {
            if (input != null) {
                input.close();
            }
            ZipFile.closeQuietly(zipFile);
            archive.delete();
        }
    }

    @Test
    public void disablingUnicodeExtraFieldsKeepsTheOriginalEncodedName() throws Exception {
        File archive = createArchiveWithUnicodePathExtraField();
        ZipFile zipFile = null;
        InputStream input = null;
        try {
            zipFile = new ZipFile(archive, "CP437", false);

            assertEquals("CP437", zipFile.getEncoding());
            assertNull(zipFile.getEntry(UNICODE_NAME));

            ZipArchiveEntry entry = zipFile.getEntry(LEGACY_NAME);
            assertNotNull("Without Unicode extra fields the original name is used", entry);

            input = zipFile.getInputStream(entry);
            assertNotNull(input);
            assertArrayEquals(CONTENT, readFully(input));
        } finally {
            if (input != null) {
                input.close();
            }
            ZipFile.closeQuietly(zipFile);
            archive.delete();
        }
    }

    private static File createArchiveWithUnicodePathExtraField() throws Exception {
        File file = File.createTempFile("unicode-extra-field", ".zip");
        file.deleteOnExit();

        byte[] legacyName = LEGACY_NAME.getBytes("US-ASCII");
        byte[] unicodeName = UNICODE_NAME.getBytes("UTF-8");
        byte[] extra = unicodePathExtraField(legacyName, unicodeName);

        CRC32 contentCrc = new CRC32();
        contentCrc.update(CONTENT);
        long crc = contentCrc.getValue();

        OutputStream out = new FileOutputStream(file);
        try {
            long localHeaderOffset = 0;

            writeInt(out, 0x04034b50L);
            writeShort(out, 20);
            writeShort(out, 0);
            writeShort(out, ZipArchiveEntry.STORED);
            writeShort(out, 0);
            writeShort(out, 0);
            writeInt(out, crc);
            writeInt(out, CONTENT.length);
            writeInt(out, CONTENT.length);
            writeShort(out, legacyName.length);
            writeShort(out, extra.length);
            out.write(legacyName);
            out.write(extra);
            out.write(CONTENT);

            long centralDirectoryOffset =
                    30 + legacyName.length + extra.length + CONTENT.length;

            writeInt(out, 0x02014b50L);
            writeShort(out, 20);
            writeShort(out, 20);
            writeShort(out, 0);
            writeShort(out, ZipArchiveEntry.STORED);
            writeShort(out, 0);
            writeShort(out, 0);
            writeInt(out, crc);
            writeInt(out, CONTENT.length);
            writeInt(out, CONTENT.length);
            writeShort(out, legacyName.length);
            writeShort(out, extra.length);
            writeShort(out, 0);
            writeShort(out, 0);
            writeShort(out, 0);
            writeInt(out, 0);
            writeInt(out, localHeaderOffset);
            out.write(legacyName);
            out.write(extra);

            long centralDirectorySize =
                    46 + legacyName.length + extra.length;

            writeInt(out, 0x06054b50L);
            writeShort(out, 0);
            writeShort(out, 0);
            writeShort(out, 1);
            writeShort(out, 1);
            writeInt(out, centralDirectorySize);
            writeInt(out, centralDirectoryOffset);
            writeShort(out, 0);
        } finally {
            out.close();
        }
        return file;
    }

    private static byte[] unicodePathExtraField(byte[] legacyName,
                                                  byte[] unicodeName)
            throws IOException {
        CRC32 nameCrc = new CRC32();
        nameCrc.update(legacyName);

        ByteArrayOutputStream data = new ByteArrayOutputStream();
        data.write(1);
        writeInt(data, nameCrc.getValue());
        data.write(unicodeName);

        byte[] payload = data.toByteArray();
        ByteArrayOutputStream field = new ByteArrayOutputStream();
        writeShort(field, 0x7075);
        writeShort(field, payload.length);
        field.write(payload);
        return field.toByteArray();
    }

    private static byte[] readFully(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[32];
        int read;
        while ((read = input.read(buffer)) != -1) {
            output.write(buffer, 0, read);
        }
        return output.toByteArray();
    }

    private static void writeShort(OutputStream out, int value)
            throws IOException {
        out.write(value & 0xff);
        out.write((value >>> 8) & 0xff);
    }

    private static void writeInt(OutputStream out, long value)
            throws IOException {
        out.write((int) (value & 0xff));
        out.write((int) ((value >>> 8) & 0xff));
        out.write((int) ((value >>> 16) & 0xff));
        out.write((int) ((value >>> 24) & 0xff));
    }
}