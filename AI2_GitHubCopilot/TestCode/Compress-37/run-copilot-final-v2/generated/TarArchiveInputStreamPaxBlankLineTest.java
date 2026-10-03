package org.apache.commons.compress.archivers.tar;

 import org.junit.Test;
 import static org.junit.Assert.*;

 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;

 /**
  * Tests for blank/empty line handling in PAX extended headers
  * (COMPRESS-355: NegativeArraySizeException on blank lines).
  */
 public class TarArchiveInputStreamPaxBlankLineTest {

     private static final int BLOCK_SIZE = 512;
     private static final int RECORD_SIZE = 512;

     // ---- helpers ----

     private byte[] buildHeader(String name, long size, byte typeFlag) {
         byte[] buf = new byte[BLOCK_SIZE];
         // name field: 100 bytes starting at offset 0
         byte[] nameBytes = name.getBytes(java.nio.charset.StandardCharsets.UTF_8);
         System.arraycopy(nameBytes, 0, buf, 0, Math.min(nameBytes.length, 100));

         // mode: default 0644 = "000644\0" at offset 100, length 8
         System.arraycopy("000644\0".getBytes(), 0, buf, 100, 7);

         // uid/gid: "000000\0" at 108 and 116
         System.arraycopy("000000\0".getBytes(), 0, buf, 108, 7);
         System.arraycopy("000000\0".getBytes(), 0, buf, 116, 7);

         // size: octal at offset 124, length 12
         String sizeStr = String.format("%011o", size);
         System.arraycopy(sizeStr.getBytes(), 0, buf, 124, 11);
         buf[135] = ' ';

         // mtime: offset 136, length 12
         System.arraycopy("00000000000 ".getBytes(), 0, buf, 136, 12);

         // checksum: 8 spaces initially at offset 148
         System.arraycopy("        ".getBytes(), 0, buf, 148, 8);

         // type flag at offset 156
         buf[156] = typeFlag;

         // linkname: 100 bytes at offset 157 (all null)
         // no need to fill

         // magic: "ustar\0" at offset 257
         System.arraycopy("ustar\0".getBytes(), 0, buf, 257, 6);

         // version: "00" at offset 263
         buf[263] = '0';
         buf[264] = '0';

         // compute and set checksum at offset 148 (8 bytes)
         long sum = 0;
         for (int i = 0; i < BLOCK_SIZE; i++) {
             // checksum field is treated as spaces for computation
             if (i >= 148 && i < 156) {
                 sum += ' ';
             } else {
                 sum += buf[i] & 0xFF;
             }
         }
         // Checksum is 6 octal digits + null + space
         String csum = String.format("%06o", sum);
         System.arraycopy((csum + "\0 ").getBytes(), 0, buf, 148, 8);

         return buf;
     }

     private TarArchiveInputStream newTis(byte[] data) {
         return new TarArchiveInputStream(new ByteArrayInputStream(data), BLOCK_SIZE, RECORD_SIZE);
     }

     /**
      * Build tar bytes: a PAX header record containing the given pax data,
      * followed by a normal file record with the given name and size=0.
      * Pax header record name is "PaxHeader\0...".
      */
     private byte[] buildSimplePaxTar(String paxData, String fileName) throws Exception {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         // PAX header record
         byte[] paxBuf = buildHeader("././@PaxHeader", paxData.getBytes("UTF-8").length, (byte)
'x');
         bos.write(paxBuf);
         // round up to 512-byte blocks
         byte[] paxPayload = paxData.getBytes("UTF-8");
         bos.write(paxPayload);
         int rem = (BLOCK_SIZE - (paxPayload.length % BLOCK_SIZE)) % BLOCK_SIZE;
         bos.write(new byte[rem]);

         // file entry
         byte[] fileBuf = buildHeader(fileName, 0, (byte) '0');
         bos.write(fileBuf);
         // end-of-archive: two zero blocks
         bos.write(new byte[BLOCK_SIZE]);
         bos.write(new byte[BLOCK_SIZE]);
         return bos.toByteArray();
     }

     // ---- test cases ----

     @Test
     public void survivesSingleBlankLineInPaxHeader() throws Exception {
         // Single blank line between two valid PAX keywords
         String paxData = "16 path=foo.txt\n\n21 size=00000000000\n";
         byte[] tar = buildSimplePaxTar(paxData, "foo.txt");
         TarArchiveInputStream tis = newTis(tar);
         TarArchiveEntry entry = tis.getNextTarEntry();
         assertNotNull("Entry should not be null", entry);
         assertEquals("Entry name should be foo.txt", "foo.txt", entry.getName());
         assertNull("No more entries", tis.getNextTarEntry());
         tis.close();
     }

     @Test
     public void survivesMultipleConsecutiveBlankLines() throws Exception {
         // Multiple consecutive blank lines
         String paxData = "16 path=bar.txt\n\n\n21 size=00000000000\n";
         byte[] tar = buildSimplePaxTar(paxData, "bar.txt");
         TarArchiveInputStream tis = newTis(tar);
         TarArchiveEntry entry = tis.getNextTarEntry();
         assertNotNull(entry);
         assertEquals("bar.txt", entry.getName());
         assertNull(tis.getNextTarEntry());
         tis.close();
     }

     @Test
     public void survivesBlankLineAtStartOfPaxData() throws Exception {
         // Blank line at the very beginning of PAX data
         String paxData = "\n16 path=baz.txt\n21 size=00000000000\n";
         byte[] tar = buildSimplePaxTar(paxData, "baz.txt");
         TarArchiveInputStream tis = newTis(tar);
         TarArchiveEntry entry = tis.getNextTarEntry();
         assertNotNull(entry);
         assertEquals("baz.txt", entry.getName());
         assertNull(tis.getNextTarEntry());
         tis.close();
     }

     @Test
     public void survivesBlankLineAtEndOfPaxData() throws Exception {
         // Blank line at the end of PAX data (after last keyword)
         String paxData = "16 path=qux.txt\n21 size=00000000000\n\n";
         byte[] tar = buildSimplePaxTar(paxData, "qux.txt");
         TarArchiveInputStream tis = newTis(tar);
         TarArchiveEntry entry = tis.getNextTarEntry();
         assertNotNull(entry);
         assertEquals("qux.txt", entry.getName());
         assertNull(tis.getNextTarEntry());
         tis.close();
     }

     @Test
     public void survivesOnlyBlankLinesInPaxData() throws Exception {
         // PAX header contains only blank lines (no keywords)
         String paxData = "\n\n\n";
         byte[] tar = buildSimplePaxTar(paxData, "onlyblanks.txt");
         TarArchiveInputStream tis = newTis(tar);
         TarArchiveEntry entry = tis.getNextTarEntry();
         assertNotNull(entry);
         assertEquals("onlyblanks.txt", entry.getName());
         assertNull(tis.getNextTarEntry());
         tis.close();
     }

     @Test
     public void survivesBlankLineConsistingOfSpacesAndTabs() throws Exception {
         // A line that consists only of spaces and tabs
         String paxData = "16 path=spaces.txt\n \t \n21 size=00000000000\n";
         byte[] tar = buildSimplePaxTar(paxData, "spaces.txt");
         TarArchiveInputStream tis = newTis(tar);
         TarArchiveEntry entry = tis.getNextTarEntry();
         assertNotNull(entry);
         assertEquals("spaces.txt", entry.getName());
         assertNull(tis.getNextTarEntry());
         tis.close();
     }

     @Test
     public void survivesBlankLineWithCarriageReturn() throws Exception {
         // Blank line with a trailing carriage return (Windows-style blank line)
         // Note: PAX uses \n as line separator; a line ending in \r\n has
         // an empty keyword and '\r' is part of the line content for a blank line.
         // We test that \r\n (empty + \r) does not throw.
         String paxData = "16 path=crlf.txt\n\r\n21 size=00000000000\n";
         byte[] tar = buildSimplePaxTar(paxData, "crlf.txt");
         TarArchiveInputStream tis = newTis(tar);
         TarArchiveEntry entry = tis.getNextTarEntry();
         assertNotNull(entry);
         assertEquals("crlf.txt", entry.getName());
         assertNull(tis.getNextTarEntry());
         tis.close();
     }

     @Test
     public void survivesBlankLineBeforeRecordPadding() throws Exception {
         // Blank line right before the record boundary (filling up the
         // PAX payload such that a blank line sits near end)
         // Build pax data that leaves a blank line just before the block fill
         String paxData = "16 path=pad.txt\n21 size=00000000000\n\n";
         byte[] tar = buildSimplePaxTar(paxData, "pad.txt");
         TarArchiveInputStream tis = newTis(tar);
         TarArchiveEntry entry = tis.getNextTarEntry();
         assertNotNull(entry);
         assertEquals("pad.txt", entry.getName());
         assertNull(tis.getNextTarEntry());
         tis.close();
     }

     @Test
     public void survivesBlankLinesWithCorrectEntryAttributes() throws Exception {
         // Verify that after skipping blank lines, remaining key=value pairs
         // are still parsed correctly (name + size)
         String paxData = "20 size=00000000042\n\n16 path=big.txt\n";
         byte[] tar = buildSimplePaxTar(paxData, "big.txt");
         TarArchiveInputStream tis = newTis(tar);
         TarArchiveEntry entry = tis.getNextTarEntry();
         assertNotNull(entry);
         assertEquals("big.txt", entry.getName());
         assertEquals(42, entry.getSize());
         assertNull(tis.getNextTarEntry());
         tis.close();
     }

     @Test
     public void survivesBlankLinesWithGlobalPaxHeader() throws Exception {
         // Global PAX header with blank lines, followed by a normal entry
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         // Global PAX header record (type 'g')
         String globalPax = "25 globexkey=globexval\n\n26 PATH=fromglobal.txt\n";
         byte[] gBuf = buildHeader("././@PaxHeader", globalPax.getBytes("UTF-8").length, (byte)
'g');
         bos.write(gBuf);
         byte[] gPayload = globalPax.getBytes("UTF-8");
         bos.write(gPayload);
         int grem = (BLOCK_SIZE - (gPayload.length % BLOCK_SIZE)) % BLOCK_SIZE;
         bos.write(new byte[grem]);

         // Normal file (note: PATH keyword in global is applied by convention)
         byte[] fBuf = buildHeader("localfile.txt", 0, (byte) '0');
         bos.write(fBuf);

         // End-of-archive
         bos.write(new byte[BLOCK_SIZE]);
         bos.write(new byte[BLOCK_SIZE]);

         byte[] tar = bos.toByteArray();
         TarArchiveInputStream tis = newTis(tar);
         TarArchiveEntry entry = tis.getNextTarEntry();
         assertNotNull(entry);
         // Global PAX with PATH=fromglobal.txt should rename entry
         assertEquals("fromglobal.txt", entry.getName());
         assertNull(tis.getNextTarEntry());
         tis.close();
     }
 }
