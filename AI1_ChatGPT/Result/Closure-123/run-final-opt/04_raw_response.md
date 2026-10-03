@org.junit.Test
public void testInOperatorInConditionalConditionOfForInitializerIsParenthesized() {
  assertEquals(
      "for(a=(b in c)?0:1;;)foo()",
      printNoInContextTestCode("for(a=(b in c)?0:1;;)foo()"));
}

@org.junit.Test
public void testInOperatorInConditionalTrueBranchOfForInitializerDoesNotNeedParentheses() {
  assertEquals(
      "for(a=b?c in d:0;;)foo()",
      printNoInContextTestCode("for(a=b?(c in d):0;;)foo()"));
}

private String printNoInContextTestCode(String source) {
  com.google.javascript.jscomp.Compiler compiler =
      new com.google.javascript.jscomp.Compiler();
  compiler.initOptions(new com.google.javascript.jscomp.CompilerOptions());
  com.google.javascript.rhino.Node root = compiler.parseTestCode(source);
  assertNotNull(root);
  return new com.google.javascript.jscomp.CodePrinter.Builder(root).build();
}