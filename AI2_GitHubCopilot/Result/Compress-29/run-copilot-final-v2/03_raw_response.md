package org.apache.commons.compress.archivers;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
import org.apache.commons.compress.archivers.dump.DumpArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.junit.Test;

public class EncodingTest {

 private static final String UTF8 = "UTF-8";
 private static final String ISO8859_1 = "ISO-8859-1";

 private byte[] createCpioArchive(String entryName, String encoding) throws IOException {
     ByteArrayOutputStream baos = new ByteArrayOutputStream();
     CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos, encoding);
     CpioArchiveEntry entry = new CpioArchiveEntry(entryName);
     entry.setSize(0);
     out.putArchiveEntry(entry);
     out.closeArchiveEntry();
     out.close();
     return baos.toByteArray();
 }

 private String readFirstCpioName(byte[] data, String encoding) throws IOException {
     CpioArchiveInputStream in = new CpioArchiveInputStream(new ByteArrayInputStream(data),

encoding);
        ArchiveEntry entry = in.getNextEntry();
        in.close();
        return entry != null ? entry.getName() : null;
    }

 @Test
 public void testFactoryConstructorStoresEncoding() {
     ArchiveStreamFactory factory = new ArchiveStreamFactory(UTF8);
     assertEquals(UTF8, factory.getEntryEncoding());
 }

 @Test
 public void testFactoryConstructorNullEncoding() {
     ArchiveStreamFactory factory = new ArchiveStreamFactory(null);
     assertNull(factory.getEntryEncoding());
 }

 @Test
 public void testFactoryDefaultConstructor() {
     ArchiveStreamFactory factory = new ArchiveStreamFactory();
     assertNull(factory.getEntryEncoding());
 }

 @Test
 public void testFactorySetEntryEncoding() {
     ArchiveStreamFactory factory = new ArchiveStreamFactory();
     factory.setEntryEncoding(ISO8859_1);
     assertEquals(ISO8859_1, factory.getEntryEncoding());
     factory.setEntryEncoding(null);
     assertNull(factory.getEntryEncoding());
 }

 @Test
 public void testCpioRoundTripUtf8() throws Exception {
     String name = "f\u00e4\u00e7il\u00e9-\u00dc\u00f1\u00ef\u00e7\u00f6d\u00eb";
     byte[] data = createCpioArchive(name, UTF8);
     String result = readFirstCpioName(data, UTF8);
     assertEquals(name, result);
 }

 @Test
 public void testCpioRoundTripIso88591() throws Exception {
     String name = "f\u00e4\u00e7il\u00e9";
     byte[] data = createCpioArchive(name, ISO8859_1);
     String result = readFirstCpioName(data, ISO8859_1);
     assertEquals(name, result);
 }

 @Test
 public void testCpioInputStreamEncodingConstructor() throws Exception {
     byte[] data = createCpioArchive("test", UTF8);
     CpioArchiveInputStream in = new CpioArchiveInputStream(new ByteArrayInputStream(data),

UTF8);
        assertNotNull(in);
        ArchiveEntry entry = in.getNextEntry();
        assertEquals("test", entry.getName());
        in.close();
    }

 @Test
 public void testCpioOutputStreamEncodingConstructor() throws Exception {
     ByteArrayOutputStream baos = new ByteArrayOutputStream();
     CpioArchiveOutputStream out = new CpioArchiveOutputStream(baos, UTF8);
     assertNotNull(out);
     CpioArchiveEntry entry = new CpioArchiveEntry("test");
     entry.setSize(0);
     out.putArchiveEntry(entry);
     out.closeArchiveEntry();
     out.close();
     assertTrue(baos.size() > 0);
 }

 @Test
 public void testFactoryEncodingPropagation() throws Exception {
     String name = "f\u00e4\u00e7il\u00e9";
     byte[] data = createCpioArchive(name, UTF8);
     ArchiveStreamFactory factory = new ArchiveStreamFactory(UTF8);
     ArchiveInputStream in = factory.createArchiveInputStream(new ByteArrayInputStream(data));
     ArchiveEntry entry = in.getNextEntry();
     assertEquals(name, entry.getName());
     in.close();
 }

 @Test
 public void testTarRoundTripUtf8() throws Exception {
     String name = "t\u00e4r-\u00e9ntry";
     ByteArrayOutputStream baos = new ByteArrayOutputStream();
     TarArchiveOutputStream out = new TarArchiveOutputStream(baos, UTF8);
     TarArchiveEntry entry = new TarArchiveEntry(name);
     entry.setSize(0);
     out.putArchiveEntry(entry);
     out.closeArchiveEntry();
     out.close();
     byte[] data = baos.toByteArray();
     TarArchiveInputStream in = new TarArchiveInputStream(new ByteArrayInputStream(data), UTF8);
     ArchiveEntry readEntry = in.getNextEntry();
     assertEquals(name, readEntry.getName());
     in.close();
 }

 @Test
 public void testDumpInputStreamEncodingConstructor() {
     try {
         new DumpArchiveInputStream(new ByteArrayInputStream(new byte[0]), UTF8);
         fail("Expected ArchiveException for invalid dump data");
     } catch (ArchiveException e) {
         // expected
     }
 }

 @Test
 public void testZipInputStreamEncodingConstructor() {
     ZipArchiveInputStream zis = new ZipArchiveInputStream(new ByteArrayInputStream(new

byte[0]), UTF8);
        assertNotNull(zis);
    }
}