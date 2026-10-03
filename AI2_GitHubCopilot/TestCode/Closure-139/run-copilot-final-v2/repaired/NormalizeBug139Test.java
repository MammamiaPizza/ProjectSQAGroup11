package com.google.javascript.jscomp;

 import com.google.javascript.jscomp.Compiler;
 import com.google.javascript.jscomp.Normalize;
 import com.google.javascript.jscomp.CompilerPass;
 import com.google.javascript.rhino.Node;
 import junit.framework.TestCase;

 public class NormalizeBug139Test extends TestCase {

   private void test(String js, String expected) {
     Compiler compiler = new Compiler();
     compiler.initOptions(new CompilerOptions());
     Node script = compiler.parseSyntheticCode("testcode", js);
     new Normalize(compiler, false).process(null, script);
     assertEquals(expected, compiler.toSource(script));
   }

   private void testSame(String js) {
     test(js, js);
   }

   private void testNoWarning(String js) {
     Compiler compiler = new Compiler();
     compiler.initOptions(new CompilerOptions());
     Node script = compiler.parseSyntheticCode("testcode", js);
     new Normalize(compiler, false).process(null, script);
     assertEquals("Expected no errors", 0, compiler.getErrorCount());
   }

   public void testSplitVarDeclarations() {
     test("var a=1,b=2;", "var a=1;var b=2;");
   }

   public void testSingleVarNoSplit() {
     testSame("var a=1;");
   }

   public void testRemoveDuplicateVarInGlobal() {
     test("var a=1;var a=2;", "var a=1;a=2;");
   }

   public void testRemoveDuplicateVarInFunction() {
     test("function f(){var a=1;var a=2;}", "function f(){var a=1;a=2;}");
   }

   public void testNoDuplicateInNestedScopes() {
     testSame("var a=1;function f(){var a=2;}");
   }

   public void testMoveFunctionDeclarations() {
     test(
         "function f(){var a=1;function g(){}return a;}",
         "function f(){function g(){}var a=1;return a;}");
   }

   public void testMoveMultipleFunctionDeclarations() {
     test(
         "function f(){var x=1;function a(){}var y=2;function b(){}return;}",
         "function f(){function a(){}function b(){}var x=1;var y=2;return;}");
   }

   public void testNormalizeLabels() {
     test("a:if(x);", "a:{if(x);}");
   }

   public void testExtractForInitializerVar() {
     test(
         "for(var i=0;i<10;i++){}",
         "var i=0;for(;i<10;i++){}");
   }

   public void testExtractForInitializerExpression() {
     test(
         "for(i=0;i<10;i++){}",
         "i=0;for(;i<10;i++){}");
   }

   public void testDuplicateVarErrorRemoved() {
     testNoWarning("var x=1;var x=2;");
   }

   public void testMoveFunctionInsideIf() {
     test(
         "function f(){if(true){var x=1;function g(){}}}",
         "function f(){function g(){}if(true){var x=1;}}");
   }
 }
