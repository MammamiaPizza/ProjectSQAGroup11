package org.apache.commons.compress.archivers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;

public class ArchiveStreamFactoryAIFFTest {

    @Test(expected = ArchiveException.class)
    public void aiffSignatureIsNotDetectedAsTarArchive() throws Exception {
        byte[] aiff = new byte[512];
        aiff[0] = 'F';
        aiff[1] = 'O';
        aiff[2] = 'R';
        aiff[3] = 'M';
        aiff[4] = 0;
        aiff[5] = 0;
        aiff[6] = 0;
        aiff[7] = 46;
        aiff[8] = 'A';
        aiff[9] = 'I';
        aiff[10] = 'F';
        aiff[11] = 'F';

        aiff[12] = 'C';
        aiff[13] = 'O';
        aiff[14] = 'M';
        aiff[15] = 'M';
        aiff[19] = 18;

        aiff[38] = 'S';
        aiff[39] = 'S';
        aiff[40] = 'N';
        aiff[41] = 'D';
        aiff[45] = 8;

        new ArchiveStreamFactory().createArchiveInputStream(
                new ByteArrayInputStream(aiff));
    }

    @Test(expected = IllegalArgumentException.class)
    public void autodetectionRejectsNullStream() throws Exception {
        new ArchiveStreamFactory().createArchiveInputStream((InputStream) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void autodetectionRequiresMarkSupportedStream() throws Exception {
        InputStream unmarkable = new InputStream() {
            public int read() {
                return -1;
            }
        };

        new ArchiveStreamFactory().createArchiveInputStream(unmarkable);
    }

    @Test(expected = ArchiveException.class)
    public void autodetectionRejectsUnknownMarkedSignature() throws Exception {
        new ArchiveStreamFactory().createArchiveInputStream(
                new ByteArrayInputStream(new byte[] { 1, 2, 3, 4 }));
    }
}
