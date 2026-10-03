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

@Test
public void explicitArchiveNamesCreateTheirCorrespondingInputStreams() throws Exception {
    final org.apache.commons.compress.archivers.ArchiveStreamFactory factory =
        new org.apache.commons.compress.archivers.ArchiveStreamFactory();

    org.junit.Assert.assertTrue(factory.createArchiveInputStream("Ar",
        new java.io.ByteArrayInputStream(new byte[0]))
        instanceof org.apache.commons.compress.archivers.ar.ArArchiveInputStream);
    org.junit.Assert.assertTrue(factory.createArchiveInputStream("ZIP",
        new java.io.ByteArrayInputStream(new byte[0]))
        instanceof org.apache.commons.compress.archivers.zip.ZipArchiveInputStream);
    org.junit.Assert.assertTrue(factory.createArchiveInputStream("tar",
        new java.io.ByteArrayInputStream(new byte[0]))
        instanceof org.apache.commons.compress.archivers.tar.TarArchiveInputStream);
    org.junit.Assert.assertTrue(factory.createArchiveInputStream("Jar",
        new java.io.ByteArrayInputStream(new byte[0]))
        instanceof org.apache.commons.compress.archivers.jar.JarArchiveInputStream);
    org.junit.Assert.assertTrue(factory.createArchiveInputStream("cpio",
        new java.io.ByteArrayInputStream(new byte[0]))
        instanceof org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream);
}

@Test
public void autoDetectionRecognizesArAndCpioSignatures() throws Exception {
    final org.apache.commons.compress.archivers.ArchiveStreamFactory factory =
        new org.apache.commons.compress.archivers.ArchiveStreamFactory();

    org.junit.Assert.assertTrue(factory.createArchiveInputStream(
        new java.io.ByteArrayInputStream(new byte[] {
            '!', '<', 'a', 'r', 'c', 'h', '>', '\n'
        })) instanceof org.apache.commons.compress.archivers.ar.ArArchiveInputStream);

    org.junit.Assert.assertTrue(factory.createArchiveInputStream(
        new java.io.ByteArrayInputStream(new byte[] {
            '0', '7', '0', '7', '0', '1'
        })) instanceof org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream);
}

@Test
public void namedCreationRejectsInvalidArgumentsAndUnknownArchivers() throws Exception {
    final org.apache.commons.compress.archivers.ArchiveStreamFactory factory =
        new org.apache.commons.compress.archivers.ArchiveStreamFactory();

    try {
        factory.createArchiveInputStream(null, new java.io.ByteArrayInputStream(new byte[0]));
        org.junit.Assert.fail("A null archiver name must be rejected");
    } catch (IllegalArgumentException expected) {
        org.junit.Assert.assertEquals("Archivername must not be null.", expected.getMessage());
    }

    try {
        factory.createArchiveInputStream(org.apache.commons.compress.archivers.ArchiveStreamFactory.AR, null);
        org.junit.Assert.fail("A null input stream must be rejected");
    } catch (IllegalArgumentException expected) {
        org.junit.Assert.assertEquals("InputStream must not be null.", expected.getMessage());
    }

    try {
        factory.createArchiveInputStream("unknown", new java.io.ByteArrayInputStream(new byte[0]));
        org.junit.Assert.fail("Unknown archiver names must be rejected");
    } catch (org.apache.commons.compress.archivers.ArchiveException expected) {
        org.junit.Assert.assertEquals("Archiver: unknown not found.", expected.getMessage());
    }
}

@Test
public void autoDetectionWrapsResetFailures() throws Exception {
    final org.apache.commons.compress.archivers.ArchiveStreamFactory factory =
        new org.apache.commons.compress.archivers.ArchiveStreamFactory();

    try {
        factory.createArchiveInputStream(new java.io.InputStream() {
            @Override
            public int read() {
                return -1;
            }

            @Override
            public boolean markSupported() {
                return true;
            }
        });
        org.junit.Assert.fail("Reset failures must be reported as archive errors");
    } catch (org.apache.commons.compress.archivers.ArchiveException expected) {
        org.junit.Assert.assertEquals("Could not use reset and mark operations.", expected.getMessage());
    }
}
}
