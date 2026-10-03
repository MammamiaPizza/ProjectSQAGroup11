package org.jfree.data.time.junit;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Date;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimePeriodValue;
import org.jfree.data.time.TimePeriodValues;

/**

 - Tests for {@link TimePeriodValues}, exercising bound-index methods
 - (especially {@link TimePeriodValues#getMaxMiddleIndex()} after
 - add/update/delete) to expose the known bug where the max-middle
 - comparison erroneously uses the min-middle item's middle value.
  */
 public class TimePeriodValuesTest {
  private static TimePeriod period(long startMs, long endMs) {
  return new SimpleTimePeriod(new Date(startMs), new Date(endMs));
  }
  // ---------------------------------------------------------------
  //  getMaxMiddleIndex
  // ---------------------------------------------------------------
  @Test
  public void testMaxMiddleIndexEmpty() {
  TimePeriodValues tpv = new TimePeriodValues("Test");
  assertEquals(-1, tpv.getMaxMiddleIndex());
  assertEquals(-1, tpv.getMinMiddleIndex());
  }
  @Test
  public void testMaxMiddleIndexNormal() {
  TimePeriodValues tpv = new TimePeriodValues("Test");
  tpv.add(period(0, 100), 1.0);      // middle 50
  tpv.add(period(100, 100), 2.0);    // middle 100
  tpv.add(period(200, 100), 3.0);    // middle 150
  assertEquals(2, tpv.getMaxMiddleIndex());
  }
  @Test
  public void testMaxMiddleIndexBugScenarios() {
  TimePeriodValues tpv1 = new TimePeriodValues("S1");
  tpv1.add(period(0, 200), 1.0);     // middle 100, index 0
  tpv1.add(period(100, 300), 2.0);   // middle 200, index 1  <-- real max
  tpv1.add(period(0, 100), 3.0);     // middle 50,  index 2
  tpv1.add(period(100, 200), 4.0);   // middle 150, index 3
  assertEquals("maxMiddle should be index 1", 1, tpv1.getMaxMiddleIndex());
  TimePeriodValues tpv2 = new TimePeriodValues("S2");
  tpv2.add(period(0, 200), 1.0);     // middle 100, index 0
  tpv2.add(period(100, 300), 2.0);   // middle 200, index 1  <-- real max
  tpv2.add(period(0, 50), 3.0);      // middle 25,  index 2
  tpv2.add(period(50, 150), 4.0);    // middle 100, index 3
  assertEquals("maxMiddle should be index 1", 1, tpv2.getMaxMiddleIndex());
  }
  @Test
  public void testMaxMiddleAfterDeleteRemoveMax() {
  TimePeriodValues tpv2 = new TimePeriodValues("DeleteTest");
  tpv2.add(period(0, 200), 1.0);      // middle 100  idx0
  tpv2.add(period(200, 200), 2.0);    // middle 300  idx1 <-- max
  tpv2.add(period(100, 200), 3.0);    // middle 200  idx2
  assertEquals("initial maxMiddle", 1, tpv2.getMaxMiddleIndex());
  tpv2.delete(1, 1);
  assertEquals("maxMiddle after removal", 0, tpv2.getMaxMiddleIndex());
  }
  @Test
  public void testMaxMiddleAfterUpdateValue() {
  TimePeriodValues tpv = new TimePeriodValues("Test");
  tpv.add(period(0, 200), 1.0);      // middle 100, idx0
  tpv.add(period(100, 300), 2.0);    // middle 200, idx1 <-- max
  tpv.add(period(0, 100), 3.0);      // middle 50,  idx2
  int before = tpv.getMaxMiddleIndex();
  tpv.update(1, 999.0);
  assertEquals("maxMiddle index unchanged after value update", before, tpv.getMaxMiddleIndex());
  }
  @Test
  public void testMaxMiddleAfterDeleteAll() {
  TimePeriodValues tpv = new TimePeriodValues("Test");
  tpv.add(period(0, 200), 1.0);
  tpv.add(period(100, 300), 2.0);
  assertEquals(1, tpv.getMaxMiddleIndex());
  tpv.delete(0, 1);
  assertEquals(-1, tpv.getMaxMiddleIndex());
  assertEquals(-1, tpv.getMinMiddleIndex());
  assertEquals(0, tpv.getItemCount());
  }
  // ---------------------------------------------------------------
  //  getMinMiddleIndex
  // ---------------------------------------------------------------
  @Test
  public void testMinMiddleIndexNormal() {
  TimePeriodValues tpv = new TimePeriodValues("Test");
  tpv.add(period(100, 300), 1.0);    // middle 200
  tpv.add(period(0, 200), 2.0);      // middle 100  <-- min
  tpv.add(period(200, 200), 3.0);    // middle 300
  assertEquals(1, tpv.getMinMiddleIndex());
  }
  @Test
  public void testMinMiddleIndexAfterDeleteMin() {
  TimePeriodValues tpv = new TimePeriodValues("Test");
  tpv.add(period(100, 300), 1.0);    // middle 200
  tpv.add(period(0, 100), 2.0);      // middle 50  <-- min
  tpv.add(period(200, 200), 3.0);    // middle 300
  assertEquals(1, tpv.getMinMiddleIndex());
  tpv.delete(1, 1);
  assertEquals(0, tpv.getMinMiddleIndex());
  }
  // ---------------------------------------------------------------
  //  getMinStartIndex / getMaxStartIndex
  // ---------------------------------------------------------------
  @Test
  public void testStartIndexBounds() {
  TimePeriodValues tpv = new TimePeriodValues("Test");
  tpv.add(period(100, 50), 1.0);   // start=100
  tpv.add(period(50, 50), 2.0);    // start=50   <-- min
  tpv.add(period(200, 50), 3.0);   // start=200  <-- max
  assertEquals(1, tpv.getMinStartIndex());
  assertEquals(2, tpv.getMaxStartIndex());
  }
  // ---------------------------------------------------------------
  //  getMinEndIndex / getMaxEndIndex
  // ---------------------------------------------------------------
  @Test
  public void testEndIndexBounds() {
  TimePeriodValues tpv = new TimePeriodValues("Test");
  tpv.add(period(0, 100), 1.0);    // end=100   <-- min
  tpv.add(period(0, 200), 2.0);    // end=200
  tpv.add(period(0, 300), 3.0);    // end=300   <-- max
  assertEquals(0, tpv.getMinEndIndex());
  assertEquals(2, tpv.getMaxEndIndex());
  }
  // ---------------------------------------------------------------
  //  Bonus: recalculateBounds via delete
  // ---------------------------------------------------------------
  @Test
  public void testMaxMiddleAfterDeleteNonMaxButTriggersBug() {
  TimePeriodValues tpv = new TimePeriodValues("Test");
  tpv.add(period(0, 200), 1.0);      // middle 100  idx0
  tpv.add(period(100, 300), 2.0);    // middle 200  idx1  <-- real max
  tpv.add(period(0, 50), 3.0);       // middle 25   idx2
  tpv.add(period(50, 150), 4.0);     // middle 100  idx3
  tpv.delete(2, 2);
  assertEquals("maxMiddle should remain 1 after removing low outlier",
          1, tpv.getMaxMiddleIndex());
  }

}
