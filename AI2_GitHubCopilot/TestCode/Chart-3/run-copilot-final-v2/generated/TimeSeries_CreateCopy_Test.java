import static org.junit.Assert.*;
 import org.junit.Test;
 import java.util.Date;

 import org.jfree.data.time.Day;
 import org.jfree.data.time.Month;
 import org.jfree.data.time.RegularTimePeriod;
 import org.jfree.data.time.TimeSeries;
 import org.jfree.data.time.TimeSeriesDataItem;

 /**
  * Tests for {@link TimeSeries#createCopy(RegularTimePeriod, RegularTimePeriod)}
  * and {@link TimeSeries#createCopy(int, int)} focusing on off‑by‑one bugs
  * in end‑boundary handling (Chart‑3b).
  */
 public class TimeSeries_CreateCopy_Test {

     // ---------- helpers ----------
     private static Day d(int day, int month, int year) {
         return new Day(day, month, year);
     }

     private static Month m(int month, int year) {
         return new Month(month, year);
     }

     /**
      * @return five daily items: 1‑Jan=10, 2‑Jan=20, 3‑Jan=30, 4‑Jan=40, 5‑Jan=50
      */
     private TimeSeries fiveDaySeries() {
         TimeSeries s = new TimeSeries("Test");
         s.add(d(1, 1, 2023), 10.0);
         s.add(d(2, 1, 2023), 20.0);
         s.add(d(3, 1, 2023), 30.0);
         s.add(d(4, 1, 2023), 40.0);
         s.add(d(5, 1, 2023), 50.0);
         return s;
     }

     // =================== period‑based createCopy ===================

     @Test
     public void testCreateCopyPeriodFullSeries() {
         TimeSeries s = fiveDaySeries();
         TimeSeries copy = s.createCopy(d(1, 1, 2023), d(5, 1, 2023));
         assertEquals("item count", 5, copy.getItemCount());
         assertEquals("minY", 10.0, copy.getMinY(), 1e-12);
         assertEquals("maxY", 50.0, copy.getMaxY(), 1e-12);
         assertEquals(10.0, copy.getValue(0).doubleValue(), 1e-12);
         assertEquals(50.0, copy.getValue(4).doubleValue(), 1e-12);
     }

     @Test
     public void testCreateCopyPeriodInnerRange() {
         // Buggy code may include an extra element after the end period.
         TimeSeries s = fiveDaySeries();
         // Copy 2‑Jan .. 3‑Jan (inclusive).  Correct: items 2,3  (values 20,30)
         TimeSeries copy = s.createCopy(d(2, 1, 2023), d(3, 1, 2023));
         assertEquals("count", 2, copy.getItemCount());
         assertEquals("first value", 20.0, copy.getValue(0).doubleValue(), 1e-12);
         assertEquals("last value", 30.0, copy.getValue(1).doubleValue(), 1e-12);
         assertEquals("minY", 20.0, copy.getMinY(), 1e-12);
         assertEquals("maxY", 30.0, copy.getMaxY(), 1e-12);
     }

     @Test
     public void testCreateCopyPeriodSingleItem() {
         TimeSeries s = fiveDaySeries();
         TimeSeries copy = s.createCopy(d(3, 1, 2023), d(3, 1, 2023));
         assertEquals(1, copy.getItemCount());
         assertEquals(30.0, copy.getMinY(), 1e-12);
         assertEquals(30.0, copy.getMaxY(), 1e-12);
     }

     @Test
     public void testCreateCopyPeriodStartBeforeData() {
         TimeSeries s = fiveDaySeries();
         // start before first element, end inside
         TimeSeries copy = s.createCopy(d(30, 12, 2022), d(3, 1, 2023));
         // Items 1..3 should be present: values 10,20,30
         assertEquals(3, copy.getItemCount());
         assertEquals(10.0, copy.getMinY(), 1e-12);
         assertEquals(30.0, copy.getMaxY(), 1e-12);
     }

     @Test
     public void testCreateCopyPeriodEndAfterData() {
         TimeSeries s = fiveDaySeries();
         // start inside, end after last element
         TimeSeries copy = s.createCopy(d(3, 1, 2023), d(10, 1, 2023));
         // Items 3,4,5
         assertEquals(3, copy.getItemCount());
         assertEquals(30.0, copy.getMinY(), 1e-12);
         assertEquals(50.0, copy.getMaxY(), 1e-12);
     }

     @Test
     public void testCreateCopyPeriodNoOverlap() {
         TimeSeries s = fiveDaySeries();
         // entirely after the existing data
         TimeSeries copy = s.createCopy(d(10, 1, 2023), d(20, 1, 2023));
         assertEquals(0, copy.getItemCount());
         assertTrue(Double.isNaN(copy.getMinY()));
         assertTrue(Double.isNaN(copy.getMaxY()));
     }

     @Test(expected = IllegalArgumentException.class)
     public void testCreateCopyPeriodStartNull() {
         TimeSeries s = fiveDaySeries();
         s.createCopy(null, d(3, 1, 2023));
     }

     @Test(expected = IllegalArgumentException.class)
     public void testCreateCopyPeriodEndNull() {
         TimeSeries s = fiveDaySeries();
         s.createCopy(d(3, 1, 2023), null);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testCreateCopyPeriodStartAfterEnd() {
         TimeSeries s = fiveDaySeries();
         s.createCopy(d(5, 1, 2023), d(1, 1, 2023));
     }

     // =================== index‑based createCopy ===================

     @Test
     public void testCreateCopyIndexNormal() {
         TimeSeries s = fiveDaySeries();
         TimeSeries copy = s.createCopy(1, 3); // indices 1,2,3 -> values 20,30,40
         assertEquals(3, copy.getItemCount());
         assertEquals(20.0, copy.getMinY(), 1e-12);
         assertEquals(40.0, copy.getMaxY(), 1e-12);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testCreateCopyIndexStartNegative() {
         TimeSeries s = fiveDaySeries();
         s.createCopy(-1, 2);
     }

     @Test(expected = IllegalArgumentException.class)
     public void testCreateCopyIndexEndTooLarge() {
         TimeSeries s = fiveDaySeries();
         s.createCopy(0, 5); // end >= itemCount
     }

     @Test(expected = IllegalArgumentException.class)
     public void testCreateCopyIndexStartAfterEnd() {
         TimeSeries s = fiveDaySeries();
         s.createCopy(3, 1);
     }

     // =================== metadata preservation ===================

     @Test
     public void testCopyPreservesMetadata() {
         TimeSeries s = fiveDaySeries();
         s.setDomainDescription("Domain");
         s.setRangeDescription("Range");
         s.setMaximumItemCount(100);
         s.setMaximumItemAge(365);
         TimeSeries copy = s.createCopy(d(1, 1, 2023), d(5, 1, 2023));
         assertEquals("Domain", copy.getDomainDescription());
         assertEquals("Range", copy.getRangeDescription());
         assertEquals(100, copy.getMaximumItemCount());
         assertEquals(365, copy.getMaximumItemAge());
         assertEquals(s.getTimePeriodClass(), copy.getTimePeriodClass());
     }

     // =================== independence ===================

     @Test
     public void testCopyIsIndependent() {
         TimeSeries s = fiveDaySeries();
         TimeSeries copy = s.createCopy(d(1, 1, 2023), d(5, 1, 2023));
         // mutate copy
         copy.add(d(6, 1, 2023), 99.0);
         assertEquals(5, s.getItemCount());
         assertEquals(6, copy.getItemCount());
     }
 }
