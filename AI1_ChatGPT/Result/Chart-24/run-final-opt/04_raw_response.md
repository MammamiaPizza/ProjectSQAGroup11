@Test
public void testEquals() {
    GrayPaintScale scale = new GrayPaintScale(-10.0, 30.0);
    org.junit.Assert.assertTrue(scale.equals(scale));
    org.junit.Assert.assertFalse(scale.equals(null));
    org.junit.Assert.assertFalse(scale.equals("not a scale"));
    org.junit.Assert.assertTrue(scale.equals(new GrayPaintScale(-10.0, 30.0)));
    org.junit.Assert.assertFalse(scale.equals(new GrayPaintScale(-9.0, 30.0)));
    org.junit.Assert.assertFalse(scale.equals(new GrayPaintScale(-10.0, 31.0)));
}

@Test
public void testClone() throws CloneNotSupportedException {
    GrayPaintScale scale = new GrayPaintScale(-10.0, 30.0);
    Object clone = scale.clone();

    org.junit.Assert.assertNotSame(scale, clone);
    org.junit.Assert.assertTrue(scale.equals(clone));
}