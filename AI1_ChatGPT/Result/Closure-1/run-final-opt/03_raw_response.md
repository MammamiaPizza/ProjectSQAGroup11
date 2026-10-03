package com.google.javascript.jscomp;

import com.google.javascript.rhino.Node;
import java.util.Collections;
import junit.framework.TestCase;

public class RemoveUnusedVarsBug253Test extends TestCase {
  private boolean removeGlobals;
  private boolean preserveFunctionExpressionNames;
  private boolean modifyCallSites;
  private boolean normalize;

  @Override
  public void setUp() {
    normalize = true;
  }

  protected CompilerPass getProcessor(Compiler compiler) {
    return new RemoveUnusedVars(
        compiler,
        removeGlobals,
        preserveFunctionExpressionNames,
        modifyCallSites);
  }

  private void test(String js, String expected) {
    assertEquals(compile(expected, false), compile(js, true));
  }

  private void testSame(String js) {
    test(js, js);
  }

  private String compile(String js, boolean runProcessor) {
    Compiler compiler = new Compiler();
    compiler.init(
        Collections.<SourceFile>emptyList(),
        Collections.singletonList(SourceFile.fromCode("testcode", js)),
        new CompilerOptions());

    Node root = compiler.parseInputs();
    Node externs = root.getFirstChild();
    Node source = root.getLastChild();

    if (normalize) {
      new Normalize(compiler, false).process(externs, source);
    }

    if (runProcessor) {
      getProcessor(compiler).process(externs, source);
    }

    return compiler.toSource();
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