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