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