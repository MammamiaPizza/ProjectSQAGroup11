package org.jfree.data.general.junit;

import java.util.Arrays;
import java.util.List;

import org.jfree.data.KeyedValues;
import org.jfree.data.Range;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.function.Function2D;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.pie.DefaultPieDataset;
import org.jfree.data.pie.PieDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for {@link DatasetUtilities}.
 */
public class DatasetUtilitiesGeneratedTest {

    private static void assertRange(double lower, double upper, Range actual) {
        assertEquals(lower, actual.getLowerBound(), 0.0000001);
        assertEquals(upper, actual.getUpperBound(), 0.0000001);
    }

    @Test
    public void testCalculatePieDatasetTotalIgnoresNullZeroAndNegativeValues() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("positive1", 2.5);
        dataset.setValue("positive2", 7.5);
        dataset.setValue("zero", 0.0);
        dataset.setValue("negative", -3.0);
        dataset.setValue("null", null);

        assertEquals(10.0, DatasetUtilities.calculatePieDatasetTotal(dataset),
                0.0000001);
    }

    @Test
    public void testCreatePieDatasetsForRowAndColumn() {
        DefaultCategoryDataset source = new DefaultCategoryDataset();
        source.addValue(1.0, "R1", "C1");
        source.addValue(2.0, "R1", "C2");
        source.addValue(3.0, "R2", "C1");
        source.addValue(4.0, "R2", "C2");

        PieDataset row = DatasetUtilities.createPieDatasetForRow(source, "R1");
        assertEquals(2, row.getItemCount());
        assertEquals(1.0, row.getValue("C1").doubleValue(), 0.0000001);
        assertEquals(2.0, row.getValue("C2").doubleValue(), 0.0000001);

        PieDataset column = DatasetUtilities.createPieDatasetForColumn(source,
                "C2");
        assertEquals(2, column.getItemCount());
        assertEquals(2.0, column.getValue("R1").doubleValue(), 0.0000001);
        assertEquals(4.0, column.getValue("R2").doubleValue(), 0.0000001);
    }

    @Test
    public void testCreateConsolidatedPieDatasetAggregatesOnlyWhenMinimumReached() {
        DefaultPieDataset source = new DefaultPieDataset();
        source.setValue("large", 80.0);
        source.setValue("small1", 10.0);
        source.setValue("small2", 10.0);

        PieDataset consolidated = DatasetUtilities.createConsolidatedPieDataset(
                source, "Other", 0.15, 2);

        assertEquals(2, consolidated.getItemCount());
        assertEquals(80.0, consolidated.getValue("large").doubleValue(),
                0.0000001);
        assertEquals(20.0, consolidated.getValue("Other").doubleValue(),
                0.0000001);
        assertNull(consolidated.getValue("small1"));

        PieDataset notConsolidated
                = DatasetUtilities.createConsolidatedPieDataset(
                        source, "Other", 0.15, 3);
        assertEquals(3, notConsolidated.getItemCount());
        assertNull(notConsolidated.getValue("Other"));
    }

    @Test
    public void testCreateCategoryDatasetWithExplicitKeysCopiesValues() {
        Comparable[] rowKeys = new Comparable[] {"R1", "R2"};
        Comparable[] columnKeys = new Comparable[] {"C1", "C2"};
        double[][] values = new double[][] {
            {1.0, 2.0},
            {3.0, 4.0}
        };

        org.jfree.data.category.CategoryDataset dataset
                = DatasetUtilities.createCategoryDataset(rowKeys, columnKeys,
                        values);

        assertEquals(2, dataset.getRowCount());
        assertEquals(2, dataset.getColumnCount());
        assertEquals(4.0, dataset.getValue("R2", "C2").doubleValue(),
                0.0000001);
    }

    @Test
    public void testCreateCategoryDatasetRejectsDuplicateAndMismatchedKeys() {
        try {
            DatasetUtilities.createCategoryDataset(
                    new Comparable[] {"R", "R"},
                    new Comparable[] {"C"},
                    new double[][] {{1.0}, {2.0}});
            fail("Duplicate row keys should be rejected.");
        }
        catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("Duplicate"));
        }

        try {
            DatasetUtilities.createCategoryDataset(
                    new Comparable[] {"R1"},
                    new Comparable[] {"C1"},
                    new double[][] {{1.0, 2.0}});
            fail("A mismatched column-key count should be rejected.");
        }
        catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("column keys"));
        }
    }

    @Test
    public void testCreateCategoryDatasetFromKeyedValuesRejectsNullArguments() {
        try {
            DatasetUtilities.createCategoryDataset(null, (KeyedValues) null);
            fail("A null row key should be rejected.");
        }
        catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("rowKey"));
        }
    }

    @Test
    public void testSampleFunction2DProducesEndpointsAndEvenlySpacedSamples() {
        Function2D square = new Function2D() {
            public double getValue(double x) {
                return x * x;
            }
        };

        XYDataset dataset = DatasetUtilities.sampleFunction2D(square, -1.0,
                1.0, 3, "square");

        assertEquals(1, dataset.getSeriesCount());
        assertEquals(3, dataset.getItemCount(0));
        assertEquals(-1.0, dataset.getXValue(0, 0), 0.0000001);
        assertEquals(0.0, dataset.getXValue(0, 1), 0.0000001);
        assertEquals(1.0, dataset.getYValue(0, 2), 0.0000001);
    }

    @Test
    public void testSampleFunction2DRejectsInvalidArguments() {
        Function2D identity = new Function2D() {
            public double getValue(double x) {
                return x;
            }
        };

        try {
            DatasetUtilities.sampleFunction2DToSeries(identity, 0.0, 1.0, 1,
                    "S");
            fail("Fewer than two samples should be rejected.");
        }
        catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("samples"));
        }

        try {
            DatasetUtilities.sampleFunction2DToSeries(identity, 1.0, 1.0, 2,
                    "S");
            fail("An empty sampling range should be rejected.");
        }
        catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("start"));
        }
    }

    @Test
    public void testIsEmptyOrNullForPieCategoryAndXYDatasets() {
        assertTrue(DatasetUtilities.isEmptyOrNull((PieDataset) null));
        assertTrue(DatasetUtilities.isEmptyOrNull((XYDataset) null));

        DefaultPieDataset emptyPie = new DefaultPieDataset();
        emptyPie.setValue("zero", 0.0);
        emptyPie.setValue("negative", -1.0);
        assertTrue(DatasetUtilities.isEmptyOrNull(emptyPie));
        emptyPie.setValue("positive", 1.0);
        assertFalse(DatasetUtilities.isEmptyOrNull(emptyPie));

        DefaultCategoryDataset category = new DefaultCategoryDataset();
        assertTrue(DatasetUtilities.isEmptyOrNull(category));
        category.addValue(0.0, "R", "C");
        assertFalse(DatasetUtilities.isEmptyOrNull(category));

        XYSeries series = new XYSeries("S");
        org.jfree.data.xy.XYSeriesCollection xy
                = new org.jfree.data.xy.XYSeriesCollection(series);
        assertTrue(DatasetUtilities.isEmptyOrNull(xy));
        series.add(1.0, 2.0);
        assertFalse(DatasetUtilities.isEmptyOrNull(xy));
    }

    @Test
    public void testFindDomainAndRangeBoundsForXYDataset() {
        XYSeries series = new XYSeries("S");
        series.add(-2.0, 4.0);
        series.add(3.0, -5.0);
        org.jfree.data.xy.XYSeriesCollection dataset
                = new org.jfree.data.xy.XYSeriesCollection(series);

        assertRange(-2.0, 3.0, DatasetUtilities.findDomainBounds(dataset));
        assertRange(-5.0, 4.0, DatasetUtilities.findRangeBounds(dataset));
        assertEquals(-2.0,
                DatasetUtilities.findMinimumDomainValue(dataset).doubleValue(),
                0.0000001);
        assertEquals(3.0,
                DatasetUtilities.findMaximumDomainValue(dataset).doubleValue(),
                0.0000001);
    }

    @Test
    public void testIntervalCategoryRangeBoundsHandleNullIntervalEndpoints() {
        NullableIntervalCategoryDataset dataset
                = new NullableIntervalCategoryDataset(
                        new Number[][] {{null, 5.0}},
                        new Number[][] {{15.0, null}});
        dataset.addValue(10.0, "S1", "C1");
        dataset.addValue(20.0, "S1", "C2");

        /*
         * The ordinary and visible-series variants must both account for
         * actual values as well as the available interval endpoints.  In
         * particular, null endpoints must not cause a NullPointerException.
         */
        assertRange(5.0, 20.0,
                DatasetUtilities.iterateRangeBounds(dataset, true));

        List visible = Arrays.asList(new Comparable[] {"S1"});
        assertRange(5.0, 20.0,
                DatasetUtilities.iterateToFindRangeBounds(dataset, visible,
                        true));
    }

    @Test
    public void testVisibleCategoryRangeBoundsExcludeHiddenSeries() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(-2.0, "visible", "C1");
        dataset.addValue(3.0, "visible", "C2");
        dataset.addValue(-100.0, "hidden", "C1");
        dataset.addValue(100.0, "hidden", "C2");

        List visible = Arrays.asList(new Comparable[] {"visible"});
        assertRange(-2.0, 3.0, DatasetUtilities.findRangeBounds(dataset,
                visible, false));
    }

    @Test
    public void testStackedAndCumulativeRangeBounds() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(2.0, "R1", "C1");
        dataset.addValue(-4.0, "R1", "C2");
        dataset.addValue(3.0, "R2", "C1");
        dataset.addValue(-1.0, "R2", "C2");

        assertRange(-5.0, 5.0, DatasetUtilities.findStackedRangeBounds(dataset));
        assertEquals(-5.0,
                DatasetUtilities.findMinimumStackedRangeValue(dataset)
                        .doubleValue(),
                0.0000001);
        assertEquals(5.0,
                DatasetUtilities.findMaximumStackedRangeValue(dataset)
                        .doubleValue(),
                0.0000001);

        DefaultCategoryDataset cumulative = new DefaultCategoryDataset();
        cumulative.addValue(3.0, "R", "C1");
        cumulative.addValue(-5.0, "R", "C2");
        cumulative.addValue(1.0, "R", "C3");
        assertRange(-2.0, 3.0,
                DatasetUtilities.findCumulativeRangeBounds(cumulative));
    }

    @Test
    public void testNullDatasetArgumentsAreRejectedWhereRequired() {
        try {
            DatasetUtilities.calculatePieDatasetTotal(null);
            fail("A null pie dataset should be rejected.");
        }
        catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("dataset"));
        }

        try {
            DatasetUtilities.findDomainBounds((XYDataset) null);
            fail("A null XY dataset should be rejected.");
        }
        catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("dataset"));
        }

        try {
            DatasetUtilities.findRangeBounds(
                    (org.jfree.data.category.CategoryDataset) null);
            fail("A null category dataset should be rejected.");
        }
        catch (IllegalArgumentException expected) {
            assertTrue(expected.getMessage().contains("dataset"));
        }
    }

    /**
     * A category dataset whose interval endpoints may independently be null.
     * This models interval datasets that triggered bug 2849731.
     */
    private static class NullableIntervalCategoryDataset
            extends DefaultCategoryDataset implements IntervalCategoryDataset {

        private Number[][] startValues;
        private Number[][] endValues;

        NullableIntervalCategoryDataset(Number[][] startValues,
                                        Number[][] endValues) {
            this.startValues = startValues;
            this.endValues = endValues;
        }

        public Number getStartValue(int row, int column) {
            return this.startValues[row][column];
        }

        public Number getEndValue(int row, int column) {
            return this.endValues[row][column];
        }

        public Number getStartValue(Comparable rowKey, Comparable columnKey) {
            return getStartValue(getRowIndex(rowKey), getColumnIndex(columnKey));
        }

        public Number getEndValue(Comparable rowKey, Comparable columnKey) {
            return getEndValue(getRowIndex(rowKey), getColumnIndex(columnKey));
        }
    }
}
