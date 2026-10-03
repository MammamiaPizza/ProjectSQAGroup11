package org.jfree.data.statistics.junit;

import java.util.Arrays;
import java.util.List;

import org.jfree.data.Range;
import org.jfree.data.statistics.BoxAndWhiskerItem;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DefaultBoxAndWhiskerCategoryDatasetRangeTest {

    private BoxAndWhiskerItem item(double minRegular, double maxRegular,
            double minOutlier, double maxOutlier) {
        return new BoxAndWhiskerItem(
                Double.valueOf((minRegular + maxRegular) / 2.0),
                Double.valueOf((minRegular + maxRegular) / 2.0),
                Double.valueOf(minRegular),
                Double.valueOf(maxRegular),
                Double.valueOf(minRegular),
                Double.valueOf(maxRegular),
                Double.valueOf(minOutlier),
                Double.valueOf(maxOutlier),
                null);
    }

    @Test
    public void testRangeBoundsUsesMinimumRegularValueBelowMinimumOutlier() {
        DefaultBoxAndWhiskerCategoryDataset dataset =
                new DefaultBoxAndWhiskerCategoryDataset();
        dataset.add(item(8.5, 9.6, 8.6, 9.6), "R1", "C1");

        Range bounds = dataset.getRangeBounds(false);

        assertEquals(8.5, bounds.getLowerBound(), 0.0000001);
        assertEquals(9.6, bounds.getUpperBound(), 0.0000001);
    }

    @Test
    public void testRangeAccessorsAreConsistentForBothIntervalFlags() {
        DefaultBoxAndWhiskerCategoryDataset dataset =
                new DefaultBoxAndWhiskerCategoryDataset();
        dataset.add(item(8.5, 9.6, 8.6, 9.6), "R1", "C1");

        assertEquals(8.5, dataset.getRangeLowerBound(false), 0.0000001);
        assertEquals(9.6, dataset.getRangeUpperBound(false), 0.0000001);
        assertEquals(8.5, dataset.getRangeLowerBound(true), 0.0000001);
        assertEquals(9.6, dataset.getRangeUpperBound(true), 0.0000001);

        Range intervalBounds = dataset.getRangeBounds(true);
        assertEquals(8.5, intervalBounds.getLowerBound(), 0.0000001);
        assertEquals(9.6, intervalBounds.getUpperBound(), 0.0000001);
    }

    @Test
    public void testReplacingAnExtremeItemRecalculatesBoundsFromOtherItems() {
        DefaultBoxAndWhiskerCategoryDataset dataset =
                new DefaultBoxAndWhiskerCategoryDataset();
        dataset.add(item(1.0, 10.0, 1.0, 10.0), "R1", "C1");
        dataset.add(item(2.0, 9.0, 2.0, 9.0), "R2", "C1");

        dataset.add(item(3.0, 8.0, 3.0, 8.0), "R1", "C1");

        Range bounds = dataset.getRangeBounds(false);
        assertEquals(2.0, bounds.getLowerBound(), 0.0000001);
        assertEquals(9.0, bounds.getUpperBound(), 0.0000001);
    }

    @Test
    public void testAddListCalculatesRangeForRegularData() {
        DefaultBoxAndWhiskerCategoryDataset dataset =
                new DefaultBoxAndWhiskerCategoryDataset();
        List values = Arrays.asList(new Double[] {
                Double.valueOf(1.0), Double.valueOf(2.0),
                Double.valueOf(3.0), Double.valueOf(4.0),
                Double.valueOf(5.0)
        });

        dataset.add(values, "R1", "C1");

        assertEquals(1.0, dataset.getRangeLowerBound(false), 0.0000001);
        assertEquals(5.0, dataset.getRangeUpperBound(false), 0.0000001);
        assertEquals(3.0, dataset.getMedianValue("R1", "C1").doubleValue(),
                0.0000001);
    }
}
