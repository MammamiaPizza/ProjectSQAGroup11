package org.jfree.chart.renderer.category.junit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.image.BufferedImage;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.StatisticalBarRenderer;
import org.jfree.data.statistics.DefaultStatisticalCategoryDataset;
import org.junit.Test;

public class StatisticalBarRendererNullValueTest {

    private ChartRenderingInfo render(DefaultStatisticalCategoryDataset dataset,
                                      PlotOrientation orientation) {
        StatisticalBarRenderer renderer = new StatisticalBarRenderer();
        CategoryPlot plot = new CategoryPlot(
                dataset,
                new CategoryAxis("Category"),
                new NumberAxis("Value"),
                renderer);
        plot.setOrientation(orientation);

        JFreeChart chart = new JFreeChart("Test", JFreeChart.DEFAULT_TITLE_FONT,
                plot, false);
        ChartRenderingInfo info = new ChartRenderingInfo();
        BufferedImage image = chart.createBufferedImage(300, 200, info);

        assertNotNull(image);
        assertNotNull(info.getEntityCollection());
        return info;
    }

    @Test
    public void testDrawWithNullMeanVerticalDoesNotCreateEntity() {
        DefaultStatisticalCategoryDataset dataset =
                new DefaultStatisticalCategoryDataset();
        dataset.add(null, Double.valueOf(4.0), "S1", "C1");

        ChartRenderingInfo info = render(dataset, PlotOrientation.VERTICAL);

        assertEquals(0, info.getEntityCollection().getEntityCount());
    }

    @Test
    public void testDrawWithNullDeviationVerticalCreatesBarEntity() {
        DefaultStatisticalCategoryDataset dataset =
                new DefaultStatisticalCategoryDataset();
        dataset.add(Double.valueOf(4.0), null, "S1", "C1");

        ChartRenderingInfo info = render(dataset, PlotOrientation.VERTICAL);

        assertEquals(1, info.getEntityCollection().getEntityCount());
    }

    @Test
    public void testDrawWithNullMeanHorizontalDoesNotCreateEntity() {
        DefaultStatisticalCategoryDataset dataset =
                new DefaultStatisticalCategoryDataset();
        dataset.add(null, Double.valueOf(4.0), "S1", "C1");

        ChartRenderingInfo info = render(dataset, PlotOrientation.HORIZONTAL);

        assertEquals(0, info.getEntityCollection().getEntityCount());
    }

    @Test
    public void testDrawWithNullDeviationHorizontalCreatesBarEntity() {
        DefaultStatisticalCategoryDataset dataset =
                new DefaultStatisticalCategoryDataset();
        dataset.add(Double.valueOf(4.0), null, "S1", "C1");

        ChartRenderingInfo info = render(dataset, PlotOrientation.HORIZONTAL);

        assertEquals(1, info.getEntityCollection().getEntityCount());
    }
}
