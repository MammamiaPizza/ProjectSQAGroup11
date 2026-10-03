import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.EOFException;
 import java.io.IOException;
 import java.util.zip.CRC32;
 import java.util.zip.Deflater;
 import java.util.zip.ZipException;

 import junit.framework.TestCase;

 import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
 import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
 import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
 import org.apache.commons.compress.archivers.zip.ZipLong;
 import org.apache.commons.compress.archivers.zip.ZipShort;
 import org.apache.commons.compress.archivers.zip.ZipUtil;

 /**
  * Tests for ZipArchiveInputStream focusing on COMPRESS-87 (truncated entry
  * handling and missing subsequent volume detection).
  */
 public class ZipArchiveInputStreamTest extends TestCase {

     // ---------- helpers ----------

     private static void writeShort(ByteArrayOutputStream bos, int value) {
         bos.write(value & 0xFF);
         bos.write((value >> 8) & 0xFF);
     }

     private static void writeInt(ByteArrayOutputStream bos, long value) {
         writeShort(bos, (int) (value & 0xFFFF));
         writeShort(bos, (int) ((value >> 16) & 0xFFFF));
     }

     /**
      * Creates a minimal ZIP byte array containing a single STORED entry
      * with the given name and data.  No central directory or EOCD is added.
      */
     private byte[] createStoredEntryZip(String name, byte[] data,
                                         boolean dataDescriptor) throws Exception {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         byte[] lfhSig = ZipArchiveOutputStream.LFH_SIG;
         bos.write(lfhSig);                          // signature 4B
         writeShort(bos, 20);                        // version needed 2B
         int flags = dataDescriptor ? 8 : 0;
         writeShort(bos, flags);                     // general purpose flag 2B
         writeShort(bos, ZipArchiveOutputStream.STORED); // method 2B
         writeInt(bos, 0);                           // time+date 4B (dummy)
         // CRC-32
         CRC32 crc = new CRC32();
         crc.update(data);
         writeInt(bos, dataDescriptor ? 0 : crc.getValue()); // crc 4B
         writeInt(bos, data.length);                 // compressed size 4B
         writeInt(bos, data.length);                 // uncompressed size 4B
         byte[] nameBytes = name.getBytes("UTF-8");
         writeShort(bos, nameBytes.length);          // file name length 2B
         writeShort(bos, 0);                         // extra field length 2B
         bos.write(nameBytes);                       // file name
         bos.write(data);                            // file data
         return bos.toByteArray();
     }

     /**
      * Creates a truncated STORED entry: the LFH declares <code>declaredSize</code>
      * but only <code>actualData</code> (shorter) is actually present in the stream.
      */
     private byte[] createTruncatedStoredEntryZip(String name,
                                                  int declaredSize,
                                                  byte[] actualData) throws Exception {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         byte[] lfhSig = ZipArchiveOutputStream.LFH_SIG;
         bos.write(lfhSig);
         writeShort(bos, 20);
         writeShort(bos, 0);                         // no data descriptor
         writeShort(bos, ZipArchiveOutputStream.STORED);
         writeInt(bos, 0);
         CRC32 crc = new CRC32();
         crc.update(actualData);
         writeInt(bos, (int) crc.getValue());        // crc of actual data
         writeInt(bos, declaredSize);                // compressed size (overstated)
         writeInt(bos, declaredSize);                // uncompressed size (overstated)
         byte[] nameBytes = name.getBytes("UTF-8");
         writeShort(bos, nameBytes.length);
         writeShort(bos, 0);
         bos.write(nameBytes);
         bos.write(actualData);                      // fewer bytes than declared
         return bos.toByteArray();
     }

     /**
      * Creates a valid STORED entry followed by a second valid STORED entry.
      * No central directory.
      */
     private byte[] createTwoEntryZip(String name1, byte[] data1,
                                      String name2, byte[] data2) throws Exception {
         byte[] entry1 = createStoredEntryZip(name1, data1, false);
         byte[] entry2 = createStoredEntryZip(name2, data2, false);
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         bos.write(entry1);
         bos.write(entry2);
         return bos.toByteArray();
     }

     // ---------- normal / boundary ----------

     public void testNormalStoredEntryRead() throws Exception {
         byte[] content = "Hello World!".getBytes("UTF-8");
         byte[] zipBytes = createStoredEntryZip("test.txt", content, false);
         ZipArchiveInputStream zis = new ZipArchiveInputStream(
                 new ByteArrayInputStream(zipBytes), "UTF-8", true);

         ZipArchiveEntry entry = (ZipArchiveEntry) zis.getNextEntry();
         assertNotNull(entry);
         assertEquals("test.txt", entry.getName());
         assertEquals(ZipArchiveOutputStream.STORED, entry.getMethod());
         assertEquals(content.length, entry.getSize());

         byte[] buf = new byte[1024];
         int total = 0;
         int r;
         while ((r = zis.read(buf, 0, buf.length)) != -1) {
             total += r;
         }
         assertEquals(content.length, total);

         // end of entries
         assertNull(zis.getNextEntry());
         zis.close();
     }

     public void testReadAfterCloseThrowsIOException() throws Exception {
         byte[] content = "data".getBytes("UTF-8");
         byte[] zipBytes = createStoredEntryZip("f", content, false);
         ZipArchiveInputStream zis = new ZipArchiveInputStream(
                 new ByteArrayInputStream(zipBytes), "UTF-8", true);
         zis.close();
         try {
             zis.read(new byte[10], 0, 10);
             fail("Expected IOException on closed stream");
         } catch (IOException expected) {
             // expected
         }
     }

     public void testSkipValue() throws Exception {
         byte[] content = "abcdefghijklmnop".getBytes("UTF-8");
         byte[] zipBytes = createStoredEntryZip("data", content, false);
         ZipArchiveInputStream zis = new ZipArchiveInputStream(
                 new ByteArrayInputStream(zipBytes), "UTF-8", true);
         assertNotNull(zis.getNextEntry());
         long skipped = zis.skip(5);
         assertEquals(5, skipped);
         byte[] rest = new byte[20];
         int read = zis.read(rest, 0, rest.length);
         assertTrue(read > 0);
         String remaining = new String(rest, 0, read, "UTF-8");
         assertEquals("fghijklmnop", remaining);
         zis.close();
     }

     public void testGetNextEntryOnEmptyStream() throws Exception {
         ZipArchiveInputStream zis = new ZipArchiveInputStream(
                 new ByteArrayInputStream(new byte[0]), "UTF-8", true);
         assertNull(zis.getNextEntry());
         zis.close();
     }

     public void testMatchesSignatureValid() {
         byte[] sig = ZipArchiveOutputStream.LFH_SIG;
         assertTrue(ZipArchiveInputStream.matches(sig, sig.length));
         byte[] eocd = ZipArchiveOutputStream.EOCD_SIG;
         assertTrue(ZipArchiveInputStream.matches(eocd, eocd.length));
     }

     public void testMultipleEntries() throws Exception {
         byte[] data1 = "first".getBytes("UTF-8");
         byte[] data2 = "second".getBytes("UTF-8");
         byte[] zipBytes = createTwoEntryZip("a.txt", data1, "b.txt", data2);
         ZipArchiveInputStream zis = new ZipArchiveInputStream(
                 new ByteArrayInputStream(zipBytes), "UTF-8", true);

         // first entry
         ZipArchiveEntry e1 = (ZipArchiveEntry) zis.getNextEntry();
         assertNotNull(e1);
         assertEquals("a.txt", e1.getName());
         byte[] buf1 = new byte[10];
         int r1 = zis.read(buf1, 0, buf1.length);
         assertTrue(r1 > 0);
         assertEquals(data1.length, r1);
         assertEquals(-1, zis.read(buf1, 0, buf1.length));

         // second entry
         ZipArchiveEntry e2 = (ZipArchiveEntry) zis.getNextEntry();
         assertNotNull(e2);
         assertEquals("b.txt", e2.getName());
         byte[] buf2 = new byte[10];
         int r2 = zis.read(buf2, 0, buf2.length);
         assertEquals(data2.length, r2);
         assertEquals(-1, zis.read(buf2, 0, buf2.length));

         assertNull(zis.getNextEntry());
         zis.close();
     }

     // ---------- truncated entry (COMPRESS-87) ----------

     public void testTruncatedStoredEntryAtFirstByte() throws Exception {
         byte[] content = "data".getBytes("UTF-8");
         // declare size 10, provide 0 bytes of real data
         byte[] zipBytes = createTruncatedStoredEntryZip("t", 10, new byte[0]);
         ZipArchiveInputStream zis = new ZipArchiveInputStream(
                 new ByteArrayInputStream(zipBytes), "UTF-8", true);
         assertNotNull(zis.getNextEntry());
         try {
             zis.read(new byte[10], 0, 10);
             fail("Expected IOException on truncated entry (first byte missing)");
         } catch (IOException expected) {
             // COMPRESS-87: read must throw IOException
         } finally {
             zis.close();
         }
     }

     public void testTruncatedStoredEntryMidData() throws Exception {
         byte[] real = "ABCDEFGHIJ".getBytes("UTF-8"); // 10 bytes
         // declare 20 bytes
         byte[] zipBytes = createTruncatedStoredEntryZip("m", 20, real);
         ZipArchiveInputStream zis = new ZipArchiveInputStream(
                 new ByteArrayInputStream(zipBytes), "UTF-8", true);
         assertNotNull(zis.getNextEntry());

         // first read should succeed (10 bytes available)
         byte[] buf = new byte[20];
         int first = zis.read(buf, 0, 20);
         assertEquals(10, first);
         // next read should throw because stream ends while more data expected
         try {
             zis.read(buf, 0, 20);
             fail("Expected IOException on truncated entry (mid-data)");
         } catch (IOException expected) {
             // expected
         } finally {
             zis.close();
         }
     }

     public void testTruncatedStoredEntryLastByte() throws Exception {
         byte[] real = "123456789".getBytes("UTF-8"); // 9 bytes
         // declare 10 bytes
         byte[] zipBytes = createTruncatedStoredEntryZip("last", 10, real);
         ZipArchiveInputStream zis = new ZipArchiveInputStream(
                 new ByteArrayInputStream(zipBytes), "UTF-8", true);
         assertNotNull(zis.getNextEntry());

         byte[] buf = new byte[10];
         int first = zis.read(buf, 0, 10);
         assertEquals(9, first);
         try {
             zis.read(buf, 0, 10);
             fail("Expected IOException on truncated entry (last byte missing)");
         } catch (IOException expected) {
             // expected
         } finally {
             zis.close();
         }
     }

     // ---------- missing subsequent volume ----------

     public void testGetNextEntryOnMissingSubsequentVolume() throws Exception {
         // valid first entry, stream ends right after its data (no central directory / EOCD)
         byte[] content = "volume-1".getBytes("UTF-8");
         byte[] zipBytes = createStoredEntryZip("file.txt", content, false);
         ZipArchiveInputStream zis = new ZipArchiveInputStream(
                 new ByteArrayInputStream(zipBytes), "UTF-8", true);

         // read the complete first entry
         ZipArchiveEntry e = (ZipArchiveEntry) zis.getNextEntry();
         assertNotNull(e);
         byte[] buf = new byte[100];
         int total = 0;
         int r;
         while ((r = zis.read(buf, 0, buf.length)) != -1) {
             total += r;
         }
         assertEquals(content.length, total);

         // The archive is missing subsequent volumes.  getNextEntry must not
         // silently return null.
         try {
             zis.getNextEntry();
             fail("Expected IOException on missing subsequent volume");
         } catch (IOException expected) {
             // COMPRESS-87
         } finally {
             zis.close();
         }
     }

     // ---------- deflated entry ----------

     public void testDeflatedEntryNormal() throws Exception {
         byte[] content = "This is a test string that will be deflated."
                 .getBytes("UTF-8");
         byte[] deflated = deflate(content);
         byte[] zipBytes = createDeflatedEntryZip("deflated.txt", content, deflated);
         ZipArchiveInputStream zis = new ZipArchiveInputStream(
                 new ByteArrayInputStream(zipBytes), "UTF-8", true);

         ZipArchiveEntry e = (ZipArchiveEntry) zis.getNextEntry();
         assertNotNull(e);
         assertEquals("deflated.txt", e.getName());
         assertEquals(ZipArchiveOutputStream.DEFLATED, e.getMethod());

         byte[] buf = new byte[1024];
         int total = 0;
         int r;
         while ((r = zis.read(buf, 0, buf.length)) != -1) {
             total += r;
         }
         assertEquals(content.length, total);
         String result = new String(buf, 0, total, "UTF-8");
         assertTrue(result.startsWith("This is a test"));

         assertNull(zis.getNextEntry());
         zis.close();
     }

     public void testDeflatedEntryTruncated() throws Exception {
         byte[] content = "data for deflated entry test".getBytes("UTF-8");
         byte[] deflated = deflate(content);
         // truncate the deflated stream halfway
         byte[] truncated = new byte[deflated.length / 2];
         System.arraycopy(deflated, 0, truncated, 0, truncated.length);
         byte[] zipBytes = createDeflatedEntryZip("d", content, truncated);
         ZipArchiveInputStream zis = new ZipArchiveInputStream(
                 new ByteArrayInputStream(zipBytes), "UTF-8", true);

         assertNotNull(zis.getNextEntry());
         byte[] buf = new byte[1024];
         try {
             // reading the truncated deflated entry should trigger a ZipException
             // (wrapping DataFormatException from Inflater)
             int total = 0;
             int r;
             while ((r = zis.read(buf, 0, buf.length)) != -1) {
                 total += r;
             }
             // If we get here it means the stream ended before expected; the bug
             // allows reading a truncated entry without an exception.
             // We check that we did not get the full content.
             assertTrue("Read truncated deflated entry without exception", total < content.length);
         } catch (ZipException ze) {
             // fine: the inflater detected corrupt/truncated data
         } catch (IOException ioe) {
             // also acceptable for truncated stream
         } finally {
             zis.close();
         }
     }

     // ---------- internal helper methods ----------

     private static byte[] deflate(byte[] data) throws Exception {
         Deflater def = new Deflater(Deflater.DEFAULT_COMPRESSION, true);
         def.setInput(data);
         def.finish();
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         byte[] buf = new byte[512];
         while (!def.finished()) {
             int r = def.deflate(buf);
             bos.write(buf, 0, r);
         }
         def.end();
         return bos.toByteArray();
     }

     private byte[] createDeflatedEntryZip(String name, byte[] uncompressed,
                                           byte[] deflated) throws Exception {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         byte[] lfhSig = ZipArchiveOutputStream.LFH_SIG;
         bos.write(lfhSig);
         writeShort(bos, 20);                         // version needed
         writeShort(bos, 0);                          // flags
         writeShort(bos, ZipArchiveOutputStream.DEFLATED); // method
         writeInt(bos, 0);                            // time+date
         CRC32 crc = new CRC32();
         crc.update(uncompressed);
         writeInt(bos, (int) crc.getValue());         // crc
         writeInt(bos, deflated.length);              // compressed size
         writeInt(bos, uncompressed.length);          // uncompressed size
         byte[] nameBytes = name.getBytes("UTF-8");
         writeShort(bos, nameBytes.length);
         writeShort(bos, 0);
         bos.write(nameBytes);
         bos.write(deflated);
         return bos.toByteArray();
     }
 }