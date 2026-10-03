@org.junit.Test
public void testDrawWithDataInBothDirections() {
    org.jfree.data.general.DefaultPieDataset dataset
            = new org.jfree.data.general.DefaultPieDataset();
    dataset.setValue("A", new Double(3.0));
    dataset.setValue("B", new Double(1.0));

    PiePlot plot = new PiePlot(dataset);
    plot.setSectionPaint("A", java.awt.Color.red);
    plot.setSectionPaint("B", java.awt.Color.blue);

    java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
            200, 200, java.awt.image.BufferedImage.TYPE_INT_ARGB);
    java.awt.Graphics2D g2 = image.createGraphics();
    try {
        java.awt.geom.Rectangle2D area
                = new java.awt.geom.Rectangle2D.Double(0.0, 0.0, 200.0, 200.0);

        plot.setDirection(org.jfree.util.Rotation.CLOCKWISE);
        plot.draw(g2, area, null, null, null);

        plot.setDirection(org.jfree.util.Rotation.ANTICLOCKWISE);
        plot.draw(g2, area, null, null, null);
    }
    finally {
        g2.dispose();
    }

    org.junit.Assert.assertTrue(image.getRGB(100, 100) != 0);
}

@org.junit.Test
public void testCloneClonesPublicCloneableGenerators()
        throws CloneNotSupportedException {
    PiePlot plot = new PiePlot();
    plot.setURLGenerator(new org.jfree.chart.urls.StandardPieURLGenerator());
    plot.setLegendLabelGenerator(
            new org.jfree.chart.labels.StandardPieSectionLabelGenerator());
    plot.setLegendLabelToolTipGenerator(
            new org.jfree.chart.labels.StandardPieSectionLabelGenerator());
    plot.setLegendLabelURLGenerator(
            new org.jfree.chart.urls.StandardPieURLGenerator());

    PiePlot clone = (PiePlot) plot.clone();

    org.junit.Assert.assertNotSame(plot, clone);
    org.junit.Assert.assertNotSame(plot.getURLGenerator(),
            clone.getURLGenerator());
    org.junit.Assert.assertNotSame(plot.getLegendLabelGenerator(),
            clone.getLegendLabelGenerator());
    org.junit.Assert.assertNotSame(plot.getLegendLabelToolTipGenerator(),
            clone.getLegendLabelToolTipGenerator());
    org.junit.Assert.assertNotSame(plot.getLegendLabelURLGenerator(),
            clone.getLegendLabelURLGenerator());
}