package org.apache.commons.compress.archivers.tar;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.compress.archivers.ArchiveEntry;

/**

 - Tests for TarArchiveInputStream focusing on octal header field parsing,
 - edge cases, and the COMPRESS-178 bug.
  */
 public class TarArchiveInputStreamTest {
  // Tar constants for header construction
  private static final int BLOCK_SIZE = 512;
  private static final int RECORD_SIZE = BLOCK_SIZE;
  private static final String MAGIC_GNU = "ustar  ";
  private static final String VERSION_GNU = " \0";
  private static final String MAGIC_POSIX = "ustar\0";
  private static final String VERSION_POSIX = "00";
  private static final String MAGIC_ANT = "ustar\0";
  private static final String VERSION_ANT = "\0\0";
  /**
  - Builds a 512-byte tar header block with specified fields.
  - Numeric fields are stored as octal strings with trailing NUL.
  - Checksum is computed correctly.
    */
   private byte[] buildHeader(String name, long mode, long uid, long gid,
   long size, long mtime, byte typeflag, String linkname,
   String magic, String version, String uname, String gname,
   long devmajor, long devminor, String prefix) {
   byte[] header = new byte[BLOCK_SIZE];
   Arrays.fill(header, (byte) 0);
   putString(header, 0, 100, name);
   putOctal(header, 100, 8, mode);
   putOctal(header, 108, 8, uid);
   putOctal(header, 116, 8, gid);
   putOctal(header, 124, 12, size);
   putOctal(header, 136, 12, mtime);
   Arrays.fill(header, 148, 156, (byte) ' ');
   header[156] = typeflag;
   putString(header, 157, 100, linkname);
   putString(header, 257, 6, magic);
   putString(header, 263, 2, version);
   putString(header, 265, 32, uname);
   putString(header, 297, 32, gname);
   putOctal(header, 329, 8, devmajor);
   putOctal(header, 337, 8, devminor);
   putString(header, 345, 155, prefix);
   long sum = 0;
   for (int i = 0; i < BLOCK_SIZE; i++) {
   sum += (header[i] & 0xFF);
   }
   putOctal(header, 148, 7, sum);
   header[155] = (byte) ' ';
   return header;
  }
  private void putString(byte[] buf, int offset, int length, String value) {
      byte[] bytes = value != null ? value.getBytes() : new byte[0];
      int copyLen = Math.min(bytes.length, length);
      System.arraycopy(bytes, 0, buf, offset, copyLen);
  }
  private void putOctal(byte[] buf, int offset, int length, long value) {
      String octal = Long.toOctalString(value);
      int len = octal.length();
      int i;
      for (i = 0; i < length - len - 1; i++) {
          buf[offset + i] = (byte) '0';
      }
      for (int j = 0; j < len && i + j < length; j++) {
          buf[offset + i + j] = (byte) octal.charAt(j);
      }
      if (i + len < length) {
          buf[offset + i + len] = (byte) 0;
      }
  }
  /**
  - Creates a byte array representing a tar file with one entry,
  - including data blocks and end-of-archive marker (two zero blocks).
    */
   private byte[] createSingleEntryTar(byte[] header, byte[] content) {
   ByteArrayOutputStream bos = new ByteArrayOutputStream();
   try {
   bos.write(header);
   if (content != null) {
       bos.write(content);
       int padding = (BLOCK_SIZE - (content.length % BLOCK_SIZE)) % BLOCK_SIZE;
       if (padding > 0) {
           bos.write(new byte[padding]);
       }
   }
   bos.write(new byte[BLOCK_SIZE
   - 2]);
   } catch (IOException e) {
   throw new RuntimeException(e);
   }
   return bos.toByteArray();
   }
  /**
  - Merges multiple entry tar representations into a single tar stream.
  - The end-of-archive marker (two zero blocks) is appended at the end.
    */
   private byte[] mergeTarEntries(byte[]... entries) {
   ByteArrayOutputStream bos = new ByteArrayOutputStream();
   try {
   for (byte[] entry : entries) {
       bos.write(entry);
   }
   bos.write(new byte[BLOCK_SIZE
   - 2]);
   } catch (IOException e) {
   throw new RuntimeException(e);
   }
   return bos.toByteArray();
   }
  // --- Tests ---
  /**
  - COMPRESS-178: A size field containing raw bytes 00<NUL>0765<NUL> should
  - not cause IllegalArgumentException. parseOctal must handle embedded NUL
  - bytes gracefully, treating the first NUL as a terminator.
    */
   @Test
   public void testCOMPRESS178Bug_NULInOctalSizeField() throws IOException {
   byte[] header = buildHeader("test.txt", 0644, 0, 0, 0, 0, (byte) '0', "",
       MAGIC_GNU, VERSION_GNU, "root", "root", 0, 0, "");
   byte[] problematicSize = new byte[]{0x30, 0x30, 0x00, 0x30, 0x37, 0x36, 0x35, 0x00, 0, 0, 0, 0};
   System.arraycopy(problematicSize, 0, header, 124, 12);
   recomputeChecksum(header);
   byte[] tarBytes = createSingleEntryTar(header, null);
   TarArchiveInputStream tis = new TarArchiveInputStream(new ByteArrayInputStream(tarBytes),
BLOCK_SIZE, RECORD_SIZE);
   TarArchiveEntry entry = tis.getNextTarEntry();
   assertNotNull("Entry should be returned without exception", entry);
   assertEquals("Parsed size should stop at first NUL, yielding 0", 0, entry.getSize());
   tis.close();
  }
  private void recomputeChecksum(byte[] header) {
      Arrays.fill(header, 148, 156, (byte) ' ');
      long sum = 0;
      for (int i = 0; i < BLOCK_SIZE; i++) {
          sum += (header[i] & 0xFF);
      }
      String octal = Long.toOctalString(sum);
      int padding = 6 - octal.length();
      int pos = 148;
      for (int i = 0; i < padding; i++) {
          header[pos++] = (byte) '0';
      }
      for (int i = 0; i < octal.length(); i++) {
          header[pos++] = (byte) octal.charAt(i);
      }
      header[pos++] = 0;
      header[pos] = (byte) ' ';
  }
  @Test
  public void testParseNormalOctalFields() throws IOException {
      byte[] header = buildHeader("file.txt", 0100644, 1000, 1000, 1024L, 1000000L, (byte) '0', "",
              MAGIC_GNU, VERSION_GNU, "user", "group", 0, 0, "");
      byte[] content = new byte[1024];
      Arrays.fill(content, (byte) 'A');
      byte[] tarBytes = createSingleEntryTar(header, content);
      TarArchiveInputStream tis = new TarArchiveInputStream(new ByteArrayInputStream(tarBytes),
BLOCK_SIZE, RECORD_SIZE);
      TarArchiveEntry entry = tis.getNextTarEntry();
      assertNotNull(entry);
      assertEquals("file.txt", entry.getName());
      assertEquals(0100644, entry.getMode());
      assertEquals(1000, entry.getUserId());
      assertEquals(1000, entry.getGroupId());
      assertEquals(1024L, entry.getSize());
      assertTrue(entry.getModTime().getTime() >= 0);
      tis.close();
  }
  @Test
  public void testAllZeroSizeField() throws IOException {
      byte[] header = buildHeader("empty.txt", 0100644, 0, 0, 0, 0, (byte) '0', "",
              MAGIC_GNU, VERSION_GNU, "root", "root", 0, 0, "");
      byte[] tarBytes = createSingleEntryTar(header, null);
      TarArchiveInputStream tis = new TarArchiveInputStream(new ByteArrayInputStream(tarBytes),
BLOCK_SIZE, RECORD_SIZE);
      TarArchiveEntry entry = tis.getNextTarEntry();
      assertNotNull(entry);
      assertEquals(0, entry.getSize());
      tis.close();
  }
  @Test
  public void testSizeFieldWithLeadingSpaces() throws IOException {
      byte[] header = buildHeader("file.txt", 0100644, 0, 0, 0, 0, (byte) '0', "",
              MAGIC_GNU, VERSION_GNU, "root", "root", 0, 0, "");
      Arrays.fill(header, 124, 136, (byte) 0);
      System.arraycopy("    765\0".getBytes(), 0, header, 124, 8);
      recomputeChecksum(header);
   byte[] content = new byte[0765];
   Arrays.fill(content, (byte) 'B');
   byte[] tarBytes = createSingleEntryTar(header, content);
   TarArchiveInputStream tis = new TarArchiveInputStream(new ByteArrayInputStream(tarBytes),
BLOCK_SIZE, RECORD_SIZE);
   TarArchiveEntry entry = tis.getNextTarEntry();
   assertNotNull(entry);
   assertEquals(0765, entry.getSize());
   tis.close();
  }
  @Test
  public void testSizeFieldTrailingSpaces() throws IOException {
      byte[] header = buildHeader("test", 0100644, 0, 0, 0L, 0L, (byte)'0', "",
              MAGIC_GNU, VERSION_GNU, "root", "root", 0, 0, "");
      byte[] sizeBytes = new byte[]{0x31, 0x30, 0x30, 0x00, 0x20, 0x20, 0x20, 0x00, 0, 0, 0, 0};
      System.arraycopy(sizeBytes, 0, header, 124, 12);
      recomputeChecksum(header);
   byte[] content = new byte[0100];
   Arrays.fill(content, (byte) 'C');
   byte[] tarBytes = createSingleEntryTar(header, content);
   TarArchiveInputStream tis = new TarArchiveInputStream(new ByteArrayInputStream(tarBytes),
BLOCK_SIZE, RECORD_SIZE);
   TarArchiveEntry entry = tis.getNextTarEntry();
   assertNotNull(entry);
   assertEquals(0100, entry.getSize());
   tis.close();
  }
  @Test
  public void testReadAndSkipEntryDataBoundaries() throws IOException {
      byte[] header = buildHeader("data.bin", 0100644, 0, 0, 256L, 0, (byte) '0', "",
              MAGIC_GNU, VERSION_GNU, "user", "group", 0, 0, "");
      byte[] content = new byte[256];
      for (int i = 0; i < content.length; i++) {
          content[i] = (byte) (i & 0xFF);
      }
      byte[] tarBytes = createSingleEntryTar(header, content);
      TarArchiveInputStream tis = new TarArchiveInputStream(new ByteArrayInputStream(tarBytes),
BLOCK_SIZE, RECORD_SIZE);
      TarArchiveEntry entry = tis.getNextTarEntry();
      assertNotNull(entry);
      assertEquals(256, entry.getSize());
   byte[] buf = new byte[100];
   int read = tis.read(buf, 0, 100);
   assertEquals(100, read);
   int avail = tis.available();
   assertEquals(256 - 100, avail);

   byte[] rest = new byte[200];
   int read2 = tis.read(rest, 0, 200);
   assertEquals(156, read2);
   int read3 = tis.read(new byte[1], 0, 1);
   assertEquals(-1, read3);
   tis.close();
  }
  @Test
  public void testSkipBeyondEntrySize() throws IOException {
      byte[] header = buildHeader("skip_test", 0100644, 0, 0, 100L, 0L, (byte)'0', "",
              MAGIC_GNU, VERSION_GNU, "root", "root", 0, 0, "");
      byte[] content = new byte[100];
      Arrays.fill(content, (byte) 'D');
      byte[] tarBytes = createSingleEntryTar(header, content);
      TarArchiveInputStream tis = new TarArchiveInputStream(new ByteArrayInputStream(tarBytes),
BLOCK_SIZE, RECORD_SIZE);
      TarArchiveEntry entry = tis.getNextTarEntry();
      assertNotNull(entry);
      long skipped = tis.skip(150);
      assertTrue(skipped <=100);
      assertEquals(-1, tis.read(new byte[1], 0, 1));
      tis.close();
  }
  @Test
  public void testMultipleEntries() throws IOException {
      byte[] header1 = buildHeader("file1.txt", 0100644, 0, 0, 50L, 0L, (byte)'0', "",
              MAGIC_GNU, VERSION_GNU, "root", "root", 0, 0, "");
      byte[] content1 = new byte[50];
      Arrays.fill(content1, (byte) '1');
      byte[] entry1 = createSingleEntryTar(header1, content1);
   byte[] header2 = buildHeader("file2.txt", 0100644, 0, 0, 70L, 0L, (byte)'0', "",
           MAGIC_GNU, VERSION_GNU, "root", "root", 0, 0, "");
   byte[] content2 = new byte[70];
   Arrays.fill(content2, (byte) '2');
   byte[] entry2 = createSingleEntryTar(header2, content2);

   byte[] fullTar = mergeTarEntries(entry1, entry2);
   TarArchiveInputStream tis = new TarArchiveInputStream(new ByteArrayInputStream(fullTar),
BLOCK_SIZE, RECORD_SIZE);

   TarArchiveEntry e1 = tis.getNextTarEntry();
   assertNotNull(e1);
   assertEquals("file1.txt", e1.getName());
   assertEquals(50, e1.getSize());
   byte[] data1 = new byte[50];
   int r1 = tis.read(data1, 0, 50);
   assertEquals(50, r1);

   TarArchiveEntry e2 = tis.getNextTarEntry();
   assertNotNull(e2);
   assertEquals("file2.txt", e2.getName());
   assertEquals(70, e2.getSize());
   byte[] data2 = new byte[70];
   int r2 = tis.read(data2, 0, 70);
   assertEquals(70, r2);

   TarArchiveEntry e3 = tis.getNextTarEntry();
   assertNull(e3);
   tis.close();
  }
  @Test
  public void testGetNextEntryDelegates() throws IOException {
      byte[] header = buildHeader("delegate.txt", 0100644, 0, 0, 0L, 0L, (byte)'0', "",
              MAGIC_GNU, VERSION_GNU, "root", "root", 0, 0, "");
      byte[] tarBytes = createSingleEntryTar(header, null);
      TarArchiveInputStream tis = new TarArchiveInputStream(new ByteArrayInputStream(tarBytes),
BLOCK_SIZE, RECORD_SIZE);
      ArchiveEntry entry = tis.getNextEntry();
      assertNotNull(entry);
      assertTrue(entry instanceof TarArchiveEntry);
      assertEquals("delegate.txt", entry.getName());
      tis.close();
  }
  @Test
  public void testMatchesMethod() {
      byte[] posixHeader = buildHeader("test", 0666, 0, 0, 0L, 0L, (byte)'0', "",
              MAGIC_POSIX, VERSION_POSIX, "root", "root", 0, 0, "");
      assertTrue(TarArchiveInputStream.matches(posixHeader, posixHeader.length));
   byte[] gnuHeader = buildHeader("test", 0666, 0, 0, 0L, 0L, (byte)'0', "",
           MAGIC_GNU, VERSION_GNU, "root", "root", 0, 0, "");
   assertTrue(TarArchiveInputStream.matches(gnuHeader, gnuHeader.length));

   byte[] antHeader = buildHeader("test", 0666, 0, 0, 0L, 0L, (byte)'0', "",
           MAGIC_ANT, VERSION_ANT, "root", "root", 0, 0, "");
   assertTrue(TarArchiveInputStream.matches(antHeader, antHeader.length));

   byte[] nonTar = new byte[512];
   Arrays.fill(nonTar, (byte)'x');
   assertFalse(TarArchiveInputStream.matches(nonTar, nonTar.length));

   byte[] shortBuf = new byte[10];
   assertFalse(TarArchiveInputStream.matches(shortBuf, shortBuf.length));
  }
  @Test
  public void testCloseNoException() throws IOException {
      byte[] header = buildHeader("close.txt", 0644, 0, 0, 0L, 0L, (byte)'0', "",
              MAGIC_GNU, VERSION_GNU, "root", "root", 0, 0, "");
      byte[] tarBytes = createSingleEntryTar(header, null);
      TarArchiveInputStream tis = new TarArchiveInputStream(new ByteArrayInputStream(tarBytes),
BLOCK_SIZE, RECORD_SIZE);
      tis.close();
      tis.close();
  }
  @Test
  public void testEmptyStreamReturnsNull() throws IOException {
      byte[] emptyTar = new byte[BLOCK_SIZE
  * 2];
      TarArchiveInputStream tis = new TarArchiveInputStream(new ByteArrayInputStream(emptyTar),
BLOCK_SIZE, RECORD_SIZE);
      TarArchiveEntry entry = tis.getNextTarEntry();
      assertNull(entry);
      tis.close();
  }
  @Test
  public void testConstructorWithBlockSize() throws IOException {
      byte[] header = buildHeader("test.txt", 0755, 0, 0, 0L, 0L, (byte)'0', "",
              MAGIC_GNU, VERSION_GNU, "root", "root", 0, 0, "");
      byte[] tarBytes = createSingleEntryTar(header, null);
      TarArchiveInputStream tis = new TarArchiveInputStream(new ByteArrayInputStream(tarBytes),
10240);
      TarArchiveEntry entry = tis.getNextTarEntry();
      assertNotNull(entry);
      assertEquals("test.txt", entry.getName());
      tis.close();
  }
  @Test
  public void testDefaultConstructor() throws IOException {
      byte[] header = buildHeader("default.txt", 0644, 0, 0, 0L, 0L, (byte)'0', "",
              MAGIC_GNU, VERSION_GNU, "root", "root", 0, 0, "");
      byte[] tarBytes = createSingleEntryTar(header, null);
      TarArchiveInputStream tis = new TarArchiveInputStream(new ByteArrayInputStream(tarBytes));
      TarArchiveEntry entry = tis.getNextTarEntry();
      assertNotNull(entry);
      assertEquals("default.txt", entry.getName());
      tis.close();
  }

}

 I see the output is getting corrupted. The issue is that my text is being rendered with formatting
issues. Let me try a different approach and use a clean write.
