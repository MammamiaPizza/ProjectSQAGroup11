@Test
public void testIsArrayZeroReturnsFalseWhenNonZeroElementAtStart() {
    assertFalse(ArchiveUtils.isArrayZero(new byte[]{1, 0, 0}, 3));
}

@Test
public void testIsArrayZeroReturnsTrueWhenAllZeros() {
    assertTrue(ArchiveUtils.isArrayZero(new byte[]{0, 0, 0}, 3));
}

@Test
public void testIsEqualWithIgnoreTrailingNullsFirstLongerWithZeros() {
    assertTrue(ArchiveUtils.isEqual(new byte[]{1, 2, 0}, new byte[]{1, 2}, true));
}

@Test
public void testIsEqualSimpleArraysWithoutIgnoreTrailingNulls() {
    assertTrue(ArchiveUtils.isEqual(new byte[]{1, 2, 3}, new byte[]{1, 2, 3}));
}