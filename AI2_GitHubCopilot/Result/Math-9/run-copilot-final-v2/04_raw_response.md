@Test
    public void testToSubSpaceAbscissaMatch() {
        Line line = new Line(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0));
        Vector3D point = new Vector3D(2, 3, 4);
        double abscissa = line.getAbscissa(point);
        Vector1D result = line.toSubSpace(point);
        assertEquals(abscissa, result.getX(), 1.0e-15);
    }

 @Test
 public void testWholeLineReturnsNonNullSubLine() {
     Line line = new Line(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0));
     org.apache.commons.math3.geometry.euclidean.threed.SubLine sub = line.wholeLine();
     assertNotNull(sub);
 }

 @Test
 public void testParallelLinesDistanceAndClosestPoint() {
     Line line1 = new Line(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0));
     assertEquals(0.0, line1.distance(line1), 1.0e-15);
     Vector3D cp = line1.closestPoint(line1);
     assertEquals("X mismatch", 0.0, cp.getX(), 1.0e-15);
     assertEquals("Y mismatch", 0.0, cp.getY(), 1.0e-15);
     assertEquals("Z mismatch", 0.0, cp.getZ(), 1.0e-15);
 }

 @Test
 public void testNonParallelLinesDistanceAndClosestPoint() {
     Line line1 = new Line(new Vector3D(0, 0, 0), new Vector3D(1, 0, 0));
     Line line2 = new Line(new Vector3D(0, 0, 1), new Vector3D(0, 1, 0));
     double d = line1.distance(line2);
     assertTrue("Distance should be positive for skew lines", d > 0);
     assertEquals("Distance must be symmetric", d, line2.distance(line1), 1.0e-15);
     Vector3D cp = line1.closestPoint(line2);
     assertTrue("Closest point must be on line1", line1.contains(cp));
     assertEquals("Distance from line2 to closest point must equal line-line distance",
             d, line2.distance(cp), 1.0e-15);
 }