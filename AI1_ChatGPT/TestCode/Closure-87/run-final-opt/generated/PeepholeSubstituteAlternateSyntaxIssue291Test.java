package com.google.javascript.jscomp;

public class PeepholeSubstituteAlternateSyntaxIssue291Test
    extends CompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new PeepholeOptimizationsPass(
        compiler, new PeepholeSubstituteAlternateSyntax(compiler));
  }

  @Override
  protected int getNumRepetitions() {
    return 2;
  }

  public void testOrdinaryFunctionCallCanBeFoldedFromIfBlock() {
    test("if (x) { f(); }", "x && f();");
  }

  public void testIssue291DoesNotFoldThisMethodCallFromIfBlock() {
    testSame("if (x) { this.f(); }");
  }

  public void testIssue291DoesNotFoldObjectMethodCallFromIfBlock() {
    testSame("if (x) { obj.f(); }");
  }

  public void testIssue291DoesNotFoldMethodCallsInBothIfBranches() {
    testSame("if (x) { obj.f(); } else { obj.g(); }");
  }
}
