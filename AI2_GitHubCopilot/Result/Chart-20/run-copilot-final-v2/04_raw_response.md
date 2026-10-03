@Test
    public void testEqualsWithNonValueMarkerMatchingAttributes() {
        ValueMarker vm = new ValueMarker(1.0);
        Stroke s = new BasicStroke(1.0f);
        vm.setPaint(Color.blue);
        vm.setStroke(s);
        vm.setOutlinePaint(Color.green);
        vm.setOutlineStroke(s);
        vm.setAlpha(0.5f);
        Marker other = new Marker(Color.blue, s, Color.green, s, 0.5f) {};
        assertFalse(vm.equals(other));
    }

 @Test
 public void testEqualsWithDifferentOutlinePaint() {
     ValueMarker m1 = new ValueMarker(1.0, Color.blue, new BasicStroke(1.0f));
     m1.setOutlinePaint(Color.green);
     ValueMarker m2 = new ValueMarker(1.0, Color.blue, new BasicStroke(1.0f));
     m2.setOutlinePaint(Color.red);
     assertFalse(m1.equals(m2));
 }

 @Test
 public void testEqualsWithDifferentOutlineStroke() {
     ValueMarker m1 = new ValueMarker(1.0, Color.blue, new BasicStroke(1.0f));
     m1.setOutlineStroke(new BasicStroke(2.0f));
     ValueMarker m2 = new ValueMarker(1.0, Color.blue, new BasicStroke(1.0f));
     m2.setOutlineStroke(new BasicStroke(3.0f));
     assertFalse(m1.equals(m2));
 }