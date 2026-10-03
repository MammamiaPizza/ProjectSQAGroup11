@Test
public void testExplicitInferredVoidReturnTypeIsPreserved() {
  com.google.javascript.rhino.jstype.FunctionType function =
      new com.google.javascript.rhino.jstype.FunctionBuilder(registry)
          .withReturnType(
              registry.getNativeType(
                  com.google.javascript.rhino.jstype.JSTypeNative.VOID_TYPE),
              true)
          .build();

  assertSame(
      registry.getNativeType(com.google.javascript.rhino.jstype.JSTypeNative.VOID_TYPE),
      function.getReturnType());
  assertTrue(function.isReturnTypeInferred());
}