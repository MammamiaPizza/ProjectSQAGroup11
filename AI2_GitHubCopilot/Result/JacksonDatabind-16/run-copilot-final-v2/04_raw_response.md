@Test
    public void testToStringWhenNull() {
        AnnotationMap map = new AnnotationMap();
        assertEquals("[null]", map.toString());
    }

 @Test
 public void testToStringNonEmpty() throws Exception {
     AnnotationMap map = new AnnotationMap();
     map.add(AnnHolder.class.getMethod("dummy").getAnnotation(A.class));
     String s = map.toString();
     assertNotNull(s);
     assertFalse("[null]".equals(s));
     assertTrue(s.contains("A"));
 }

 @Test
 public void testAddWithDifferentAnnotationSameType() throws Exception {
     AnnotationMap map = new AnnotationMap();
     A a1 = AnnHolder.class.getMethod("dummy1").getAnnotation(A.class);
     A a2 = AnnHolder.class.getMethod("dummy2").getAnnotation(A.class);
     map.add(a1);
     assertFalse(map.add(a2));
     assertSame(a2, map.get(A.class));
 }

 @Test
 public void testAnnotationsWhenMapNull() {
     AnnotationMap map = new AnnotationMap();
     Iterable<Annotation> it = map.annotations();
     assertNotNull(it);
     assertFalse(it.iterator().hasNext());
 }

 static class AnnHolder {
     @A("primary")
     @B("secondary")
     public void dummy() {}
     @A("first")
     public void dummy1() {}
     @A("second")
     public void dummy2() {}
 }