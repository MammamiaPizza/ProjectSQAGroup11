@Test
public void testSymbolsPast512EntriesArePublishedAndFindableFromNewChild() {
    ByteQuadsCanonicalizer root = ByteQuadsCanonicalizer.createRoot();
    ByteQuadsCanonicalizer writer = root.makeChild(0);

    for (int i = 1; i <= 600; ++i) {
        writer.addName("field-" + i, i);
    }
    writer.release();

    ByteQuadsCanonicalizer reader = root.makeChild(0);
    for (int i = 1; i <= 600; ++i) {
        assertEquals("field-" + i, reader.findName(i));
    }
}

@Test
public void testUnreleasedChildSymbolsAreNotVisibleToSiblingButVisibleAfterRelease() {
    ByteQuadsCanonicalizer root = ByteQuadsCanonicalizer.createRoot();
    ByteQuadsCanonicalizer writer = root.makeChild(0);
    ByteQuadsCanonicalizer sibling = root.makeChild(0);

    writer.addName("published", 0x12345678);

    assertNull(sibling.findName(0x12345678));

    writer.release();

    ByteQuadsCanonicalizer reader = root.makeChild(0);
    assertEquals("published", reader.findName(0x12345678));
}