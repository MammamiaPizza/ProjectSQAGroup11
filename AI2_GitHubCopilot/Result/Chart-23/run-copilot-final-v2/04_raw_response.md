@Test
    public void testEqualsAfterSerialization() throws Exception {
        MinMaxCategoryRenderer r1 = new MinMaxCategoryRenderer();
        MinMaxCategoryRenderer r2;
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(baos);
        oos.writeObject(r1);
        oos.close();
        java.io.ByteArrayInputStream bais = new java.io.ByteArrayInputStream(baos.toByteArray());
        java.io.ObjectInputStream ois = new java.io.ObjectInputStream(bais);
        r2 = (MinMaxCategoryRenderer) ois.readObject();
        ois.close();
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

 @Test
 public void testSetGroupPaintEquality() {
     MinMaxCategoryRenderer r1 = new MinMaxCategoryRenderer();
     MinMaxCategoryRenderer r2 = new MinMaxCategoryRenderer();
     java.awt.Color c = java.awt.Color.RED;
     r1.setGroupPaint(c);
     r2.setGroupPaint(c);
     assertEquals(r1, r2);
 }

 @Test
 public void testSetGroupStrokeEquality() {
     MinMaxCategoryRenderer r1 = new MinMaxCategoryRenderer();
     MinMaxCategoryRenderer r2 = new MinMaxCategoryRenderer();
     java.awt.Stroke s = new java.awt.BasicStroke(2.0f);
     r1.setGroupStroke(s);
     r2.setGroupStroke(s);
     assertEquals(r1, r2);
 }

 @Test
 public void testObjectIconDimensions() {
     MinMaxCategoryRenderer r = new MinMaxCategoryRenderer();
     javax.swing.Icon icon = r.getObjectIcon();
     assertNotNull(icon);
     assertTrue(icon.getIconWidth() > 0);
     assertTrue(icon.getIconHeight() > 0);
 }