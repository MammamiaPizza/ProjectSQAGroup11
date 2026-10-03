import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import org.apache.commons.compress.archivers.ar.ArArchiveInputStream;

public class ArArchiveInputStreamTest extends TestCase {

 private static final String GLOBAL_HEADER = "!<arch>\n";

 private byte[] buildArchive(String[][] entries) {
     // entries: each is {filename, size, data}
     // compute total length
     int total = GLOBAL_HEADER.length();
     for (String[] entry : entries) {
         String name = entry[0];
         int size = Integer.parseInt(entry[1]);
         total += 60 + size;
         if (size % 2 != 0) total += 1; // padding
     }
     byte[] buf = new byte[total];
     int pos = 0;
     System.arraycopy(GLOBAL_HEADER.getBytes(), 0, buf, pos, GLOBAL_HEADER.length());
     pos += GLOBAL_HEADER.length();
     for (String[] entry : entries) {
         String name = entry[0];
         int size = Integer.parseInt(entry[1]);
         byte[] data = entry[2].getBytes();
         // header fields
         writePaddedString(buf, pos, name, 16);
         pos += 16;
         writePaddedString(buf, pos, "1000000000", 12); // timestamp
         pos += 12;
         writePaddedString(buf, pos, "0", 6); // uid
         pos += 6;
         writePaddedString(buf, pos, "0", 6); // gid
         pos += 6;
         writePaddedString(buf, pos, "100644", 8); // mode
         pos += 8;
         writePaddedString(buf, pos, String.valueOf(size), 10);
         pos += 10;
         // trailer
         byte[] trailer = ArArchiveEntry.TRAILER.getBytes();
         System.arraycopy(trailer, 0, buf, pos, trailer.length);
         pos += trailer.length;
         // data
         System.arraycopy(data, 0, buf, pos, data.length);
         pos += data.length;
         // padding
         if (size % 2 != 0) {
             buf[pos++] = (byte) '\n';
         }
     }
     return buf;
 }

 private void writePaddedString(byte[] buf, int offset, String s, int len) {
     byte[] src = s.getBytes();
     int i = 0;
     for (; i < src.length && i < len; i++) {
         buf[offset + i] = src[i];
     }
     for (; i < len; i++) {
         buf[offset + i] = (byte) ' ';
     }
 }

 // ---- Tests ----

 public void testMatchesValidSignature() {
     byte[] sig = (GLOBAL_HEADER).getBytes();
     assertTrue(ArArchiveInputStream.matches(sig, sig.length));
 }

 public void testMatchesInvalidTooShort() {
     assertFalse(ArArchiveInputStream.matches(new byte[] { '!' }, 1));
 }

 public void testMatchesInvalidWrongBytes() {
     byte[] sig = (GLOBAL_HEADER).getBytes();
     sig[2] = 'X'; // change 'a' to 'X'
     assertFalse(ArArchiveInputStream.matches(sig, sig.length));
 }

 public void testEmptyArchive() throws IOException {
     byte[] archive = (GLOBAL_HEADER).getBytes();
     ArArchiveInputStream in = new ArArchiveInputStream(new ByteArrayInputStream(archive));
     assertNull(in.getNextArEntry());
     in.close();
 }

 public void testSingleEntry() throws IOException {
     byte[] archive = buildArchive(new String[][] {
             { "file.txt", "10", "0123456789" }
     });
     ArArchiveInputStream in = new ArArchiveInputStream(new ByteArrayInputStream(archive));
     ArArchiveEntry entry = in.getNextArEntry();
     assertNotNull(entry);
     assertEquals("file.txt", entry.getName());
     assertEquals(10, entry.getSize());
     // read data
     byte[] data = new byte[10];
     assertEquals(10, in.read(data));
     assertEquals("0123456789", new String(data));
     assertNull(in.getNextArEntry());
     in.close();
 }

