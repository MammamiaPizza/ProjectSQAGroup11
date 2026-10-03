package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import java.util.Collections;
import junit.framework.TestCase;

public class PeepholeSubstituteAlternateSyntaxIssue291Test extends TestCase {

  protected CompilerPass getProcessor(Compiler compiler) {
    return new PeepholeOptimizationsPass(
        compiler, new PeepholeSubstituteAlternateSyntax(compiler));
  }

  protected int getNumRepetitions() {
    return 2;
  }

  private void test(String input, String expected) {
    assertEquals(compile(expected, false), compile(input, true));
  }

  private void testSame(String input) {
    test(input, input);
  }

  private String compile(String source, boolean optimize) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", source)),
        new CompilerOptions());
    Node root = compiler.parseInputs();

    if (optimize) {
      CompilerPass processor = getProcessor(compiler);
      for (int i = 0; i < getNumRepetitions(); i++) {
        processor.process(null, root);
      }
    }

    return compiler.toSource(root);
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