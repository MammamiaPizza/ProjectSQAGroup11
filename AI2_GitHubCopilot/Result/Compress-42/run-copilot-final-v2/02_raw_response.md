package org.apache.commons.compress.archivers.zip;

 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for ZipArchiveEntry.isUnixSymlink() related to bug COMPRESS-379.
  * The bug causes isUnixSymlink() to return true when multiple Unix file type
  * flags are set, rather than requiring exactly the symlink flag.
  */
 public class ZipArchiveEntryTest {

     /**
      * Helper that creates a ZipArchiveEntry and sets its Unix mode.
      */
     private static ZipArchiveEntry createEntry(int mode) {
         ZipArchiveEntry entry = new ZipArchiveEntry("test");
         entry.setUnixMode(mode);
         return entry;
     }

     @Test
     public void isUnixSymlinkIsFalseIfMoreThanOneFlagIsSet() {
         // symlink + regular file type bit: must be false
         ZipArchiveEntry entry = createEntry(UnixStat.LINK_FLAG | UnixStat.FILE_FLAG);
         assertFalse(entry.isUnixSymlink());
     }

     @Test
     public void isUnixSymlinkWithOnlySymlinkFlag() {
         // exactly symlink flag set -> true
         ZipArchiveEntry entry = createEntry(UnixStat.LINK_FLAG);
         assertTrue(entry.isUnixSymlink());
     }

     @Test
     public void isUnixSymlinkWithSymlinkAndDirectory() {
         ZipArchiveEntry entry = createEntry(UnixStat.LINK_FLAG | UnixStat.DIR_FLAG);
         assertFalse(entry.isUnixSymlink());
     }

     @Test
     public void isUnixSymlinkWithNoSymlinkFlag() {
         // a plain file, no symlink bit
         ZipArchiveEntry entry = createEntry(UnixStat.FILE_FLAG);
         assertFalse(entry.isUnixSymlink());
     }

     @Test
     public void isUnixSymlinkWithZeroMode() {
         ZipArchiveEntry entry = createEntry(0);
         assertFalse(entry.isUnixSymlink());
     }

     @Test
     public void isUnixSymlinkWithOnlyPermissions() {
         // permission bits only, no type bits -> false
         ZipArchiveEntry entry = createEntry(0755);
         assertFalse(entry.isUnixSymlink());
     }

     @Test
     public void isUnixSymlinkWithAllTypeBitsExceptSymlink() {
         ZipArchiveEntry entry = createEntry(UnixStat.FILE_FLAG | UnixStat.DIR_FLAG);
         assertFalse(entry.isUnixSymlink());
     }

     @Test
     public void isUnixSymlinkWithSymlinkAndPermissions() {
         // symlink flag plus typical permissions is a valid symlink -> true
         ZipArchiveEntry entry = createEntry(UnixStat.LINK_FLAG | 0777);
         assertTrue(entry.isUnixSymlink());
     }

     @Test
     public void isUnixSymlinkWithSymlinkAndUnknownTypeBit() {
         // symlink flag + another high bit (not a recognized type flag) -> false
         int unknownFlag = 0010000; // typical FIFO flag
         ZipArchiveEntry entry = createEntry(UnixStat.LINK_FLAG | unknownFlag);
         assertFalse(entry.isUnixSymlink());
     }

     @Test
     public void isUnixSymlinkWithRegularFileAndDirectory() {
         ZipArchiveEntry entry = createEntry(UnixStat.FILE_FLAG | UnixStat.DIR_FLAG);
         assertFalse(entry.isUnixSymlink());
     }

     @Test
     public void isUnixSymlinkWithSymlinkAndStickyBit() {
         // symlink flag + permission bits including sticky bit -> still only one type flag -> true
         ZipArchiveEntry entry = createEntry(UnixStat.LINK_FLAG | 01777);
         assertTrue(entry.isUnixSymlink());
     }
 }