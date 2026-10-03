@org.junit.Test
public void testNumberAxisDrawsLabeledAxisWithNullRenderingInfoOnAllEdges() {
    java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(
            200, 200, java.awt.image.BufferedImage.TYPE_INT_ARGB);
    java.awt.Graphics2D g2 = image.createGraphics();
    try {
        org.jfree.chart.axis.NumberAxis axis
                = new org.jfree.chart.axis.NumberAxis("Axis Label");
        java.awt.geom.Rectangle2D area
                = new java.awt.geom.Rectangle2D.Double(20.0, 20.0, 160.0, 160.0);

        org.junit.Assert.assertNotNull(axis.draw(g2, 20.0, area, area,
                org.jfree.ui.RectangleEdge.TOP, null));
        org.junit.Assert.assertNotNull(axis.draw(g2, 180.0, area, area,
                org.jfree.ui.RectangleEdge.BOTTOM, null));
        org.junit.Assert.assertNotNull(axis.draw(g2, 20.0, area, area,
                org.jfree.ui.RectangleEdge.LEFT, null));
        org.junit.Assert.assertNotNull(axis.draw(g2, 180.0, area, area,
                org.jfree.ui.RectangleEdge.RIGHT, null));
    }
    finally {
        g2.dispose();
    }
}