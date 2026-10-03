package com.google.javascript.jscomp;

 import junit.framework.TestCase;
 import com.google.javascript.rhino.Node;
 import java.util.Collections;
 import java.util.List;

 /**
  * Tests for {@link FunctionRewriter}.
  */
 public class FunctionRewriterTest extends TestCase {

     private void test(String js, String expected) {
         Compiler compiler = new Compiler();
         CompilerOptions options = new CompilerOptions();

         List<SourceFile> externs = Collections.singletonList(
             SourceFile.fromCode("externs", ""));
         List<SourceFile> inputs = Collections.singletonList(
             SourceFile.fromCode("testcode", js));

         compiler.init(externs, inputs, options);
         compiler.parse();

         Node root = compiler.getRoot();
         Node externsRoot = compiler.getExternsRoot();

         FunctionRewriter rewriter = new FunctionRewriter(compiler);
         rewriter.process(externsRoot, root);

         String actual = compiler.toSource();
         assertEquals(expected, actual);
     }

     private void testSame(String js) {
         test(js, js);
     }

     /** Regression test for issue 538: non-function nodes must not trigger IllegalStateException.
*/
     public void testIssue538() {
         testSame("f();");
         testSame("a.b;");
         testSame("new a();");
     }

     public void testEmptyFunctionReduction() {
         test("var f = function(){}", "var f=JSCompiler_emptyFn()");
     }

     public void testIdentityFunctionReduction() {
         test("var f = function(a){return a}", "var f=JSCompiler_identity()");
     }

     public void testReturnConstantNumberReduction() {
         test("var f = function(){return 42}", "var f=JSCompiler_returnConstant(42)");
     }

     public void testReturnConstantStringReduction() {
         test("var f = function(){return \"hello\"}",
              "var f=JSCompiler_returnConstant(\"hello\")");
     }

     public void testReturnConstantBooleanReduction() {
         test("var f = function(){return true}",
              "var f=JSCompiler_returnConstant(true)");
     }

     public void testReturnConstantNullReduction() {
         test("var f = function(){return null}",
              "var f=JSCompiler_returnConstant(null)");
     }

     public void testGetterReduction() {
         test("var f = function(){return this.prop}",
              "var f=JSCompiler_get(\"prop\")");
     }

     public void testSetterReduction() {
         test("var f = function(val){this.prop=val}",
              "var f=JSCompiler_set(\"prop\")");
     }

     public void testNonReduceableMultiStatement() {
         testSame("var f = function(){var a=1;return a}");
     }

     public void testNonReduceableIdentityNoParameters() {
         testSame("var f = function(){return 1}");
     }

     public void testNonReduceableGetterOnNonThis() {
         testSame("var f = function(){return a.prop}");
     }
 }
