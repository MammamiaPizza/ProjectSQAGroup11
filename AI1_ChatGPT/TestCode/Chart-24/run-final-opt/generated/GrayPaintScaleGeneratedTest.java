package org.jfree.chart.renderer.junit;

import java.awt.Color;

import org.jfree.chart.renderer.GrayPaintScale;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class GrayPaintScaleGeneratedTest {

    @Test
    public void testDefaultBounds() {
        GrayPaintScale scale = new GrayPaintScale();

        assertEquals(0.0, scale.getLowerBound(), 0.0);
        assertEquals(1.0, scale.getUpperBound(), 0.0);
    }

    @Test
    public void testExplicitBoundsAreReturned() {
        GrayPaintScale scale = new GrayPaintScale(-10.0, 30.0);

        assertEquals(-10.0, scale.getLowerBound(), 0.0);
        assertEquals(30.0, scale.getUpperBound(), 0.0);
    }

    @Test
    public void testGetPaintAtDefaultBounds() {
        GrayPaintScale scale = new GrayPaintScale();

        assertEquals(new Color(0, 0, 0), scale.getPaint(0.0));
        assertEquals(new Color(255, 255, 255), scale.getPaint(1.0));
    }

    @Test
    public void testGetPaintForInteriorValue() {
        GrayPaintScale scale = new GrayPaintScale();

        assertEquals(new Color(127, 127, 127), scale.getPaint(0.5));
    }

    @Test
    public void testGetPaintForInteriorValueWithExplicitBounds() {
        GrayPaintScale scale = new GrayPaintScale(10.0, 20.0);

        assertEquals(new Color(127, 127, 127), scale.getPaint(15.0));
    }

    @Test
    public void testGetPaintClampsValueBelowLowerBound() {
        GrayPaintScale scale = new GrayPaintScale(10.0, 20.0);

        assertEquals(new Color(0, 0, 0), scale.getPaint(-5.0));
    }

    @Test
    public void testGetPaintClampsValueAboveUpperBound() {
        GrayPaintScale scale = new GrayPaintScale(10.0, 20.0);

        assertEquals(new Color(255, 255, 255), scale.getPaint(25.0));
    }

    @Test
    public void testConstructorRejectsEqualBounds() {
        try {
            new GrayPaintScale(2.0, 2.0);
            fail("Expected IllegalArgumentException for equal bounds.");
        }
        catch (IllegalArgumentException expected) {
            assertEquals("Requires lowerBound < upperBound.", expected.getMessage());
        }
    }

    @Test
    public void testConstructorRejectsReversedBounds() {
        try {
            new GrayPaintScale(3.0, -1.0);
            fail("Expected IllegalArgumentException for reversed bounds.");
        }
        catch (IllegalArgumentException expected) {
            assertEquals("Requires lowerBound < upperBound.", expected.getMessage());
        }
    }
}
