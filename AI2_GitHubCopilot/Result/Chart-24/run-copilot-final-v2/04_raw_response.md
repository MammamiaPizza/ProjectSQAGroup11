@Test
    public void testEqualsSameReference() {
        GrayPaintScale scale = new GrayPaintScale();
        assertTrue(scale.equals(scale));
    }

 @Test
 public void testEqualsDifferentLowerBound() {
     GrayPaintScale scale1 = new GrayPaintScale(0.0, 1.0);
     GrayPaintScale scale2 = new GrayPaintScale(0.5, 1.0);
     assertFalse(scale1.equals(scale2));
 }

 @Test
 public void testEqualsNull() {
     GrayPaintScale scale = new GrayPaintScale();
     assertFalse(scale.equals(null));
 }

 @Test
 public void testEqualsNonGrayPaintScale() {
     GrayPaintScale scale = new GrayPaintScale();
     assertFalse(scale.equals("not a GrayPaintScale"));
 }