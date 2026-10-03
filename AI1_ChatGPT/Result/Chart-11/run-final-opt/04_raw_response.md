@org.junit.Test
public void equalGeneralPathsComparesQuadraticAndCubicControlPoints() {
    java.awt.geom.GeneralPath p1 = new java.awt.geom.GeneralPath();
    p1.moveTo(0.0f, 0.0f);
    p1.quadTo(1.0f, 2.0f, 3.0f, 0.0f);
    p1.curveTo(4.0f, 1.0f, 5.0f, 2.0f, 6.0f, 0.0f);
    p1.closePath();

    java.awt.geom.GeneralPath p2 = new java.awt.geom.GeneralPath();
    p2.moveTo(0.0f, 0.0f);
    p2.quadTo(1.0f, 2.0f, 3.0f, 0.0f);
    p2.curveTo(4.0f, 1.0f, 5.0f, 2.0f, 6.0f, 0.0f);
    p2.closePath();

    java.awt.geom.GeneralPath p3 = new java.awt.geom.GeneralPath();
    p3.moveTo(0.0f, 0.0f);
    p3.quadTo(1.0f, 2.0f, 3.0f, 0.0f);
    p3.curveTo(4.0f, 1.0f, 5.0f, 3.0f, 6.0f, 0.0f);
    p3.closePath();

    org.junit.Assert.assertTrue(ShapeUtilities.equal(p1, p2));
    org.junit.Assert.assertFalse(ShapeUtilities.equal(p1, p3));
}

@org.junit.Test
public void cloneCreatesIndependentCloneForCloneableShape() {
    java.awt.geom.Rectangle2D.Double source
            = new java.awt.geom.Rectangle2D.Double(1.0, 2.0, 3.0, 4.0);

    java.awt.Shape copy = ShapeUtilities.clone(source);

    org.junit.Assert.assertNotNull(copy);
    org.junit.Assert.assertNotSame(source, copy);
    org.junit.Assert.assertEquals(source.getBounds2D(), copy.getBounds2D());
}

@org.junit.Test
public void containsIncludesBoundaryAndRejectsRectanglesOutsideAnyEdge() {
    java.awt.geom.Rectangle2D outer
            = new java.awt.geom.Rectangle2D.Double(0.0, 0.0, 10.0, 10.0);

    org.junit.Assert.assertTrue(ShapeUtilities.contains(outer,
            new java.awt.geom.Rectangle2D.Double(0.0, 0.0, 10.0, 10.0)));
    org.junit.Assert.assertFalse(ShapeUtilities.contains(outer,
            new java.awt.geom.Rectangle2D.Double(-0.1, 0.0, 1.0, 1.0)));
    org.junit.Assert.assertFalse(ShapeUtilities.contains(outer,
            new java.awt.geom.Rectangle2D.Double(0.0, -0.1, 1.0, 1.0)));
    org.junit.Assert.assertFalse(ShapeUtilities.contains(outer,
            new java.awt.geom.Rectangle2D.Double(9.5, 0.0, 1.0, 1.0)));
    org.junit.Assert.assertFalse(ShapeUtilities.contains(outer,
            new java.awt.geom.Rectangle2D.Double(0.0, 9.5, 1.0, 1.0)));
}

@org.junit.Test
public void createDiagonalCrossHasExpectedBounds() {
    java.awt.Shape cross = ShapeUtilities.createDiagonalCross(2.0f, 1.0f);

    java.awt.geom.Rectangle2D bounds = cross.getBounds2D();
    org.junit.Assert.assertEquals(-3.0, bounds.getX(), 0.0);
    org.junit.Assert.assertEquals(-3.0, bounds.getY(), 0.0);
    org.junit.Assert.assertEquals(6.0, bounds.getWidth(), 0.0);
    org.junit.Assert.assertEquals(6.0, bounds.getHeight(), 0.0);
}