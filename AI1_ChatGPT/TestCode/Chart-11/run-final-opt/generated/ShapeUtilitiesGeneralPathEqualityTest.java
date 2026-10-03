package org.jfree.chart.util.junit;

import java.awt.geom.GeneralPath;

import org.jfree.chart.util.ShapeUtilities;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ShapeUtilitiesGeneralPathEqualityTest {

    @Test
    public void equalGeneralPathsWithMatchingClosedSegments() {
        GeneralPath p1 = new GeneralPath();
        p1.moveTo(1.0f, 2.0f);
        p1.lineTo(3.0f, 4.0f);
        p1.lineTo(5.0f, 1.0f);
        p1.closePath();

        GeneralPath p2 = new GeneralPath();
        p2.moveTo(1.0f, 2.0f);
        p2.lineTo(3.0f, 4.0f);
        p2.lineTo(5.0f, 1.0f);
        p2.closePath();

        assertTrue(ShapeUtilities.equal(p1, p2));
    }

    @Test
    public void equalGeneralPathsRejectsDifferentSegmentCoordinates() {
        GeneralPath p1 = new GeneralPath();
        p1.moveTo(0.0f, 0.0f);
        p1.lineTo(10.0f, 5.0f);

        GeneralPath p2 = new GeneralPath();
        p2.moveTo(0.0f, 0.0f);
        p2.lineTo(10.0f, 6.0f);

        assertFalse(ShapeUtilities.equal(p1, p2));
    }

    @Test
    public void equalGeneralPathsRejectsDifferentSegmentTypes() {
        GeneralPath p1 = new GeneralPath();
        p1.moveTo(0.0f, 0.0f);
        p1.lineTo(4.0f, 4.0f);

        GeneralPath p2 = new GeneralPath();
        p2.moveTo(0.0f, 0.0f);
        p2.quadTo(2.0f, 2.0f, 4.0f, 4.0f);

        assertFalse(ShapeUtilities.equal(p1, p2));
    }

    @Test
    public void equalGeneralPathsRejectsDifferentSegmentCounts() {
        GeneralPath p1 = new GeneralPath();
        p1.moveTo(0.0f, 0.0f);
        p1.lineTo(1.0f, 1.0f);

        GeneralPath p2 = new GeneralPath();
        p2.moveTo(0.0f, 0.0f);
        p2.lineTo(1.0f, 1.0f);
        p2.closePath();

        assertFalse(ShapeUtilities.equal(p1, p2));
    }

    @Test
    public void equalGeneralPathsRejectsDifferentWindingRules() {
        GeneralPath p1 = new GeneralPath(GeneralPath.WIND_NON_ZERO);
        p1.moveTo(0.0f, 0.0f);
        p1.lineTo(1.0f, 0.0f);
        p1.lineTo(1.0f, 1.0f);
        p1.closePath();

        GeneralPath p2 = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        p2.moveTo(0.0f, 0.0f);
        p2.lineTo(1.0f, 0.0f);
        p2.lineTo(1.0f, 1.0f);
        p2.closePath();

        assertFalse(ShapeUtilities.equal(p1, p2));
    }

    @Test
    public void equalGeneralPathsHandlesMoveAndCloseOnlyPaths() {
        GeneralPath p1 = new GeneralPath();
        p1.moveTo(2.0f, 3.0f);
        p1.closePath();

        GeneralPath p2 = new GeneralPath();
        p2.moveTo(2.0f, 3.0f);
        p2.closePath();

        assertTrue(ShapeUtilities.equal(p1, p2));
    }

    @Test
    public void equalGeneralPathsComparesAllSubpaths() {
        GeneralPath p1 = new GeneralPath();
        p1.moveTo(0.0f, 0.0f);
        p1.lineTo(1.0f, 1.0f);
        p1.moveTo(10.0f, 10.0f);
        p1.lineTo(11.0f, 11.0f);

        GeneralPath p2 = new GeneralPath();
        p2.moveTo(0.0f, 0.0f);
        p2.lineTo(1.0f, 1.0f);
        p2.moveTo(10.0f, 10.0f);
        p2.lineTo(11.0f, 12.0f);

        assertFalse(ShapeUtilities.equal(p1, p2));
    }

    @Test
    public void equalGeneralPathsHandlesNullArguments() {
        assertTrue(ShapeUtilities.equal((GeneralPath) null, (GeneralPath) null));

        GeneralPath path = new GeneralPath();
        path.moveTo(0.0f, 0.0f);
        assertFalse(ShapeUtilities.equal(path, null));
        assertFalse(ShapeUtilities.equal(null, path));
    }
}
