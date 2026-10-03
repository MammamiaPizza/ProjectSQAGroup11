import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;

 import junit.framework.TestCase;

 import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
 import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
 import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;

 /**
  * JUnit 3.8.2 tests for CpioArchiveOutputStream capturing the premature
  * EOFException bug (COMPRESS-28) when reading back written archives.
  */
 public class CpioArchiveOutputStream_Bug1_Test extends TestCase {

     private static void writeBytes(CpioArchiveOutputStream out, byte[] data) throws IOException {
         out.write(data, 0, data.length);
     }

     private static byte[] readAllBytes(CpioArchiveInputStream in) throws IOException {
         ByteArrayOutputStream buf = new ByteArrayOutputStream();
         byte[] tmp = new byte[4096];
         int n;
         while ((n = in.read(tmp)) != -1) {
             buf.write(tmp, 0, n);
         }
         return buf.toByteArray();
     }

     private static byte[] generateData(int size) {
         byte[] data = new byte[size];
         for (int i = 0; i < size; i++) {
             data[i] = (byte) (i & 0xFF);
         }
         return data;
     }

     private CpioArchiveEntry createEntry(String name, long size, short format) {
         CpioArchiveEntry entry = new CpioArchiveEntry(format);
         entry.setName(name);
         entry.setSize(size);
         entry.setMode(0644);
         entry.setTime(System.currentTimeMillis() / 1000 * 1000); // ms -> s, remove sub-second
         return entry;
     }

     // ---------- Round-trip tests (detect padding/alignment bug) ----------

     public void testWriteAndReadNewFormatEntry() throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos,
CpioArchiveOutputStream.FORMAT_NEW);
         byte[] data = generateData(4097);
         CpioArchiveEntry entry = createEntry("file.bin", data.length,
CpioArchiveOutputStream.FORMAT_NEW);
         out.putNextEntry(entry);
         writeBytes(out, data);
         out.closeArchiveEntry();
         out.finish();
         out.close();

         byte[] archiveBytes = baos.toByteArray();
         CpioArchiveInputStream in = new CpioArchiveInputStream(new
ByteArrayInputStream(archiveBytes));
         CpioArchiveEntry readEntry = in.getNextCPIOEntry();
         assertNotNull("Expected an entry", readEntry);
         assertEquals("file.bin", readEntry.getName());
         assertEquals(data.length, readEntry.getSize());
         byte[] readData = readAllBytes(in);
         assertEquals(data.length, readData.length);
         for (int i = 0; i < data.length; i++) {
             assertEquals("Byte at pos " + i, data[i], readData[i]);
         }
         assertNull(in.getNextCPIOEntry());
         in.close();
     }

     public void testWriteAndReadOldAsciiEntry() throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos,
CpioArchiveOutputStream.FORMAT_OLD_ASCII);
         byte[] data = generateData(0);
         CpioArchiveEntry entry = createEntry("zerofile", data.length,
CpioArchiveOutputStream.FORMAT_OLD_ASCII);
         out.putNextEntry(entry);
         out.closeArchiveEntry();
         out.finish();
         out.close();

         CpioArchiveInputStream in = new CpioArchiveInputStream(new
ByteArrayInputStream(baos.toByteArray()));
         CpioArchiveEntry readEntry = in.getNextCPIOEntry();
         assertNotNull(readEntry);
         assertEquals("zerofile", readEntry.getName());
         assertEquals(0, readEntry.getSize());
         assertNull(in.getNextCPIOEntry());
         in.close();
     }

     public void testWriteAndReadOldBinaryEntry() throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos,
CpioArchiveOutputStream.FORMAT_OLD_BINARY);
         byte[] data = generateData(1);
         CpioArchiveEntry entry = createEntry("small", data.length,
CpioArchiveOutputStream.FORMAT_OLD_BINARY);
         out.putNextEntry(entry);
         writeBytes(out, data);
         out.closeArchiveEntry();
         out.finish();
         out.close();

         CpioArchiveInputStream in = new CpioArchiveInputStream(new
ByteArrayInputStream(baos.toByteArray()));
         CpioArchiveEntry readEntry = in.getNextCPIOEntry();
         assertEquals("small", readEntry.getName());
         assertEquals(1, readEntry.getSize());
         byte[] readData = readAllBytes(in);
         assertEquals(1, readData.length);
         assertNull(in.getNextCPIOEntry());
         in.close();
     }

     public void testMultipleEntriesDifferentFormats() throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos,
