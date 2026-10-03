package org.apache.commons.compress.archivers.zip;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Test;

public class ZipArchiveEntryBackslashNameTest {

    @Test
    public void inputStreamNormalizesBackslashesAndRetainsRawName() throws Exception {
        final String rawName = "\u00e4\\\u00fc.txt";
        final File archive = createArchive(rawName);
        try {
            ZipArchiveInputStream in = new ZipArchiveInputStream(new FileInputStream(archive));
            try {
                ZipArchiveEntry entry = in.getNextZipEntry();

                assertNotNull(entry);
                assertEquals("\u00e4/\u00fc.txt", entry.getName());
                assertArrayEquals(rawName.getBytes("UTF-8"), entry.getRawName());
                assertFalse(entry.isDirectory());
            } finally {
                in.close();
            }
        } finally {
            archive.delete();
        }
    }

    @Test
    public void inputStreamTreatsFatBackslashTerminatedNameAsDirectory() throws Exception {
        final File archive = createArchive("\u00e4\\");
        try {
            ZipArchiveInputStream in = new ZipArchiveInputStream(new FileInputStream(archive));
            try {
                ZipArchiveEntry entry = in.getNextZipEntry();

                assertNotNull(entry);
                assertEquals("\u00e4/", entry.getName());
                assertTrue(entry.isDirectory());
            } finally {
                in.close();
            }
        } finally {
            archive.delete();
        }
    }

    @Test
    public void zipFileLooksUpFatBackslashEntryUsingNormalizedNameOnly() throws Exception {
        final String rawName = "\u00e4\\\u00fc.txt";
        final File archive = createArchive(rawName);
        try {
            ZipFile zipFile = new ZipFile(archive);
            try {
                ZipArchiveEntry normalized = zipFile.getEntry("\u00e4/\u00fc.txt");

                assertNotNull(normalized);
                assertEquals("\u00e4/\u00fc.txt", normalized.getName());
                assertArrayEquals(rawName.getBytes("UTF-8"), normalized.getRawName());
                assertNull(zipFile.getEntry(rawName));
            } finally {
                zipFile.close();
            }
        } finally {
            archive.delete();
        }
    }

    private File createArchive(String entryName) throws Exception {
        File archive = File.createTempFile("compress-backslash-name-", ".zip");
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(archive));
        try {
            out.putNextEntry(new ZipEntry(entryName));
            out.write(1);
            out.closeEntry();
        } finally {
            out.close();
        }
        return archive;
    }

@Test
public void stringConstructorNormalizesFatBackslashes() {
    ZipArchiveEntry entry = new ZipArchiveEntry("\u00e4\\\u00fc.txt");

    assertEquals("\u00e4/\u00fc.txt", entry.getName());
    assertFalse(entry.isDirectory());
}

@Test
public void zipEntryConstructorCopiesMetadataWithoutExtraData() throws Exception {
    java.util.zip.ZipEntry source = new java.util.zip.ZipEntry("stored.txt");
    source.setMethod(java.util.zip.ZipEntry.STORED);
    source.setSize(7);

    ZipArchiveEntry entry = new ZipArchiveEntry(source);

    assertEquals("stored.txt", entry.getName());
    assertEquals(java.util.zip.ZipEntry.STORED, entry.getMethod());
    assertEquals(7, entry.getSize());
    assertEquals(0, entry.getLocalFileDataExtra().length);
}

@Test
public void fileConstructorAppendsDirectorySeparatorAndSetsFileSize() throws Exception {
    java.io.File file = null;
    java.io.File directory = null;
    try {
        file = java.io.File.createTempFile("zip-entry", ".tmp");
        ZipArchiveEntry fileEntry = new ZipArchiveEntry(file, "file.txt");

        assertEquals("file.txt", fileEntry.getName());
        assertEquals(file.length(), fileEntry.getSize());

        directory = java.io.File.createTempFile("zip-entry", ".dir");
        assertTrue(directory.delete());
        assertTrue(directory.mkdir());

        ZipArchiveEntry directoryEntry = new ZipArchiveEntry(directory, "directory");

        assertEquals("directory/", directoryEntry.getName());
        assertTrue(directoryEntry.isDirectory());
    } finally {
        if (file != null) {
            file.delete();
        }
        if (directory != null) {
            directory.delete();
        }
    }
}
}
