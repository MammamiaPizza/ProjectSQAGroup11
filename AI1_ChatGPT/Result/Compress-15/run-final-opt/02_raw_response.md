package org.apache.commons.compress.archivers.zip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ZipArchiveEntryNullCommentEqualityTest {

    @Test
    public void nullAndEmptyCommentsAreEqualSymmetricallyAndHaveSameHashCode() {
        ZipArchiveEntry nullComment = new ZipArchiveEntry("entry");
        ZipArchiveEntry emptyComment = new ZipArchiveEntry("entry");
        emptyComment.setComment("");

        assertTrue(nullComment.equals(emptyComment));
        assertTrue(emptyComment.equals(nullComment));
        assertEquals(nullComment.hashCode(), emptyComment.hashCode());
    }

    @Test
    public void entriesWithEqualNonEmptyCommentsAreEqual() {
        ZipArchiveEntry first = new ZipArchiveEntry("entry");
        ZipArchiveEntry second = new ZipArchiveEntry("entry");
        first.setComment("comment");
        second.setComment("comment");

        assertTrue(first.equals(second));
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void entriesWithDifferentNonEmptyCommentsAreNotEqual() {
        ZipArchiveEntry first = new ZipArchiveEntry("entry");
        ZipArchiveEntry second = new ZipArchiveEntry("entry");
        first.setComment("first");
        second.setComment("second");

        assertFalse(first.equals(second));
        assertFalse(second.equals(first));
    }

    @Test
    public void entriesWithBothNullCommentsAreEqual() {
        ZipArchiveEntry first = new ZipArchiveEntry("entry");
        ZipArchiveEntry second = new ZipArchiveEntry("entry");

        assertTrue(first.equals(second));
        assertEquals(first.hashCode(), second.hashCode());
    }

    @Test
    public void entriesWithBothEmptyCommentsAreEqual() {
        ZipArchiveEntry first = new ZipArchiveEntry("entry");
        ZipArchiveEntry second = new ZipArchiveEntry("entry");
        first.setComment("");
        second.setComment("");

        assertTrue(first.equals(second));
        assertEquals(first.hashCode(), second.hashCode());
    }
}