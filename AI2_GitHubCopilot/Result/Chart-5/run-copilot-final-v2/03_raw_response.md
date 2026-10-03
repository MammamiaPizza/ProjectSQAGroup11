package org.jfree.data.xy.junit;

import static org.junit.Assert.*;

import org.junit.Test;
import org.jfree.data.general.SeriesException;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;

/**

 - Tests for XYSeries focusing on bug 862: duplicate x with allowDuplicateXValues=false
 - should replace/update instead of throwing IndexOutOfBoundsException.
  */
 public class XYSeriesBugTest { @Test public void
testAddDuplicateX_AutoSortTrue_AllowDuplicateFalse_ShouldReplace() { XYSeries series = new
XYSeries("S", true, false); series.add(1.0, 10.0); assertEquals(1, series.getItemCount()); // Adding
duplicate x should not throw and should replace series.add(1.0, 20.0); assertEquals(1,
series.getItemCount()); assertEquals(20.0, series.getY(0).doubleValue(), 1e-9); } @Test public void
testAddDuplicateX_AutoSortFalse_AllowDuplicateFalse_ShouldReplace() { XYSeries series = new
XYSeries("S", false, false); series.add(1.0, 10.0); assertEquals(1, series.getItemCount());
series.add(1.0, 20.0); assertEquals(1, series.getItemCount()); assertEquals(20.0,
series.getY(0).doubleValue(), 1e-9); } @Test public void
testAddDuplicateX_AllowDuplicateTrue_ShouldAddMultipleItems() { XYSeries series = new XYSeries("S",
true, true); series.add(1.0, 10.0); series.add(1.0, 20.0); assertEquals(2, series.getItemCount());
assertEquals(10.0, series.getY(0).doubleValue(), 1e-9); assertEquals(20.0,
series.getY(1).doubleValue(), 1e-9); } @Test public void
testAddOrUpdate_NewItem_AddsItemAndReturnsNull() { XYSeries series = new XYSeries("S", true, false);
XYDataItem overwritten = series.addOrUpdate(1.0, 10.0); assertNull(overwritten); assertEquals(1,
series.getItemCount()); assertEquals(10.0, series.getY(0).doubleValue(), 1e-9); } @Test public void
testAddOrUpdate_ExistingItem_UpdatesYAndReturnsOverwritten() { XYSeries series = new XYSeries("S",
true, false); series.addOrUpdate(1.0, 10.0); XYDataItem overwritten = series.addOrUpdate(1.0, 20.0);
assertNotNull(overwritten); assertEquals(10.0, overwritten.getY().doubleValue(), 1e-9);
assertEquals(1, series.getItemCount()); assertEquals(20.0, series.getY(0).doubleValue(), 1e-9); }
@Test public void testUpdateByIndex_ChangesYValue() { XYSeries series = new XYSeries("S", true,
false); series.add(1.0, 10.0); series.add(2.0, 20.0); series.updateByIndex(0, 99.0);
assertEquals(99.0, series.getY(0).doubleValue(), 1e-9); assertEquals(20.0,
series.getY(1).doubleValue(), 1e-9); } @Test public void testUpdate_WithExistingX_UpdatesY() {
XYSeries series = new XYSeries("S", true, false); series.add(1.0, 10.0); series.add(2.0, 20.0);
series.update(2.0, 30.0); assertEquals(10.0, series.getY(0).doubleValue(), 1e-9); assertEquals(30.0,
series.getY(1).doubleValue(), 1e-9); assertEquals(2, series.getItemCount()); } @Test(expected =
SeriesException.class) public void testUpdate_NonExistentX_ThrowsSeriesException() { XYSeries series
= new XYSeries("S", true, false); series.add(1.0, 10.0); series.update(2.0, 20.0); // x not present
} @Test public void testIndexOf_WithAutoSort_ReturnsCorrectIndex() { XYSeries series = new
XYSeries("S", true, false); series.add(1.0, 10.0); series.add(3.0, 30.0); series.add(5.0, 50.0);
assertEquals(1, series.indexOf(3.0)); assertTrue(series.indexOf(4.0) < 0); // insertion point
negative // For x not in sorted list, binarySearch returns negative; just verify <
  0. assertTrue(series.indexOf(6.0) < 0); } @Test public void testDelete_RemovesSpecifiedRange() {
XYSeries series = new XYSeries("S", true, false); series.add(1.0, 10.0); series.add(2.0, 20.0);
series.add(3.0, 30.0); series.add(4.0, 40.0); series.add(5.0, 50.0); series.delete(1, 3); // remove
indices 1,2,3 assertEquals(2, series.getItemCount()); assertEquals(10.0,
series.getY(0).doubleValue(), 1e-9); assertEquals(50.0, series.getY(1).doubleValue(), 1e-9); } @Test
public void testToArray_ReturnsCorrect2DArray() { XYSeries series = new XYSeries("S", true, false);
series.add(1.0, 10.0); series.add(2.0, 20.0); double[][] arr = series.toArray(); assertEquals(2,
arr.length); assertEquals(2, arr[0].length); assertEquals(1.0, arr[0][0], 1e-9); assertEquals(2.0,
arr[0][1], 1e-9); assertEquals(10.0, arr[1][0], 1e-9); assertEquals(20.0, arr[1][1], 1e-9); } @Test
public void testAddSingleItem_GetYReturnsCorrectValue() { XYSeries series = new XYSeries("S", true,
false); series.add(5.0, 42.0); assertEquals(1, series.getItemCount()); assertEquals(42.0,
series.getY(0).doubleValue(), 1e-9); assertEquals(5.0, series.getX(0).doubleValue(), 1e-9); }
 }