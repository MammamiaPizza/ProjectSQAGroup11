package org.jfree.chart.plot.junit;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import org.junit.Test;

public class PiePlotNullDatasetTest {

    @Test
    public void testDrawWithExplicitNullDataset() {
        PiePlot plot = new PiePlot(null);

        assertNull(plot.getDataset());

        draw(plot);

        assertNull(plot.getDataset());
    }

    @Test
    public void testDrawWithDefaultConstructorDataset() {
        PiePlot plot = new PiePlot();

        assertNull(plot.getDataset());

        draw(plot);

        assertNull(plot.getDataset());
    }

    @Test
    public void testDrawAfterDatasetIsSetToNull() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("A", 1.0);
        PiePlot plot = new PiePlot(dataset);

        assertSame(dataset, plot.getDataset());

        plot.setDataset(null);
        assertNull(plot.getDataset());

        draw(plot);

        assertNull(plot.getDataset());
    }

    private void draw(PiePlot plot) {
        BufferedImage image = new BufferedImage(
                200, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        try {
            plot.draw(graphics, new Rectangle2D.Double(0.0, 0.0, 200.0, 100.0),
                    null, null, null);
        } finally {
            graphics.dispose();
        }
    }
}