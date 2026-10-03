package org.jfree.chart.renderer.category.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.junit.Test;

public class AbstractCategoryItemRendererLegendItemsTest {

    private DefaultCategoryDataset createDataset(String... seriesKeys) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < seriesKeys.length; i++) {
            dataset.addValue(i + 1.0, seriesKeys[i], "Category");
        }
        return dataset;
    }

    @Test
    public void getLegendItemsIncludesVisibleSeries() {
        DefaultCategoryDataset dataset = createDataset("Series 1");
        CategoryPlot plot = new CategoryPlot();
        BarRenderer renderer = new BarRenderer();
        plot.setDataset(dataset);
        plot.setRenderer(renderer);

        LegendItemCollection items = renderer.getLegendItems();

        assertEquals(1, items.getItemCount());
        assertEquals("Series 1", items.get(0).getLabel());
        assertEquals(0, items.get(0).getSeriesIndex());
    }

    @Test
    public void getLegendItemsIncludesEveryEligibleSeries() {
        DefaultCategoryDataset dataset = createDataset("First", "Second", "Third");
        CategoryPlot plot = new CategoryPlot();
        BarRenderer renderer = new BarRenderer();
        plot.setDataset(dataset);
        plot.setRenderer(renderer);

        LegendItemCollection items = renderer.getLegendItems();

        assertEquals(3, items.getItemCount());
        assertEquals("First", items.get(0).getLabel());
        assertEquals("Second", items.get(1).getLabel());
        assertEquals("Third", items.get(2).getLabel());
    }

    @Test
    public void getLegendItemsExcludesSeriesHiddenFromPlotRendering() {
        DefaultCategoryDataset dataset = createDataset("Visible", "Hidden");
        CategoryPlot plot = new CategoryPlot();
        BarRenderer renderer = new BarRenderer();
        plot.setDataset(dataset);
        plot.setRenderer(renderer);
        renderer.setSeriesVisible(1, Boolean.FALSE);

        LegendItemCollection items = renderer.getLegendItems();

        assertEquals(1, items.getItemCount());
        assertEquals("Visible", items.get(0).getLabel());
    }

    @Test
    public void getLegendItemsExcludesSeriesHiddenOnlyFromLegend() {
        DefaultCategoryDataset dataset = createDataset("Visible", "No legend");
        CategoryPlot plot = new CategoryPlot();
        BarRenderer renderer = new BarRenderer();
        plot.setDataset(dataset);
        plot.setRenderer(renderer);
        renderer.setSeriesVisibleInLegend(1, Boolean.FALSE);

        LegendItemCollection items = renderer.getLegendItems();

        assertEquals(1, items.getItemCount());
        assertEquals("Visible", items.get(0).getLabel());
    }

    @Test
    public void getLegendItemsUsesDatasetAssignedToRendererIndex() {
        DefaultCategoryDataset primaryDataset = createDataset("Primary");
        DefaultCategoryDataset secondaryDataset = createDataset("Secondary");
        CategoryPlot plot = new CategoryPlot();
        BarRenderer renderer = new BarRenderer();
        plot.setDataset(0, primaryDataset);
        plot.setDataset(1, secondaryDataset);
        plot.setRenderer(1, renderer);

        LegendItemCollection items = renderer.getLegendItems();

        assertEquals(1, items.getItemCount());
        assertEquals("Secondary", items.get(0).getLabel());
        assertEquals(1, items.get(0).getDatasetIndex());
    }

    @Test
    public void getLegendItemReturnsNullWhenRendererHasNoPlot() {
        BarRenderer renderer = new BarRenderer();

        assertNull(renderer.getLegendItem(0, 0));
    }
}
