public void testBulkReadStopsAtCurrentEntryAndLeavesFollowingEntryReadable() throws Exception {
    final java.io.ByteArrayOutputStream archive = new java.io.ByteArrayOutputStream();
    archive.write("!<arch>\n".getBytes("US-ASCII"));
    compress2bWriteArMember(archive, "first/", "A");
    compress2bWriteArMember(archive, "second/", "B");

    final org.apache.commons.compress.archivers.ar.ArArchiveInputStream input =
        new org.apache.commons.compress.archivers.ar.ArArchiveInputStream(
            new java.io.ByteArrayInputStream(archive.toByteArray()));

    assertNotNull(input.getNextArEntry());
    final byte[] data = new byte[5];
    assertEquals(1, input.read(data, 2, 3));
    assertEquals('A', data[2]);
    assertEquals(-1, input.read(data, 0, data.length));

    assertNotNull(input.getNextArEntry());
    assertEquals('B', input.read());
    assertEquals(-1, input.read());
    assertNull(input.getNextArEntry());
    input.close();
}

public void testMatchesRequiresCompleteArSignatureIncludingTrailingNewline() {
    assertTrue(org.apache.commons.compress.archivers.ar.ArArchiveInputStream.matches(
        new byte[] { '!', '<', 'a', 'r', 'c', 'h', '>', '\n' }, 8));
    assertFalse(org.apache.commons.compress.archivers.ar.ArArchiveInputStream.matches(
        new byte[] { '!', '<', 'a', 'r', 'c', 'h', '>', '\r' }, 8));
}

private void compress2bWriteArMember(final java.io.ByteArrayOutputStream archive,
                                     final String name, final String data) throws java.io.IOException {
    archive.write(String.format("%-16s%-12s%-6s%-6s%-8s%-10s`\n",
                                name, "0", "0", "0", "100644", String.valueOf(data.length()))
                      .getBytes("US-ASCII"));
    archive.write(data.getBytes("US-ASCII"));
    if ((data.length() & 1) != 0) {
        archive.write('\n');
    }
}