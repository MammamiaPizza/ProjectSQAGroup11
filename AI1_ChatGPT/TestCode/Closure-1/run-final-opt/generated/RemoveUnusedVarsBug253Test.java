package com.google.javascript.jscomp;

public class RemoveUnusedVarsBug253Test extends CompilerTestCase {
  private boolean removeGlobals;
  private boolean preserveFunctionExpressionNames;
  private boolean modifyCallSites;

  @Override
  public void setUp() {
    enableNormalize();
  }

  @Override
  protected CompilerPass getProcessor(Compiler compiler) {
    return new RemoveUnusedVars(
        compiler,
        removeGlobals,
        preserveFunctionExpressionNames,
        modifyCallSites);
  }

  public void testRemovesUnusedGlobalDeclaration() {
    removeGlobals = true;
    test("var unused;", "");
  }

  public void testRemovesPureUnusedGlobalInitializer() {
    removeGlobals = true;
    test("var unused = 1;", "");
  }

  public void testPreservesSideEffectsOfUnusedGlobalInitializer() {
    removeGlobals = true;
    test("var unused = sideEffect();", "sideEffect();");
  }

  public void testKeepsReferencedGlobal() {
    removeGlobals = true;
    testSame("var used = 1; used;");
  }

  public void testSimpleModeLeavesUnusedFunctionParameters() {
    removeGlobals = false;
    testSame("function f(a, unused) { return a; } f(1, 2);");
  }

  public void testAdvancedModeRemovesTrailingUnusedFunctionParameter() {
    removeGlobals = true;
    modifyCallSites = false;
    test(
        "function f(a, unused) { return a; } f(1, 2);",
        "function f(a) { return a; } f(1, 2);");
  }

  public void testCallSiteOptimizationRemovesUnusedSideEffectFreeArgument() {
    removeGlobals = true;
    modifyCallSites = true;
    test(
        "function f(a, unused) { return a; } f(1, 2);",
        "function f(a) { return a; } f(1);");
  }

  public void testCallSiteOptimizationPreservesSideEffectingArgument() {
    removeGlobals = true;
    modifyCallSites = true;
    testSame("function f(a, unused) { return a; } f(1, sideEffect());");
  }
}
