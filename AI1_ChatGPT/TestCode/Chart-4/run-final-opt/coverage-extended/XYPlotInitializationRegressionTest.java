package org.jfree.chart.plot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.junit.Test;

public class XYPlotInitializationRegressionTest {

    private XYSeriesCollection createDataset(double x, double y) {
        XYSeries series = new XYSeries("S");
        series.add(x, y);
        return new XYSeriesCollection(series);
    }

    @Test
    public void testDefaultPlotInitializesQuadrantPaintStorage() {
        XYPlot plot = new XYPlot();

        assertNull(plot.getQuadrantPaint(0));

        plot.setQuadrantPaint(0, Color.RED);
        assertEquals(Color.RED, plot.getQuadrantPaint(0));
    }

    @Test
    public void testNumberAxesAreConfiguredForInitialDataset() {
        XYSeriesCollection dataset = createDataset(2.0, 10.0);
        NumberAxis domain = new NumberAxis("X");
        NumberAxis range = new NumberAxis("Y");

        new XYPlot(dataset, domain, range, new StandardXYItemRenderer());

        assertTrue(domain.getRange().getLowerBound() <= 2.0);
        assertTrue(domain.getRange().getUpperBound() >= 2.0);
        assertTrue(range.getRange().getLowerBound() <= 10.0);
        assertTrue(range.getRange().getUpperBound() >= 10.0);
    }

    @Test
    public void testLogAxisIsConfiguredForPositiveDataset() {
        XYSeriesCollection dataset = createDataset(1.0, 100.0);
        LogAxis domain = new LogAxis("X");
        LogAxis range = new LogAxis("Y");

        new XYPlot(dataset, domain, range, new StandardXYItemRenderer());

        assertTrue(domain.getRange().getLowerBound() > 0.0);
        assertTrue(domain.getRange().getLowerBound() <= 1.0);
        assertTrue(domain.getRange().getUpperBound() >= 1.0);
        assertTrue(range.getRange().getLowerBound() > 0.0);
        assertTrue(range.getRange().getUpperBound() >= 100.0);
    }

    @Test
    public void testAxisMarginsAreAppliedWhenAxesAreConfigured() {
        XYSeriesCollection dataset = createDataset(1.0, 5.0);
        NumberAxis domain = new NumberAxis("X");
        domain.setAutoRangeIncludesZero(false);
        domain.setLowerMargin(0.25);
        domain.setUpperMargin(0.25);

        XYSeries series = dataset.getSeries(0);
        series.add(9.0, 6.0);

        XYPlot plot = new XYPlot(dataset, domain, new NumberAxis("Y"),
                new StandardXYItemRenderer());
        plot.configureDomainAxes();

        assertTrue(domain.getRange().getLowerBound() < 1.0);
        assertTrue(domain.getRange().getUpperBound() > 9.0);
    }

    @Test
    public void testReplacingDatasetUpdatesPlotAndAutoRange() {
        XYSeriesCollection first = createDataset(1.0, 2.0);
        NumberAxis range = new NumberAxis("Y");
        XYPlot plot = new XYPlot(first, new NumberAxis("X"), range,
                new StandardXYItemRenderer());

        XYSeriesCollection replacement = createDataset(4.0, 50.0);
        plot.setDataset(replacement);

        assertSame(replacement, plot.getDataset());
        assertTrue(range.getRange().getUpperBound() >= 50.0);
    }

    @Test
    public void testSeriesToolTipGeneratorCanBeInstalledOnRenderer() {
        XYSeriesCollection dataset = createDataset(1.0, 2.0);
        StandardXYItemRenderer renderer = new StandardXYItemRenderer();
        XYPlot plot = new XYPlot(dataset, new NumberAxis("X"),
                new NumberAxis("Y"), renderer);
        StandardXYToolTipGenerator generator = new StandardXYToolTipGenerator();

        renderer.setSeriesToolTipGenerator(0, generator);

        assertSame(generator, renderer.getSeriesToolTipGenerator(0));
        assertNotNull(renderer.getToolTipGenerator(0, 0, false));
        assertSame(renderer, plot.getRenderer());
    }

    @Test
    public void testChartDrawAcceptsNullRenderingInfo() {
        JFreeChart chart = ChartFactory.createScatterPlot("Scatter", "X", "Y",
                createDataset(2.0, 3.0), PlotOrientation.VERTICAL, true, true,
                false);
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setRangeGridlinePaint(Color.BLACK);

        BufferedImage image = new BufferedImage(240, 180,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        try {
            chart.draw(g2, new Rectangle2D.Double(0.0, 0.0, 240.0, 180.0),
                    null);
        } finally {
            g2.dispose();
        }

        assertTrue(plot.isRangeGridlinesVisible());
        assertTrue(image.getRGB(120, 90) != 0);
    }

    @Test
    public void testChartContainingXYPlotCanBeSerialized() throws Exception {
        JFreeChart chart = ChartFactory.createScatterPlot("Scatter", "X", "Y",
                createDataset(1.0, 2.0), PlotOrientation.VERTICAL, true, true,
                false);

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(bytes);
        output.writeObject(chart);
        output.close();

        ObjectInputStream input = new ObjectInputStream(
                new ByteArrayInputStream(bytes.toByteArray()));
        JFreeChart restored = (JFreeChart) input.readObject();
        input.close();

        assertNotNull(restored);
        assertTrue(restored.getPlot() instanceof XYPlot);
        assertNotNull(((XYPlot) restored.getPlot()).getDataset());
    }

@Test
public void testAddAnnotationStoresAnnotation() {
    org.jfree.chart.plot.XYPlot plot = new org.jfree.chart.plot.XYPlot();
    org.jfree.chart.annotations.XYTextAnnotation annotation
            = new org.jfree.chart.annotations.XYTextAnnotation("A", 1.0, 2.0);

    plot.addAnnotation(annotation, false);

    org.junit.Assert.assertTrue(plot.getAnnotations().contains(annotation));
}

@Test(expected = IllegalArgumentException.class)
public void testAddAnnotationRejectsNullAnnotation() {
    org.jfree.chart.plot.XYPlot plot = new org.jfree.chart.plot.XYPlot();

    plot.addAnnotation(null);
}

@Test
public void testAddDomainMarkersSeparatesLayersAndIndices() {
    org.jfree.chart.plot.XYPlot plot = new org.jfree.chart.plot.XYPlot();
    org.jfree.chart.plot.ValueMarker foreground
            = new org.jfree.chart.plot.ValueMarker(1.0);
    org.jfree.chart.plot.ValueMarker background
            = new org.jfree.chart.plot.ValueMarker(2.0);

    plot.addDomainMarker(1, foreground, org.jfree.ui.Layer.FOREGROUND, false);
    plot.addDomainMarker(1, background, org.jfree.ui.Layer.BACKGROUND, false);

    org.junit.Assert.assertTrue(plot.getDomainMarkers(1,
            org.jfree.ui.Layer.FOREGROUND).contains(foreground));
    org.junit.Assert.assertTrue(plot.getDomainMarkers(1,
            org.jfree.ui.Layer.BACKGROUND).contains(background));
}

@Test(expected = IllegalArgumentException.class)
public void testAddDomainMarkerRejectsNullMarker() {
    org.jfree.chart.plot.XYPlot plot = new org.jfree.chart.plot.XYPlot();

    plot.addDomainMarker(0, null, org.jfree.ui.Layer.FOREGROUND);
}
}