 public void testMultipleEntries() throws IOException {
     byte[] archive = buildArchive(new String[][] {
             { "a.txt", "5", "hello" },
             { "b.bin", "3", "abc" }
     });
     ArArchiveInputStream in = new ArArchiveInputStream(new ByteArrayInputStream(archive));
     ArArchiveEntry e1 = in.getNextArEntry();
     assertNotNull(e1);
     assertEquals("a.txt", e1.getName());
     assertEquals(5, e1.getSize());
     ArArchiveEntry e2 = in.getNextArEntry();
     assertNotNull(e2);
     assertEquals("b.bin", e2.getName());
     assertEquals(3, e2.getSize());
     assertNull(in.getNextArEntry());
     in.close();
 }

 public void testZeroLengthEntry() throws IOException {
     byte[] archive = buildArchive(new String[][] {
             { "", "0", "" }
     });
     ArArchiveInputStream in = new ArArchiveInputStream(new ByteArrayInputStream(archive));
     ArArchiveEntry entry = in.getNextArEntry();
     assertNotNull(entry);
     assertEquals(0, entry.getSize());
     assertNull(in.getNextArEntry());
     in.close();
 }

 public void testMissingGlobalHeader() throws IOException {
     byte[] noHeader = "bad header".getBytes();
     ArArchiveInputStream in = new ArArchiveInputStream(new ByteArrayInputStream(noHeader));
     try {
         in.getNextArEntry();
         fail("Expected IOException");
     } catch (IOException e) {
         // expected
     }
     in.close();
 }

 public void testReadEntryDataPartialBuffer() throws IOException {
     byte[] archive = buildArchive(new String[][] {
             { "x", "8", "abcdefgh" }
     });
     ArArchiveInputStream in = new ArArchiveInputStream(new ByteArrayInputStream(archive));
     ArArchiveEntry entry = in.getNextArEntry();
     assertNotNull(entry);
     byte[] buf = new byte[4];
     int read = in.read(buf, 0, 4);
     assertEquals(4, read);
     assertEquals("abcd", new String(buf));
     read = in.read(buf, 0, 4);
     assertEquals(4, read);
     assertEquals("efgh", new String(buf));
     assertNull(in.getNextArEntry());
     in.close();
 }

 public void testCloseBeforeGetNextEntry() throws IOException {
     byte[] archive = buildArchive(new String[][] {
             { "f", "1", "a" }
     });
     ArArchiveInputStream in = new ArArchiveInputStream(new ByteArrayInputStream(archive));
     in.close();
     try {
         in.getNextArEntry();
         fail("Expected IOException after close");
     } catch (IOException e) {
         // expected
     }
 }

 public void testReadAfterClose() throws IOException {
     byte[] archive = buildArchive(new String[][] {
             { "f", "2", "ab" }
     });
     ArArchiveInputStream in = new ArArchiveInputStream(new ByteArrayInputStream(archive));
     in.close();
     try {
         in.read();
         fail("Expected IOException after close");
     } catch (IOException e) {
         // expected
     }
 }

 public void testAvailableBugMissesEntry() throws IOException {
     // Given a valid archive with one entry, if available() returns 0 after
     // the global header, the buggy code returns null prematurely.
     byte[] archive = buildArchive(new String[][] {
             { "onlyfile", "3", "abc" }
     });
     // Wrap in a stream that reports available == 0
     InputStream raw = new ByteArrayInputStream(archive);
     InputStream zeroAvailable = new InputStream() {
         public int read() throws IOException {
             return raw.read();
         }
         public int read(byte[] b, int off, int len) throws IOException {
             return raw.read(b, off, len);
         }
         public int available() {
             return 0; // always zero – triggers the bug
         }
         public void close() throws IOException {
             raw.close();
         }
     };
     ArArchiveInputStream in = new ArArchiveInputStream(zeroAvailable);
     ArArchiveEntry entry = in.getNextArEntry();
     // According to the AR format specification, the entry should be returned.
     // However, due to the bug (using available() to detect EOF), it returns null.
     assertNotNull("Expected an entry but got null – bug COMPRESS-11", entry);
     assertEquals("onlyfile", entry.getName());
     in.close();
 }

}