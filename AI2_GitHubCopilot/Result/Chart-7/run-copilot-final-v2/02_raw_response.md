package org.jfree.data.time.junit;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Date;
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
  return new TimePeriod(new Date(startMs), new Date(endMs));
  }
  // ---------------------------------------------------------------
  //  getMaxMiddleIndex
  // ---------------------------------------------------------------
  @Test
  public void testMaxMiddleIndexEmpty() {
  TimePeriodValues tpv = new TimePeriodValues("Test");
  // no items added — indices should be -1
  assertEquals(-1, tpv.getMaxMiddleIndex());
  assertEquals(-1, tpv.getMinMiddleIndex());
  }
  @Test
  public void testMaxMiddleIndexNormal() {
  TimePeriodValues tpv = new TimePeriodValues("Test");
  // middles: 50, 100, 150
  tpv.add(period(0, 100), 1.0);      // middle 50
  tpv.add(period(100, 100), 2.0);    // middle 100
  tpv.add(period(200, 100), 3.0);    // middle 150
  assertEquals(2, tpv.getMaxMiddleIndex()); // last item has largest middle
  }
  @Test
  public void testMaxMiddleIndexBugScenarios() {
  // Bug: updateBounds reads minMiddleIndex instead of maxMiddleIndex
  // for comparison, allowing a later item with merely average middle
  // to replace the true maximum.
  // Scenario 1: add items with middles 100, 200, 50, 150
  TimePeriodValues tpv1 = new TimePeriodValues("S1");
  tpv1.add(period(0, 200), 1.0);     // middle 100, index 0
  tpv1.add(period(100, 300), 2.0);   // middle 200, index 1  <-- real max
  tpv1.add(period(0, 100), 3.0);     // middle 50,  index 2
  tpv1.add(period(100, 200), 4.0);   // middle 150, index 3
  // real max-middle is index 1 (middle 200)
  assertEquals("maxMiddle should be index 1", 1, tpv1.getMaxMiddleIndex());
  // Scenario 2: add in monotonic order then a low then a medium
  TimePeriodValues tpv2 = new TimePeriodValues("S2");
  tpv2.add(period(0, 200), 1.0);     // middle 100, index 0
  tpv2.add(period(100, 300), 2.0);   // middle 200, index 1  <-- real max
  tpv2.add(period(0, 50), 3.0);      // middle 25,  index 2
  tpv2.add(period(50, 150), 4.0);    // middle 100, index 3
  // index 3's middle (100) > minMiddle's middle (25), triggering bug
  assertEquals("maxMiddle should be index 1", 1, tpv2.getMaxMiddleIndex());
  }
  @Test
  public void testMaxMiddleAfterDeleteRemoveMax() {
  TimePeriodValues tpv = new TimePeriodValues("Test");
  tpv.add(period(0, 200), 1.0);      // middle 100  idx0
  tpv.add(period(200, 200), 2.0);    // middle 300  idx1  <-- max
  tpv.add(period(400, 100), 3.0);    // middle 450  idx2  wait, not -- we want distinct
  // Let's make clear: middles 100,300,200
  TimePeriodValues tpv2 = new TimePeriodValues("DeleteTest");
  tpv2.add(period(0, 200), 1.0);      // middle 100  idx0
  tpv2.add(period(200, 200), 2.0);    // middle 300  idx1 <-- max
  tpv2.add(period(100, 200), 3.0);    // middle 200  idx2
  assertEquals("initial maxMiddle", 1, tpv2.getMaxMiddleIndex());
  // delete the max-middle item
  tpv2.delete(1, 1);
  // now remaining: idx0 (100) and idx1 (formerly 2, now idx1 with 200)
  assertEquals("maxMiddle after removal", 0, tpv2.getMaxMiddleIndex());
  }
  @Test
  public void testMaxMiddleAfterUpdateValue() {
  TimePeriodValues tpv = new TimePeriodValues("Test");
  tpv.add(period(0, 200), 1.0);      // middle 100, idx0
  tpv.add(period(100, 300), 2.0);    // middle 200, idx1 <-- max
  tpv.add(period(0, 100), 3.0);      // middle 50,  idx2
  int before = tpv.getMaxMiddleIndex();
  tpv.update(1, 999.0); // value-only change, period unchanged
  assertEquals("maxMiddle index unchanged after value update", before, tpv.getMaxMiddleIndex());
  }
  @Test
  public void testMaxMiddleAfterDeleteAll() {
  TimePeriodValues tpv = new TimePeriodValues("Test");
  tpv.add(period(0, 200), 1.0);
  tpv.add(period(100, 300), 2.0);
  assertEquals(1, tpv.getMaxMiddleIndex());
  tpv.delete(0, 1); // remove all
  assertEquals(-1, tpv.getMaxMiddleIndex());
  assertEquals(-1, tpv.getMinMiddleIndex());
  assertEquals(0, tpv.getItemCount());
  }
  // ---------------------------------------------------------------
  //  getMinMiddleIndex  (sanity — these should NOT be affected)
  // ---------------------------------------------------------------
  @Test
  public void testMinMiddleIndexNormal() {
  TimePeriodValues tpv = new TimePeriodValues("Test");
  tpv.add(period(100, 300), 1.0);    // middle 200
  tpv.add(period(0, 200), 2.0);      // middle 100  <-- min
  tpv.add(period(200, 200), 3.0);    // middle 300
  assertEquals(1, tpv.getMinMiddleIndex()); // index 1 has smallest middle
  }
  @Test
  public void testMinMiddleIndexAfterDeleteMin() {
  TimePeriodValues tpv = new TimePeriodValues("Test");
  tpv.add(period(100, 300), 1.0);    // middle 200
  tpv.add(period(0, 100), 2.0);      // middle 50  <-- min
  tpv.add(period(200, 200), 3.0);    // middle 300
  assertEquals(1, tpv.getMinMiddleIndex());
  tpv.delete(1, 1);
  // remaining: index0 (200), index1 (300) => min middle = 200 (index0)
  assertEquals(0, tpv.getMinMiddleIndex());
  }
  // ---------------------------------------------------------------
  //  getMinStartIndex / getMaxStartIndex  (sanity)
  // ---------------------------------------------------------------
  @Test
  public void testStartIndexBounds() {
  TimePeriodValues tpv = new TimePeriodValues("Test");
  // start times: 50, 100, 200
  tpv.add(period(100, 50), 1.0);   // start=100
  tpv.add(period(50, 50), 2.0);    // start=50   <-- min
  tpv.add(period(200, 50), 3.0);   // start=200  <-- max
  assertEquals(1, tpv.getMinStartIndex());
  assertEquals(2, tpv.getMaxStartIndex());
  }
  // ---------------------------------------------------------------
  //  getMinEndIndex / getMaxEndIndex  (sanity)
  // ---------------------------------------------------------------
  @Test
  public void testEndIndexBounds() {
  TimePeriodValues tpv = new TimePeriodValues("Test");
  // end times (start+length): 150, 150, 300 (but careful: end = start + length)
  // We'll use distinct ends: 100, 200, 300
  tpv.add(period(0, 100), 1.0);    // end=100   <-- min
  tpv.add(period(0, 200), 2.0);    // end=200
  tpv.add(period(0, 300), 3.0);    // end=300   <-- max
  assertEquals(0, tpv.getMinEndIndex());
  assertEquals(2, tpv.getMaxEndIndex());
  }
  // ---------------------------------------------------------------
  //  Bonus: recalculateBounds via delete exposes bug again
  // ---------------------------------------------------------------
  @Test
  public void testMaxMiddleAfterDeleteNonMaxButTriggersBug() {
  // After delete, recalculateBounds iterates all remaining items
  // and can still exhibit the minMiddleIndex confusion.
  TimePeriodValues tpv = new TimePeriodValues("Test");
  tpv.add(period(0, 200), 1.0);      // middle 100  idx0
  tpv.add(period(100, 300), 2.0);    // middle 200  idx1  <-- real max
  tpv.add(period(0, 50), 3.0);       // middle 25   idx2
  tpv.add(period(50, 150), 4.0);     // middle 100  idx3
  // remove item at idx2 (the one with middle 25)
  tpv.delete(2, 2);
  // remaining: [0:100, 1:200, 2:100] — max should still be 1
  assertEquals("maxMiddle should remain 1 after removing low outlier",
          1, tpv.getMaxMiddleIndex());
  }

}