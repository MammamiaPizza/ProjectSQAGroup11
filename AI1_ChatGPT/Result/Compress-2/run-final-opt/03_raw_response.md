package org.apache.commons.compress.archivers.ar;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

public class ArArchiveInputStreamDefects4JTest extends TestCase {

    public void testReadsEntryFromValidArchive() throws Exception {
        byte[] archive = archive(new byte[][] { bytes("") });
        ArArchiveInputStream input =
            new ArArchiveInputStream(new ByteArrayInputStream(archive));

        assertNotNull(input.getNextArEntry());
        assertNull(input.getNextArEntry());
    }

    public void testGetNextEntryReturnsEntryFromValidArchive() throws Exception {
        ArArchiveInputStream input =
            new ArArchiveInputStream(new ByteArrayInputStream(archive(new byte[][] { bytes("data") })));

        assertNotNull(input.getNextEntry());
        assertNull(input.getNextEntry());
    }

    public void testReadDoesNotConsumeFollowingEntryHeader() throws Exception {
        ArArchiveInputStream input = new ArArchiveInputStream(
            new ByteArrayInputStream(archive(new byte[][] { bytes("abc"), bytes("Z") })));

        assertNotNull(input.getNextArEntry());

        byte[] buffer = new byte[32];
        assertEquals(3, input.read(buffer, 0, buffer.length));
        assertEquals("abc", new String(buffer, 0, 3, "US-ASCII"));
        assertEquals(-1, input.read());

        assertNotNull(input.getNextArEntry());
        assertEquals('Z', input.read());
        assertEquals(-1, input.read());
        assertNull(input.getNextArEntry());
    }

    public void testRequestingNextEntrySkipsUnreadPreviousEntryData() throws Exception {
        ArArchiveInputStream input = new ArArchiveInputStream(
            new ByteArrayInputStream(archive(new byte[][] { bytes("abc"), bytes("Z") })));

        assertNotNull(input.getNextArEntry());
        assertNotNull(input.getNextArEntry());
        assertEquals('Z', input.read());
        assertNull(input.getNextArEntry());
    }

    public void testEmptyValidArchiveHasNoEntries() throws Exception {
        ArArchiveInputStream input =
            new ArArchiveInputStream(new ByteArrayInputStream(bytes("!<arch>\n")));

        assertNull(input.getNextArEntry());
    }

    public void testInvalidGlobalHeaderIsRejected() throws Exception {
        ArArchiveInputStream input =
            new ArArchiveInputStream(new ByteArrayInputStream(bytes("not-ar!!")));

        try {
            input.getNextArEntry();
            fail("An invalid global header must be rejected");
        } catch (IOException expected) {
            assertNotNull(expected.getMessage());
        }
    }

    private static byte[] archive(byte[][] contents) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(bytes("!<arch>\n"));

        for (int i = 0; i < contents.length; i++) {
            byte[] content = contents[i];
            output.write(field("entry" + i + "/", 16));
            output.write(field("0", 12));
            output.write(field("0", 6));
            output.write(field("0", 6));
            output.write(field("100644", 8));
            output.write(field(String.valueOf(content.length), 10));
            output.write(bytes("`\n"));
            output.write(content);
            if ((content.length & 1) != 0) {
                output.write('\n');
            }
        }

        return output.toByteArray();
    }

    private static byte[] field(String value, int length) throws IOException {
        StringBuffer result = new StringBuffer(value);
        while (result.length() < length) {
            result.append(' ');
        }
        return bytes(result.substring(0, length));
    }

    private static byte[] bytes(String value) throws IOException {
        return value.getBytes("US-ASCII");
    }
}