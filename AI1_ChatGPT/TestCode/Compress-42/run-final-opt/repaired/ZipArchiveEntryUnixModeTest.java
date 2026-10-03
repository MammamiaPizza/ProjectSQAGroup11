package org.apache.commons.compress.archivers.zip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ZipArchiveEntryUnixModeTest {

    @Test
    public void recognizesSymlinkWhenModeContainsOnlySymlinkType() {
        ZipArchiveEntry entry = new ZipArchiveEntry("link");
        int mode = UnixStat.LINK_FLAG | UnixStat.DEFAULT_LINK_PERM;

        entry.setUnixMode(mode);

        assertTrue(entry.isUnixSymlink());
        assertEquals(mode, entry.getUnixMode());
        assertEquals(ZipArchiveEntry.PLATFORM_UNIX, entry.getPlatform());
    }

    @Test
    public void isUnixSymlinkIsFalseWhenSymlinkAndDirectoryTypeBitsAreCombined() {
        ZipArchiveEntry entry = new ZipArchiveEntry("ambiguous");
        int mode = UnixStat.LINK_FLAG | UnixStat.DIR_FLAG | UnixStat.DEFAULT_LINK_PERM;

        entry.setUnixMode(mode);

        assertFalse(entry.isUnixSymlink());
        assertEquals(mode, entry.getUnixMode());
    }

    @Test
    public void regularFileAndDirectoryModesAreNotReportedAsSymlinks() {
        ZipArchiveEntry file = new ZipArchiveEntry("file");
        file.setUnixMode(UnixStat.FILE_FLAG | UnixStat.DEFAULT_FILE_PERM);

        ZipArchiveEntry directory = new ZipArchiveEntry("directory/");
        directory.setUnixMode(UnixStat.DIR_FLAG | UnixStat.DEFAULT_DIR_PERM);

        assertFalse(file.isUnixSymlink());
        assertFalse(directory.isUnixSymlink());
    }

    @Test
    public void setUnixModeStoresModeInExternalAttributesAndSetsUnixPlatform() {
        ZipArchiveEntry entry = new ZipArchiveEntry("file");
        int mode = UnixStat.FILE_FLAG | UnixStat.DEFAULT_FILE_PERM;

        entry.setUnixMode(mode);

        assertEquals(mode, entry.getUnixMode());
        assertEquals((long) (mode << 16), entry.getExternalAttributes());
        assertEquals(ZipArchiveEntry.PLATFORM_UNIX, entry.getPlatform());
    }

    @Test
    public void directoryUnixModeSetsDosDirectoryAttribute() {
        ZipArchiveEntry entry = new ZipArchiveEntry("directory/");
        int mode = UnixStat.DIR_FLAG | UnixStat.DEFAULT_DIR_PERM;

        entry.setUnixMode(mode);

        assertEquals(mode, entry.getUnixMode());
        assertEquals((((long) mode) << 16) | 0x10L, entry.getExternalAttributes());
    }

    @Test
    public void nonWritableUnixModeSetsDosReadOnlyAttribute() {
        ZipArchiveEntry entry = new ZipArchiveEntry("readonly");
        int mode = UnixStat.FILE_FLAG | 0444;

        entry.setUnixMode(mode);

        assertEquals(mode, entry.getUnixMode());
        assertEquals((long) ((mode << 16) | 1), entry.getExternalAttributes());
    }
}
