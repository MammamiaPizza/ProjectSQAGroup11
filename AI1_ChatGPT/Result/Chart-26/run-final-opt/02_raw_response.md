package org.jfree.chart.axis.junit;

import java.awt.BasicStroke;
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
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.event.AxisChangeEvent;
import org.jfree.chart.event.AxisChangeListener;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AxisRegressionTest {

    private interface ThrowingAction {
        void run();
    }

    private static class CountingAxisChangeListener implements AxisChangeListener {
        private int count;

        public void axisChanged(AxisChangeEvent event) {
            this.count++;
        }
    }

    private Object serializeAndDeserialize(Object object) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        ObjectOutputStream output = new ObjectOutputStream(buffer);
        output.writeObject(object);
        output.close();

        ObjectInputStream input = new ObjectInputStream(
                new ByteArrayInputStream(buffer.toByteArray()));
        Object result = input.readObject();
        input.close();
        return result;
    }

    private void assertIllegalArgument(ThrowingAction action) {
        try {
            action.run();
            fail("Expected IllegalArgumentException.");
        }
        catch (IllegalArgumentException expected) {
            assertNotNull(expected.getMessage());
        }
    }

    private DefaultCategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(3.0, "S1", "C1");
        dataset.addValue(5.0, "S1", "C2");
        dataset.addValue(2.0, "S2", "C1");
        dataset.addValue(4.0, "S2", "C2");
        return dataset;
    }

    @Test
    public void testCategoryChartDrawsWithNullRenderingInfoWhenAxesHaveLabels() {
        JFreeChart chart = ChartFactory.createBarChart(
                "Chart", "Category", "Value", createDataset(),
                PlotOrientation.VERTICAL, true, true, true);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.getDomainAxis().setLabelToolTip("domain tooltip");
        plot.getDomainAxis().setLabelURL("domain-url");
        plot.getRangeAxis().setLabelToolTip("range tooltip");
        plot.getRangeAxis().setLabelURL("range-url");

        BufferedImage image = new BufferedImage(
                400, 300, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            chart.draw(graphics, new Rectangle2D.Double(0.0, 0.0, 400.0, 300.0),
                    null);
        }
        finally {
            graphics.dispose();
        }

        assertNotNull(chart.getCategoryPlot().getDomainAxis());
        assertNotNull(chart.getCategoryPlot().getRangeAxis());
    }

    @Test
    public void testConfiguredNumberAxisSurvivesSerializationAndRemainsUsable()
            throws Exception {
        NumberAxis axis = new NumberAxis("Values");
        axis.setVisible(false);
        axis.setLabelFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
        axis.setLabelPaint(Color.BLUE);
        axis.setLabelAngle(Math.PI / 4.0);
        axis.setLabelToolTip("value tooltip");
        axis.setLabelURL("value-url");
        axis.setAxisLinePaint(Color.RED);
        axis.setAxisLineStroke(new BasicStroke(2.0f));
        axis.setTickLabelsVisible(false);
        axis.setTickMarkInsideLength(1.5f);
        axis.setTickMarkOutsideLength(4.5f);
        axis.setTickMarkPaint(Color.GREEN);
        axis.setTickMarkStroke(new BasicStroke(3.0f));
        axis.setFixedDimension(17.0);

        NumberAxis restored = (NumberAxis) serializeAndDeserialize(axis);

        assertEquals(axis, restored);
        assertEquals("Values", restored.getLabel());
        assertEquals("value tooltip", restored.getLabelToolTip());
        assertEquals("value-url", restored.getLabelURL());
        assertEquals(17.0, restored.getFixedDimension(), 0.0);

        restored.setLabel("Restored");
        assertEquals("Restored", restored.getLabel());
    }

    @Test
    public void testCategoryPlotSerializationPreservesAxisConfiguration()
            throws Exception {
        CategoryAxis domainAxis = new CategoryAxis("Domain");
        domainAxis.setLabelToolTip("domain tooltip");
        domainAxis.setLabelURL("domain-url");
        NumberAxis rangeAxis = new NumberAxis("Range");
        rangeAxis.setLabelToolTip("range tooltip");
        rangeAxis.setLabelURL("range-url");

        CategoryPlot plot = new CategoryPlot(createDataset(), domainAxis,
                rangeAxis, new BarRenderer());

        CategoryPlot restored = (CategoryPlot) serializeAndDeserialize(plot);

        assertEquals(plot, restored);
        assertEquals("domain tooltip",
                restored.getDomainAxis().getLabelToolTip());
        assertEquals("range-url", restored.getRangeAxis().getLabelURL());
    }

    @Test
    public void testCloneDoesNotRetainOriginalAxisListeners() throws Exception {
        NumberAxis axis = new NumberAxis("Original");
        CountingAxisChangeListener listener = new CountingAxisChangeListener();
        axis.addChangeListener(listener);

        NumberAxis clone = (NumberAxis) axis.clone();

        assertFalse(clone.hasListener(listener));
        clone.setLabel("Clone");
        assertEquals(0, listener.count);

        axis.setLabel("Changed original");
        assertEquals(1, listener.count);
        assertEquals("Clone", clone.getLabel());
        assertEquals("Changed original", axis.getLabel());
    }

    @Test
    public void testAxisNotifiesOnlyForActualVisibleAndLabelChanges() {
        NumberAxis axis = new NumberAxis("Axis");
        CountingAxisChangeListener listener = new CountingAxisChangeListener();
        axis.addChangeListener(listener);

        axis.setVisible(true);
        axis.setLabel("Axis");
        assertEquals(0, listener.count);

        axis.setVisible(false);
        axis.setLabel("Changed");
        assertEquals(2, listener.count);

        axis.removeChangeListener(listener);
        axis.setLabel("Changed again");
        assertEquals(2, listener.count);
    }

    @Test
    public void testAxisRejectsNullRequiredVisualProperties() {
        final NumberAxis axis = new NumberAxis("Axis");

        assertIllegalArgument(new ThrowingAction() {
            public void run() {
                axis.setLabelFont(null);
            }
        });
        assertIllegalArgument(new ThrowingAction() {
            public void run() {
                axis.setLabelPaint(null);
            }
        });
        assertIllegalArgument(new ThrowingAction() {
            public void run() {
                axis.setAxisLinePaint(null);
            }
        });
        assertIllegalArgument(new ThrowingAction() {
            public void run() {
                axis.setAxisLineStroke(null);
            }
        });
        assertIllegalArgument(new ThrowingAction() {
            public void run() {
                axis.setTickLabelFont(null);
            }
        });
        assertIllegalArgument(new ThrowingAction() {
            public void run() {
                axis.setTickMarkPaint(null);
            }
        });
        assertTrue(axis.isVisible());
    }
}