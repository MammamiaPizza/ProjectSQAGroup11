package org.apache.commons.compress.archivers.tar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

public class TarUtilsChecksumTest {

    @Test
    public void computeCheckSumUsesUnsignedByteValues() {
        byte[] bytes = new byte[] { 0, (byte) 0x80, (byte) 0xff };

        assertEquals(383L, TarUtils.computeCheckSum(bytes));
    }

    @Test
    public void verifyCheckSumAcceptsStandardUnsignedChecksum() {
        byte[] header = new byte[512];
        header[0] = 'f';
        header[1] = 'i';
        header[2] = 'l';
        header[3] = 'e';
        header[100] = '0';
        header[257] = 'u';
        header[258] = 's';
        header[259] = 't';
        header[260] = 'a';
        header[261] = 'r';
        storeChecksum(header, unsignedChecksumWithSpaces(header));

        assertTrue(TarUtils.verifyCheckSum(header));
    }

    @Test
    public void verifyCheckSumTreatsChecksumFieldAsSpaces() {
        byte[] header = new byte[512];
        Arrays.fill(header, TarConstants.CHKSUM_OFFSET,
                TarConstants.CHKSUM_OFFSET + TarConstants.CHKSUMLEN, (byte) ' ');
        long checksum = TarUtils.computeCheckSum(header);
        writeChecksumField(header, checksum);

        assertTrue(TarUtils.verifyCheckSum(header));
    }

    @Test
    public void verifyCheckSumAcceptsUnsignedChecksumForHighBitBytes() {
        byte[] header = new byte[512];
        header[0] = (byte) 0x80;
        storeChecksum(header, unsignedChecksumWithSpaces(header));

        assertTrue(TarUtils.verifyCheckSum(header));
    }

    @Test
    public void verifyCheckSumAcceptsHistoricSignedChecksumForHighBitBytes() {
        byte[] header = new byte[512];
        header[0] = (byte) 0x80;
        storeChecksum(header, signedChecksumWithSpaces(header));

        assertTrue(TarUtils.verifyCheckSum(header));
    }

    @Test
    public void verifyCheckSumRejectsHeaderWhoseContentChangedAfterChecksumWasStored() {
        byte[] header = new byte[512];
        header[0] = 'a';
        storeChecksum(header, unsignedChecksumWithSpaces(header));
        header[1] = 'b';

        assertFalse(TarUtils.verifyCheckSum(header));
    }

    @Test(expected = IllegalArgumentException.class)
    public void verifyCheckSumRejectsNonOctalChecksumField() {
        byte[] header = new byte[512];
        header[0] = 'a';
        Arrays.fill(header, TarConstants.CHKSUM_OFFSET,
                TarConstants.CHKSUM_OFFSET + TarConstants.CHKSUMLEN, (byte) ' ');
        header[TarConstants.CHKSUM_OFFSET] = '0';
        header[TarConstants.CHKSUM_OFFSET + 1] = '0';
        header[TarConstants.CHKSUM_OFFSET + 2] = 'x';
        header[TarConstants.CHKSUM_OFFSET + 3] = '0';
        header[TarConstants.CHKSUM_OFFSET + 4] = '0';
        header[TarConstants.CHKSUM_OFFSET + 5] = '0';
        header[TarConstants.CHKSUM_OFFSET + 6] = 0;
        header[TarConstants.CHKSUM_OFFSET + 7] = ' ';

        TarUtils.verifyCheckSum(header);
    }

    private static void storeChecksum(byte[] header, long checksum) {
        writeChecksumField(header, checksum);
    }

    private static void writeChecksumField(byte[] header, long checksum) {
        String octal = Long.toOctalString(checksum);
        while (octal.length() < 6) {
            octal = "0" + octal;
        }
        if (octal.length() > 6) {
            throw new IllegalArgumentException("Checksum does not fit in a tar checksum field");
        }

        for (int i = 0; i < 6; i++) {
            header[TarConstants.CHKSUM_OFFSET + i] = (byte) octal.charAt(i);
        }
        header[TarConstants.CHKSUM_OFFSET + 6] = 0;
        header[TarConstants.CHKSUM_OFFSET + 7] = ' ';
    }

    private static long unsignedChecksumWithSpaces(byte[] header) {
        long sum = 0;
        for (int i = 0; i < header.length; i++) {
            byte value = inChecksumField(i) ? (byte) ' ' : header[i];
            sum += value & 0xff;
        }
        return sum;
    }

    private static long signedChecksumWithSpaces(byte[] header) {
        long sum = 0;
        for (int i = 0; i < header.length; i++) {
            sum += inChecksumField(i) ? ' ' : header[i];
        }
        return sum;
    }

    private static boolean inChecksumField(int index) {
        return index >= TarConstants.CHKSUM_OFFSET
                && index < TarConstants.CHKSUM_OFFSET + TarConstants.CHKSUMLEN;
    }
}
