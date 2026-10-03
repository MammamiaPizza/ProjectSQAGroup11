package org.apache.commons.compress.archivers.zip;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.junit.Test;

public class ZipArchiveInputStreamRegressionTest {

    @Test
    public void invalidLocalFileHeaderIsRejected() throws Exception {
        final byte[] invalidLocalHeader = new byte[30];
        invalidLocalHeader[0] = 'P';
        invalidLocalHeader[1] = 'K';
        invalidLocalHeader[2] = 3;
        invalidLocalHeader[3] = 5;
        final ZipArchiveInputStream input =
            new ZipArchiveInputStream(new ByteArrayInputStream(invalidLocalHeader));

        try {
            input.getNextZipEntry();
            fail("An invalid local file header must cause an IOException");
        } catch (final IOException expected) {
            // expected
        } finally {
            input.close();
        }
    }

    @Test
    public void readsNestedZipEntryAndTraversesItsContents() throws Exception {
        final byte[] payload = "nested archive payload".getBytes("UTF-8");
        final byte[] nestedZip = zipWithSingleEntry("inside.txt", payload);
        final byte[] outerZip = zipWithSingleEntry("nested.zip", nestedZip);

        final ZipArchiveInputStream outer =
            new ZipArchiveInputStream(new ByteArrayInputStream(outerZip));
        final ZipArchiveEntry outerEntry = outer.getNextZipEntry();

        assertNotNull(outerEntry);
        assertEquals("nested.zip", outerEntry.getName());
        final byte[] extractedNestedZip = readAll(outer);
        assertArrayEquals(nestedZip, extractedNestedZip);
        assertNull(outer.getNextZipEntry());
        outer.close();

        final ZipArchiveInputStream nested =
            new ZipArchiveInputStream(new ByteArrayInputStream(extractedNestedZip));
        final ZipArchiveEntry nestedEntry = nested.getNextZipEntry();

        assertNotNull(nestedEntry);
        assertEquals("inside.txt", nestedEntry.getName());
        assertArrayEquals(payload, readAll(nested));
        assertNull(nested.getNextZipEntry());
        nested.close();
    }

    @Test
    public void advancingToNextEntryDrainsUnreadPreviousEntry() throws Exception {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final ZipOutputStream zip = new ZipOutputStream(bytes);
        zip.putNextEntry(new ZipEntry("first.txt"));
        zip.write("first entry content".getBytes("UTF-8"));
        zip.closeEntry();
        zip.putNextEntry(new ZipEntry("second.txt"));
        zip.write("second entry content".getBytes("UTF-8"));
        zip.closeEntry();
        zip.close();

        final ZipArchiveInputStream input =
            new ZipArchiveInputStream(new ByteArrayInputStream(bytes.toByteArray()));

        final ZipArchiveEntry first = input.getNextZipEntry();
        assertNotNull(first);
        assertEquals("first.txt", first.getName());

        final ZipArchiveEntry second = input.getNextZipEntry();
        assertNotNull(second);
        assertEquals("second.txt", second.getName());
        assertArrayEquals("second entry content".getBytes("UTF-8"), readAll(input));
        assertNull(input.getNextZipEntry());
        input.close();
    }

    private static byte[] zipWithSingleEntry(final String name, final byte[] contents)
            throws IOException {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final ZipOutputStream zip = new ZipOutputStream(bytes);
        zip.putNextEntry(new ZipEntry(name));
        zip.write(contents);
        zip.closeEntry();
        zip.close();
        return bytes.toByteArray();
    }

    private static byte[] readAll(final ZipArchiveInputStream input) throws IOException {
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        final byte[] buffer = new byte[256];
        int read;
        while ((read = input.read(buffer)) != -1) {
            bytes.write(buffer, 0, read);
        }
        return bytes.toByteArray();
    }
}