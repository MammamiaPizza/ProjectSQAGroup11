package org.apache.commons.compress.archivers.zip;

import junit.framework.TestCase;

public class ZipArchiveEntryGeneratedTest extends TestCase {

    private ZipArchiveEntry createConfiguredEntry(String name) {
        ZipArchiveEntry entry = new ZipArchiveEntry(name);
        entry.setMethod(8);
        entry.setInternalAttributes(7);
        entry.setExternalAttributes(0x12340000L);
        entry.setPlatform(ZipArchiveEntry.PLATFORM_UNIX);
        entry.setExtra(new byte[] { 1, 0, 1, 0, 42 });
        return entry;
    }

    public void testEqualIndependentlyCreatedEntriesHaveEqualHashCodes() {
        ZipArchiveEntry first = createConfiguredEntry("entry.txt");
        ZipArchiveEntry second = createConfiguredEntry("entry.txt");

        assertEquals(first, second);
        assertEquals(second, first);
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals(first, first);
    }

    public void testEntriesWithDifferentNamesAreNotEqual() {
        ZipArchiveEntry first = new ZipArchiveEntry("first.txt");
        ZipArchiveEntry second = new ZipArchiveEntry("second.txt");

        assertFalse(first.equals(second));
        assertFalse(second.equals(first));
    }

    public void testEntryIsNotEqualToNullOrDifferentType() {
        ZipArchiveEntry entry = new ZipArchiveEntry("entry.txt");

        assertFalse(entry.equals(null));
        assertFalse(entry.equals("entry.txt"));
    }

    public void testEntriesWithDifferentCompressionMethodsAreEqual() {
        ZipArchiveEntry first = new ZipArchiveEntry("entry.txt");
        ZipArchiveEntry second = new ZipArchiveEntry("entry.txt");
        first.setMethod(0);
        second.setMethod(8);

        assertEquals(first, second);
    }

    public void testEntriesWithDifferentInternalAttributesAreEqual() {
        ZipArchiveEntry first = new ZipArchiveEntry("entry.txt");
        ZipArchiveEntry second = new ZipArchiveEntry("entry.txt");
        first.setInternalAttributes(1);
        second.setInternalAttributes(2);

        assertEquals(first, second);
    }

    public void testEntriesWithDifferentExternalAttributesAreEqual() {
        ZipArchiveEntry first = new ZipArchiveEntry("entry.txt");
        ZipArchiveEntry second = new ZipArchiveEntry("entry.txt");
        first.setExternalAttributes(1L);
        second.setExternalAttributes(2L);

        assertEquals(first, second);
    }

    public void testEntriesWithDifferentPlatformsAreEqual() {
        ZipArchiveEntry first = new ZipArchiveEntry("entry.txt");
        ZipArchiveEntry second = new ZipArchiveEntry("entry.txt");
        first.setPlatform(ZipArchiveEntry.PLATFORM_FAT);
        second.setPlatform(ZipArchiveEntry.PLATFORM_UNIX);

        assertEquals(first, second);
    }

    public void testEntriesWithDifferentUnixModesAreEqual() {
        ZipArchiveEntry first = new ZipArchiveEntry("entry.txt");
        ZipArchiveEntry second = new ZipArchiveEntry("entry.txt");
        first.setUnixMode(0644);
        second.setUnixMode(0755);

        assertEquals(0644, first.getUnixMode());
        assertEquals(0755, second.getUnixMode());
        assertEquals(first, second);
    }

    public void testEntriesWithDifferentExtraDataAreEqual() {
        ZipArchiveEntry first = new ZipArchiveEntry("entry.txt");
        ZipArchiveEntry second = new ZipArchiveEntry("entry.txt");
        first.setExtra(new byte[] { 1, 0, 1, 0, 1 });
        second.setExtra(new byte[] { 1, 0, 1, 0, 2 });

        assertEquals(first, second);
    }

    public void testNegativeCompressionMethodIsRejected() {
        ZipArchiveEntry entry = new ZipArchiveEntry("entry.txt");

        try {
            entry.setMethod(-1);
            fail("Negative compression methods must be rejected");
        } catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().indexOf("negative") >= 0);
        }
    }
}