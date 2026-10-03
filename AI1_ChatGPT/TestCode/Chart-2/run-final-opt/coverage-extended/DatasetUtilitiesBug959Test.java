package org.jfree.data.general.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jfree.data.Range;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.Test;

public class DatasetUtilitiesBug959Test {

    private static final double EPSILON = 0.0000001;

    @Test
    public void testFindMinimumRangeValueIgnoresNullValuesAfterNumbers() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(Double.valueOf(3.0), "S1", "C1");
        dataset.addValue(Double.valueOf(1.0), "S1", "C2");
        dataset.addValue(null, "S2", "C1");
        dataset.addValue(null, "S2", "C2");

        Number result = DatasetUtilities.findMinimumRangeValue(dataset);

        assertEquals(1.0, result.doubleValue(), EPSILON);
    }

    @Test
    public void testFindMaximumRangeValueIgnoresNullValuesAfterNumbers() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(Double.valueOf(-2.0), "S1", "C1");
        dataset.addValue(Double.valueOf(7.0), "S1", "C2");
        dataset.addValue(null, "S2", "C1");
        dataset.addValue(null, "S2", "C2");

        Number result = DatasetUtilities.findMaximumRangeValue(dataset);

        assertEquals(7.0, result.doubleValue(), EPSILON);
    }

    @Test
    public void testCategoryMinimumAndMaximumUseOnlyNonNullValues() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(null, "S1", "C1");
        dataset.addValue(Double.valueOf(4.0), "S1", "C2");
        dataset.addValue(Double.valueOf(-5.0), "S2", "C1");
        dataset.addValue(null, "S2", "C2");
        dataset.addValue(Double.valueOf(9.0), "S3", "C1");

        assertEquals(-5.0,
                DatasetUtilities.findMinimumRangeValue(dataset).doubleValue(),
                EPSILON);
        assertEquals(9.0,
                DatasetUtilities.findMaximumRangeValue(dataset).doubleValue(),
                EPSILON);
    }

    @Test
    public void testCategoryMinimumAndMaximumAreNullWhenAllValuesAreNull() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(null, "S1", "C1");
        dataset.addValue(null, "S1", "C2");
        dataset.addValue(null, "S2", "C1");

        assertNull(DatasetUtilities.findMinimumRangeValue(dataset));
        assertNull(DatasetUtilities.findMaximumRangeValue(dataset));
    }

    @Test
    public void testFindRangeBoundsIgnoresNullAndNaNCategoryValues() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(null, "S1", "C1");
        dataset.addValue(Double.valueOf(Double.NaN), "S1", "C2");
        dataset.addValue(Double.valueOf(-3.5), "S2", "C1");
        dataset.addValue(Double.valueOf(8.0), "S2", "C2");

        Range result = DatasetUtilities.findRangeBounds(dataset);

        assertEquals(-3.5, result.getLowerBound(), EPSILON);
        assertEquals(8.0, result.getUpperBound(), EPSILON);
    }

    @Test
    public void testFindRangeBoundsReturnsNullForCategoryDatasetWithoutValues() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(null, "S1", "C1");
        dataset.addValue(Double.valueOf(Double.NaN), "S1", "C2");

        assertNull(DatasetUtilities.findRangeBounds(dataset));
    }

    @Test
    public void testFindMinimumAndMaximumXYRangeValuesIgnoreNullYValues() {
        XYSeries series = new XYSeries("S1");
        series.add(Double.valueOf(1.0), null);
        series.add(Double.valueOf(2.0), Double.valueOf(6.0));
        series.add(Double.valueOf(3.0), Double.valueOf(-4.0));
        series.add(Double.valueOf(4.0), null);
        XYSeriesCollection dataset = new XYSeriesCollection(series);

        assertEquals(-4.0,
                DatasetUtilities.findMinimumRangeValue(dataset).doubleValue(),
                EPSILON);
        assertEquals(6.0,
                DatasetUtilities.findMaximumRangeValue(dataset).doubleValue(),
                EPSILON);
    }

    @Test
    public void testFindStackedRangeBoundsIgnoresNullValues() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(Double.valueOf(2.0), "S1", "C1");
        dataset.addValue(null, "S1", "C2");
        dataset.addValue(Double.valueOf(-1.0), "S2", "C1");
        dataset.addValue(Double.valueOf(3.0), "S2", "C2");

        Range result = DatasetUtilities.findStackedRangeBounds(dataset);

        assertEquals(-1.0, result.getLowerBound(), EPSILON);
        assertEquals(3.0, result.getUpperBound(), EPSILON);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindMinimumRangeValueRejectsNullDataset() {
        DatasetUtilities.findMinimumRangeValue((org.jfree.data.category.CategoryDataset) null);
    }

@Test
public void testCalculatePieDatasetTotalIgnoresNullAndNonPositiveValues() {
    org.jfree.data.general.DefaultPieDataset dataset
            = new org.jfree.data.general.DefaultPieDataset();
    dataset.setValue("Positive 1", 2.5);
    dataset.setValue("Null", null);
    dataset.setValue("Zero", 0.0);
    dataset.setValue("Negative", -4.0);
    dataset.setValue("Positive 2", 3.5);

    assertEquals(6.0, DatasetUtilities.calculatePieDatasetTotal(dataset),
            EPSILON);
}

@Test(expected = IllegalArgumentException.class)
public void testCalculatePieDatasetTotalRejectsNullDataset() {
    DatasetUtilities.calculatePieDatasetTotal(null);
}
}
