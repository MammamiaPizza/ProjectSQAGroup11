package org.jfree.data.time.junit;

 import org.jfree.data.time.Day;
 import org.jfree.data.time.TimeSeries;
 import org.junit.Test;
 import static org.junit.Assert.*;

 /**
  * Tests for {@link TimeSeries} targeting createCopy and related methods,
  * including reproduction of bug 1832432.
  */
 public class TimeSeriesTests {

     /**
      * Bug 1832432: createCopy(RegularTimePeriod, RegularTimePeriod)
      * throws IllegalArgumentException "Requires start <= end." when
      * both bounds map to the same insertion point between two
      * existing data items. The expected result is an empty copy.
      */
     @Test
     public void testBug1832432() throws Exception {
         TimeSeries series = new TimeSeries("Bug1832432");
         series.add(new Day(1, 1, 2000), 1.0);
         series.add(new Day(4, 1, 2000), 4.0);
         series.add(new Day(5, 1, 2000), 5.0);

         // Both dates lie between Day 1 and Day 4 → same insertion point
         Day start = new Day(2, 1, 2000);
         Day end   = new Day(3, 1, 2000);

         TimeSeries copy = series.createCopy(start, end);
         assertNotNull("Copy must not be null", copy);
         assertEquals("Copied series keeps original name",
                 "Bug1832432", copy.getKey());
         assertEquals("No data falls in [Day2, Day3]", 0, copy.getItemCount());
     }

     /** Copy a range where both endpoints match existing data items exactly. */
     @Test
     public void testCreateCopyNormalRange() throws Exception {
         TimeSeries series = new TimeSeries("Normal");
         series.add(new Day(1, 1, 2000), 1.0);
         series.add(new Day(2, 1, 2000), 2.0);
         series.add(new Day(3, 1, 2000), 3.0);
         series.add(new Day(4, 1, 2000), 4.0);
         series.add(new Day(5, 1, 2000), 5.0);

         TimeSeries copy = series.createCopy(new Day(2, 1, 2000),
                                             new Day(4, 1, 2000));
         assertEquals(3, copy.getItemCount());
         assertEquals(2.0, copy.getValue(0).doubleValue(), 0.001);
         assertEquals(3.0, copy.getValue(1).doubleValue(), 0.001);
         assertEquals(4.0, copy.getValue(2).doubleValue(), 0.001);
     }

     /** Equal start and end bounds produce a single-item copy. */
     @Test
     public void testCreateCopySingleItemEqualBounds() throws Exception {
         TimeSeries series = new TimeSeries("Single");
         series.add(new Day(1, 1, 2000), 1.0);
         series.add(new Day(2, 1, 2000), 2.0);
         series.add(new Day(3, 1, 2000), 3.0);

         TimeSeries copy = series.createCopy(new Day(2, 1, 2000),
                                             new Day(2, 1, 2000));
         assertEquals(1, copy.getItemCount());
         assertEquals(2.0, copy.getValue(0).doubleValue(), 0.001);
     }

     /** Start before the first data item; end matches an existing item. */
     @Test
     public void testCreateCopyStartBeforeFirstEndMatch() throws Exception {
         TimeSeries series = new TimeSeries("BeforeFirst");
         series.add(new Day(5, 1, 2000), 5.0);
         series.add(new Day(6, 1, 2000), 6.0);
         series.add(new Day(7, 1, 2000), 7.0);

         TimeSeries copy = series.createCopy(new Day(1, 1, 2000),
                                             new Day(6, 1, 2000));
         assertEquals(2, copy.getItemCount());
         assertEquals(5.0, copy.getValue(0).doubleValue(), 0.001);
         assertEquals(6.0, copy.getValue(1).doubleValue(), 0.001);
     }

     /** Start matches an existing item; end after the last data item. */
     @Test
     public void testCreateCopyStartMatchEndAfterLast() throws Exception {
         TimeSeries series = new TimeSeries("AfterLast");
         series.add(new Day(5, 1, 2000), 5.0);
         series.add(new Day(6, 1, 2000), 6.0);
         series.add(new Day(7, 1, 2000), 7.0);

         TimeSeries copy = series.createCopy(new Day(6, 1, 2000),
                                             new Day(10, 1, 2000));
         assertEquals(2, copy.getItemCount());
         assertEquals(6.0, copy.getValue(0).doubleValue(), 0.001);
         assertEquals(7.0, copy.getValue(1).doubleValue(), 0.001);
     }

     /** Both bounds unmatched, but in different gaps between data items. */
     @Test
     public void testCreateCopyBothUnmatchedDifferentGaps() throws Exception {
         TimeSeries series = new TimeSeries("DiffGaps");
         series.add(new Day(1, 1,2000),10.);
         series.add(new Day(5,1,2000),50.0);
         series.add(new Day(10,1,2000),100.0);

         // Day 2 inserts after idx 0: Day 7 inserts after idx 1
         TimeSeries copy = series.createCopy(new Day(2,1,2000),
                                             new Day(7, 1,2000));
         assertEquals(1, copy.getItemCount());
         assertEquals(50.0, copy.getValue(0).doubleValue(),0.001);
     }

     /** Start chronologically after end must throw IllegalArgumentException. */
     @Test
     public void testCreateCopyStartAfterEndThrows() {
         TimeSeries series = new TimeSeries("InvalidOrder");
         series.add(new Day(1,1,2000),10.0);
         series.add(new Day(5,1,2000),50.0);

         try {
             series.createCopy(new Day(5,1,2000),new Day(1,1,2000));
             fail("Expected IllegalArgumentException");
         } catch (IllegalArgumentException e) {
             assertTrue(e.getMessage().contains("start on or before end"));
         } catch (CloneNotSupportedException e) {
             fail("Unexpected CloneNotSupportedException");
         }
     }

     /** Copy from an empty series returns an empty copy with the same name. */
     @Test
     public void testCreateCopyEmptySeries() throws Exception {
         TimeSeries series = new TimeSeries("Empty");

         TimeSeries copy = series.createCopy(new Day(1,1,2000),
                                             new Day(5,1,2000));
         assertNotNull(copy);
         assertEquals("Empty", copy.getKey());
         assertEquals(0, copy.getItemCount());
     }

     /** Null start argument must throw IllegalArgumentException. */
     @Test
     public void testCreateCopyNullStartThrows() {
         TimeSeries series = new TimeSeries("NullStart");
         series.add(new Day(1,1,2000),1.0);

         try {
             series.createCopy(null, new Day(1,1,2000));
             fail("Expected IllegalArgumentException");
         } catch (IllegalArgumentException e) {
             assertEquals("Null 'start' argument.", e.getMessage());
         } catch (CloneNotSupportedException e) {
             fail("Unexpected CloneNotSupportedException");
         }
     }

     /** Null end argument must throw IllegalArgumentException. */
     @Test
     public void testCreateCopyNullEndThrows() {
         TimeSeries series = new TimeSeries("NullEnd");
         series.add(new Day(1,1,2000),1.0);

         try {
             series.createCopy(new Day(1,1,2000),null);
             fail("Expected IllegalArgumentException");
         } catch (IllegalArgumentException e) {
             assertEquals("Null 'end' argument.",e.getMessage());
         } catch (CloneNotSupportedException e) {
             fail("Unexpected CloneNotSupportedException");
         }
     }

     /** createCopy(int,int) with a negative start index must throw. */
     @Test(expected = IllegalArgumentException.class)
     public void testCreateCopyIntNegativeStart() throws Exception {
         TimeSeries series = new TimeSeries("NegStart");
         series.add(new Day(1,1,2000),1.0);
         series.createCopy(-1,0);
     }

     /** createCopy(int,int) with start > end must throw. */
     @Test(expected = IllegalArgumentException.class)
     public void testCreateCopyIntStartGreaterThanEnd() throws Exception {
         TimeSeries series = new TimeSeries("StartGtEnd");
         series.add(new Day(1,1,2000),1.0);
         series.add(new Day(2,1,2000),2.0);
         series.createCopy(1,0);
     }
 }
