package org.jfree.chart.renderer.category.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.CategorySeriesLabelGenerator;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.renderer.category.AbstractCategoryItemRenderer;
import org.jfree.chart.renderer.category.CategoryItemRendererState;
import org.jfree.chart.urls.CategoryURLGenerator;
import org.jfree.data.Range;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.junit.Test;

/**
 * Tests for {@link AbstractCategoryItemRenderer}.
 */
public class AbstractCategoryItemRendererGeneratedTest {

    /**
     * Concrete renderer used to exercise the non-abstract behavior in
     * {@link AbstractCategoryItemRenderer}.
     */
    private static class TestRenderer extends AbstractCategoryItemRenderer {

        private static final long serialVersionUID = 1L;

        /**
         * Implements the CategoryItemRenderer method signature used by this
         * version of JFreeChart.
         */
        public void drawItem(Graphics2D g2, CategoryItemRendererState state,
                Rectangle2D dataArea, CategoryPlot plot,
                CategoryAxis domainAxis, ValueAxis rangeAxis,
                CategoryDataset dataset, int row, int column,
                boolean selected, int pass) {
            // No drawing is required for these tests.
        }
    }

    /**
     * Regression test for bug 2947660 / Chart-1.  A renderer assigned to a
     * plot with a dataset must generate legend items for its dataset series.
     */
    @Test
    public void testGetLegendItemsReturnsOneItemForAssignedDataset() {
        TestRenderer renderer = new TestRenderer();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1.0, "Series 1", "Category 1");

        renderer.setLegendItemLabelGenerator(
                new CategorySeriesLabelGenerator() {
                    public String generateLabel(CategoryDataset d,
                            int series) {
                        return "label-" + d.getRowKey(series);
                    }
                });
        renderer.setLegendItemToolTipGenerator(
                new CategorySeriesLabelGenerator() {
                    public String generateLabel(CategoryDataset d,
                            int series) {
                        return "tooltip-" + series;
                    }
                });
        renderer.setLegendItemURLGenerator(
                new CategorySeriesLabelGenerator() {
                    public String generateLabel(CategoryDataset d,
                            int series) {
                        return "url-" + series;
                    }
                });

        new CategoryPlot(dataset, new CategoryAxis(), new NumberAxis(),
                renderer);

        LegendItemCollection items = renderer.getLegendItems();