CpioArchiveOutputStream.FORMAT_NEW);
         byte[] data1 = generateData(100);
         byte[] data2 = generateData(200);
         CpioArchiveEntry e1 = createEntry("a.bin", data1.length,
CpioArchiveOutputStream.FORMAT_NEW);
         out.putNextEntry(e1);
         writeBytes(out, data1);
         out.closeArchiveEntry();
         CpioArchiveEntry e2 = createEntry("b.bin", data2.length,
CpioArchiveOutputStream.FORMAT_OLD_ASCII);
         e2.setFormat(CpioArchiveOutputStream.FORMAT_OLD_ASCII); // override stream default
         out.putNextEntry(e2);
         writeBytes(out, data2);
         out.closeArchiveEntry();
         out.finish();
         out.close();

         CpioArchiveInputStream in = new CpioArchiveInputStream(new
ByteArrayInputStream(baos.toByteArray()));
         CpioArchiveEntry r1 = in.getNextCPIOEntry();
         assertEquals("a.bin", r1.getName());
         assertEquals(data1.length, r1.getSize());
         byte[] rd1 = readAllBytes(in);
         assertEquals(data1.length, rd1.length);

         CpioArchiveEntry r2 = in.getNextCPIOEntry();
         assertEquals("b.bin", r2.getName());
         assertEquals(data2.length, r2.getSize());
         byte[] rd2 = readAllBytes(in);
         assertEquals(data2.length, rd2.length);
         assertNull(in.getNextCPIOEntry());
         in.close();
     }

     public void testDataPaddingWithUnalignedSizes() throws IOException {
         // sizes that trigger padding logic both for header and data
         int[] sizes = { 1, 3, 5, 257, 1023, 4097 };
         for (int size : sizes) {
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
             CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos,
CpioArchiveOutputStream.FORMAT_NEW);
             byte[] data = generateData(size);
             CpioArchiveEntry e = createEntry("test_" + size, size,
CpioArchiveOutputStream.FORMAT_NEW);
             out.putNextEntry(e);
             writeBytes(out, data);
             out.closeArchiveEntry();
             out.finish();
             out.close();

             CpioArchiveInputStream in = new CpioArchiveInputStream(new
ByteArrayInputStream(baos.toByteArray()));
             CpioArchiveEntry read = in.getNextCPIOEntry();
             assertEquals(size, read.getSize());
             byte[] rd = readAllBytes(in);
             assertEquals(size, rd.length);
             assertNull(in.getNextCPIOEntry());
             in.close();
         }
     }

     // ---------- Edge cases and exception tests ----------

     public void testCloseWithoutAnyEntry() throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos);
         out.close(); // should produce a trailer and close
         // reading should see empty archive (only trailer, getNextCPIOEntry returns null)
         CpioArchiveInputStream in = new CpioArchiveInputStream(new
ByteArrayInputStream(baos.toByteArray()));
         CpioArchiveEntry entry = in.getNextCPIOEntry();
         assertNull("No entry expected", entry);
         in.close();
     }

     public void testFinishBeforeClose() throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos);
         out.finish(); // manually write trailer
         out.close();
         // read back, should be empty
         CpioArchiveInputStream in = new CpioArchiveInputStream(new
ByteArrayInputStream(baos.toByteArray()));
         assertNull(in.getNextCPIOEntry());
         in.close();
     }

     public void testDuplicateEntryNameThrowsException() throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos);
         CpioArchiveEntry e1 = createEntry("dup", 10, CpioArchiveOutputStream.FORMAT_NEW);
         out.putNextEntry(e1);
         out.closeArchiveEntry();
         CpioArchiveEntry e2 = createEntry("dup", 5, CpioArchiveOutputStream.FORMAT_NEW);
         try {
             out.putNextEntry(e2);
             fail("Expected IOException for duplicate entry");
         } catch (IOException expected) {
             assertTrue(expected.getMessage().contains("duplicate"));
         }
         out.close();
     }

     public void testWriteExceedingEntrySizeThrowsException() throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos);
         CpioArchiveEntry entry = createEntry("overflow", 5, CpioArchiveOutputStream.FORMAT_NEW);
         out.putNextEntry(entry);
         try {
             out.write(generateData(6));
             fail("Expected IOException for writing past end");
         } catch (IOException expected) {
             assertTrue(expected.getMessage().contains("past end"));
         }
         out.close();
     }

     public void testWriteWithoutEntryThrowsException() throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos);
         try {
             out.write(generateData(10));
             fail("Expected IOException for missing entry");
         } catch (IOException expected) {
             assertTrue(expected.getMessage().contains("no current CPIO entry"));
         }
         out.close();
     }

     public void testEntrySizeMismatchThrowsOnCloseArchiveEntry() throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos);
         CpioArchiveEntry entry = createEntry("size_mismatch", 100,
CpioArchiveOutputStream.FORMAT_NEW);
         out.putNextEntry(entry);
         out.write(generateData(50));
         try {
             out.closeArchiveEntry();
             fail("Expected IOException for size mismatch");
         } catch (IOException expected) {
             assertTrue(expected.getMessage().contains("invalid entry size"));
         }
         out.close();
     }

     public void testClosedStreamWriteThrowsException() throws IOException {
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos);
         out.close();
         try {
             out.write(0);
             fail("Expected IOException for closed stream");
         } catch (IOException expected) {
             assertTrue(expected.getMessage().contains("closed") ||
                        expected.getMessage().contains("Stream closed"));
         }
     }
 }