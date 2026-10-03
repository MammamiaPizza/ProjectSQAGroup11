package org.apache.commons.compress.archivers.tar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.compress.archivers.TarArchiveInputStream;
import org.junit.Test;

public class TarUtilsCOMPRESS301Test {

    @Test
    public void parseOctalParsesLeadingSpacesAndTrailingPadding() {
        final byte[] field = new byte[] { ' ', ' ', '0', '0', '1', '7', 0, ' ' };

        assertEquals(15L, TarUtils.parseOctal(field, 0, field.length));
    }

    @Test
    public void parseOctalParsesFieldAtNonZeroOffset() {
        final byte[] buffer = new byte[] { 99, '0', '0', '7', 0, 88 };

        assertEquals(7L, TarUtils.parseOctal(buffer, 1, 4));
    }

    @Test
    public void parseOctalAcceptsAnEmptyNulPaddedFieldAsZero() {
        final byte[] field = new byte[] { 0, 0, 0, 0 };

        assertEquals(0L, TarUtils.parseOctal(field, 0, field.length));
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseOctalRejectsEmbeddedNulBeforeLaterDigits() {
        final byte[] field = new byte[] { '0', '0', '1', 0, '0', '2', 0, ' ' };

        TarUtils.parseOctal(field, 0, field.length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseOctalRejectsNonOctalDigit() {
        final byte[] field = new byte[] { '0', '0', '8', 0 };

        TarUtils.parseOctal(field, 0, field.length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void parseOctalRejectsTooShortField() {
        TarUtils.parseOctal(new byte[] { '0' }, 0, 1);
    }

    @Test
    public void malformedTarHeaderWithEmbeddedNulInSizeFieldThrowsIOException() throws Exception {
        final byte[] header = new byte[512];
        Arrays.fill(header, (byte) 0);

        putAscii(header, 0, "bad-entry");
        putAscii(header, 100, "0000644");
        putAscii(header, 108, "0000000");
        putAscii(header, 116, "0000000");
        header[124] = '0';
        header[125] = '0';
        header[126] = '0';
        header[127] = '1';
        header[128] = 0;
        header[129] = '0';
        header[130] = '0';
        header[131] = '2';
        putAscii(header, 136, "00000000000");
        header[156] = '0';
        putAscii(header, 257, "ustar");
        putAscii(header, 263, "00");

        for (int i = 148; i < 156; i++) {
            header[i] = ' ';
        }
        long checksum = 0;
        for (byte b : header) {
            checksum += b & 0xff;
        }
        final String checksumText = String.format("%06o", checksum);
        putAscii(header, 148, checksumText);
        header[154] = 0;
        header[155] = ' ';

        TarArchiveInputStream input = new TarArchiveInputStream(new ByteArrayInputStream(header));
        try {
            input.getNextTarEntry();
            fail("Expected IOException for an embedded NUL in the size field");
        } catch (IOException expected) {
            // expected
        } finally {
            input.close();
        }
    }

    private static void putAscii(final byte[] target, final int offset, final String value) {
        for (int i = 0; i < value.length(); i++) {
            target[offset + i] = (byte) value.charAt(i);
        }
    }
}
