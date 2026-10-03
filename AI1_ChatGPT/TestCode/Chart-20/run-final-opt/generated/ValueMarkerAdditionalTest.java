package org.jfree.chart.plot.junit;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;

import org.jfree.chart.plot.ValueMarker;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ValueMarkerAdditionalTest {

    @Test
    public void testConstructorRetainsPrimaryAndOutlineStyles() {
        Stroke stroke = new BasicStroke(1.0f);
        Stroke outlineStroke = new BasicStroke(2.0f);

        ValueMarker marker = new ValueMarker(12.5, Color.RED, stroke,
                Color.BLUE, outlineStroke, 0.35f);

        assertEquals(Color.RED, marker.getPaint());
        assertEquals(stroke, marker.getStroke());
        assertEquals(Color.BLUE, marker.getOutlinePaint());
        assertEquals(outlineStroke, marker.getOutlineStroke());
        assertEquals(0.35f, marker.getAlpha(), 0.0f);
    }

    @Test
    public void testConstructorAllowsNullOutlineStyle() {
        Stroke stroke = new BasicStroke(1.0f);

        ValueMarker marker = new ValueMarker(3.0, Color.BLUE, stroke,
                null, null, 1.0f);

        assertEquals(Color.BLUE, marker.getPaint());
        assertNull(marker.getOutlinePaint());
        assertNull(marker.getOutlineStroke());
    }

    @Test
    public void testThreeArgumentConstructorUsesPaintForMarkerAndOutline() {
        Stroke stroke = new BasicStroke(1.5f);

        ValueMarker marker = new ValueMarker(8.0, Color.BLUE, stroke);

        assertEquals(Color.BLUE, marker.getPaint());
        assertEquals(Color.BLUE, marker.getOutlinePaint());
        assertEquals(stroke, marker.getStroke());
        assertEquals(stroke, marker.getOutlineStroke());
    }

    @Test
    public void testValueCanBeReadAndChanged() {
        ValueMarker marker = new ValueMarker(-2.5);

        assertEquals(-2.5, marker.getValue(), 0.0);

        marker.setValue(7.25);

        assertEquals(7.25, marker.getValue(), 0.0);
    }

    @Test
    public void testEqualsConsidersValueAndMarkerStyle() {
        Stroke stroke = new BasicStroke(1.0f);
        ValueMarker marker1 = new ValueMarker(4.0, Color.RED, stroke);
        ValueMarker marker2 = new ValueMarker(4.0, Color.RED,
                new BasicStroke(1.0f));
        ValueMarker differentValue = new ValueMarker(5.0, Color.RED,
                new BasicStroke(1.0f));
        ValueMarker differentPaint = new ValueMarker(4.0, Color.BLUE,
                new BasicStroke(1.0f));

        assertTrue(marker1.equals(marker2));
        assertTrue(marker2.equals(marker1));
        assertFalse(marker1.equals(differentValue));
        assertFalse(marker1.equals(differentPaint));
        assertFalse(marker1.equals(null));
        assertFalse(marker1.equals("not a marker"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRejectsNullPaint() {
        new ValueMarker(1.0, null, new BasicStroke(1.0f));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorRejectsNullStroke() {
        new ValueMarker(1.0, Color.BLACK, null);
    }
}
