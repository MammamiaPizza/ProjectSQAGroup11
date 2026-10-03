package org.apache.commons.compress.archivers;

 import static org.junit.Assert.*;

 import java.io.ByteArrayInputStream;
 import java.io.InputStream;
 import java.io.IOException;

 import org.junit.Before;
 import org.junit.Test;

 import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
 import org.apache.commons.compress.archivers.ar.ArArchiveInputStream;
 import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
 import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

 public class ArchiveStreamFactoryTest {

     private ArchiveStreamFactory factory;

     @Before
     public void setUp() {
         factory = new ArchiveStreamFactory();
     }

     @Test(expected = IllegalArgumentException.class)
     public void nullStreamThrowsIllegalArgumentException() throws ArchiveException {
         factory.createArchiveInputStream(null);
     }

     @Test(expected = IllegalArgumentException.class)
     public void streamWithoutMarkThrowsIllegalArgumentException() throws ArchiveException {
         InputStream noMark = new ByteArrayInputStream(new byte[0]) {
             @Override
             public boolean markSupported() {
                 return false;
             }
         };
         factory.createArchiveInputStream(noMark);
     }

     @Test(expected = ArchiveException.class)
     public void shortTextFileIsNoTAR() throws ArchiveException {
         byte[] data = "a".getBytes();
         InputStream in = new ByteArrayInputStream(data);
         factory.createArchiveInputStream(in);
     }

     @Test(expected = ArchiveException.class)
     public void emptyStreamThrowsArchiveException() throws ArchiveException {
         byte[] data = new byte[0];
         InputStream in = new ByteArrayInputStream(data);
         factory.createArchiveInputStream(in);
     }

     @Test(expected = ArchiveException.class)
     public void binaryGarbageThrowsArchiveException() throws ArchiveException {
         byte[] data = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0a };
         InputStream in = new ByteArrayInputStream(data);
         factory.createArchiveInputStream(in);
     }

     @Test(expected = ArchiveException.class)
     public void truncatedZipSignatureThrowsArchiveException() throws ArchiveException {
         byte[] data = new byte[] { 'P', 'K' };
         InputStream in = new ByteArrayInputStream(data);
         factory.createArchiveInputStream(in);
     }

     @Test(expected = ArchiveException.class)
     public void truncatedArSignatureThrowsArchiveException() throws ArchiveException {
         byte[] data = "!<arc".getBytes();
         InputStream in = new ByteArrayInputStream(data);
         factory.createArchiveInputStream(in);
     }

     @Test
     public void validZipIsDetected() throws ArchiveException {
         byte[] data = new byte[] { 'P', 'K', 0x03, 0x04 };
         InputStream in = new ByteArrayInputStream(data);
         ArchiveInputStream ais = factory.createArchiveInputStream(in);
         assertTrue("Should be a ZipArchiveInputStream", ais instanceof ZipArchiveInputStream);
     }

     @Test
     public void validArIsDetected() throws ArchiveException {
         byte[] data = new byte[] { 0x21, 0x3c, 0x61, 0x72, 0x63, 0x68, 0x3e, 0x0a }; // "!<arch>\n"
         InputStream in = new ByteArrayInputStream(data);
         ArchiveInputStream ais = factory.createArchiveInputStream(in);
         assertTrue("Should be an ArArchiveInputStream", ais instanceof ArArchiveInputStream);
     }

     @Test
     public void validCpioIsDetected() throws ArchiveException {
         byte[] data = new byte[] { '0', '7', '0', '7', '0', '7' };
         InputStream in = new ByteArrayInputStream(data);
         ArchiveInputStream ais = factory.createArchiveInputStream(in);
         assertTrue("Should be a CpioArchiveInputStream", ais instanceof CpioArchiveInputStream);
     }

     @Test
     public void validTarIsDetected() throws ArchiveException {
         byte[] header = new byte[512];
         // name "test.txt"
         byte[] name = "test.txt".getBytes();
         System.arraycopy(name, 0, header, 0, name.length);
         // mode "000644 "
         byte[] mode = "000644 ".getBytes();
         System.arraycopy(mode, 0, header, 100, mode.length);
         // uid, gid left as zeros
         // size "00000000000\0"
         byte[] size = "00000000000\0".getBytes();
         System.arraycopy(size, 0, header, 124, size.length);
         // mtime "00000000000\0"
         byte[] mtime = "00000000000\0".getBytes();
         System.arraycopy(mtime, 0, header, 136, mtime.length);
         // chksum placeholder: 8 spaces
         for (int i = 0; i < 8; i++) {
             header[148 + i] = ' ';
         }
         // typeflag '0'
         header[156] = '0';
         // magic "ustar\0"
         byte[] magic = "ustar\0".getBytes();
         System.arraycopy(magic, 0, header, 257, magic.length);
         // version "00"
         header[263] = '0';
         header[264] = '0';
         // compute checksum
         long sum = 0;
         for (int i = 0; i < 512; i++) {
             sum += (header[i] & 0xff);
         }
         // format checksum as 6-digit octal, null, space
         String octalSum = Long.toOctalString(sum);
         while (octalSum.length() < 6) {
             octalSum = "0" + octalSum;
         }
         byte[] chksum = (octalSum + "\0 ").getBytes();
         System.arraycopy(chksum, 0, header, 148, chksum.length);
         InputStream in = new ByteArrayInputStream(header);
         ArchiveInputStream ais = factory.createArchiveInputStream(in);
         assertTrue("Should be a TarArchiveInputStream", ais instanceof TarArchiveInputStream);
     }
 }
