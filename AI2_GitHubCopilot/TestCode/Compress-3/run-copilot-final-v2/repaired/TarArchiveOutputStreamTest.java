package org.apache.commons.compress.archivers.tar;

 import java.io.ByteArrayInputStream;
 import java.io.ByteArrayOutputStream;
 import java.io.IOException;
 import junit.framework.TestCase;

 public class TarArchiveOutputStreamTest extends TestCase {

     private ByteArrayOutputStream baos;
     private TarArchiveOutputStream tos;

     protected void setUp() throws Exception {
         baos = new ByteArrayOutputStream();
         tos = new TarArchiveOutputStream(baos);
     }

     public void testFinishAfterPutAndCloseEntry() throws IOException {
         TarArchiveEntry entry = new TarArchiveEntry("test.txt");
         byte[] data = "hello".getBytes("UTF-8");
         entry.setSize(data.length);
         tos.putArchiveEntry(entry);
         tos.write(data);
         tos.closeArchiveEntry();
         tos.finish();
         assertTrue(baos.size() > 0);
         verifyArchive(baos.toByteArray(), 1);
     }

     public void testFinishAfterPutWithoutClose() throws IOException {
         TarArchiveEntry entry = new TarArchiveEntry("file.dat");
         byte[] data = new byte[100];
         entry.setSize(100);
         tos.putArchiveEntry(entry);
         tos.write(data);
         tos.closeArchiveEntry();
         tos.finish();
         verifyArchive(baos.toByteArray(), 1);
     }

     public void testCloseAfterPutAndCloseEntry() throws IOException {
         TarArchiveEntry entry = new TarArchiveEntry("a.txt");
         byte[] data = "data".getBytes("UTF-8");
         entry.setSize(data.length);
         tos.putArchiveEntry(entry);
         tos.write(data);
         tos.closeArchiveEntry();
         tos.close();
         verifyArchive(baos.toByteArray(), 1);
     }

     public void testCloseAfterPutWithoutClose() throws IOException {
         TarArchiveEntry entry = new TarArchiveEntry("b.txt");
         byte[] data = new byte[512];
         entry.setSize(512);
         tos.putArchiveEntry(entry);
         tos.write(data);
         tos.closeArchiveEntry();
         tos.close();
         verifyArchive(baos.toByteArray(), 1);
     }

     public void testDoubleClose() throws IOException {
         TarArchiveEntry entry = new TarArchiveEntry("c.txt");
         entry.setSize(0);
         tos.putArchiveEntry(entry);
         tos.closeArchiveEntry();
         tos.close();
         tos.close();
         verifyArchive(baos.toByteArray(), 1);
     }

     public void testCloseAfterFinish() throws IOException {
         TarArchiveEntry entry = new TarArchiveEntry("d.txt");
         entry.setSize(0);
         tos.putArchiveEntry(entry);
         tos.closeArchiveEntry();
         tos.finish();
         tos.close();
         verifyArchive(baos.toByteArray(), 1);
     }

     public void testDataWrittenButEntryOpenAtFinish() throws IOException {
         TarArchiveEntry entry = new TarArchiveEntry("e.txt");
         byte[] data = "abcdef".getBytes("UTF-8");
         entry.setSize(100);
         tos.putArchiveEntry(entry);
         tos.write(data);
         tos.closeArchiveEntry();
         tos.finish();
         TarArchiveInputStream tin = new TarArchiveInputStream(new
ByteArrayInputStream(baos.toByteArray()));
         TarArchiveEntry readEntry = (TarArchiveEntry) tin.getNextEntry();
         assertNotNull(readEntry);
         assertEquals(100, readEntry.getSize());
         byte[] readBuf = new byte[100];
         int total = 0;
         int n;
         while (total < 100 && (n = tin.read(readBuf, total, 100 - total)) != -1) {
             total += n;
         }
         assertEquals(100, total);
         for (int i = 0; i < 6; i++) {
             assertEquals(data[i], readBuf[i]);
         }
         for (int i = 6; i < 100; i++) {
             assertEquals(0, readBuf[i]);
         }
         assertNull(tin.getNextEntry());
         tin.close();
     }

     public void testMultipleEntriesWithTrailingOpen() throws IOException {
         TarArchiveEntry e1 = new TarArchiveEntry("first.txt");
         byte[] d1 = "first".getBytes("UTF-8");
         e1.setSize(d1.length);
         tos.putArchiveEntry(e1);
         tos.write(d1);
         tos.closeArchiveEntry();

         TarArchiveEntry e2 = new TarArchiveEntry("second.txt");
         byte[] d2 = "second".getBytes("UTF-8");
         e2.setSize(d2.length);
         tos.putArchiveEntry(e2);
         tos.write(d2);
         tos.closeArchiveEntry();
         tos.finish();
         verifyArchive(baos.toByteArray(), 2);
     }

     public void testEmptyArchiveFinish() throws IOException {
         tos.finish();
         byte[] buf = baos.toByteArray();
         assertEquals(1024, buf.length);
         for (int i = 0; i < buf.length; i++) {
             assertEquals(0, buf[i]);
         }
     }

     public void testWriteExceedingSize() throws IOException {
         TarArchiveEntry entry = new TarArchiveEntry("small.txt");
         entry.setSize(10);
         tos.putArchiveEntry(entry);
         try {
             tos.write(new byte[11]);
             fail("Should have thrown IOException");
         } catch (IOException e) {
             // expected
         }
     }

     public void testCloseArchiveEntryPrematurely() throws IOException {
         TarArchiveEntry entry = new TarArchiveEntry("prem.txt");
         entry.setSize(100);
         tos.putArchiveEntry(entry);
         tos.write(new byte[50]);
         try {
             tos.closeArchiveEntry();
             fail("Should have thrown IOException");
         } catch (IOException e) {
             assertTrue(e.getMessage().contains("closed at"));
         }
     }

     public void testNormalWriteAndClose() throws IOException {
         TarArchiveEntry entry = new TarArchiveEntry("normal.txt");
         byte[] data = "normal data".getBytes("UTF-8");
         entry.setSize(data.length);
         tos.putArchiveEntry(entry);
         tos.write(data);
         tos.closeArchiveEntry();
         tos.close();
         verifyArchive(baos.toByteArray(), 1);
     }

     private void verifyArchive(byte[] archiveData, int expectedEntries) throws IOException {
         TarArchiveInputStream tin = new TarArchiveInputStream(new
ByteArrayInputStream(archiveData));
         int count = 0;
         TarArchiveEntry entry;
         while ((entry = (TarArchiveEntry) tin.getNextEntry()) != null) {
             count++;
             byte[] buf = new byte[512];
             long remaining = entry.getSize();
             while (remaining > 0) {
                 int toRead = (int) Math.min(remaining, buf.length);
                 int r = tin.read(buf, 0, toRead);
                 if (r == -1) break;
                 remaining -= r;
             }
         }
         assertEquals(expectedEntries, count);
         tin.close();
     }
 }
