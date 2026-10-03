package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import java.util.Collections;
import org.junit.Test;

public final class ExploitAssignsGeneratedTest extends GeneratedCompilerTestCase {

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new PeepholeOptimizationsPass(compiler, new ExploitAssigns());
  }

  @Override
  protected int getNumRepetitions() {
    return 1;
  }

  @Test
  public void testChainsNameAssignmentIntoFollowingAssignment() {
    test("a = 3; b = a;", "b = a = 3;");
  }

  @Test
  public void testChainsImmutableValueIntoFollowingAssignment() {
    test("a = true; b = true;", "b = a = true;");
  }

  @Test
  public void testChainsThisPropertyAssignment() {
    test("this.a = 0; this.a;", "this.a = this.a = 0;");
  }

  @Test
  public void testDoesNotExploitReplacementThatReassignsReferencedName() {
    testSame("a = a.b; a.b;");
  }

  @Test
  public void testIssue1017DoesNotExploitReplacementThatChangesQualifiedBase() {
    testSame("a.b = a.b.c; a.b.c;");
  }

  @Test
  public void testDoesNotMoveAssignmentAcrossArbitraryPropertyLValue() {
    testSame("a.b = 0; a.b = 0;");
  }
}

abstract class GeneratedCompilerTestCase {
  protected abstract CompilerPass getProcessor(Compiler compiler);

  protected int getNumRepetitions() {
    return 1;
  }

  protected final void testSame(String code) {
    test(code, code);
  }

  protected final void test(String code, String expected) {
    Compiler compiler = createCompiler(code);
    Node root = compiler.getRoot();
    Node externs = root.getFirstChild();
    Node jsRoot = root.getLastChild();

    for (int i = 0; i < getNumRepetitions(); i++) {
      getProcessor(compiler).process(externs, jsRoot);
    }

    Compiler expectedCompiler = createCompiler(expected);
    Node expectedRoot = expectedCompiler.getRoot().getLastChild();
    if (!jsRoot.isEquivalentTo(expectedRoot)) {
      throw new AssertionError("Unexpected transformed AST");
    }
  }

  private Compiler createCompiler(String code) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", code)),
        new CompilerOptions());
    compiler.parseInputs();
    return compiler;
  }
}