        assertEquals(1, items.getItemCount());
        LegendItem item = items.get(0);
        assertEquals("label-Series 1", item.getLabel());
        assertEquals("tooltip-0", item.getToolTipText());
        assertEquals("url-0", item.getURLText());
        assertEquals("Series 1", item.getSeriesKey());
        assertEquals(0, item.getSeriesIndex());
        assertSame(dataset, item.getDataset());
        assertEquals(0, item.getDatasetIndex());
    }

    @Test
    public void testGetLegendItemsWithNoDatasetReturnsEmptyCollection() {
        TestRenderer renderer = new TestRenderer();
        new CategoryPlot(null, new CategoryAxis(), new NumberAxis(), renderer);

        LegendItemCollection items = renderer.getLegendItems();

        assertNotNull(items);
        assertEquals(0, items.getItemCount());
    }

    @Test
    public void testGeneratorResolutionUsesSeriesGeneratorBeforeBaseGenerator() {
        TestRenderer renderer = new TestRenderer();

        CategoryItemLabelGenerator baseLabel =
                new CategoryItemLabelGenerator() {
                    public String generateLabel(CategoryDataset dataset,
                            int row, int column) {
                        return "base-label";
                    }
                };
        CategoryItemLabelGenerator seriesLabel =
                new CategoryItemLabelGenerator() {
                    public String generateLabel(CategoryDataset dataset,
                            int row, int column) {
                        return "series-label";
                    }
                };

        CategoryToolTipGenerator baseToolTip =
                new CategoryToolTipGenerator() {
                    public String generateToolTip(CategoryDataset dataset,
                            int row, int column) {
                        return "base-tooltip";
                    }
                };
        CategoryToolTipGenerator seriesToolTip =
                new CategoryToolTipGenerator() {
                    public String generateToolTip(CategoryDataset dataset,
                            int row, int column) {
                        return "series-tooltip";
                    }
                };

        CategoryURLGenerator baseURL =
                new CategoryURLGenerator() {
                    public String generateURL(CategoryDataset dataset,
                            int row, int column) {
                        return "base-url";
                    }
                };
        CategoryURLGenerator seriesURL =
                new CategoryURLGenerator() {
                    public String generateURL(CategoryDataset dataset,
                            int row, int column) {
                        return "series-url";
                    }
                };

        assertNull(renderer.getItemLabelGenerator(0, 0, false));
        assertNull(renderer.getToolTipGenerator(0, 0, false));
        assertNull(renderer.getURLGenerator(0, 0, false));

        renderer.setBaseItemLabelGenerator(baseLabel, false);
        renderer.setBaseToolTipGenerator(baseToolTip, false);
        renderer.setBaseURLGenerator(baseURL, false);

        assertSame(baseLabel, renderer.getItemLabelGenerator(3, 1, false));
        assertSame(baseToolTip, renderer.getToolTipGenerator(3, 1, false));
        assertSame(baseURL, renderer.getURLGenerator(3, 1, false));

        renderer.setSeriesItemLabelGenerator(3, seriesLabel, false);
        renderer.setSeriesToolTipGenerator(3, seriesToolTip, false);
        renderer.setSeriesURLGenerator(3, seriesURL, false);

        assertSame(seriesLabel, renderer.getItemLabelGenerator(3, 1, true));
        assertSame(seriesToolTip, renderer.getToolTipGenerator(3, 1, true));
        assertSame(seriesURL, renderer.getURLGenerator(3, 1, true));

        renderer.setSeriesItemLabelGenerator(3, null, false);
        renderer.setSeriesToolTipGenerator(3, null, false);
        renderer.setSeriesURLGenerator(3, null, false);

        assertSame(baseLabel, renderer.getItemLabelGenerator(3, 1, false));
        assertSame(baseToolTip, renderer.getToolTipGenerator(3, 1, false));
        assertSame(baseURL, renderer.getURLGenerator(3, 1, false));
    }

    @Test
    public void testInitialiseUpdatesCountsAndReturnsState() {
        TestRenderer renderer = new TestRenderer();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(1.0, "S1", "C1");
        dataset.addValue(2.0, "S1", "C2");
        dataset.addValue(3.0, "S2", "C1");

        CategoryPlot plot = new CategoryPlot(dataset, new CategoryAxis(),
                new NumberAxis(), renderer);
        BufferedImage image = new BufferedImage(20, 20,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        try {
            CategoryItemRendererState state = renderer.initialise(g2,
                    new Rectangle2D.Double(0.0, 0.0, 20.0, 20.0), plot,
                    dataset, (PlotRenderingInfo) null);

            assertNotNull(state);
            assertSame(plot, renderer.getPlot());
            assertEquals(2, renderer.getRowCount());
            assertEquals(2, renderer.getColumnCount());
        }
        finally {
            g2.dispose();
        }
    }

    @Test
    public void testInitialiseWithNullDatasetResetsCountsToZero() {
        TestRenderer renderer = new TestRenderer();
        CategoryPlot plot = new CategoryPlot(null, new CategoryAxis(),
                new NumberAxis(), renderer);
        BufferedImage image = new BufferedImage(10, 10,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        try {
            CategoryItemRendererState state = renderer.initialise(g2,
                    new Rectangle2D.Double(0.0, 0.0, 10.0, 10.0), plot,
                    null, null);

            assertNotNull(state);
            assertEquals(0, renderer.getRowCount());
            assertEquals(0, renderer.getColumnCount());
        }
        finally {
            g2.dispose();
        }
    }

    @Test
    public void testFindRangeBoundsForValuesAndNullDataset() {
        TestRenderer renderer = new TestRenderer();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(-2.0, "S1", "C1");
        dataset.addValue(5.0, "S1", "C2");
        dataset.addValue(1.0, "S2", "C1");

        assertEquals(new Range(-2.0, 5.0), renderer.findRangeBounds(dataset));
        assertNull(renderer.findRangeBounds(null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetPlotRejectsNullPlot() {
        new TestRenderer().setPlot(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetLegendItemLabelGeneratorRejectsNullGenerator() {
        new TestRenderer().setLegendItemLabelGenerator(null);
    }

    @Test
    public void testDefaultPassCountIsOne() {
        TestRenderer renderer = new TestRenderer();

        assertEquals(1, renderer.getPassCount());
        assertTrue(renderer.equals(renderer));
    }
}
