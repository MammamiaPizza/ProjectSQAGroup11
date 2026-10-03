package com.google.javascript.jscomp;

import com.google.javascript.jscomp.testing.CompilerTestCase;
import org.junit.Test;

public class NameAnalyzerIssue284Test extends CompilerTestCase {
  private boolean removeUnreferenced = true;

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new NameAnalyzer(compiler, removeUnreferenced);
  }

  @Test
  public void testRemovesUnusedLocalDeclaration() {
    removeUnreferenced = true;
    test(
        "function f(){var unused;}f();",
        "function f(){}f();");
  }

  @Test
  public void testPreservesSideEffectingInitializerOfUnusedLocal() {
    removeUnreferenced = true;
    test(
        "function f(){var unused=sideEffect();}f();",
        "function f(){sideEffect();}f();");
  }

  @Test
  public void testPreservesShortCircuitInitializerOfUnusedLocal() {
    removeUnreferenced = true;
    test(
        "function f(){var unused=first()&&second();}f();",
        "function f(){first()&&second();}f();");
  }

  @Test
  public void testReplacesUnusedAssignmentWhoseValueIsReturned() {
    removeUnreferenced = true;
    test(
        "function f(){var unused;return unused=sideEffect();}f();",
        "function f(){return sideEffect();}f();");
  }

  @Test
  public void testReplacesUnusedAssignmentWithShortCircuitValueWhenConsumed() {
    removeUnreferenced = true;
    test(
        "function f(){var unused;return unused=first()||second();}f();",
        "function f(){return first()||second();}f();");
  }

  @Test
  public void testAnalyzesAliasesAndPropertyReferencesWithoutChangingAst() {
    removeUnreferenced = false;
    testSame(
        "function f(){"
            + "var original={};"
            + "var alias=original;"
            + "alias.value=1;"
            + "return original.value;"
            + "}f();");
  }

  @Test
  public void testAnalyzesPrototypeAndClassReferencesWithoutChangingAst() {
    removeUnreferenced = false;
    testSame(
        "function C(){}"
            + "C.prototype.method=function(){return 1;};"
            + "(new C()).method();");
  }

  @Test
  public void testAnalyzesClassDefiningCallWithoutChangingAst() {
    removeUnreferenced = false;
    testSame(
        "function Parent(){}"
            + "function Child(){}"
            + "goog.inherits(Child,Parent);"
            + "new Child();");
  }
}
