package org.apache.commons.compress.archivers.tar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TarArchiveInputStreamCOMPRESS178Test {

    @Test
    public void readsEntryWhoseModeContainsAnEmbeddedNulAndContinuesToNextEntry()
            throws Exception {
        byte[] archive = createArchive();
        setModeWithEmbeddedNul(archive, 0);
        updateChecksum(archive, 0);

        TarArchiveInputStream input =
                new TarArchiveInputStream(new ByteArrayInputStream(archive));
        try {
            TarArchiveEntry first = input.getNextTarEntry();
            assertNotNull(first);
            assertEquals("first", first.getName());

            byte[] firstContents = new byte[8];
            assertEquals(3, input.read(firstContents, 0, firstContents.length));
            assertEquals('o', firstContents[0]);
            assertEquals('n', firstContents[1]);
            assertEquals('e', firstContents[2]);
            assertEquals(-1, input.read(firstContents, 0, firstContents.length));

            TarArchiveEntry second = input.getNextTarEntry();
            assertNotNull(second);
            assertEquals("second", second.getName());

            byte[] secondContents = new byte[4];
            assertEquals(1, input.read(secondContents, 0, secondContents.length));
            assertEquals('x', secondContents[0]);
            assertEquals(-1, input.read(secondContents, 0, secondContents.length));
            assertEquals(null, input.getNextTarEntry());
        } finally {
            input.close();
        }
    }

    private byte[] createArchive() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        TarArchiveOutputStream output = new TarArchiveOutputStream(bytes);
        try {
            TarArchiveEntry first = new TarArchiveEntry("first");
            first.setSize(3);
            output.putArchiveEntry(first);
            output.write(new byte[] { 'o', 'n', 'e' });
            output.closeArchiveEntry();

            TarArchiveEntry second = new TarArchiveEntry("second");
            second.setSize(1);
            output.putArchiveEntry(second);
            output.write(new byte[] { 'x' });
            output.closeArchiveEntry();
        } finally {
            output.close();
        }
        return bytes.toByteArray();
    }

    private void setModeWithEmbeddedNul(byte[] archive, int headerOffset) {
        int modeOffset = headerOffset + 100;
        archive[modeOffset] = '0';
        archive[modeOffset + 1] = '0';
        archive[modeOffset + 2] = 0;
        archive[modeOffset + 3] = '0';
        archive[modeOffset + 4] = '7';
        archive[modeOffset + 5] = '6';
        archive[modeOffset + 6] = '5';
        archive[modeOffset + 7] = 0;
    }

    private void updateChecksum(byte[] archive, int headerOffset) {
        int checksumOffset = headerOffset + 148;
        for (int i = 0; i < 8; i++) {
            archive[checksumOffset + i] = ' ';
        }

        long checksum = 0;
        for (int i = 0; i < 512; i++) {
            checksum += archive[headerOffset + i] & 0xff;
        }

        String value = Long.toOctalString(checksum);
        for (int i = 0; i < 6; i++) {
            archive[checksumOffset + i] = '0';
        }
        for (int i = 0; i < value.length(); i++) {
            archive[checksumOffset + 6 - value.length() + i] =
                    (byte) value.charAt(i);
        }
        archive[checksumOffset + 6] = 0;
        archive[checksumOffset + 7] = ' ';
    }
}
