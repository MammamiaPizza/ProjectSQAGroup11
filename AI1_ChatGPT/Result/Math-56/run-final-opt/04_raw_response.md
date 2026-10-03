@Test
public void testTwoDimensionalIterationMaintainsOuterDimensionAcrossRollover() {
    final org.apache.commons.math.util.MultidimensionalCounter counter =
        new org.apache.commons.math.util.MultidimensionalCounter(4, 3);
    final org.apache.commons.math.util.MultidimensionalCounter.Iterator iterator =
        counter.iterator();

    for (int index = 0; index < counter.getSize(); index++) {
        final int[] expected = new int[] { index / 3, index % 3 };

        org.junit.Assert.assertTrue(iterator.hasNext());
        org.junit.Assert.assertEquals(index, iterator.next().intValue());
        org.junit.Assert.assertArrayEquals(expected, iterator.getCounts());
        org.junit.Assert.assertEquals(expected[0], iterator.getCount(0));
        org.junit.Assert.assertEquals(expected[1], iterator.getCount(1));
    }

    org.junit.Assert.assertFalse(iterator.hasNext());
}

@Test
public void testDimensionSizesAreReportedDefensivelyAndStringified() {
    final org.apache.commons.math.util.MultidimensionalCounter counter =
        new org.apache.commons.math.util.MultidimensionalCounter(2, 3, 4);

    org.junit.Assert.assertEquals(3, counter.getDimension());
    org.junit.Assert.assertArrayEquals(new int[] { 2, 3, 4 }, counter.getSizes());
    org.junit.Assert.assertEquals("[2][3][4]", counter.toString());

    final int[] sizes = counter.getSizes();
    sizes[0] = 99;
    org.junit.Assert.assertArrayEquals(new int[] { 2, 3, 4 }, counter.getSizes());
}