@Test
public void testDrawWithNonNullDataset() {
    org.jfree.data.category.DefaultCategoryDataset dataset = new
org.jfree.data.category.DefaultCategoryDataset();
    dataset.addValue(1.0, "R1", "C1");
    dataset.addValue(2.0, "R1", "C2");
    dataset.addValue(3.0, "R2", "C1");
    dataset.addValue(4.0, "R2", "C2");
    MultiplePiePlot plot = new MultiplePiePlot(dataset);
    org.jfree.chart.JFreeChart chart = new org.jfree.chart.JFreeChart(plot);
    java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
            200, 100, java.awt.image.BufferedImage.TYPE_INT_ARGB);
    java.awt.Graphics2D g2 = image.createGraphics();
    chart.draw(g2, new java.awt.Rectangle(0, 0, 200, 100), null, null);
    g2.dispose();
}

@Test
public void testDrawWithNullDataset() {
    MultiplePiePlot plot = new MultiplePiePlot();
    org.jfree.chart.JFreeChart chart = new org.jfree.chart.JFreeChart(plot);
    java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
            200, 100, java.awt.image.BufferedImage.TYPE_INT_ARGB);
    java.awt.Graphics2D g2 = image.createGraphics();
    chart.draw(g2, new java.awt.Rectangle(0, 0, 200, 100), null, null);
    g2.dispose();
}

@Test
public void testDrawWithDataExtractOrderByRow() {
    org.jfree.data.category.DefaultCategoryDataset dataset = new
org.jfree.data.category.DefaultCategoryDataset();
    dataset.addValue(1.0, "R1", "C1");
    dataset.addValue(2.0, "R2", "C1");
    MultiplePiePlot plot = new MultiplePiePlot(dataset);
    plot.setDataExtractOrder(TableOrder.BY_ROW);
    org.jfree.chart.JFreeChart chart = new org.jfree.chart.JFreeChart(plot);
    java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
            200, 100, java.awt.image.BufferedImage.TYPE_INT_ARGB);
    java.awt.Graphics2D g2 = image.createGraphics();
    chart.draw(g2, new java.awt.Rectangle(0, 0, 200, 100), null, null);
    g2.dispose();
}

@Test
public void testGetLegendItemsWithNonNullDataset() {
    org.jfree.data.category.DefaultCategoryDataset dataset = new
org.jfree.data.category.DefaultCategoryDataset();
    dataset.addValue(1.0, "R1", "C1");
    dataset.addValue(2.0, "R2", "C1");
    MultiplePiePlot plot = new MultiplePiePlot(dataset);
    LegendItemCollection items = plot.getLegendItems();
    assertNotNull(items);
    assertTrue(items.getItemCount() > 0);
}