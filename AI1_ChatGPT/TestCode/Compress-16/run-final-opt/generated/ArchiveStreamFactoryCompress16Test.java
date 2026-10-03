package org.apache.commons.compress.archivers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.junit.Assert;
import org.junit.Test;

public class ArchiveStreamFactoryCompress16Test {

    @Test
    public void aiffSignatureIsNotDetectedAsTarArchive() throws Exception {
        ArchiveStreamFactory factory = new ArchiveStreamFactory();

        try {
            factory.createArchiveInputStream(new ByteArrayInputStream(createAiffData()));
            Assert.fail("An AIFF stream must not be accepted as an archive");
        } catch (ArchiveException expected) {
            Assert.assertTrue(expected.getMessage().length() > 0);
        }
    }

    @Test
    public void validTarHeaderIsAutoDetectedAsTar() throws Exception {
        ArchiveStreamFactory factory = new ArchiveStreamFactory();
        ArchiveInputStream stream =
                factory.createArchiveInputStream(new ByteArrayInputStream(createTarHeader()));

        try {
            Assert.assertTrue(stream instanceof TarArchiveInputStream);
        } finally {
            stream.close();
        }
    }

    @Test
    public void nullAutoDetectStreamIsRejected() throws Exception {
        try {
            new ArchiveStreamFactory().createArchiveInputStream((InputStream) null);
            Assert.fail("Null streams must be rejected");
        } catch (IllegalArgumentException expected) {
            Assert.assertTrue(expected.getMessage().length() > 0);
        }
    }

    @Test
    public void autoDetectRequiresMarkSupportedStream() throws Exception {
        InputStream nonMarkingStream = new InputStream() {
            @Override
            public int read() throws IOException {
                return -1;
            }
        };

        try {
            new ArchiveStreamFactory().createArchiveInputStream(nonMarkingStream);
            Assert.fail("Streams without mark support must be rejected");
        } catch (IllegalArgumentException expected) {
            Assert.assertTrue(expected.getMessage().length() > 0);
        }
    }

    private static byte[] createAiffData() {
        byte[] aiff = new byte[512];

        writeAscii(aiff, 0, "FORM");
        writeInt(aiff, 4, 504);
        writeAscii(aiff, 8, "AIFF");

        writeAscii(aiff, 12, "COMM");
        writeInt(aiff, 16, 18);
        writeShort(aiff, 20, 1);
        writeInt(aiff, 22, 229);
        writeShort(aiff, 26, 16);
        aiff[28] = 0x40;
        aiff[29] = 0x0e;
        aiff[30] = (byte) 0xac;
        aiff[31] = 0x44;

        writeAscii(aiff, 38, "SSND");
        writeInt(aiff, 42, 466);
        writeInt(aiff, 46, 0);
        writeInt(aiff, 50, 0);

        return aiff;
    }

    private static byte[] createTarHeader() {
        byte[] header = new byte[512];

        writeAscii(header, 0, "file");
        writeOctal(header, 100, 8, 0644);
        writeOctal(header, 108, 8, 0);
        writeOctal(header, 116, 8, 0);
        writeOctal(header, 124, 12, 0);
        writeOctal(header, 136, 12, 0);

        for (int i = 148; i < 156; i++) {
            header[i] = (byte) ' ';
        }

        header[156] = (byte) '0';
        writeAscii(header, 257, "ustar");
        header[262] = 0;
        writeAscii(header, 263, "00");

        int checksum = 0;
        for (int i = 0; i < header.length; i++) {
            checksum += header[i] & 0xff;
        }

        String value = Integer.toOctalString(checksum);
        while (value.length() < 6) {
            value = "0" + value;
        }
        writeAscii(header, 148, value);
        header[154] = 0;
        header[155] = (byte) ' ';

        return header;
    }

    private static void writeAscii(byte[] target, int offset, String value) {
        for (int i = 0; i < value.length(); i++) {
            target[offset + i] = (byte) value.charAt(i);
        }
    }

    private static void writeOctal(byte[] target, int offset, int length, int value) {
        String octal = Integer.toOctalString(value);
        while (octal.length() < length - 1) {
            octal = "0" + octal;
        }
        writeAscii(target, offset, octal);
        target[offset + length - 1] = 0;
    }

    private static void writeShort(byte[] target, int offset, int value) {
        target[offset] = (byte) (value >>> 8);
        target[offset + 1] = (byte) value;
    }

    private static void writeInt(byte[] target, int offset, int value) {
        target[offset] = (byte) (value >>> 24);
        target[offset + 1] = (byte) (value >>> 16);
        target[offset + 2] = (byte) (value >>> 8);
        target[offset + 3] = (byte) value;
    }
}
