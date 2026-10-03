package org.apache.commons.compress.archivers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import static org.junit.Assert.fail;

public class ArchiveStreamFactoryBugTest {

    @Test
    public void rejectsEmptyStreamAsUnknownArchive() throws Exception {
        assertNotDetected(new byte[0]);
    }

    @Test
    public void rejectsShortPlainTextAsUnknownArchive() throws Exception {
        assertNotDetected("This is a short text file.".getBytes("UTF-8"));
    }

    @Test
    public void rejectsShortNumericTextAsUnknownArchive() throws Exception {
        assertNotDetected("1234567890".getBytes("UTF-8"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullInputStream() throws Exception {
        new ArchiveStreamFactory().createArchiveInputStream((InputStream) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void requiresMarkSupportedInputStream() throws Exception {
        InputStream stream = new InputStream() {
            @Override
            public int read() {
                return -1;
            }

            @Override
            public boolean markSupported() {
                return false;
            }
        };

        new ArchiveStreamFactory().createArchiveInputStream(stream);
    }

    private void assertNotDetected(byte[] contents) throws Exception {
        try {
            new ArchiveStreamFactory().createArchiveInputStream(
                    new ByteArrayInputStream(contents));
            fail("created an input stream for a non-archive");
        } catch (ArchiveException expected) {
            // expected
        }
    }
}
