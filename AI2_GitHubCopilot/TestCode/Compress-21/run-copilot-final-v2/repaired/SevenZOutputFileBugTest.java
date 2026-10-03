package org.apache.commons.compress.archivers.sevenz;

import static org.junit.Assert.;
import java.io.;
import java.util.*;

import org.junit.*;

public class SevenZOutputFileBugTest {
    private File tempFile;

 @Before
 public void setUp() throws Exception {
     tempFile = File.createTempFile("sevenzbug", ".7z");
     tempFile.deleteOnExit();
 }

 @After
 public void tearDown() {
     if (tempFile != null && tempFile.exists()) {
         tempFile.delete();
     }
 }

 private void roundtrip(int numEmptyEntries, boolean includeNonEmpty) throws Exception {
     SevenZOutputFile out = new SevenZOutputFile(tempFile);
     List<String> expectedNames = new ArrayList<String>();
     byte[] nonEmptyContent = "nonempty".getBytes("UTF-8");

  for (int i = 0; i < numEmptyEntries; i++) {
      SevenZArchiveEntry entry = new SevenZArchiveEntry();
      entry.setDirectory(true);
      entry.setName("dir" + i + "/");
      expectedNames.add(entry.getName());
      out.putArchiveEntry(entry);
      out.closeArchiveEntry();
  }
  if (includeNonEmpty) {
      SevenZArchiveEntry entry = new SevenZArchiveEntry();
      entry.setName("file.txt");
      expectedNames.add(entry.getName());
      out.putArchiveEntry(entry);
      out.write(nonEmptyContent);
      out.closeArchiveEntry();
  }
  out.close();

  SevenZFile file = new SevenZFile(tempFile);
  List<SevenZArchiveEntry> readEntries = new ArrayList<SevenZArchiveEntry>();
  SevenZArchiveEntry e;
  while ((e = file.getNextEntry()) != null) {
      readEntries.add(e);
      if (!e.isDirectory() && e.hasStream()) {
          byte[] buf = new byte[(int) e.getSize()];
          int len = file.read(buf);
          assertEquals(e.getSize(), len);
          assertArrayEquals(nonEmptyContent, buf);
      }
  }
  file.close();

  assertEquals(expectedNames.size(), readEntries.size());
  for (int i = 0; i < expectedNames.size(); i++) {
      assertEquals(expectedNames.get(i), readEntries.get(i).getName());
  }
  for (int i = 0; i < numEmptyEntries; i++) {
      assertTrue(readEntries.get(i).isDirectory());
  }

 }

 @Test
 public void testEmptyFilesSixSevenEightNine() throws Exception {
     for (int count : new int[]{6, 7, 8, 9}) {
         roundtrip(count, false);
     }
 }

 @Test
 public void testEmptyFilesWithOneNonEmpty() throws Exception {
     for (int empty : new int[]{5, 6, 7, 8}) {
         roundtrip(empty, true);
     }
 }

 @Test
 public void testSingleEmptyFile() throws Exception {
     roundtrip(1, false);
 }

 @Test
 public void testFiveEmptyFiles() throws Exception {
     roundtrip(5, false);
 }

 @Test
 public void testTenEmptyFiles() throws Exception {
     roundtrip(10, false);
 }

 @Test
 public void testNoEntries() throws Exception {
     SevenZOutputFile out = new SevenZOutputFile(tempFile);
     out.close();
     SevenZFile file = new SevenZFile(tempFile);
     assertNull(file.getNextEntry());
     file.close();
 }

 @Test
 public void testMultipleNonEmptyFiles() throws Exception {
     SevenZOutputFile out = new SevenZOutputFile(tempFile);
     String[] names = {"a.txt", "b.txt", "c.txt"};
     for (String name : names) {
         SevenZArchiveEntry entry = new SevenZArchiveEntry();
         entry.setName(name);
         out.putArchiveEntry(entry);
         out.write("data".getBytes("UTF-8"));
         out.closeArchiveEntry();
     }
     out.close();
     SevenZFile file = new SevenZFile(tempFile);
     int count = 0;
     while (file.getNextEntry() != null) {
         count++;
     }
     file.close();
     assertEquals(3, count);
 }

 @Test
 public void testZeroLengthFileEntry() throws Exception {
     SevenZOutputFile out = new SevenZOutputFile(tempFile);
     SevenZArchiveEntry entry = new SevenZArchiveEntry();
     entry.setName("empty.txt");
     out.putArchiveEntry(entry);
     out.closeArchiveEntry();
     out.close();
     SevenZFile file = new SevenZFile(tempFile);
     SevenZArchiveEntry readEntry = file.getNextEntry();
     assertNotNull(readEntry);
     assertEquals("empty.txt", readEntry.getName());
     assertFalse(readEntry.isDirectory());
     assertFalse(readEntry.hasStream());
     file.close();
 }

}
