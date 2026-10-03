package org.apache.commons.compress.archivers.sevenz;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class SevenZOutputFileBitSetBoundaryTest {

    @Test
    public void testSixEmptyFilesRoundTrip() throws Exception {
        createAndVerifyEmptyArchive(6);
    }

    @Test
    public void testSevenEmptyFilesRoundTrip() throws Exception {
        createAndVerifyEmptyArchive(7);
    }

    @Test
    public void testEightEmptyFilesRoundTrip() throws Exception {
        createAndVerifyEmptyArchive(8);
    }

    @Test
    public void testNineEmptyFilesRoundTrip() throws Exception {
        createAndVerifyEmptyArchive(9);
    }

    @Test
    public void testSixFilesWithSomeContentRoundTrip() throws Exception {
        createAndVerifyMixedArchive(6);
    }

    @Test
    public void testSevenFilesWithSomeContentRoundTrip() throws Exception {
        createAndVerifyMixedArchive(7);
    }

    @Test
    public void testEightFilesWithSomeContentRoundTrip() throws Exception {
        createAndVerifyMixedArchive(8);
    }

    @Test
    public void testNineFilesWithSomeContentRoundTrip() throws Exception {
        createAndVerifyMixedArchive(9);
    }

    private void createAndVerifyEmptyArchive(final int count) throws Exception {
        final File archive = newArchiveFile();
        try {
            final String[] names = new String[count];
            final byte[][] contents = new byte[count][];
            final SevenZOutputFile output = new SevenZOutputFile(archive);
            try {
                for (int i = 0; i < count; i++) {
                    names[i] = "empty-" + i;
                    contents[i] = new byte[0];

                    final SevenZArchiveEntry entry = new SevenZArchiveEntry();
                    entry.setName(names[i]);
                    output.putArchiveEntry(entry);
                    output.closeArchiveEntry();
                }
            } finally {
                output.close();
            }

            assertArchiveContents(archive, names, contents);
        } finally {
            archive.delete();
        }
    }

    private void createAndVerifyMixedArchive(final int count) throws Exception {
        final File archive = newArchiveFile();
        try {
            final String[] names = new String[count];
            final byte[][] contents = new byte[count][];
            final SevenZOutputFile output = new SevenZOutputFile(archive);
            try {
                for (int i = 0; i < count; i++) {
                    names[i] = "entry-" + i;
                    if (i % 3 == 0) {
                        contents[i] = new byte[0];
                    } else {
                        contents[i] = new byte[] {
                            (byte) (count + i),
                            (byte) (i * 7),
                            (byte) (127 - i),
                            (byte) (i + 1)
                        };
                    }

                    final SevenZArchiveEntry entry = new SevenZArchiveEntry();
                    entry.setName(names[i]);
                    output.putArchiveEntry(entry);
                    if (contents[i].length > 0) {
                        output.write(contents[i][0]);
                        output.write(contents[i], 1, contents[i].length - 1);
                    }
                    output.closeArchiveEntry();
                }
            } finally {
                output.close();
            }

            assertArchiveContents(archive, names, contents);
        } finally {
            archive.delete();
        }
    }

    private void assertArchiveContents(final File archive, final String[] names,
            final byte[][] expectedContents) throws Exception {
        final SevenZFile input = new SevenZFile(archive);
        try {
            for (int i = 0; i < names.length; i++) {
                final SevenZArchiveEntry entry = input.getNextEntry();
                assertNotNull("Missing entry at index " + i, entry);
                assertEquals(names[i], entry.getName());
                assertEquals((long) expectedContents[i].length, entry.getSize());

                if (expectedContents[i].length > 0) {
                    final byte[] actual = new byte[expectedContents[i].length];
                    int offset = 0;
                    while (offset < actual.length) {
                        final int read = input.read(actual, offset, actual.length - offset);
                        assertTrue("Unexpected end of entry " + names[i], read > 0);
                        offset += read;
                    }
                    assertArrayEquals(expectedContents[i], actual);
                }
            }
            assertNull(input.getNextEntry());
        } finally {
            input.close();
        }
    }

    private File newArchiveFile() throws IOException {
        final File archive = File.createTempFile("seven-z-boundary-", ".7z");
        if (!archive.delete()) {
            throw new IOException("Unable to prepare temporary archive file");
        }
        return archive;
    }

@org.junit.Test
public void testCopyCompressionWithLastModifiedDateRoundTrips() throws Exception {
    final java.io.File archive = java.io.File.createTempFile("sevenz-copy-", ".7z");
    try {
        final SevenZOutputFile output = new SevenZOutputFile(archive);
        try {
            output.setContentCompression(SevenZMethod.COPY);

            final SevenZArchiveEntry entry = new SevenZArchiveEntry();
            entry.setName("copy.txt");
            entry.setLastModifiedDate(new java.util.Date(123456789000L));
            output.putArchiveEntry(entry);
            output.write(new byte[] { 1, 2, 3, 4 });
            output.closeArchiveEntry();
        } finally {
            output.close();
        }

        assertArchiveContents(archive, new String[] { "copy.txt" },
                new byte[][] { new byte[] { 1, 2, 3, 4 } });
    } finally {
        archive.delete();
    }
}

@org.junit.Test
public void testMixedLastModifiedDatesForEightEntriesRoundTrip() throws Exception {
    final java.io.File archive = java.io.File.createTempFile("sevenz-mtime-", ".7z");
    final String[] names = new String[8];
    final byte[][] contents = new byte[8][];
    try {
        final SevenZOutputFile output = new SevenZOutputFile(archive);
        try {
            for (int i = 0; i < names.length; i++) {
                names[i] = "entry-" + i;
                contents[i] = new byte[] { (byte) i, (byte) (i + 1) };

                final SevenZArchiveEntry entry = new SevenZArchiveEntry();
                entry.setName(names[i]);
                if ((i & 1) == 0) {
                    entry.setLastModifiedDate(new java.util.Date(123456789000L + i * 1000L));
                }
                output.putArchiveEntry(entry);
                output.write(contents[i]);
                output.closeArchiveEntry();
            }
        } finally {
            output.close();
        }

        assertArchiveContents(archive, names, contents);
    } finally {
        archive.delete();
    }
}

@org.junit.Test
public void testFinishCannotBeCalledTwice() throws Exception {
    final java.io.File archive = java.io.File.createTempFile("sevenz-finish-", ".7z");
    try {
        final SevenZOutputFile output = new SevenZOutputFile(archive);
        final SevenZArchiveEntry entry = new SevenZArchiveEntry();
        entry.setName("entry");
        output.putArchiveEntry(entry);
        output.closeArchiveEntry();

        output.finish();

        try {
            output.finish();
            org.junit.Assert.fail("Expected finish to reject a second invocation");
        } catch (final java.io.IOException expected) {
            // expected
        }
    } finally {
        archive.delete();
    }
}
}
