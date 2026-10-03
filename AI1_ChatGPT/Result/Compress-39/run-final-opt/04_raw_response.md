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