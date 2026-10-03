@org.junit.Test
public void testGetDomainAxisIndexUsesAxisIdentityForEqualAxes() {
    org.jfree.chart.plot.CategoryPlot plot = new org.jfree.chart.plot.CategoryPlot();
    org.jfree.chart.axis.CategoryAxis first
            = new org.jfree.chart.axis.CategoryAxis("Domain");
    org.jfree.chart.axis.CategoryAxis second
            = new org.jfree.chart.axis.CategoryAxis("Domain");
    plot.setDomainAxis(0, first);
    plot.setDomainAxis(1, second);

    org.junit.Assert.assertEquals(1, plot.getDomainAxisIndex(second));
}

@org.junit.Test
public void testGetRangeAxisIndexUsesAxisIdentityForEqualAxes() {
    org.jfree.chart.plot.CategoryPlot plot = new org.jfree.chart.plot.CategoryPlot();
    org.jfree.chart.axis.NumberAxis first
            = new org.jfree.chart.axis.NumberAxis("Range");
    org.jfree.chart.axis.NumberAxis second
            = new org.jfree.chart.axis.NumberAxis("Range");
    plot.setRangeAxis(0, first);
    plot.setRangeAxis(1, second);

    org.junit.Assert.assertEquals(1, plot.getRangeAxisIndex(second));
}