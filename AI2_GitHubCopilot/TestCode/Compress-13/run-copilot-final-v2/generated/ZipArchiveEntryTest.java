package org.apache.commons.compress.archivers.zip;

 import static org.junit.Assert.*;

 import org.junit.Test;

 /**
  * Tests for backslash-to-forward-slash normalization in
  * {@link ZipArchiveEntry#getName()}.
  *
  * <p>Bug COMPRESS-176: WinZip archives may store entry names with
  * backslashes; getName() must return them with forward slashes,
  * isDirectory() must detect slash-terminated names after
  * normalization, and getRawName() must preserve the original bytes.</p>
  */
 public class ZipArchiveEntryTest {

     /* Helper to expose protected setName(String,byte[]) for testing. */
     private static final class TestableEntry extends ZipArchiveEntry {
         private static final long serialVersionUID = 1L;

         TestableEntry(String name) {
             super(name);
         }

         @Override
         public void setName(String name, byte[] rawName) {
             super.setName(name, rawName);
         }
     }

     // --------------- core backslash normalization ---------------

     @Test
     public void testBackslashNormalizedToForwardSlash() {
         ZipArchiveEntry entry = new ZipArchiveEntry("ä\\ü.txt");
         assertEquals("ä/ü.txt", entry.getName());
     }

     @Test
     public void testTrailingBackslashMakesDirectory() {
         ZipArchiveEntry entry = new ZipArchiveEntry("dir\\");
         assertTrue("trailing backslash should imply directory after normalization",
                 entry.isDirectory());
         assertEquals("dir/", entry.getName());
     }

     @Test
     public void testMixedSlashesAllNormalized() {
         ZipArchiveEntry entry = new ZipArchiveEntry("a/b\\c");
         assertEquals("a/b/c", entry.getName());
     }

     @Test
     public void testSingleBackslashBecomesForwardSlash() {
         ZipArchiveEntry entry = new ZipArchiveEntry("\\");
         assertEquals("/", entry.getName());
     }

     @Test
     public void testMultipleBackslashesBecomeForwardSlashes() {
         ZipArchiveEntry entry = new ZipArchiveEntry("\\\\\\");
         assertEquals("///", entry.getName());
     }

     // --------------- names without backslashes ---------------

     @Test
     public void testOnlyForwardSlashesUnchanged() {
         ZipArchiveEntry entry = new ZipArchiveEntry("///");
         assertEquals("///", entry.getName());
     }

     @Test
     public void testNormalNameWithoutBackslashesUnchanged() {
         ZipArchiveEntry entry = new ZipArchiveEntry("normal/name.txt");
         assertEquals("normal/name.txt", entry.getName());
     }

     @Test
     public void testEmptyNameUnchanged() {
         ZipArchiveEntry entry = new ZipArchiveEntry("");
         assertEquals("", entry.getName());
     }

     // --------------- directory detection ---------------

     @Test
     public void testNonDirectoryWithBackslashInMiddle() {
         ZipArchiveEntry entry = new ZipArchiveEntry("dir\\sub\\file.txt");
         assertEquals("dir/sub/file.txt", entry.getName());
         assertFalse("backslash in the middle does not make it a directory",
                 entry.isDirectory());
     }

     // --------------- equality ---------------

     @Test
     public void testEqualsNormalizesNames() {
         ZipArchiveEntry e1 = new ZipArchiveEntry("a\\b");
         ZipArchiveEntry e2 = new ZipArchiveEntry("a/b");
         e1.setTime(0L);
         e2.setTime(0L);
         assertTrue("entries whose names normalize to the same value must be equal",
                 e1.equals(e2));
     }

     // --------------- raw name ---------------

     @Test
     public void testGetRawNamePreservesOriginalBytes() {
         TestableEntry entry = new TestableEntry("dummy");
         byte[] raw = "a\\b.txt".getBytes();
         entry.setName("a/b.txt", raw);
         assertEquals("a/b.txt", entry.getName());
         assertArrayEquals(raw, entry.getRawName());
     }

     // --------------- copy-constructor from java.util.zip.ZipEntry ---------------

     @Test
     public void testFromZipEntryNormalizesBackslashes() throws java.util.zip.ZipException {
         java.util.zip.ZipEntry ze = new java.util.zip.ZipEntry("ä\\ü.txt");
         ZipArchiveEntry entry = new ZipArchiveEntry(ze);
         assertEquals("ä/ü.txt", entry.getName());
     }
 }
