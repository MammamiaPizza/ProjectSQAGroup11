import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioConstants;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ArchiveStreamFactoryEncodingRegressionTest {

    private static final String UTF8 = "UTF-8";
    private static final String LATIN1 = "ISO-8859-1";

    @Test
    public void configuredEncodingIsReportedByFactory() {
        ArchiveStreamFactory factory = new ArchiveStreamFactory(UTF8);

        assertEquals(UTF8, factory.getEntryEncoding());
    }

    @Test
    public void explicitCpioStreamCreationPreservesUtf8EntryName() throws Exception {
        String name = "ä-東京.txt";
        byte[] archive = createArchive(ArchiveStreamFactory.CPIO, UTF8, name);

        assertEntryName(name, new ArchiveStreamFactory(UTF8)
                .createArchiveInputStream(ArchiveStreamFactory.CPIO,
                        new ByteArrayInputStream(archive)));
    }

    @Test
    public void explicitTarStreamCreationPreservesUtf8EntryName() throws Exception {
        String name = "ä-東京.txt";
        byte[] archive = createArchive(ArchiveStreamFactory.TAR, UTF8, name);

        assertEntryName(name, new ArchiveStreamFactory(UTF8)
                .createArchiveInputStream(ArchiveStreamFactory.TAR,
                        new ByteArrayInputStream(archive)));
    }

    @Test
    public void explicitZipStreamCreationPreservesLatin1EntryName() throws Exception {
        String name = "café.txt";
        byte[] archive = createArchive(ArchiveStreamFactory.ZIP, LATIN1, name);

        assertEntryName(name, new ArchiveStreamFactory(LATIN1)
                .createArchiveInputStream(ArchiveStreamFactory.ZIP,
                        new ByteArrayInputStream(archive)));
    }

    @Test
    public void autodetectedCpioStreamUsesConfiguredEncoding() throws Exception {
        String name = "ä-東京.txt";
        byte[] archive = createArchive(ArchiveStreamFactory.CPIO, UTF8, name);

        assertEntryName(name, new ArchiveStreamFactory(UTF8)
                .createArchiveInputStream(new ByteArrayInputStream(archive)));
    }

    @Test
    public void autodetectedTarStreamUsesConfiguredEncoding() throws Exception {
        String name = "ä-東京.txt";
        byte[] archive = createArchive(ArchiveStreamFactory.TAR, UTF8, name);

        assertEntryName(name, new ArchiveStreamFactory(UTF8)
                .createArchiveInputStream(new ByteArrayInputStream(archive)));
    }

    @Test
    public void autodetectedZipStreamUsesConfiguredEncoding() throws Exception {
        String name = "café.txt";
        byte[] archive = createArchive(ArchiveStreamFactory.ZIP, LATIN1, name);

        assertEntryName(name, new ArchiveStreamFactory(LATIN1)
                .createArchiveInputStream(new ByteArrayInputStream(archive)));
    }

    @Test
    public void zipOutputCreatedByFactoryUsesConfiguredEncoding() throws Exception {
        String name = "café.txt";
        byte[] archive = createArchive(ArchiveStreamFactory.ZIP, LATIN1, name);

        ZipArchiveInputStream input = new ZipArchiveInputStream(
                new ByteArrayInputStream(archive), LATIN1);
        assertEntryName(name, input);
    }

    private byte[] createArchive(String format, String encoding, String name) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ArchiveOutputStream output = new ArchiveStreamFactory(encoding)
                .createArchiveOutputStream(format, bytes);

        if (ArchiveStreamFactory.CPIO.equals(format)) {
            CpioArchiveEntry entry = new CpioArchiveEntry(CpioConstants.FORMAT_NEW, name);
            entry.setSize(0);
            output.putArchiveEntry(entry);
        } else if (ArchiveStreamFactory.TAR.equals(format)) {
            TarArchiveEntry entry = new TarArchiveEntry(name);
            entry.setSize(0);
            output.putArchiveEntry(entry);
        } else if (ArchiveStreamFactory.ZIP.equals(format)) {
            ZipArchiveEntry entry = new ZipArchiveEntry(name);
            entry.setSize(0);
            output.putArchiveEntry(entry);
        } else {
            throw new IllegalArgumentException("Unsupported test format: " + format);
        }

        output.closeArchiveEntry();
        output.finish();
        output.close();
        return bytes.toByteArray();
    }

    private void assertEntryName(String expectedName, ArchiveInputStream input) throws Exception {
        try {
            ArchiveEntry entry = input.getNextEntry();
            assertNotNull(entry);
            assertEquals(expectedName, entry.getName());
        } finally {
            input.close();
        }
    }
}
