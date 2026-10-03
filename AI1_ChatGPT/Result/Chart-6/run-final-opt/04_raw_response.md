@Test
public void cloneCreatesIndependentEqualList() throws java.lang.CloneNotSupportedException {
    ShapeList original = new ShapeList();
    original.setShape(1, new java.awt.geom.Line2D.Double(1.0, 2.0, 3.0, 4.0));

    ShapeList clone = (ShapeList) original.clone();

    org.junit.Assert.assertNotSame(original, clone);
    org.junit.Assert.assertEquals(original, clone);

    clone.setShape(2, new java.awt.geom.Line2D.Double(5.0, 6.0, 7.0, 8.0));
    org.junit.Assert.assertNull(original.getShape(2));
}