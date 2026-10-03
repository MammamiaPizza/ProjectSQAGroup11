@Test
    public void testConvertClassesToClassNames_nullInput() {

org.junit.Assert.assertNull(org.apache.commons.lang.ClassUtils.convertClassesToClassNames(null));
    }

 @Test
 public void testConvertClassesToClassNames_withNullEntry() {
     java.util.List<Class<?>> input = new java.util.ArrayList<Class<?>>();
     input.add(null);
     input.add(java.lang.String.class);
     java.util.List<String> output =
org.apache.commons.lang.ClassUtils.convertClassesToClassNames(input);
     org.junit.Assert.assertNull(output.get(0));
     org.junit.Assert.assertEquals("java.lang.String", output.get(1));
     org.junit.Assert.assertEquals(2, output.size());
 }

 @Test
 public void testIsAssignable_primitiveWidening_intToLong() {
     org.junit.Assert.assertTrue(org.apache.commons.lang.ClassUtils.isAssignable(int.class,
long.class));
 }

 @Test
 public void testIsAssignable_array_primitiveWidening_intToLong() {
     org.junit.Assert.assertTrue(org.apache.commons.lang.ClassUtils.isAssignable(new
Class[]{int.class}, new Class[]{long.class}));
 }