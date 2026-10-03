package org.apache.commons.compress.archivers.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipFile;

import junit.framework.TestCase;

import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveInputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;

public class ArchiveOutputStreamFinalizationTest extends TestCase {

    public void testZipCloseWritesReadableCentralDirectory() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ZipArchiveOutputStream out = new ZipArchiveOutputStream(bytes);

        ZipArchiveEntry entry = new ZipArchiveEntry("directory/file.txt");
        out.putArchiveEntry(entry);
        out.write("zip-content".getBytes("UTF-8"));
        out.closeArchiveEntry();
        out.close();

        File file = writeToTempFile(bytes.toByteArray());
        try {
            ZipFile zip = new ZipFile(file);
            try {
                assertZipEntry(zip, "directory/file.txt", "zip-content");
            } finally {
                zip.close();
            }
        } finally {
            file.delete();
        }
    }

    public void testZipFinishWritesAllCentralDirectoryEntries() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ZipArchiveOutputStream out = new ZipArchiveOutputStream(bytes);

        out.putArchiveEntry(new ZipArchiveEntry("first.txt"));
        out.write("first".getBytes("UTF-8"));
        out.closeArchiveEntry();

        out.putArchiveEntry(new ZipArchiveEntry("second.txt"));
        out.write("second".getBytes("UTF-8"));
        out.closeArchiveEntry();

        out.finish();
        out.close();

        File file = writeToTempFile(bytes.toByteArray());
        try {
            ZipFile zip = new ZipFile(file);
            try {
                assertEquals(2, zip.size());
                assertZipEntry(zip, "first.txt", "first");
                assertZipEntry(zip, "second.txt", "second");
            } finally {
                zip.close();
            }
        } finally {
            file.delete();
        }
    }

    public void testZipClosePreservesUtf8EntryNameInCentralDirectory() throws Exception {
        String name = "données/über-文件.txt";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ZipArchiveOutputStream out = new ZipArchiveOutputStream(bytes);

        out.putArchiveEntry(new ZipArchiveEntry(name));
        out.write("unicode".getBytes("UTF-8"));
        out.closeArchiveEntry();
        out.close();

        File file = writeToTempFile(bytes.toByteArray());
        try {
            ZipFile zip = new ZipFile(file);
            try {
                assertZipEntry(zip, name, "unicode");
            } finally {
                zip.close();
            }
        } finally {
            file.delete();
        }
    }

    public void testJarCloseWritesReadableCentralDirectory() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        JarArchiveOutputStream out = new JarArchiveOutputStream(bytes);

        out.putArchiveEntry(new ZipArchiveEntry("META-INF/test.txt"));
        out.write("jar-content".getBytes("UTF-8"));
        out.closeArchiveEntry();
        out.close();

        File file = writeToTempFile(bytes.toByteArray());
        try {
            ZipFile jar = new ZipFile(file);
            try {
                assertZipEntry(jar, "META-INF/test.txt", "jar-content");
            } finally {
                jar.close();
            }
        } finally {
            file.delete();
        }
    }

    public void testTarCloseFinishesArchiveWithReadableEntry() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        TarArchiveOutputStream out = new TarArchiveOutputStream(bytes);
        TarArchiveEntry entry = new TarArchiveEntry("file.txt");
        byte[] content = "tar-content".getBytes("UTF-8");
        entry.setSize(content.length);

        out.putArchiveEntry(entry);
        out.write(content);
        out.closeArchiveEntry();
        out.close();

        TarArchiveInputStream in =
            new TarArchiveInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        try {
            TarArchiveEntry read = in.getNextTarEntry();
            assertNotNull(read);
            assertEquals("file.txt", read.getName());
            assertEquals("tar-content", readString(in));
            assertNull(in.getNextTarEntry());
        } finally {
            in.close();
        }
    }

    public void testCpioCloseFinishesArchiveWithReadableEntry() throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        CpioArchiveOutputStream out = new CpioArchiveOutputStream(bytes);
        CpioArchiveEntry entry = new CpioArchiveEntry("file.txt");
        byte[] content = "cpio-content".getBytes("UTF-8");
        entry.setSize(content.length);

        out.putArchiveEntry(entry);
        out.write(content);
        out.closeArchiveEntry();
        out.close();

        CpioArchiveInputStream in =
            new CpioArchiveInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        try {
            CpioArchiveEntry read = in.getNextCPIOEntry();
            assertNotNull(read);
            assertEquals("file.txt", read.getName());
            assertEquals("cpio-content", readString(in));
            assertNull(in.getNextCPIOEntry());
        } finally {
            in.close();
        }
    }

    private File writeToTempFile(byte[] data) throws Exception {
        File file = File.createTempFile("compress-finalization", ".zip");
        FileOutputStream out = new FileOutputStream(file);
        try {
            out.write(data);
        } finally {
            out.close();
        }
        return file;
    }

    private void assertZipEntry(ZipFile zip, String name, String expected)
        throws Exception {
        java.util.zip.ZipEntry entry = zip.getEntry(name);
        assertNotNull(entry);
        InputStream in = zip.getInputStream(entry);
        try {
            assertEquals(expected, readString(in));
        } finally {
            in.close();
        }
    }

    private String readString(InputStream in) throws Exception {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[128];
        int read;
        while ((read = in.read(buffer)) != -1) {
            result.write(buffer, 0, read);
        }
        return new String(result.toByteArray(), "UTF-8");
    }
}