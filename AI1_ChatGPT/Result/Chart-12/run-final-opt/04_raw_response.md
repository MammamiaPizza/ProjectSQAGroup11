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