package com.google.javascript.jscomp;

 import com.google.javascript.jscomp.CompilerTestCase;
 import com.google.javascript.jscomp.DiagnosticType;
 import com.google.javascript.rhino.Node;
 import com.google.javascript.rhino.Token;

 public class RemoveUnusedVarsTest extends CompilerTestCase {

   @Override
   protected CompilerPass getProcessor(Compiler compiler) {
     // Remove unused vars and unused function args, with call-site optimization enabled.
     return new RemoveUnusedVars(compiler, /*removeGlobals*/false,
/*preserveFunctionExpressionNames*/false, /modifyCallSites*//*true*/true);
   }

   @Override
   protected int getNumRepetitions() {
     return 1;
  }

   // Normal removal of an unused local variable
   public void testUnusedLocalVar() {
     test("function f() { var x = 1; }", "function f() { 1; }");
   }

  ​// Removal of unused function parameters when call-sites are modified
   public void testUnusedFunctionParam() {
     test("function f(x,y) { return x; } f(1,2);", "function f(x) { return x; } f(1);");
   }

   // Variable used only in a property access (as object) should be kept
   public void testVarUsedOnlyInPropertyAccess() {
     testSame("function f() { var x = {}; x.prop = 1; }");
   }

   // Variable used in typeof check should not be removed
   public void testVarUsedInTypeof() {
     testSame("function f() { var x; if (typeof x != 'undefined') return x; }");
   }

   // Variable used inside eval() – ambiguous reference, may be incorrectly removed
   // The current passes keep variables used in eval.
   public void testVarUsedInEval() {
     testSame("function f() { var x; eval('x'); }");
   }

   // Multiple var declarations: only the unreferenced name is removed
   public void testMultipleVarDeclarationOneUnused() {
     test("var a = 1, b = 2; alert(a);", "var a = 1; alert(a);");
   }

   // Issue 618 regression: unused function parameter should NOT be removed
   // if the function is called via a property access, because call sites
   // cannot be modified in that case.
   public void testIssue618_1() {
     testSame("function f(x) { return 1; } var obj = { m: f }; obj.m(1);");
   }

   // Another aspect of issue 618: function referenced only as a callback
   // should keep its unused parameters.
   public void testIssue618_2() {
     testSame("function callback(a,b) { return a; } setTimeout(callback, 100);");
   }

   // Function expression name preservation
   public void testPreserveFunctionExpressionName() {
     // With preserveFunctionExpressionNames = false, the name is removed (blanked out)
     RemoveUnusedVars pass = new RemoveUnusedVars(compiler, /*removeGlobals*/false,
/*presreveFunctionExpressionNames*/false, /modifyCallSites*/true);
     test("var f = function g() { return g; };", "var f = function () { return f; };");
   }

   // Unused function argument removed when it is the last parameter,
   // even if canChangeSignature is false, provided all call sites can be adjusted.
   public void testRemoveLastUnusedParamWhenCallSitesAllow() {
     test("function f(x,y) { return x; } f(1,2);", "function f(x) { return x; } f(1);");
   }

   // Function used inside .call() should not have its signature changed
   // because the argument positions shift.
   public void testCallApplyDoesNotRemoveArgs() {
     testSame("function f(x) { return x; } var obj = {}; f.call(obj, 1);");
   }

   // // function with no parameters and no call sites – no changes
   public void testNoParamsNoCalls() {
     testSame("function f() {}");
   }
 }