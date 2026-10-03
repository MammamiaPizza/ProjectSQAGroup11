package org.jfree.chart.plot.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.awt.Color;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.util.TableOrder;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.junit.Test;

public class MultiplePiePlotGeneratedTest {

    private static final double EPSILON = 0.0000001;

    @Test
    public void defaultConstructorEstablishesDocumentedDefaults() {
        MultiplePiePlot plot = new MultiplePiePlot();

        assertNull(plot.getDataset());
        assertNotNull(plot.getPieChart());
        assertEquals(TableOrder.BY_COLUMN, plot.getDataExtractOrder());
        assertEquals(0.0, plot.getLimit(), EPSILON);
        assertEquals("Other", plot.getAggregatedItemsKey());
        assertEquals(Color.lightGray, plot.getAggregatedItemsPaint());
    }

    @Test
    public void datasetConstructorRetainsSuppliedDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1.0, "R1", "C1");

        MultiplePiePlot plot = new MultiplePiePlot(dataset);

        assertSame(dataset, plot.getDataset());
    }

    @Test
    public void datasetConstructorAcceptsNullDataset() {
        MultiplePiePlot plot = new MultiplePiePlot((CategoryDataset) null);

        assertNull(plot.getDataset());
    }

    @Test
    public void setDatasetReplacesAndCanClearDataset() {
        DefaultCategoryDataset first = new DefaultCategoryDataset();
        first.addValue(1.0, "R1", "C1");
        DefaultCategoryDataset second = new DefaultCategoryDataset();
        second.addValue(2.0, "R2", "C2");
        MultiplePiePlot plot = new MultiplePiePlot(first);

        plot.setDataset(second);
        assertSame(second, plot.getDataset());

        plot.setDataset(null);
        assertNull(plot.getDataset());
    }

    @Test
    public void dataExtractOrderCanBeChanged() {
        MultiplePiePlot plot = new MultiplePiePlot();

        plot.setDataExtractOrder(TableOrder.BY_ROW);

        assertEquals(TableOrder.BY_ROW, plot.getDataExtractOrder());
    }

    @Test
    public void setDataExtractOrderRejectsNull() {
        MultiplePiePlot plot = new MultiplePiePlot();

        try {
            plot.setDataExtractOrder(null);
            fail("Expected IllegalArgumentException for a null order");
        } catch (IllegalArgumentException ex) {
            assertNotNull(ex.getMessage());
        }
    }

    @Test
    public void limitRetainsConfiguredValuesIncludingBoundaryZero() {
        MultiplePiePlot plot = new MultiplePiePlot();

        plot.setLimit(0.25);
        assertEquals(0.25, plot.getLimit(), EPSILON);

        plot.setLimit(0.0);
        assertEquals(0.0, plot.getLimit(), EPSILON);

        plot.setLimit(-1.0);
        assertEquals(-1.0, plot.getLimit(), EPSILON);
    }

    @Test
    public void setPieChartAcceptsChartBasedOnPiePlot() {
        MultiplePiePlot plot = new MultiplePiePlot();
        JFreeChart chart = new JFreeChart(new PiePlot());

        plot.setPieChart(chart);

        assertSame(chart, plot.getPieChart());
    }

    @Test
    public void setPieChartRejectsNull() {
        MultiplePiePlot plot = new MultiplePiePlot();

        try {
            plot.setPieChart(null);
            fail("Expected IllegalArgumentException for a null chart");
        } catch (IllegalArgumentException ex) {
            assertNotNull(ex.getMessage());
        }
    }

    @Test
    public void setPieChartRejectsChartWithoutPiePlot() {
        MultiplePiePlot plot = new MultiplePiePlot();
        JFreeChart nonPieChart = new JFreeChart(new MultiplePiePlot());

        try {
            plot.setPieChart(nonPieChart);
            fail("Expected IllegalArgumentException for a non-pie chart");
        } catch (IllegalArgumentException ex) {
            assertNotNull(ex.getMessage());
        }
    }

    @Test
    public void aggregatedItemPropertiesCanBeConfigured() {
        MultiplePiePlot plot = new MultiplePiePlot();

        plot.setAggregatedItemsKey("Remaining");
        plot.setAggregatedItemsPaint(Color.blue);

        assertEquals("Remaining", plot.getAggregatedItemsKey());
        assertEquals(Color.blue, plot.getAggregatedItemsPaint());
    }

    @Test
    public void aggregatedItemPropertiesRejectNullValues() {
        MultiplePiePlot plot = new MultiplePiePlot();

        try {
            plot.setAggregatedItemsKey(null);
            fail("Expected IllegalArgumentException for a null key");
        } catch (IllegalArgumentException ex) {
            assertNotNull(ex.getMessage());
        }

        try {
            plot.setAggregatedItemsPaint(null);
            fail("Expected IllegalArgumentException for a null paint");
        } catch (IllegalArgumentException ex) {
            assertNotNull(ex.getMessage());
        }
    }

@org.junit.Test
public void drawHandlesANullDataset() {
    org.jfree.chart.plot.MultiplePiePlot plot
            = new org.jfree.chart.plot.MultiplePiePlot(null);
    renderPlot(plot, 120.0, 80.0);
}

@org.junit.Test
public void drawHandlesDataExtractedByColumn() {
    org.jfree.data.category.DefaultCategoryDataset dataset
            = new org.jfree.data.category.DefaultCategoryDataset();
    dataset.addValue(1.0, "R1", "C1");
    dataset.addValue(2.0, "R1", "C2");
    dataset.addValue(3.0, "R1", "C3");
    dataset.addValue(4.0, "R2", "C1");
    dataset.addValue(5.0, "R2", "C2");
    dataset.addValue(6.0, "R2", "C3");

    org.jfree.chart.plot.MultiplePiePlot plot
            = new org.jfree.chart.plot.MultiplePiePlot(dataset);
    renderPlot(plot, 300.0, 100.0);
}

@org.junit.Test
public void drawHandlesDataExtractedByRowInATallArea() {
    org.jfree.data.category.DefaultCategoryDataset dataset
            = new org.jfree.data.category.DefaultCategoryDataset();
    dataset.addValue(1.0, "R1", "C1");
    dataset.addValue(2.0, "R1", "C2");
    dataset.addValue(3.0, "R1", "C3");
    dataset.addValue(4.0, "R2", "C1");
    dataset.addValue(5.0, "R2", "C2");
    dataset.addValue(6.0, "R2", "C3");

    org.jfree.chart.plot.MultiplePiePlot plot
            = new org.jfree.chart.plot.MultiplePiePlot(dataset);
    plot.setDataExtractOrder(TableOrder.BY_ROW);
    renderPlot(plot, 100.0, 300.0);
}

private void renderPlot(org.jfree.chart.plot.MultiplePiePlot plot,
        double width, double height) {
    java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
            (int) width, (int) height,
            java.awt.image.BufferedImage.TYPE_INT_ARGB);
    java.awt.Graphics2D g2 = image.createGraphics();
    try {
        plot.draw(g2, new java.awt.geom.Rectangle2D.Double(
                0.0, 0.0, width, height), null, null, null);
    }
    finally {
        g2.dispose();
    }
}
}
