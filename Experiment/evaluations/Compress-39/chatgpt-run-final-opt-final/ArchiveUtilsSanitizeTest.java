package org.apache.commons.compress;

import static org.junit.Assert.assertEquals;

import org.apache.commons.compress.utils.ArchiveUtils;
import org.junit.Test;

public class ArchiveUtilsSanitizeTest {

    @Test
    public void sanitizeLeavesPrintableTextUnchanged() {
        assertEquals("archive-file_123.txt", ArchiveUtils.sanitize("archive-file_123.txt"));
    }

    @Test
    public void sanitizeReplacesIsoControlCharacters() {
        assertEquals("a?b?c?d", ArchiveUtils.sanitize("a\u0000b\nc\u007Fd"));
    }

    @Test
    public void sanitizeReplacesCharactersFromSpecialsUnicodeBlock() {
        assertEquals("before?after", ArchiveUtils.sanitize("before\uFFFDafter"));
    }

    @Test
    public void sanitizeKeepsStringAtMaximumLength() {
        final String input = repeatedPattern(255);
        assertEquals(input, ArchiveUtils.sanitize(input));
        assertEquals(255, ArchiveUtils.sanitize(input).length());
    }

    @Test
    public void sanitizeShortensStringLongerThanMaximumLength() {
        final String input = repeatedPattern(256);
        final String sanitized = ArchiveUtils.sanitize(input);

        assertEquals(input.substring(0, 252) + "...", sanitized);
        assertEquals(255, sanitized.length());
    }

    @Test
    public void sanitizeTruncatesAfterReplacingControlsWithinRetainedPrefix() {
        final String input = "A\u0000" + repeatedPattern(300);
        final String sanitized = ArchiveUtils.sanitize(input);

        assertEquals("A?" + repeatedPattern(250) + "...", sanitized);
        assertEquals(255, sanitized.length());
    }

    private static String repeatedPattern(final int length) {
        final StringBuilder value = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            value.append((char) ('0' + (i % 10)));
        }
        return value.toString();
    }

@Test
public void isEqualCanIgnoreOnlyTrailingNullBytes() {
    final byte[] shorter = new byte[] { 1, 2 };
    final byte[] longerWithNull = new byte[] { 1, 2, 0 };
    final byte[] longerWithValue = new byte[] { 1, 2, 3 };

    org.junit.Assert.assertFalse(ArchiveUtils.isEqual(shorter, longerWithNull, false));
    org.junit.Assert.assertTrue(ArchiveUtils.isEqual(shorter, longerWithNull, true));
    org.junit.Assert.assertTrue(ArchiveUtils.isEqual(longerWithNull, shorter, true));
    org.junit.Assert.assertFalse(ArchiveUtils.isEqual(shorter, longerWithValue, true));
}

@Test
public void isArrayZeroChecksOnlyRequestedRange() {
    org.junit.Assert.assertTrue(ArchiveUtils.isArrayZero(new byte[] { 0, 0, 1 }, 2));
    org.junit.Assert.assertFalse(ArchiveUtils.isArrayZero(new byte[] { 0, 1, 0 }, 3));
}
}
