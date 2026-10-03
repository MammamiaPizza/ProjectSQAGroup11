package com.google.javascript.jscomp;

 import com.google.javascript.rhino.Node;

 public class RemoveUnusedVarsTest extends CompilerTestCase {

   private boolean removeGlobals = false;
   private boolean preserveFunctionExpressionNames = false;
   private boolean modifyCallSites = false;

   @Override
   protected void setUp() throws Exception {
     super.setUp();
     enableNormalize();
     removeGlobals = false;
     preserveFunctionExpressionNames = false;
     modifyCallSites = false;
   }

   @Override
   protected CompilerPass getProcessor(Compiler compiler) {
     return new RemoveUnusedVars(
         compiler, removeGlobals, preserveFunctionExpressionNames, modifyCallSites);
   }

   public void testSimpleModePreservesUnusedGlobalVar() {
     removeGlobals = false;
     testSame("var a = 1; function b() { return 2; } b();");
   }

   public void testSimpleModePreservesUnusedFunctionArg() {
     removeGlobals = false;
     testSame("function f(a,b) { return a; } f(1,2);");
   }

   public void testSimpleModeRemovesUnusedLocalVar() {
     removeGlobals = false;
     test("function f() { var a = 1; return 2; } f();",
          "function f() { return 2; } f();");
   }

   public void testAdvancedModeRemovesUnusedGlobalVar() {
     removeGlobals = true;
     test("var a = 1; function b() { return 2; } b();",
          "function b() { return 2; } b();");
   }

   public void testAdvancedModeRemovesUnusedFunctionArg() {
     removeGlobals = true;
     test("function f(a,b) { return a; } f(1,2);",
          "function f(a) { return a; } f(1,2);");
   }

   public void testAdvancedModeRemovesUnusedLocalVar() {
     removeGlobals = true;
     test("function f() { var a = 1; return 2; } f();",
          "function f() { return 2; } f();");
   }

   public void testPreserveUsedGlobalVar() {
     removeGlobals = true;
     testSame("var a = 1; function b() { return a; } b();");
   }

   public void testPreserveUsedFunctionArg() {
     removeGlobals = true;
     testSame("function f(a,b) { return a+b; } f(1,2);");
   }

   public void testPreserveGlobalFunctionUsedInNestedScope() {
     removeGlobals = true;
     testSame("function g() { return 1; } function f() { return g(); } f();");
   }

   public void testPreserveParamUsedInClosure() {
     removeGlobals = false;
     testSame("function f(a) { function g() { return a; } return g; } f(1);");
   }

   public void testCallSiteOptimizerRemovesUnusedTrailingArgs() {
     removeGlobals = true;
     modifyCallSites = true;
     test("function f(a,b) { return a; } f(1,2);",
          "function f(a) { return a; } f(1);");
   }

   public void testPreserveFunctionExpressionNameWhenFlagSet() {
     removeGlobals = true;
     preserveFunctionExpressionNames = true;
     testSame("var f = function myFunc() { return 1; }; f();");
   }
 }
