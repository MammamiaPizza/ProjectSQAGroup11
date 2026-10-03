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
}