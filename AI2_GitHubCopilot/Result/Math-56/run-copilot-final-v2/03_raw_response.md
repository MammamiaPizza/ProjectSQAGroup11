package org.apache.commons.math.util;

import static org.junit.Assert.*;

import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.exception.OutOfRangeException;
import org.junit.Test;

/**

 - Tests for {@link MultidimensionalCounter}.
  */
 public class MultidimensionalCounterTest {
  @Test
  public void testOneDimensional() {
  MultidimensionalCounter counter = new MultidimensionalCounter(5);
  assertEquals(1, counter.getDimension());
  assertEquals(5, counter.getSize());
  for (int i = 0; i < counter.getSize(); i++) {
      assertEquals(i, counter.getCount(i));
      assertArrayEquals(new int[] { i }, counter.getCounts(i));
  }
  MultidimensionalCounter.Iterator iter = counter.iterator();
  int expected = 0;
  while (iter.hasNext()) {
      int unidimensional = iter.next();
      assertEquals(expected, unidimensional);
      assertEquals(expected, iter.getCount());
      assertArrayEquals(new int[] { expected }, iter.getCounts());
      expected++;
  }
  assertEquals(5, expected);
  }
  @Test
  public void testGetCountBasic() {
  MultidimensionalCounter counter = new MultidimensionalCounter(2, 3, 4);
  assertEquals(0, counter.getCount(0, 0, 0));
  assertEquals(1, counter.getCount(0, 0, 1));
  assertEquals(2, counter.getCount(0, 0, 2));
  assertEquals(3, counter.getCount(0, 0, 3));
  assertEquals(4, counter.getCount(0, 1, 0));
  assertEquals(8, counter.getCount(0, 2, 0));
  assertEquals(12, counter.getCount(1, 0, 0));
  assertEquals(23, counter.getCount(1, 2, 3)); // from Javadoc example
  }
  @Test
  public void testGetCountsCorrectnessTrigger() {
  // Sizes {3,4} exposed the original bug (MATH-552):
  // The last-coordinate computation in getCounts was wrong.
  MultidimensionalCounter counter = new MultidimensionalCounter(3, 4);
  assertArrayEquals(new int[] { 0, 3 }, counter.getCounts(3));
  assertArrayEquals(new int[] { 2, 3 }, counter.getCounts(11));
  }
  @Test
  public void testIterationConsistency2D_3x4() {
  checkIterationConsistency(3, 4);
  }
  @Test
  public void testIterationConsistency2D_4x3() {
  checkIterationConsistency(4, 3);
  }
  @Test
  public void testIterationConsistency2D_2x2() {
  checkIterationConsistency(2, 2);
  }
  @Test
  public void testIterationConsistency3D() {
  checkIterationConsistency(2, 3, 4);
  }
  @Test(expected = OutOfRangeException.class)
  public void testGetCountsIndexTooLow() {
  new MultidimensionalCounter(3, 4).getCounts(-1);
  }
  @Test(expected = OutOfRangeException.class)
  public void testGetCountsIndexTooHigh() {
  new MultidimensionalCounter(3, 4).getCounts(12); // totalSize = 12, max valid index = 11
  }
  @Test(expected = DimensionMismatchException.class)
  public void testGetCountDimensionMismatch() {
  new MultidimensionalCounter(2, 3).getCount(1); // only one argument, need two
  }
  @Test(expected = OutOfRangeException.class)
  public void testGetCountOutOfRangeLow() {
  new MultidimensionalCounter(3, 4).getCount(-1, 0);
  }
  @Test(expected = OutOfRangeException.class)
  public void testGetCountOutOfRangeHigh() {
  new MultidimensionalCounter(3, 4).getCount(3, 0); // first dimension size is 3, max index 2
  }
  @Test(expected = NotStrictlyPositiveException.class)
  public void testConstructorZeroSize() {
  new MultidimensionalCounter(3, 0);
  }
  @Test(expected = NotStrictlyPositiveException.class)
  public void testConstructorNegativeSize() {
  new MultidimensionalCounter(-2);
  }
  /**
  - Iterates over the entire counter and verifies that
  - {@code getCounts(getCount(c)) == c} and that the reverse mapping is consistent.
    */
   private void checkIterationConsistency(int... sizes) {
   MultidimensionalCounter counter = new MultidimensionalCounter(sizes);
   MultidimensionalCounter.Iterator iter = counter.iterator();
   while (iter.hasNext()) {
   int unidimensional = iter.next();
   int[] countsFromIterator = iter.getCounts();
   // Verify that the outer getCounts yields the same as the iterator
   int[] countsFromOuter = counter.getCounts(unidimensional);
   assertArrayEquals(
           "Mismatch at index " + unidimensional + " for sizes " +

java.util.Arrays.toString(sizes),
                    countsFromIterator, countsFromOuter);

         // Verify that we can round-trip: outer getCount -> back to unidimensional
         int backToUnidimensional = counter.getCount(countsFromIterator);
         assertEquals("Round-trip failed at index " + unidimensional,
                 unidimensional, backToUnidimensional);
     }
 }

}