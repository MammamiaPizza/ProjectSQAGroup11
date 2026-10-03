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