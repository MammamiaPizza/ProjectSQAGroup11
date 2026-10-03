@Test
public void testConstructorWithCloseableMsgAndThrowable() {
    JsonMappingException ex = new JsonMappingException((java.io.Closeable) null, "test message",
            new RuntimeException("root cause"));
    assertEquals("test message", ex.getMessage());
    assertNotNull(ex.getCause());
    assertEquals("root cause", ex.getCause().getMessage());
    assertNull(ex.getProcessor());
}

@Test
public void testConstructorWithCloseableMsgAndLocation() {
    com.fasterxml.jackson.core.JsonLocation loc = new com.fasterxml.jackson.core.JsonLocation(null,
0L, 0L, 10, 5);
    JsonMappingException ex = new JsonMappingException((java.io.Closeable) null, "test message",
loc);
    assertEquals("test message", ex.getMessage());
    assertNull(ex.getProcessor());
}

@Test
@SuppressWarnings("deprecation")
public void testDeprecatedConstructorWithLocationAndCause() {
    com.fasterxml.jackson.core.JsonLocation loc = new com.fasterxml.jackson.core.JsonLocation(null,
0L, 0L, 1, 1);
    JsonMappingException ex = new JsonMappingException("deprecated msg", loc, new
RuntimeException("root"));
    assertEquals("deprecated msg", ex.getMessage());
    assertNotNull(ex.getCause());
    assertEquals("root", ex.getCause().getMessage());
}

@Test
public void testGetPathReferenceRetainsEnclosingClassForInnerClassReferrer() {
    JsonMappingException ex = new JsonMappingException("test error");
    Object innerRef = new Object() {};
    ex.prependPath(innerRef, "innerField");
    String ref = ex.getPathReference();
    assertTrue("Path reference must include enclosing class name for inner class referrer",
            ref.contains(getClass().getName()));
}