@Test
public void testOutOfRangeNumericEntityIsLeftUnchanged() {
    final NumericEntityUnescaper unescaper = new NumericEntityUnescaper();
    org.junit.Assert.assertEquals("&#2147483648;", unescaper.translate("&#2147483648;"));
}