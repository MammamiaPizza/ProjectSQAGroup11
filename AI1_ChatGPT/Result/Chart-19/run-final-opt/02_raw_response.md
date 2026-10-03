package org.jfree.chart.plot.junit;

import static org.junit.Assert.assertEquals;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.junit.Test;

public class CategoryPlotAxisIndexTest {

    @Test
    public void testGetDomainAxisIndexForPrimaryAxis() {
        CategoryPlot plot = new CategoryPlot();
        CategoryAxis axis = new CategoryAxis("Domain");

        plot.setDomainAxis(0, axis);

        assertEquals(0, plot.getDomainAxisIndex(axis));
    }

    @Test
    public void testGetRangeAxisIndexForPrimaryAxis() {
        CategoryPlot plot = new CategoryPlot();
        NumberAxis axis = new NumberAxis("Range");

        plot.setRangeAxis(0, axis);

        assertEquals(0, plot.getRangeAxisIndex(axis));
    }

    @Test
    public void testGetDomainAxisIndexForMultipleAndSparseAxes() {
        CategoryPlot plot = new CategoryPlot();
        CategoryAxis first = new CategoryAxis("First");
        CategoryAxis sparse = new CategoryAxis("Sparse");

        plot.setDomainAxis(1, first);
        plot.setDomainAxis(4, sparse);

        assertEquals(1, plot.getDomainAxisIndex(first));
        assertEquals(4, plot.getDomainAxisIndex(sparse));
    }

    @Test
    public void testGetRangeAxisIndexForMultipleAndSparseAxes() {
        CategoryPlot plot = new CategoryPlot();
        NumberAxis first = new NumberAxis("First");
        NumberAxis sparse = new NumberAxis("Sparse");

        plot.setRangeAxis(1, first);
        plot.setRangeAxis(4, sparse);

        assertEquals(1, plot.getRangeAxisIndex(first));
        assertEquals(4, plot.getRangeAxisIndex(sparse));
    }

    @Test
    public void testGetDomainAxisIndexReturnsMinusOneForAxisNotInPlot() {
        CategoryPlot plot = new CategoryPlot();
        CategoryAxis installed = new CategoryAxis("Installed");
        CategoryAxis missing = new CategoryAxis("Missing");

        plot.setDomainAxis(0, installed);

        assertEquals(-1, plot.getDomainAxisIndex(missing));
    }

    @Test
    public void testGetRangeAxisIndexReturnsMinusOneForAxisNotInPlot() {
        CategoryPlot plot = new CategoryPlot();
        NumberAxis installed = new NumberAxis("Installed");
        NumberAxis missing = new NumberAxis("Missing");

        plot.setRangeAxis(0, installed);

        assertEquals(-1, plot.getRangeAxisIndex(missing));
    }
}