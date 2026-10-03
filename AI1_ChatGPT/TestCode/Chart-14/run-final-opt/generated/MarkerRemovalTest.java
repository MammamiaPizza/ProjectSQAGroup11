package org.jfree.chart.plot.junit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.ui.Layer;
import org.junit.Test;

public class MarkerRemovalTest {

    @Test
    public void testCategoryPlotRemovesDefaultDomainMarker() {
        CategoryPlot plot = new CategoryPlot();
        CategoryMarker marker = new CategoryMarker("Category 1");

        plot.addDomainMarker(marker);

        assertTrue(plot.removeDomainMarker(marker));
        assertFalse(plot.removeDomainMarker(marker));
    }

    @Test
    public void testCategoryPlotRemovesDefaultRangeMarker() {
        CategoryPlot plot = new CategoryPlot();
        ValueMarker marker = new ValueMarker(1.0);

        plot.addRangeMarker(marker);

        assertTrue(plot.removeRangeMarker(marker));
        assertFalse(plot.removeRangeMarker(marker));
    }

    @Test
    public void testXYPlotRemovesDefaultDomainMarker() {
        XYPlot plot = new XYPlot();
        ValueMarker marker = new ValueMarker(1.0);

        plot.addDomainMarker(marker);

        assertTrue(plot.removeDomainMarker(marker));
        assertFalse(plot.removeDomainMarker(marker));
    }

    @Test
    public void testXYPlotRemovesDefaultRangeMarker() {
        XYPlot plot = new XYPlot();
        ValueMarker marker = new ValueMarker(1.0);

        plot.addRangeMarker(marker);

        assertTrue(plot.removeRangeMarker(marker));
        assertFalse(plot.removeRangeMarker(marker));
    }

    @Test
    public void testCategoryPlotRemovesBackgroundDomainMarkerAtIndexZero() {
        CategoryPlot plot = new CategoryPlot();
        CategoryMarker marker = new CategoryMarker("Category 1");

        plot.addDomainMarker(0, marker, Layer.BACKGROUND);

        assertTrue(plot.removeDomainMarker(0, marker, Layer.BACKGROUND));
        assertFalse(plot.removeDomainMarker(0, marker, Layer.BACKGROUND));
    }

    @Test
    public void testCategoryPlotRemovesBackgroundRangeMarkerAtIndexZero() {
        CategoryPlot plot = new CategoryPlot();
        ValueMarker marker = new ValueMarker(2.0);

        plot.addRangeMarker(0, marker, Layer.BACKGROUND);

        assertTrue(plot.removeRangeMarker(0, marker, Layer.BACKGROUND));
        assertFalse(plot.removeRangeMarker(0, marker, Layer.BACKGROUND));
    }

    @Test
    public void testXYPlotRemovesBackgroundDomainMarkerAtIndexZero() {
        XYPlot plot = new XYPlot();
        ValueMarker marker = new ValueMarker(2.0);

        plot.addDomainMarker(0, marker, Layer.BACKGROUND);

        assertTrue(plot.removeDomainMarker(0, marker, Layer.BACKGROUND));
        assertFalse(plot.removeDomainMarker(0, marker, Layer.BACKGROUND));
    }

    @Test
    public void testXYPlotRemovesBackgroundRangeMarkerAtIndexZero() {
        XYPlot plot = new XYPlot();
        ValueMarker marker = new ValueMarker(2.0);

        plot.addRangeMarker(0, marker, Layer.BACKGROUND);

        assertTrue(plot.removeRangeMarker(0, marker, Layer.BACKGROUND));
        assertFalse(plot.removeRangeMarker(0, marker, Layer.BACKGROUND));
    }
}
