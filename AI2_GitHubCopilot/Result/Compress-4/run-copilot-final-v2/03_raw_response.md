import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import junit.framework.TestCase;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.changes.ChangeSet;
import org.apache.commons.compress.changes.ChangeSetPerformer;
import org.apache.commons.compress.changes.ChangeSetResults;

/**

 - Tests verifying that finish() on CpioArchiveOutputStream, TarArchiveOutputStream,
 - and ZipArchiveOutputStream properly flushes the central directory / trailer so that
 - subsequent reads produce valid archives instead of "central directory is empty".
  */
 public class BugCompress64FinishTest extends TestCase {
  // ---- CpioArchiveOutputStream tests ----
  public void testCpioSingleEntryRoundtrip() throws Exception {
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos);
  CpioArchiveEntry entry = new CpioArchiveEntry("file1.txt");
  entry.setSize(5);
  out.putArchiveEntry(entry);
  out.write(new byte[]{1, 2, 3, 4, 5});
  out.closeArchiveEntry();
  out.finish();
  out.close();
  byte[] data = baos.toByteArray();
  assertTrue("Written archive is empty", data.length > 0);
  CpioArchiveInputStream in = new CpioArchiveInputStream(new ByteArrayInputStream(data));
  CpioArchiveEntry readEntry = (CpioArchiveEntry) in.getNextEntry();
  assertNotNull("No entry found in archive", readEntry);
  assertEquals("file1.txt", readEntry.getName());
  byte[] buf = new byte[5];
  assertEquals(5, in.read(buf));
  assertEquals(1, buf[0]);
  assertEquals(5, buf[4]);
  assertNull(in.getNextEntry());
  in.close();
  }
  public void testCpioEmptyArchive() throws Exception {
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos);
  out.finish();
  out.close();
  byte[] data = baos.toByteArray();
  CpioArchiveInputStream in = new CpioArchiveInputStream(new ByteArrayInputStream(data));
  assertNull("Empty archive should have no entries", in.getNextEntry());
  in.close();
  }
  public void testCpioMultiEntryRoundtrip() throws Exception {
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos);
  String[] names = {"a.txt", "b.txt", "c.txt"};
  byte[][] contents = {
      "hello".getBytes("US-ASCII"),
      "world".getBytes("US-ASCII"),
      "foobar".getBytes("US-ASCII")
  };
  for (int i = 0; i < names.length; i++) {
      CpioArchiveEntry entry = new CpioArchiveEntry(names[i]);
      entry.setSize(contents[i].length);
      out.putArchiveEntry(entry);
      out.write(contents[i]);
      out.closeArchiveEntry();
  }
  out.finish();
  out.close();
  byte[] data = baos.toByteArray();
  assertTrue(data.length > 0);
  CpioArchiveInputStream in = new CpioArchiveInputStream(new ByteArrayInputStream(data));
  for (int i = 0; i < names.length; i++) {
      CpioArchiveEntry readEntry = (CpioArchiveEntry) in.getNextEntry();
      assertNotNull(readEntry);
      assertEquals(names[i], readEntry.getName());
      byte[] buf = new byte[contents[i].length];
      assertEquals(contents[i].length, in.read(buf));
      for (int j = 0; j < contents[i].length; j++) {
          assertEquals(contents[i][j], buf[j]);
      }
  }
  assertNull(in.getNextEntry());
  in.close();
  }
  public void testCpioUnicodeName() throws Exception {
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos);
  String unicodeName = "\u00e4\u00f6\u00fc.txt"; // a-umlaut, o-umlaut, u-umlaut
  CpioArchiveEntry entry = new CpioArchiveEntry(unicodeName);
  entry.setSize(3);
  out.putArchiveEntry(entry);
  out.write(new byte[]{7, 8, 9});
  out.closeArchiveEntry();
  out.finish();
  out.close();
  byte[] data = baos.toByteArray();
  assertTrue(data.length > 0);
  CpioArchiveInputStream in = new CpioArchiveInputStream(new ByteArrayInputStream(data));
  CpioArchiveEntry readEntry = (CpioArchiveEntry) in.getNextEntry();
  assertNotNull(readEntry);
  assertEquals(unicodeName, readEntry.getName());
  in.close();
  }
  // ---- TarArchiveOutputStream tests ----
  public void testTarSingleEntryRoundtrip() throws Exception {
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  TarArchiveOutputStream out = new TarArchiveOutputStream(baos);
  TarArchiveEntry entry = new TarArchiveEntry("readme.txt");
  byte[] content = "This is a test file.\n".getBytes("US-ASCII");
  entry.setSize(content.length);
  out.putArchiveEntry(entry);
  out.write(content);
  out.closeArchiveEntry();
  out.finish();
  out.close();
  byte[] data = baos.toByteArray();
  assertTrue(data.length > 0);
  TarArchiveInputStream in = new TarArchiveInputStream(new ByteArrayInputStream(data));
  TarArchiveEntry readEntry = in.getNextEntry();
  assertNotNull(readEntry);
  assertEquals("readme.txt", readEntry.getName());
  byte[] buf = new byte[content.length];
  assertEquals(content.length, in.read(buf));
  assertEquals("This is a test file.\n", new String(buf, "US-ASCII"));
  assertNull(in.getNextEntry());
  in.close();
  }
  public void testTarEmptyArchive() throws Exception {
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  TarArchiveOutputStream out = new TarArchiveOutputStream(baos);
  out.finish();
  out.close();
  byte[] data = baos.toByteArray();
  TarArchiveInputStream in = new TarArchiveInputStream(new ByteArrayInputStream(data));
  assertNull("Empty tar archive should have no entries", in.getNextEntry());
  in.close();
  }
  public void testTarMultiEntryRoundtrip() throws Exception {
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  TarArchiveOutputStream out = new TarArchiveOutputStream(baos);
  TarArchiveEntry e1 = new TarArchiveEntry("one");
  byte[] c1 = "111".getBytes("US-ASCII");
  e1.setSize(c1.length);
  out.putArchiveEntry(e1);
  out.write(c1);
  out.closeArchiveEntry();
  TarArchiveEntry e2 = new TarArchiveEntry("two");
  byte[] c2 = "22222".getBytes("US-ASCII");
  e2.setSize(c2.length);
  out.putArchiveEntry(e2);
  out.write(c2);
  out.closeArchiveEntry();
  out.finish();
  out.close();
  byte[] data = baos.toByteArray();
  assertTrue(data.length > 0);
  TarArchiveInputStream in = new TarArchiveInputStream(new ByteArrayInputStream(data));
  TarArchiveEntry r1 = in.getNextEntry();
  assertNotNull(r1);
  assertEquals("one", r1.getName());
  byte[] b1 = new byte[3];
  assertEquals(3, in.read(b1));
  assertEquals("111", new String(b1, "US-ASCII"));
  TarArchiveEntry r2 = in.getNextEntry();
  assertNotNull(r2);
  assertEquals("two", r2.getName());
  byte[] b2 = new byte[5];
  assertEquals(5, in.read(b2));
  assertEquals("22222", new String(b2, "US-ASCII"));
  assertNull(in.getNextEntry());
  in.close();
  }
  public void testTarLongFileName() throws Exception {
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  TarArchiveOutputStream out = new TarArchiveOutputStream(baos);
  out.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
  String longName = "a/very/long/path/with/many/components/and/a/file/name/that/is/really/really/lon
g/and/exceeds/the/standard/tar/limit/somewhat.txt";
  TarArchiveEntry entry = new TarArchiveEntry(longName);
  byte[] content = "data".getBytes("US-ASCII");
  entry.setSize(content.length);
  out.putArchiveEntry(entry);
  out.write(content);
  out.closeArchiveEntry();
  out.finish();
  out.close();
  byte[] data = baos.toByteArray();
  assertTrue(data.length > 0);
  TarArchiveInputStream in = new TarArchiveInputStream(new ByteArrayInputStream(data));
  TarArchiveEntry readEntry = in.getNextEntry();
  assertNotNull(readEntry);
  assertEquals(longName, readEntry.getName());
  in.close();
  }
  // ---- ZipArchiveOutputStream tests ----
  public void testZipSingleEntryRoundtrip() throws Exception {
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  ZipArchiveOutputStream out = new ZipArchiveOutputStream(baos);
  ZipArchiveEntry entry = new ZipArchiveEntry("data.bin");
  byte[] content = new byte[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
  entry.setSize(content.length);
  out.putArchiveEntry(entry);
  out.write(content);
  out.closeArchiveEntry();
  out.finish();
  out.close();
  byte[] data = baos.toByteArray();
  assertTrue(data.length > 0);
  ZipArchiveInputStream in = new ZipArchiveInputStream(new ByteArrayInputStream(data));
  ZipArchiveEntry readEntry = in.getNextEntry();
  assertNotNull(readEntry);
  assertEquals("data.bin", readEntry.getName());
  byte[] buf = new byte[10];
  assertEquals(10, in.read(buf));
  for (int i = 0; i < 10; i++) {
      assertEquals(i, buf[i]);
  }
  assertNull(in.getNextEntry());
  in.close();
  }
  public void testZipEmptyArchive() throws Exception {
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  ZipArchiveOutputStream out = new ZipArchiveOutputStream(baos);
  out.finish();
  out.close();
  byte[] data = baos.toByteArray();
  assertTrue("Empty zip must have at least end-of-central-directory", data.length >= 22);
  ZipArchiveInputStream in = new ZipArchiveInputStream(new ByteArrayInputStream(data));
  assertNull("Empty zip archive should have no entries", in.getNextEntry());
  in.close();
  }
  public void testZipUnicodeNameRoundtrip() throws Exception {
  ByteArrayOutputStream baos = new ByteArrayOutputStream();
  ZipArchiveOutputStream out = new ZipArchiveOutputStream(baos);
  String unicodeName = "\u00df\u00e9\u00f1.txt"; // sharp-s, e-acute, n-tilde
  ZipArchiveEntry entry = new ZipArchiveEntry(unicodeName);
  byte[] content = "unicode content".getBytes("UTF-8");
  entry.setSize(content.length);
  out.putArchiveEntry(entry);
  out.write(content);
  out.closeArchiveEntry();
  out.finish();
  out.close();
  byte[] data = baos.toByteArray();
  assertTrue(data.length > 0);
  ZipArchiveInputStream in = new ZipArchiveInputStream(new ByteArrayInputStream(data));
  ZipArchiveEntry readEntry = in.getNextEntry();
  assertNotNull(readEntry);
  assertEquals(unicodeName, readEntry.getName());
  in.close();
  }
  // ---- ChangeSetPerformer tests ----
  public void testChangeSetPerformerFinish() throws Exception {
  // Build a source zip in memory
  ByteArrayOutputStream srcBaos = new ByteArrayOutputStream();
  ZipArchiveOutputStream srcOut = new ZipArchiveOutputStream(srcBaos);
  ZipArchiveEntry srcEntry = new ZipArchiveEntry("original.txt");
  byte[] srcContent = "original".getBytes("US-ASCII");
  srcEntry.setSize(srcContent.length);
  srcOut.putArchiveEntry(srcEntry);
  srcOut.write(srcContent);
  srcOut.closeArchiveEntry();
  srcOut.finish();
  srcOut.close();
  // Build a ChangeSet that deletes the entry and adds a new one
  ChangeSet changes = new ChangeSet();
  changes.delete("original.txt");
  ArchiveEntry newEntry = new ZipArchiveEntry("new.txt");
  byte[] newContent = "new content".getBytes("US-ASCII");
  newEntry.setSize(newContent.length);
  changes.add(newEntry, new ByteArrayInputStream(newContent));
  // Perform changes
  ZipArchiveInputStream srcIn = new ZipArchiveInputStream(new
ByteArrayInputStream(srcBaos.toByteArray()));
  ByteArrayOutputStream destBaos = new ByteArrayOutputStream();
  ZipArchiveOutputStream destOut = new ZipArchiveOutputStream(destBaos);
  ChangeSetPerformer performer = new ChangeSetPerformer(changes);
  ChangeSetResults results = performer.perform(srcIn, destOut);
  destOut.finish();
  destOut.close();
  srcIn.close();
  // Verify resulting archive is readable (no "central directory is empty")
  byte[] resultData = destBaos.toByteArray();
  assertTrue(resultData.length > 0);
  ZipArchiveInputStream checkIn = new ZipArchiveInputStream(new ByteArrayInputStream(resultData));
  ArchiveEntry checkEntry = checkIn.getNextEntry();
  assertNotNull(checkEntry);
  assertEquals("new.txt", checkEntry.getName());
  assertNull(checkIn.getNextEntry());
  checkIn.close();
  }

}