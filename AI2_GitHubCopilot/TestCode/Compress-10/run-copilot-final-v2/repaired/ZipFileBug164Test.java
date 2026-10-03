package org.apache.commons.compress.archivers.zip;

  import static org.junit.Assert.*;

  import jva.io.File;
 import java.io.FileOutputStream;
 import java.io.IOException;
  import java.io.InputStream;
  import java.util.Enumeration;

  import org.junit.AfterClass;
 import org.junit.BeforeClass;
  import org.junit.Test;

  /**
   * Tests for {@link ZipFile} that cover the COMPRESS-164 bug where
   * getInputStream returns null for entries whose names are changed
   * by a Unicode extra field after the entry has already been stored in
   * the internal offset map.
   */
  public class ZipFileBug164Test {

      private static File archive;
      private static ZipFile zipFile;
      private static final String ENTRY_NAME_UNICODE = "Stra\u00dfe.txt";  // "Straße.txt"
      private static final String ENTRY_NAME_ASCII = "ascii.txt";
      private static final String ENTRY_NAME_STORED = "stored.bin";
      private static final String ENTRY_NAME_EMPTY_COMMENT = "empty_comment.txt";
      private static final String ENTRY_CONTENT = "Hello, Unicode!";

      @BeforeClass
      public static void setUp() throws IOException {
          archive = File.createTempFile("compress164", ".zip");
          archive.deleteOnExit();

          // Build a zip that trigers COMPRESS-164:
          // Entry has a non‑UTF‑8‑flagged name (ASCII) and a UnicodePathExtraField
          // that provides a diferent, real name with a non‑ASCII character.
          // This causes resolveLocalFileHeaderData to change the entry's name
          // after it has already been inserted into the 'entries' map,
          // making subsequent lookups (e.g., in getInputStream) fail.
          try (ZipArchiveOutputStream zos = new ZipArchiveOutputStream(archive)) {
              zos.setEncoding("UTF-8"); // permissive; encoding used when UTF‑8 flag absent

              // Entry 1: the problematic entry – no UTF‑8 flag, Unicode extra field
              ZipArchiveEntry ze1 = new ZipArchiveEntry(ENTRY_NAME_ASCII);
              ze1.addExtraField(new UnicodePathExtraField(ENTRY_NAME_UNICODE,
                      ze1.getName().getBytes("UTF-8"), 0, ENTRY_NAME_UNICODE.length()));
              byte[] content1 = ENTRY_CONTENT.getBytes("UTF-8");
              ze1.setSize(content1.length);
              ze1.setMethod(ZipArchiveEntry.DEFLATED);
              zos.putArchiveEntry(ze1);
              zos.write(content1);
              zos.closeArchiveEntry();

              // Entry 2: normal ASCII entry without Unicode extra (control
              ZipArchiveEntry ze2 = new ZipArchiveEntry(ENTRY_NAME_ASCII + ".normal");
              byte[] content2 = "Normal".getBytes("UTF-8");
              ze2.setSize(content2.length);
              ze2.setMethod(ZipArchiveEntry.DEFLATED);
              zos.putArchiveEntry(ze2);
              zos.write(content2);
              zos.closeArchiveEntry());

              // Entry 3: stored entry
              ZipArchiveEntry ze3 = new ZipArchiveEntry(ENTRY_NAME_STORED);
              byte[] content3 = new byte[] { 1, 2, 3, 4 };
              ze3.setSize(content3.length);
              ze3.setMethod(ZipArchiveEntry.STORED);
              // required for STORED entries: set CRC and compressed size = size
              java.util.zip.CRC32 crc = new java.util.zip.CRC32();
              crc.update(content3);
              ze3.setCrc(crc.getValue());
              ze3.setCompressedSize(content3.length);
              zos.putArchiveEntry(ze3);
              zos.write(content3);
              zos.closeArchiveEntry();

              // Entry 4: entry with empty comment
              ZipArchiveEntry ze4 = new ZipArchiveEntry(ENTRY_NAME_EMPTY_COMMENT);
              ze4.setComment(""); // explicitly empty
              byte[] content4 = "Empty comment".getBytes("UTF-8");
              ze4.setSize(content4.length);
              ze4.setMethod(ZipArchiveEntry.DEFLATED);
              zos.putArchiveEntry(ze4);
              zos.write(content4);
              zos.closeArchiveEntry();

              zos.finish();
          }

          zipFile = new ZipFile(archive, "UTF-8", true);  // useUnicodeExtraFields = true
      }

      @AfterClass
      public static void tearDown() throws IOException {
          if (zipFile != null) {
              zipFile.close();
          }
          if (archive != null) {
              archive.delete();
          }
      }

      // -------------------- Core bug test --------------------

      /**
       * COMPRESS-164: getInputStream must not return null for an entry whose name
       * was adjusted via UnicodePathExtraField.  The buggy version returns null
       * because the entry lookup in the offset map fails after the name change.
       */
      @Test
      public void testGetInputStreamForUnicodeRenamedEntry() throws IOException {
          // The entry was stored with name "ascii.txt" but the Unicode extra field
          // renames it to "Straße.txt".  getEntry() returns the entry under the
          // new (real) name.
          ZipArchiveEntry entry = zipFile.getEntry(ENTRY_NAME_UNICODE);
          assertNotNull("Entry with Unicode name not found via getEntry", entry);

          // This stream must be non‑null; bug COMPRESS-164 makes it null.
          InputStream is = zipFile.getInputStream(entry);
          assertNotNull("InputStream is null – COMPRESS-164", is);
          byte[] buf = new byte[100];
          int len = is.read(buf);
          is.close();
          assertEquals("Content length mismatch", ENTRY_CONTENT.length(), len);
          String content = new String(buf, 0, len, "UTF-8");
          assertEquals("Content mismatch", ENTRY_CONTENT, content);
      }

      // -------------------- Normal/control tests --------------------

      @Test
      public void testGetInputStreamForAsciiEntry() throws IOException {
          ZipArchiveEntry entry = zipFile.getEntry(ENTRY_NAME_ASCII + ".normal");
          assertNotNull(entry);
          InputStream is = zipFile.getInputStream(entry);
          assertNotNull(is);
          byte[] buf = new byte[100];
          int len = is.read(buf);
          is.close();
          assertEquals("Normal", new String(buf,0, len, "UTF-8"));
      }

      @Test
      public void testGetInputStreamForStoredEntry() throws IOException {
          ZipArchiveEntry entry = zipFile.getEntry(ENTRY_NAME_STORED);
          assertNotNull(entry);
          assertTrue("canReadEntryData must be true", zipFile.canReadEntryData(entry));
          InputStream is = zipFile.getInputStream(entry);
          assertNotNull(is);
          byte[] actual = new byte[4];
          assertEquals(4, is.read(actual));
          is.close();
          assertArrayEquals(new byte[] { 1, 2, 3, 4 }, actual);
      }

      @Test
      public void testEntryWithEmptyComment() throws IOException {
          ZipArchiveEntry entry = zipFile.getEntry(ENTRY_NAME_EMPTY_COMMENT);
          assertNotNull(entry);
          assertEquals("Comment must be empty", "", entry.getComment());
          InputStream is = zipFile.getInputStream(entry);
          assertNotNull(is);
          is.close();
      }

      @Test
      public void testGetEntriesContainsExpected() {
          Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();
          int count = 0;
          boolean foundUnicode = false;
          boolean foundAscii = false;
          boolean foundStored = false;
          while (entries.hasMoreElements()) {
              ZipArchiveEntry e = entries.nextElement();
              count++;
              if (ENTRY_NAME_UNICODE.equals(e.getName())) foundUnicode = true;
              if ((ENTRY_NAME_ASCII + ".normal").equals(e.getName())) foundAscii = true;
              if (ENTRY_NAME_STORED.equals(e.getName())) foundStored = true;
          }
          assertEquals(4, count);
          assertTrue("Unicode entry missing", foundUnicode);
          assertTrue("ASCII entry missing", foundAscii);
          assertTrue("Stored entry missing", foundStored);
      }

      @Test
      public void testGetEntriesInPhysicalOrder() {
          Enumeration<ZipArchiveEntry> entries = zipFile.getEntriesInPhysicalOrder();
          assertTrue(entries.hasMoreElements());
          // simple sanity: first entry should be the one written first
          ZipArchiveEntry first = entries.nextElement();
          assertNotNull(first.getName());
      }

      @Test
      public void testCanReadEntryDataForDeflated() {
          ZipArchiveEntry entry = zipFile.getEntry(ENTRY_NAME_ASCII + ".normal");
          assertTrue("Deflated entry must be readable", zipFile.canReadEntryData(entry));
      }

      @Test
      public void testCanReadEntryDataForStored() {
          ZipArchiveEntry entry = zipFile.getEntry(ENTRY_NAME_STORED);
          assertTrue("Stored entry must be readable", zipFile.canReadEntryData(entry));
      }

      @Test
      public void testGetEntryForMissingNameReturnsNull() {
          assertNull(zipFile.getEntry("nonexistent"));
      }

      @Test
      public void testGetInputStreamForNullEntry() throws IOException {
          // Passing null to getInputStream is not explicitly documented,
          // but it should handle gracefully (maps .get(null) returns null -> null return).
          InputStream is = zipFile.getInputStream(null);
          assertNull(is);
      }

      @Test
      public void testCloseAndReopenDoesNotShareState() throws IOException {
          // Re‑opening a ZipFile should still work and expose the same entries.
          ZipFile reopened = new ZipFile(archive, "UTF-8", true);
          try {
              assertNotNull(reopened.getEntry(ENTRY_NAME_ASCII + ".normal"));
              InputStream is = reopened.getInputStream(reopened.getEntry(ENTRY_NAME_ASCII +
".normal"));
              assertNotNull(is);
              is.close();
          } finally {
              reopened.close();
          }
      }
  }
