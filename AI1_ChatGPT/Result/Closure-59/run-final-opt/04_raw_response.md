@Test
public void testDebugLogIsEmptyBeforeCompilation() {
  com.google.javascript.jscomp.Compiler compiler =
      new com.google.javascript.jscomp.Compiler();

  assertEquals("", compiler.get());
}