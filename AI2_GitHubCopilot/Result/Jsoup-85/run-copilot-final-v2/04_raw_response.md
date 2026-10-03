@Test
    public void testClone() {
        Attribute attr = new Attribute("key", "value");
        Attribute clone = attr.clone();
        assertTrue(attr != clone);
        assertEquals("key", clone.getKey());
        assertEquals("value", clone.getValue());
    }

 @Test
 public void testEquals() {
     Attribute a1 = new Attribute("key", "value");
     Attribute a2 = new Attribute("key", "value");
     Attribute a3 = new Attribute("key", "different");
     assertTrue(a1.equals(a1));
     assertTrue(a1.equals(a2));
     assertFalse(a1.equals(a3));
     assertFalse(a1.equals(null));
     assertFalse(a1.equals("notAttribute"));
 }

 @Test
 public void testToString() {
     Attribute attr = new Attribute("key", "value");
     assertEquals("key=\"value\"", attr.toString());
 }

 @Test
 public void testCreateFromEncoded() {
     Attribute attr = Attribute.createFromEncoded("key", "value");
     assertEquals("key", attr.getKey());
     assertEquals("value", attr.getValue());
 }