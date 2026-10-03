package org.apache.commons.compress.archivers;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.junit.Assert;
import org.junit.Test;

public class ArchiveStreamFactoryRegressionTest {

    @Test
    public void autoDetectionRejectsAiffFormDataThatIsNotATarArchive() throws Exception {
        byte[] aiff = new byte[512];
        aiff[0] = 'F';
        aiff[1] = 'O';
        aiff[2] = 'R';
        aiff[3] = 'M';
        aiff[7] = 4;
        aiff[8] = 'A';
        aiff[9] = 'I';
        aiff[10] = 'F';
        aiff[11] = 'F';

        try {
            new ArchiveStreamFactory().createArchiveInputStream(
                    new ByteArrayInputStream(aiff));
            Assert.fail("AIFF FORM data must not be detected as a TAR archive");
        } catch (ArchiveException expected) {
            Assert.assertNotNull(expected.getMessage());
        }
    }

    @Test
    public void autoDetectionRecognizesAnEmptyZipArchive() throws Exception {
        byte[] emptyZip = new byte[22];
        emptyZip[0] = 'P';
        emptyZip[1] = 'K';
        emptyZip[2] = 5;
        emptyZip[3] = 6;

        ArchiveInputStream stream = new ArchiveStreamFactory()
                .createArchiveInputStream(new ByteArrayInputStream(emptyZip));

        try {
            Assert.assertTrue(stream instanceof ZipArchiveInputStream);
        } finally {
            stream.close();
        }
    }

    @Test(expected = ArchiveException.class)
    public void autoDetectionRejectsUnknownSignatures() throws Exception {
        new ArchiveStreamFactory().createArchiveInputStream(
                new ByteArrayInputStream(new byte[] { 1, 2, 3, 4, 5, 6 }));
    }

    @Test(expected = IllegalArgumentException.class)
    public void autoDetectionRejectsNullStream() throws Exception {
        new ArchiveStreamFactory().createArchiveInputStream((InputStream) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void autoDetectionRequiresMarkSupport() throws Exception {
        InputStream withoutMarkSupport = new InputStream() {
            @Override
            public int read() {
                return -1;
            }
        };

        new ArchiveStreamFactory().createArchiveInputStream(withoutMarkSupport);
    }
}
