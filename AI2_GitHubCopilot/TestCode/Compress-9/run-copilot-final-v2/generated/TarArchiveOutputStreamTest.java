import static org.junit.Assert.assertEquals;
 import static org.junit.Assert.assertTrue;
 import static org.junit.Assert.fail;

 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.IOException;
 import java.io.OutputStream;

 import org.apache.commons.compress.archivers.ArchiveEntry;
 import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
 import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
 import org.junit.Test;

 /**
  * Tests for {@link TarArchiveOutputStream} focusing on byte counting
  * correctness (COMPRESS-160).  The bug causes {@code getBytesWritten()}
  * to return only the raw user-data bytes, ignoring headers, padding,
  * and EOF records.
  */
 public class TarArchiveOutputStreamTest {

     // -- helpers ---------------------------------------------------------------

     /** Tar header size in bytes (name + metadata). */
     private static final int HEADER_SIZE = 512;

     /** Number of EOF filler records written by finish(). */
     private static final int EOF_RECORDS = 2;

     /** Record size of the default TarBuffer. */
     private static final int RECORD_SIZE = 512;

     /**
      * @param contentSize  number of user-data bytes written for the entry
      * @return total bytes on stream after the entry is closed (header + padded content)
      */
     static long expectedEntryBytes(long contentSize) {
         long total = HEADER_SIZE + contentSize;
         // ceil(total / RECORD_SIZE) * RECORD_SIZE
         long blocks = (total + RECORD_SIZE - 1) / RECORD_SIZE;
         return blocks * RECORD_SIZE;
     }

     /** After finish() adds two EOF records (each RECORD_SIZE bytes). */
     static long expectedAfterFinish(long entryBytes) {
         return entryBytes + (EOF_RECORDS * RECORD_SIZE);
     }

     /** Create and close a single entry with the given content. */
     private void writeSingleEntry(TarArchiveOutputStream tos,
                                    String name, byte[] content) throws IOException {
         TarArchiveEntry entry = new TarArchiveEntry(name);
         entry.setSize(content.length);
         tos.putArchiveEntry(entry);
         tos.write(content);
         tos.closeArchiveEntry();
     }

     // -- Normal / boundary cases before finish ---------------------------------

     @Test
     public void testSingleEntry3BytesBeforeFinish() throws Exception {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         TarArchiveOutputStream tos = new TarArchiveOutputStream(bos);
         writeSingleEntry(tos, "foo", new byte[]{1,2,3});
         assertEquals("3-byte entry before finish",
                      expectedEntryBytes(3), tos.getBytesWritten());
     }

     @Test
     public void testSingleEntry512Bytes() throws Exception {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         TarArchiveOutputStream tos = new TarArchiveOutputStream(bos);
         writeSingleEntry(tos, "a", new byte[512]);
         assertEquals("exact record boundary content",
                      expectedEntryBytes(512), tos.getBytesWritten());
     }

     @Test
     public void testSingleEntry513Bytes() throws Exception {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         TarArchiveOutputStream tos = new TarArchiveOutputStream(bos);
         writeSingleEntry(tos, "b", new byte[513]);
         assertEquals("content spanning two records",
                      expectedEntryBytes(513), tos.getBytesWritten());
     }

     @Test
     public void testSingleEntryZeroBytes() throws Exception {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         TarArchiveOutputStream tos = new TarArchiveOutputStream(bos);
         writeSingleEntry(tos, "empty", new byte[0]);
         assertEquals("zero-byte entry (header only)",
                      expectedEntryBytes(0), tos.getBytesWritten());
     }

     // -- Multiple entries ------------------------------------------------------

     @Test
     public void testMultiEntryCumulativeBeforeFinish() throws Exception {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         TarArchiveOutputStream tos = new TarArchiveOutputStream(bos);

         writeSingleEntry(tos, "f1", new byte[10]);
         long after1 = expectedEntryBytes(10);
         assertEquals("after entry 1", after1, tos.getBytesWritten());

         writeSingleEntry(tos, "f2", new byte[200]);
         long after2 = after1 + expectedEntryBytes(200);
         assertEquals("after entry 2", after2, tos.getBytesWritten());

         writeSingleEntry(tos, "f3", new byte[700]);
         long after3 = after2 + expectedEntryBytes(700);
         assertEquals("after entry 3", after3, tos.getBytesWritten());
     }

     // -- finish / close effects ------------------------------------------------

     @Test
     public void testEmptyArchiveAfterFinish() throws Exception {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         TarArchiveOutputStream tos = new TarArchiveOutputStream(bos);
         assertEquals("before finish", 0, tos.getBytesWritten());
         tos.finish();
         assertEquals("empty archive after finish",
                      2 * RECORD_SIZE, tos.getBytesWritten());
     }

     @Test
     public void testSingleEmptyEntryAfterFinish() throws Exception {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         TarArchiveOutputStream tos = new TarArchiveOutputStream(bos);
         writeSingleEntry(tos, "empty", new byte[0]);
         long before = expectedEntryBytes(0);
         assertEquals("before finish", before, tos.getBytesWritten());
         tos.finish();
         assertEquals("after finish", expectedAfterFinish(before), tos.getBytesWritten());
     }

     @Test
     public void testCloseAutoFinish() throws Exception {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         TarArchiveOutputStream tos = new TarArchiveOutputStream(bos);
         writeSingleEntry(tos, "x", new byte[5]);
         long before = expectedEntryBytes(5);
         tos.close();
         assertEquals("close should finish and add EOF",
                      expectedAfterFinish(before), tos.getBytesWritten());
     }

     // -- write() increments during assembly ------------------------------------

     @Test
     public void testWriteIncrementsCount() throws Exception {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         TarArchiveOutputStream tos = new TarArchiveOutputStream(bos);
         TarArchiveEntry entry = new TarArchiveEntry("partial");
         entry.setSize(100);
         tos.putArchiveEntry(entry);

         long before = tos.getBytesWritten(); // header might already be counted? depends on impl
         tos.write(new byte[30], 0, 30);
         long after30 = tos.getBytesWritten();
         assertTrue("count should increase after first write",
                    after30 > before || (before == HEADER_SIZE && after30 == before));

         tos.write(new byte[70], 0, 70);
         long after100 = tos.getBytesWritten();
         assertEquals("count after all data written (header + data padded)",
                      expectedEntryBytes(100), after100);
         tos.closeArchiveEntry();
         assertEquals("count unchanged after close (already padded)",
                      expectedEntryBytes(100), tos.getBytesWritten());
     }

     // -- Error handling --------------------------------------------------------

     @Test
     public void testCannotPutEntryAfterFinish() throws Exception {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         TarArchiveOutputStream tos = new TarArchiveOutputStream(bos);
         tos.finish();
         try {
             tos.putArchiveEntry(new TarArchiveEntry("late"));
             fail("Expected IOException after finish");
         } catch (IOException expected) {
             // pass
         }
     }

     @Test
     public void testCannotCloseEntryWhenNoneOpen() throws Exception {
         ByteArrayOutputStream bos = new ByteArrayOutputStream();
         TarArchiveOutputStream tos = new TarArchiveOutputStream(bos);
         try {
             tos.closeArchiveEntry();
             fail("Expected IOException when no entry is open");
         } catch (IOException expected) {
             // pass
         }
     }
 }
