package org.apache.commons.compress.archivers.sevenz;

 import static org.junit.Assert.*;

 import java.io.File;
 import java.io.IOException;
 import java.net.URISyntaxException;
 import java.net.URL;

 import org.junit.Test;

 /**
  * Test class for {@link SevenZFile} targeting the zero-size entry bug (COMPRESS-348).
  * The buggy version throws {@link IllegalStateException} when trying to read
  * from an entry whose size is zero, instead of correctly returning -1.
  */
 public class SevenZFileTest {

     private static final String TEST_RESOURCE =
             "org/apache/commons/compress/archivers/sevenz/multiple-files-empty-file.7z";

     private static File getTestFile() throws IOException {
         URL url = SevenZFileTest.class.getClassLoader().getResource(TEST_RESOURCE);
         if (url == null) {
             throw new IOException("Test resource not found: " + TEST_RESOURCE);
         }
         try {
             return new File(url.toURI());
         } catch (URISyntaxException e) {
             throw new IOException(e);
         }
     }

     @Test(expected = IllegalStateException.class)
     public void testReadWithoutNextEntry() throws IOException {
         try (SevenZFile file = new SevenZFile(getTestFile())) {
             file.read();
         }
     }

     @Test(expected = IllegalStateException.class)
     public void testReadByteArrayWithoutNextEntry() throws IOException {
         try (SevenZFile file = new SevenZFile(getTestFile())) {
             file.read(new byte[10]);
         }
     }

     @Test
     public void testZeroSizeEntryReadReturnsMinusOne() throws IOException {
         try (SevenZFile file = new SevenZFile(getTestFile())) {
             SevenZArchiveEntry entry;
             boolean foundZero = false;
             while ((entry = file.getNextEntry()) != null) {
                 if (entry.getSize() == 0) {
                     foundZero = true;
                     assertEquals(-1, file.read());
                     assertEquals(-1, file.read(new byte[10]));
                     assertEquals(-1, file.read(new byte[10], 0, 5));
                 }
             }
             assertTrue("Should have at least one zero-size entry", foundZero);
         }
     }

     @Test
     public void testNonZeroEntryReadContent() throws IOException {
         try (SevenZFile file = new SevenZFile(getTestFile())) {
             SevenZArchiveEntry entry;
             boolean foundNonZero = false;
             while ((entry = file.getNextEntry()) != null) {
                 if (entry.getSize() > 0) {
                     foundNonZero = true;
                     int first = file.read();
                     assertTrue("First byte should be non-negative", first >= 0);
                     byte[] rest = new byte[(int) entry.getSize() - 1];
                     int bytesRead = file.read(rest);
                     assertEquals(entry.getSize() - 1, bytesRead);
                     assertEquals(-1, file.read());
                 }
             }
             assertTrue("Should have at least one non-zero entry", foundNonZero);
         }
     }

     @Test
     public void testReadAfterEndOfEntryReturnsMinusOne() throws IOException {
         try (SevenZFile file = new SevenZFile(getTestFile())) {
             SevenZArchiveEntry entry;
             while ((entry = file.getNextEntry()) != null) {
                 if (entry.getSize() > 0) {
                     byte[] buf = new byte[(int) entry.getSize()];
                     int totalRead = 0;
                     int read;
                     while (totalRead < buf.length
                             && (read = file.read(buf, totalRead, buf.length - totalRead)) != -1) {
                         totalRead += read;
                     }
                     assertEquals(entry.getSize(), totalRead);
                     assertEquals(-1, file.read());
                     assertEquals(-1, file.read(new byte[10]));
                     break;
                 }
             }
         }
     }

     @Test
     public void testMultipleZeroSizeEntries() throws IOException {
         try (SevenZFile file = new SevenZFile(getTestFile())) {
             SevenZArchiveEntry entry;
             int zeroCount = 0;
             while ((entry = file.getNextEntry()) != null) {
                 if (entry.getSize() == 0) {
                     zeroCount++;
                     assertEquals(-1, file.read());
                 }
             }
             assertTrue("Expected at least two zero-size entries", zeroCount >= 2);
         }
     }

     @Test
     public void testGetNextEntryReturnsNullAtEnd() throws IOException {
         try (SevenZFile file = new SevenZFile(getTestFile())) {
             while (file.getNextEntry() != null) {
                 // exhaust entries
             }
             assertNull(file.getNextEntry());
         }
     }

     @Test
     public void testGetEntriesConsistency() throws IOException {
         try (SevenZFile file = new SevenZFile(getTestFile())) {
             int entryCount = 0;
             for (@SuppressWarnings("unused") SevenZArchiveEntry e : file.getEntries()) {
                 entryCount++;
             }
             int iterCount = 0;
             while (file.getNextEntry() != null) {
                 iterCount++;
             }
             assertEquals("getEntries() and getNextEntry() should return same count",
                     entryCount, iterCount);
         }
     }

     @Test
     public void testCloseIsIdempotent() throws IOException {
         SevenZFile file = new SevenZFile(getTestFile());
         file.close();
         file.close(); // second close must not throw
     }
 }
