@Test
public void testEqualsWithDifferentClass() {
    ResolvedRecursiveType rec = new ResolvedRecursiveType(Object.class, null);
    rec.setReference(com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance().constructType
(String.class));
    assertFalse(rec.equals(new Object()));
}

@Test
public void testGetGenericSignatureAfterSetReferenceNonNull() {
    ResolvedRecursiveType rec = new ResolvedRecursiveType(Object.class, null);
    rec.setReference(com.fasterxml.jackson.databind.type.TypeFactory.defaultInstance().constructType
(String.class));
    StringBuilder sb = new StringBuilder();
    StringBuilder result = rec.getGenericSignature(sb);
    assertNotNull(result);
}

@Test
public void testRefineReturnsNullViaReflection() throws Exception {
    ResolvedRecursiveType rec = new ResolvedRecursiveType(Object.class, null);
    java.lang.reflect.Method refine = ResolvedRecursiveType.class.getDeclaredMethod("refine",
Class.class, com.fasterxml.jackson.databind.type.TypeBindings.class,
com.fasterxml.jackson.databind.JavaType.class, com.fasterxml.jackson.databind.JavaType[].class);
    refine.setAccessible(true);
    Object result = refine.invoke(rec, String.class, null, null, null);
    assertNull(result);
}

@Test
public void testEqualsBothUnresolved() {
    ResolvedRecursiveType rec1 = new ResolvedRecursiveType(Object.class, null);
    ResolvedRecursiveType rec2 = new ResolvedRecursiveType(Object.class, null);
    assertTrue(rec1.equals(rec2));
}