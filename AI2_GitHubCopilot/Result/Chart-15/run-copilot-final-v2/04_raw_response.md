@Test
 public void testCloneWithURLGenerator() throws CloneNotSupportedException {
     PiePlot plot = new PiePlot();
     plot.setURLGenerator(new org.jfree.chart.urls.StandardPieURLGenerator());
     PiePlot clone = (PiePlot) plot.clone();
     assertNotNull(clone.getURLGenerator());
 }

 @Test
 public void testCloneWithLegendLabelGenerator() throws CloneNotSupportedException {
     PiePlot plot = new PiePlot();
     plot.setLegendLabelGenerator(new org.jfree.chart.labels.StandardPieSectionLabelGenerator());
     PiePlot clone = (PiePlot) plot.clone();
     assertNotNull(clone.getLegendLabelGenerator());
 }

 @Test
 public void testCloneWithLegendLabelToolTipGenerator() throws CloneNotSupportedException {
     PiePlot plot = new PiePlot();
     plot.setLegendLabelToolTipGenerator(new
org.jfree.chart.labels.StandardPieSectionLabelGenerator());
     PiePlot clone = (PiePlot) plot.clone();
     assertNotNull(clone.getLegendLabelToolTipGenerator());
 }

 @Test
 public void testCloneWithLegendLabelURLGenerator() throws CloneNotSupportedException {
     PiePlot plot = new PiePlot();
     plot.setLegendLabelURLGenerator(new org.jfree.chart.urls.StandardPieURLGenerator());
     PiePlot clone = (PiePlot) plot.clone();
     assertNotNull(clone.getLegendLabelURLGenerator());
 }