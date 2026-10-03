package org.apache.commons.compress.utils;

 import static org.junit.Assert.*;
 import org.junit.Test;
 import java.io.ByteArrayInputStream;
 import java.io.IOException;
 import java.util.zip.CRC32;
 import java.util.zip.Checksum;

 public class ChecksumCalculatingInputStreamTest {

     @Test(expected = NullPointerException.class)
     public void testClassInstantiationWithParameterBeingNullThrowsNullPointerExceptionOne() {
         new ChecksumCalculatingInputStream(null, new ByteArrayInputStream(new byte[0]));
     }

     @Test(expected = NullPointerException.class)
     public void testClassInstantiationWithParameterBeingNullThrowsNullPointerExceptionTwo() {
         new ChecksumCalculatingInputStream(new CRC32(), null);
     }

     @Test(expected = NullPointerException.class)
     public void testClassInstantiationWithParameterBeingNullThrowsNullPointerExceptionThree() {
         new ChecksumCalculatingInputStream(null, null);
     }

     @Test
     public void testReadSingleByte() throws IOException {
         byte[] data = {10, 20, 30};
         CRC32 expectedChecksum = new CRC32();
         expectedChecksum.update(data, 0, data.length);
         Checksum checksum = new CRC32();
         ChecksumCalculatingInputStream cis =
             new ChecksumCalculatingInputStream(checksum, new ByteArrayInputStream(data));
         assertEquals(10, cis.read());
         assertEquals(20, cis.read());
         assertEquals(30, cis.read());
         assertEquals(-1, cis.read());
         assertEquals(expectedChecksum.getValue(), cis.getValue());
     }

     @Test
     public void testReadByteArray() throws IOException {
         byte[] data = {1, 2, 3, 4, 5};
         CRC32 expectedChecksum = new CRC32();
         expectedChecksum.update(data, 0, data.length);
         Checksum checksum = new CRC32();
         ChecksumCalculatingInputStream cis =
             new ChecksumCalculatingInputStream(checksum, new ByteArrayInputStream(data));
         byte[] buf = new byte[5];
         int bytesRead = cis.read(buf);
         assertEquals(5, bytesRead);
         assertArrayEquals(data, buf);
         assertEquals(-1, cis.read());
         assertEquals(expectedChecksum.getValue(), cis.getValue());
     }

     @Test
     public void testReadByteArrayWithOffsetAndLength() throws IOException {
         byte[] data = {5, 10, 15, 20, 25};
         CRC32 expectedChecksum = new CRC32();
         expectedChecksum.update(data, 0, data.length);
         Checksum checksum = new CRC32();
         ChecksumCalculatingInputStream cis =
             new ChecksumCalculatingInputStream(checksum, new ByteArrayInputStream(data));
         byte[] buf = new byte[3];
         int bytesRead = cis.read(buf, 0, 2);
         assertEquals(2, bytesRead);
         assertEquals(5, buf[0]);
         assertEquals(10, buf[1]);
         bytesRead = cis.read(buf, 1, 2);
         assertEquals(2, bytesRead);
         assertEquals(15, buf[1]);
         assertEquals(20, buf[2]);
         bytesRead = cis.read(buf, 0, 3);
         assertEquals(1, bytesRead);
         assertEquals(25, buf[0]);
         assertEquals(-1, cis.read());
         assertEquals(expectedChecksum.getValue(), cis.getValue());
     }

     @Test
     public void testSkip() throws IOException {
         byte[] data = {10, 20, 30, 40};
         CRC32 expectedChecksum = new CRC32();
         expectedChecksum.update(data, 0, data.length);
         Checksum checksum = new CRC32();
         ChecksumCalculatingInputStream cis =
             new ChecksumCalculatingInputStream(checksum, new ByteArrayInputStream(data));
         long skipped = cis.skip(2);
         assertEquals(1, skipped);
         assertEquals(20, cis.read());
         skipped = cis.skip(10);
         assertEquals(1, skipped);
         assertEquals(40, cis.read());
         assertEquals(-1, cis.read());
         assertEquals(expectedChecksum.getValue(), cis.getValue());
     }

     @Test
     public void testGetValueBeforeAnyRead() throws IOException {
         Checksum checksum = new CRC32();
         ChecksumCalculatingInputStream cis =
             new ChecksumCalculatingInputStream(checksum, new ByteArrayInputStream(new byte[] {1,
2}));
         assertEquals(0, cis.getValue());
     }

     @Test
     public void testGetValueAfterPartialRead() throws IOException {
         byte[] data = {100, 50};
         CRC32 step1 = new CRC32();
         step1.update(100);
         long expectedMid = step1.getValue();
         step1.update(50);
         long expectedFinal = step1.getValue();
         Checksum checksum = new CRC32();
         ChecksumCalculatingInputStream cis =
             new ChecksumCalculatingInputStream(checksum, new ByteArrayInputStream(data));
         cis.read();
         assertEquals(expectedMid, cis.getValue());
         cis.read();
         assertEquals(expectedFinal, cis.getValue());
         assertEquals(-1, cis.read());
     }

     @Test
     public void testReadFromEmptyStream() throws IOException {
         Checksum checksum = new CRC32();
         ChecksumCalculatingInputStream cis =
             new ChecksumCalculatingInputStream(checksum, new ByteArrayInputStream(new byte[0]));
         assertEquals(-1, cis.read());
         assertEquals(0, cis.getValue());
         byte[] buf = new byte[5];
         assertEquals(-1, cis.read(buf));
         assertEquals(-1, cis.read(buf, 0, 2));
     }

     @Test
     public void testReadWithZeroLength() throws IOException {
         Checksum checksum = new CRC32();
         ChecksumCalculatingInputStream cis =
             new ChecksumCalculatingInputStream(checksum, new ByteArrayInputStream(new byte[] {1, 2,
3}));
         byte[] buf = new byte[5];
         int bytesRead = cis.read(buf, 0, 0);
         assertEquals(0, bytesRead);
         assertEquals(0, cis.getValue());
     }
 }
