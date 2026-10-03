@Test
public void testAddAnnotation() {
    XYPlot plot = new XYPlot();
    org.jfree.chart.annotations.XYAnnotation annotation = new
org.jfree.chart.annotations.XYAnnotation() {
        public void draw(java.awt.Graphics2D g2, org.jfree.chart.plot.XYPlot plot,
                java.awt.geom.Rectangle2D dataArea, org.jfree.chart.axis.ValueAxis domainAxis,
                org.jfree.chart.axis.ValueAxis rangeAxis, int rendererIndex,
                org.jfree.chart.plot.PlotRenderingInfo info) {
        }
        public void addChangeListener(org.jfree.chart.event.AnnotationChangeListener listener) {
        }
        public void removeChangeListener(org.jfree.chart.event.AnnotationChangeListener listener) {
        }
    };
    plot.addAnnotation(annotation);
}

@Test(expected = IllegalArgumentException.class)
public void testAddAnnotationNull() {
    XYPlot plot = new XYPlot();
    plot.addAnnotation(null);
}

@Test
public void testAddDomainMarkerForeground() {
    XYPlot plot = new XYPlot();
    org.jfree.chart.plot.Marker marker = new org.jfree.chart.plot.ValueMarker(1.0);
    plot.addDomainMarker(0, marker, org.jfree.chart.util.Layer.FOREGROUND);
}

@Test
public void testAddDomainMarkerBackground() {
    XYPlot plot = new XYPlot();
    org.jfree.chart.plot.Marker marker = new org.jfree.chart.plot.ValueMarker(1.0);
    plot.addDomainMarker(0, marker, org.jfree.chart.util.Layer.BACKGROUND);
}