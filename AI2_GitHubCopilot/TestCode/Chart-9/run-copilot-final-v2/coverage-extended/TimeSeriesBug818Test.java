package org.jfree.data.time.junit;

 import static org.junit.Assert.*;
 import org.junit.Test;
 import org.jfree.data.time.Day;
 import org.jfree.data.time.TimeSeries;

 /**
  * Tests targeting bug report 818:
  * TimeSeries.createCopy(RegularTimePeriod,RegularTimePeriod) must not throw
  * when start<=end yields an empty range, and must properly validate arguments.
  */
 public class TimeSeriesBug818Test {

     @Test(expected = IllegalArgumentException.class)
     public void testCreateCopyWithStartAfterEndThrows() throws Exception {
         TimeSeries series = new TimeSeries("Test");
         series.createCopy(new Day(2, 1, 2000), new Day(1, 1, 2000));
     }

     @Test
     public void testCreateCopyWithStartEqualsEndOnEmptySeries() throws Exception {
         TimeSeries series = new TimeSeries("Test");
         TimeSeries copy = series.createCopy(
             new Day(15, 6, 2001), new Day(15, 6, 2001)
         );
         assertEquals(0, copy.getItemCount());
     }

     @Test
     public void testCreateCopyValidSubset() throws Exception {
         TimeSeries series = new TimeSeries("Test");
         series.add(new Day(1, 1, 2000), 1.0);
         series.add(new Day(3, 1, 2000), 3.0);
         series.add(new Day(5, 1, 2000), 5.0);

         TimeSeries copy = series.createCopy(
             new Day(2, 1, 2000), new Day(4, 1, 2000));
         assertEquals(1, copy.getItemCount());
         assertEquals(new Day(3, 1, 2000), copy.getTimePeriod(0));
         assertEquals(3.0, copy.getValue(0).doubleValue(), 0.0);
     }

     @Test
     public void testCreateCopyWithStartEqualsEndExactMatch() throws Exception {
         TimeSeries series = new TimeSeries("Test");
         series.add(new Day(10, 6, 2001), 42.0);
         TimeSeries copy = series.createCopy(
             new Day(10, 6, 2001), new Day(10, 6, 2001));
         assertEquals(1, copy.getItemCount());
         assertEquals(new Day(10, 6, 2001), copy.getTimePeriod(0));
         assertEquals(42.0, copy.getValue(0).doubleValue(), 0.0);
     }

     @Test
     public void testCreateCopyWithStartEqualsEndPeriodBetwenDataPoints() throws Exception {
         TimeSeries series = new TimeSeries("Test");
         series.add(new Day(1, 1, 2000), 1.0);
         series.add(new Day(5, 1, 2000), 5.0);
         // start==end but period not in series, falls between two data points
         // Should return empty copy (no exception) instead of throwing "Requires start <= end"
         TimeSeries copy = series.createCopy(
             new Day(3, 1, 2000), new Day(3, 1, 2000));
         assertEquals(0, copy.getItemCount());
     }

     @Test
     public void testCreateCopyWithStartEqualsEndBeforeFirstPeriod() throws Exception {
         TimeSeries series = new TimeSeries("Test");
         series.add(new Day(10, 6, 2001), 10.0);
         // start==end before any data
         TimeSeries copy = series.createCopy(
             new Day(1, 6, 2001), new Day(1, 6, 2001));
         assertEquals(0, copy.getItemCount());
     }

     @Test
     public void testCreateCopyWithStartEqualsEndAfterLastPeriod() throws Exception {
         TimeSeries series = new TimeSeries("Test");
         series.add(new Day(15, 6, 2001), 20.0);
         // start==end after the last data item
         TimeSeries copy = series.createCopy(
             new Day(20, 6, 2001), new Day(20, 6, 2001));
         assertEquals(0, copy.getItemCount());
     }

     @Test
     public void testCreateCopyStartLessThanEndButRangeEmptyBetweenDataPoints() throws Exception {
         TimeSeries series = new TimeSeries("Test");
         series.add(new Day(1, 1, 2000), 10.0);
         series.add(new Day(10, 1, 2000), 100.0);
         // start<end but both periods are between existing data points with no items inside
         TimeSeries copy = series.createCopy(
             new Day(3, 1, 2000), new Day(7, 1, 2000));
         assertEquals(0, copy.getItemCount());
     }

     @Test
     public void testCreateCopyBothPeriodsBeforeFirstItem() throws Exception {
         TimeSeries series = new TimeSeries("Test");
         series.add(new Day(20, 6, 2001), 1.0);
         TimeSeries copy = series.createCopy(
             new Day(1, 6, 2001), new Day(10, 6, 2001));
         assertEquals(0, copy.getItemCount());
     }

     @Test
     public void testCreateCopyBothPeriodsAfterLastItem() throws Exception {
         TimeSeries series = new TimeSeries("Test");
         series.add(new Day(5, 6, 2001), 5.0);
         TimeSeries copy = series.createCopy(
             new Day(6, 6, 2001), new Day(15, 6, 2001));
         assertEquals(0, copy.getItemCount());
     }

     @Test(expected = IllegalArgumentException.class)
     public void testCreateCopyWithNullStart() throws Exception {
         TimeSeries series = new TimeSeries("Test");
         series.createCopy(null, new Day(1, 1, 2000));
     }

     @Test(expected = IllegalArgumentException.class)
     public void testCreateCopyWithNullEnd() throws Exception {
         TimeSeries series = new TimeSeries("Test");
         series.createCopy(new Day(1, 1, 2000), null);
     }
 }
